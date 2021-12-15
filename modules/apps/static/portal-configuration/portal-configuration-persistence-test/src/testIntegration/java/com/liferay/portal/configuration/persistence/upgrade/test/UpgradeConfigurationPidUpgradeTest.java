/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.configuration.persistence.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.util.PropsValues;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Dictionary;
import java.util.Objects;

import org.apache.felix.cm.file.ConfigurationHandler;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Sam Ziemer
 */
@RunWith(Arquillian.class)
public class UpgradeConfigurationPidUpgradeTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		_upgradeStepRegistrator.register(
			(fromSchemaVersionString, toSchemaVersionString, upgradeSteps) -> {
				for (UpgradeStep upgradeStep : upgradeSteps) {
					Class<?> clazz = upgradeStep.getClass();

					if (Objects.equals(
							clazz.getName(),
							"com.liferay.portal.configuration.persistence." +
								"internal.upgrade.v1_0_0." +
									"UpgradeConfigurationPid")) {

						_upgradeConfigurationPidUpgradeProcess =
							(UpgradeProcess)upgradeStep;
					}
				}
			});
	}

	@After
	public void tearDown() throws Exception {
		DB db = DBManagerUtil.getDB();

		db.runSQL(
			"delete from Configuration_ where configurationId like '" +
				_SERVICE_FACTORY_PID + "%'");
	}

	@Test
	public void testUpgradeConfigurationPid() throws Exception {
		_testUpgradeConfigurationPid(CharPool.PERIOD, "1234");
		_testUpgradeConfigurationPid(CharPool.DASH, "abcd-abcd");
		_testUpgradeConfigurationPid(CharPool.TILDE, "1234");
	}

	@Test
	public void testUpgradeFileConfiguration() throws Exception {
		_testUpgradeFileConfiguration(CharPool.PERIOD, "1234", CharPool.PERIOD);
		_testUpgradeFileConfiguration(
			CharPool.DASH, "abcd-abcd", CharPool.PERIOD);
		_testUpgradeFileConfiguration(CharPool.TILDE, "abcd", CharPool.PERIOD);

		_testUpgradeFileConfiguration(CharPool.PERIOD, "1234", CharPool.DASH);
		_testUpgradeFileConfiguration(
			CharPool.DASH, "abcd-abcd", CharPool.DASH);
		_testUpgradeFileConfiguration(CharPool.TILDE, "abcd", CharPool.DASH);

		_testUpgradeFileConfiguration(CharPool.PERIOD, "1234", CharPool.TILDE);
		_testUpgradeFileConfiguration(
			CharPool.DASH, "abcd-abcd", CharPool.TILDE);
		_testUpgradeFileConfiguration(CharPool.TILDE, "abcd", CharPool.TILDE);
	}

	private void _createConfiguration(
			String configurationPid, Dictionary<String, String> dictionary)
		throws Exception {

		UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
			new UnsyncByteArrayOutputStream();

		ConfigurationHandler.write(unsyncByteArrayOutputStream, dictionary);

		try (Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				"insert into Configuration_ (configurationId, dictionary) " +
					"values(?, ?)")) {

			preparedStatement.setString(1, configurationPid);

			preparedStatement.setString(
				2, unsyncByteArrayOutputStream.toString());

			preparedStatement.execute();
		}
	}

	private Dictionary<String, String> _getDictionary() throws Exception {
		String dictionaryString = null;

		try (Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select dictionary from Configuration_ where ",
					"configurationId like '", _SERVICE_FACTORY_PID, "%'"));
			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				dictionaryString = resultSet.getString("dictionary");
			}
		}

		return ConfigurationHandler.read(
			new UnsyncByteArrayInputStream(
				dictionaryString.getBytes(StringPool.UTF8)));
	}

	private void _testUpgradeConfigurationPid(char separator, String postfix)
		throws Exception {

		String configurationPid = _SERVICE_FACTORY_PID + separator + postfix;

		_createConfiguration(
			configurationPid,
			HashMapDictionaryBuilder.put(
				"service.factoryPid", _SERVICE_FACTORY_PID
			).put(
				"service.pid", configurationPid
			).build());

		_upgradeConfigurationPidUpgradeProcess.upgrade();

		Dictionary<String, String> dictionary = _getDictionary();

		Assert.assertEquals(
			_SERVICE_FACTORY_PID, dictionary.get("service.factoryPid"));

		Assert.assertEquals(
			_SERVICE_FACTORY_PID + StringPool.TILDE + postfix,
			dictionary.get("service.pid"));
	}

	private void _testUpgradeFileConfiguration(
			char configurationPidSeparator, String configurationPidPostfix,
			char fileSeparator)
		throws Exception {

		String fileName = StringBundler.concat(
			_SERVICE_FACTORY_PID, fileSeparator, "default.config");

		String configurationPid =
			_SERVICE_FACTORY_PID + configurationPidSeparator +
				configurationPidPostfix;

		File file = new File(
			PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR, fileName);

		Path path = file.toPath();

		if (!file.exists()) {
			Files.createFile(path);
		}

		try {
			_createConfiguration(
				configurationPid,
				HashMapDictionaryBuilder.put(
					"felix.fileinstall.filename", fileName
				).put(
					"service.factoryPid", _SERVICE_FACTORY_PID
				).put(
					"service.pid", configurationPid
				).build());

			_upgradeConfigurationPidUpgradeProcess.upgrade();

			Dictionary<String, String> dictionary = _getDictionary();

			Assert.assertEquals(
				_SERVICE_FACTORY_PID + "~default",
				dictionary.get("service.pid"));
		}
		finally {
			Files.deleteIfExists(path);
		}
	}

	private static final String _SERVICE_FACTORY_PID = "test.configuration";

	private static UpgradeProcess _upgradeConfigurationPidUpgradeProcess;

	@Inject(
		filter = "(&(objectClass=com.liferay.portal.configuration.persistence.internal.upgrade.ConfigurationPersistenceUpgrade))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

}