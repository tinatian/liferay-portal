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
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import java.io.File;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Dictionary;

import org.apache.felix.cm.file.ConfigurationHandler;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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

	@Before
	public void setUp() throws Exception {
		_configsDir = new File(
			_props.get(PropsKeys.MODULE_FRAMEWORK_CONFIGS_DIR));

		File configFile = new File(
			_configsDir, "test.configuration.instance1.config");

		configFile.createNewFile();

		Dictionary<String, String> dictionary = HashMapDictionaryBuilder.put(
			"felix.fileinstall.filename", "test.configuration.instance1.config"
		).put(
			"service.factoryPid", "test.configuration"
		).put(
			"service.pid", "test.configuration.instance1"
		).build();

		UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
			new UnsyncByteArrayOutputStream();

		ConfigurationHandler.write(unsyncByteArrayOutputStream, dictionary);

		try (Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				"insert into Configuration_ (configurationId, dictionary) " +
					"values(?, ?)")) {

			preparedStatement.setString(1, _CONFIGURATION_PID);

			preparedStatement.setString(
				2, unsyncByteArrayOutputStream.toString());

			preparedStatement.execute();
		}

		setUpUpgradeConfigurationPid();

		_upgradeConfigurationPidUpgradeProcess.upgrade();
	}

	@After
	public void tearDown() throws Exception {
		DB db = DBManagerUtil.getDB();

		db.runSQL(
			"delete from Configuration_ where configurationId like " +
				"'test.configuration%'");
	}

	@Test
	public void testUpgradeConfigurationPid() throws Exception {
		try (Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				"select * from Configuration_ where configurationId like " +
					"'test.configuration%'");
			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				String configurationId = resultSet.getString("configurationId");

				String dictionaryString = resultSet.getString("dictionary");

				Dictionary<String, String> dictionary =
					ConfigurationHandler.read(
						new UnsyncByteArrayInputStream(
							dictionaryString.getBytes(StringPool.UTF8)));

				String serviceFactoryPid = dictionary.get("service.factoryPid");

				String servicePid = dictionary.get("service.pid");

				String felixFileName = dictionary.get(
					"felix.fileinstall.filename");

				File configFile = new File(_configsDir, felixFileName);

				Assert.assertEquals("test.configuration", serviceFactoryPid);

				Assert.assertEquals("test.configuration~instance1", servicePid);

				Assert.assertEquals(
					"test.configuration~instance1", configurationId);

				Assert.assertEquals(
					"test.configuration~instance1.config", felixFileName);

				Assert.assertTrue(configFile.exists());
			}
		}
	}

	protected void setUpUpgradeConfigurationPid() {
		_upgradeStepRegistrator.register(
			new UpgradeStepRegistrator.Registry() {

				@Override
				public void register(
					String fromSchemaVersionString,
					String toSchemaVersionString, UpgradeStep... upgradeSteps) {

					for (UpgradeStep upgradeStep : upgradeSteps) {
						Class<?> clazz = upgradeStep.getClass();

						String className = clazz.getName();

						if (className.contains(_CLASS_NAME)) {
							_upgradeConfigurationPidUpgradeProcess =
								(UpgradeProcess)upgradeStep;
						}
					}
				}

			});
	}

	private static final String _CLASS_NAME =
		"com.liferay.portal.configuration.persistence.internal.upgrade." +
			"v1_0_0.UpgradeConfigurationPid";

	private static final String _CONFIGURATION_PID =
		"test.configuration.instance1";

	@Inject(
		filter = "(&(objectClass=com.liferay.portal.configuration.persistence.internal.upgrade.ConfigurationPersistenceUpgrade))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	private File _configsDir;

	@Inject
	private Props _props;

	private UpgradeProcess _upgradeConfigurationPidUpgradeProcess;

}