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

package com.liferay.portal.upgrade.v7_0_0;

import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.WildcardMode;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Preston Crary
 */
public class UpgradeKernelPackageTest extends UpgradeKernelPackage {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_db = DBManagerUtil.getDB();

		_connection = DataAccess.getConnection();

		_db.runSQL(
			_connection,
			"create table UpgradeKernelPackageTest (" +
				"id LONG not null primary key, data VARCHAR(40) null, " +
					"textData TEXT null)");
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		if (_connection == null) {
			return;
		}

		try {
			_db.runSQL(_connection, "drop table UpgradeKernelPackageTest");
		}
		finally {
			_connection.close();
		}
	}

	@Before
	public void setUp() throws Exception {
		connection = _connection;
	}

	@After
	public void tearDown() throws Exception {
		connection = null;
	}

	@Test
	public void testDeprecatedUpgradeLongTextTable() throws Exception {
		try {
			upgradeLongTextTable(
				"UpgradeKernelPackageTest", "textData", _TEST_CLASS_NAMES,
				WildcardMode.SURROUND);

			Assert.fail("Should throw UnsupportedOperationException");
		}
		catch (UnsupportedOperationException uoe) {
			Assert.assertEquals(
				"This method is deprecated and replaced by " +
					"upgradeLongTextTable(String, String, String, " +
						"String[][], WildcardMode)",
				uoe.getMessage());
		}

		try {
			upgradeLongTextTable(
				"textData", "selectSQL", "updateSQL", _TEST_CLASS_NAMES[0]);

			Assert.fail("Should throw UnsupportedOperationException");
		}
		catch (UnsupportedOperationException uoe) {
			Assert.assertEquals(
				"This method is deprecated and replaced by " +
					"upgradeLongTextTable(String, String, String, String, " +
						"String[])",
				uoe.getMessage());
		}
	}

	@Test
	public void testDoUpgrade() throws Exception {

		// Just invoke this method to have code coverage
		// All methods are tested in other tests

		doUpgrade();

		// Only thing left is to check the table and column combination is right

		DBInspector dbInspector = new DBInspector(connection);

		_assertTableAndColumn(dbInspector, "ClassName_", "value");
		_assertTableAndColumn(dbInspector, "Counter", "name");
		_assertTableAndColumn(dbInspector, "Lock_", "className");
		_assertTableAndColumn(dbInspector, "ResourceAction", "name");
		_assertTableAndColumn(dbInspector, "ResourceBlock", "name");
		_assertTableAndColumn(dbInspector, "ResourcePermission", "name");
		_assertTableAndColumn(dbInspector, "ListType", "type_");
		_assertTableAndColumn(
			dbInspector, "UserNotificationEvent", "payload",
			"userNotificationEventId");
	}

	@Test
	public void testUpgradeLongTextTable() throws Exception {
		try {
			runSQL(
				StringBundler.concat(
					"insert into UpgradeKernelPackageTest values(",
					String.valueOf(RandomTestUtil.nextLong()), ", '', '",
					_PREFIX_OLD_CLASS_NAME, "')"));
			runSQL(
				StringBundler.concat(
					"insert into UpgradeKernelPackageTest values(",
					String.valueOf(RandomTestUtil.nextLong()), ", '', '",
					_POSTFIX_OLD_CLASS_NAME, "')"));

			upgradeLongTextTable(
				"UpgradeKernelPackageTest", "textData", "id", _TEST_CLASS_NAMES,
				WildcardMode.LEADING);

			_assertDataExist("textData", _PREFIX_NEW_CLASS_NAME, true);
			_assertDataExist("textData", _POSTFIX_NEW_CLASS_NAME, false);

			runSQL(
				StringBundler.concat(
					"insert into UpgradeKernelPackageTest values(",
					String.valueOf(RandomTestUtil.nextLong()), ", '', '",
					_PREFIX_OLD_RESOURCE_NAME, "')"));
			runSQL(
				StringBundler.concat(
					"insert into UpgradeKernelPackageTest values(",
					String.valueOf(RandomTestUtil.nextLong()), ", '', '",
					_POSTFIX_OLD_RESOURCE_NAME, "')"));

			upgradeLongTextTable(
				"UpgradeKernelPackageTest", "textData", "id",
				_TEST_RESOURCE_NAMES, WildcardMode.TRAILING);

			_assertDataExist("textData", _PREFIX_NEW_RESOURCE_NAME, false);
			_assertDataExist("textData", _POSTFIX_NEW_RESOURCE_NAME, true);

			upgradeLongTextTable(
				"UpgradeKernelPackageTest", "textData", "id", _TEST_CLASS_NAMES,
				WildcardMode.SURROUND);
			upgradeLongTextTable(
				"UpgradeKernelPackageTest", "textData", "id",
				_TEST_RESOURCE_NAMES, WildcardMode.SURROUND);

			_assertDataExist("textData", _POSTFIX_NEW_CLASS_NAME, true);
			_assertDataExist("textData", _PREFIX_NEW_RESOURCE_NAME, true);
		}
		finally {
			runSQL(
				"delete from UpgradeKernelPackageTest where textData like '%" +
					_OLD_CLASS_NAME + "%'");
			runSQL(
				"delete from UpgradeKernelPackageTest where textData like '%" +
					_NEW_CLASS_NAME + "%'");
			runSQL(
				"delete from UpgradeKernelPackageTest where textData like '%" +
					_OLD_RESOURCE_NAME + "%'");
			runSQL(
				"delete from UpgradeKernelPackageTest where textData like '%" +
					_NEW_RESOURCE_NAME + "%'");
		}
	}

	@Test
	public void testUpgradeLongTextTableWithSelectAndUpdateSQL()
		throws Exception {

		try {
			StringBundler updateSB = new StringBundler(2);

			updateSB.append("update UpgradeKernelPackageTest set textData = ");
			updateSB.append("? where id = ?");

			StringBundler selectSB = new StringBundler(4);

			selectSB.append("select textData, id from ");
			selectSB.append("UpgradeKernelPackageTest where textData like '%");
			selectSB.append(_OLD_CLASS_NAME);
			selectSB.append("%'");

			runSQL(
				StringBundler.concat(
					"insert into UpgradeKernelPackageTest values(",
					String.valueOf(RandomTestUtil.nextLong()), ", '', '",
					_PREFIX_OLD_CLASS_NAME, "')"));
			runSQL(
				StringBundler.concat(
					"insert into UpgradeKernelPackageTest values(",
					String.valueOf(RandomTestUtil.nextLong()), ", '', '",
					_POSTFIX_OLD_CLASS_NAME, "')"));

			upgradeLongTextTable(
				"textData", "id", selectSB.toString(), updateSB.toString(),
				_TEST_CLASS_NAMES[0]);

			_assertDataExist("textData", _PREFIX_NEW_CLASS_NAME, true);
			_assertDataExist("textData", _POSTFIX_NEW_CLASS_NAME, true);
		}
		finally {
			runSQL(
				"delete from UpgradeKernelPackageTest where textData like '%" +
					_OLD_CLASS_NAME + "%'");
			runSQL(
				"delete from UpgradeKernelPackageTest where textData like '%" +
					_NEW_CLASS_NAME + "%'");
		}
	}

	@Test
	public void testUpgradeTable() throws Exception {
		try {
			runSQL(
				StringBundler.concat(
					"insert into UpgradeKernelPackageTest values(",
					String.valueOf(RandomTestUtil.nextLong()), ", '",
					_PREFIX_OLD_CLASS_NAME, "', '')"));
			runSQL(
				StringBundler.concat(
					"insert into UpgradeKernelPackageTest values(",
					String.valueOf(RandomTestUtil.nextLong()), ", '",
					_POSTFIX_OLD_CLASS_NAME, "', '')"));

			upgradeTable(
				"UpgradeKernelPackageTest", "data", _TEST_CLASS_NAMES,
				WildcardMode.LEADING);

			_assertDataExist("data", _PREFIX_NEW_CLASS_NAME, true);
			_assertDataExist("data", _POSTFIX_NEW_CLASS_NAME, false);

			runSQL(
				StringBundler.concat(
					"insert into UpgradeKernelPackageTest values(",
					String.valueOf(RandomTestUtil.nextLong()), ", '",
					_PREFIX_OLD_RESOURCE_NAME, "', '')"));
			runSQL(
				StringBundler.concat(
					"insert into UpgradeKernelPackageTest values(",
					String.valueOf(RandomTestUtil.nextLong()), ", '",
					_POSTFIX_OLD_RESOURCE_NAME, "', '')"));

			upgradeTable(
				"UpgradeKernelPackageTest", "data", _TEST_RESOURCE_NAMES,
				WildcardMode.TRAILING);

			_assertDataExist("data", _PREFIX_NEW_RESOURCE_NAME, false);
			_assertDataExist("data", _POSTFIX_NEW_RESOURCE_NAME, true);

			upgradeTable(
				"UpgradeKernelPackageTest", "data", _TEST_CLASS_NAMES,
				WildcardMode.SURROUND);
			upgradeTable(
				"UpgradeKernelPackageTest", "data", _TEST_RESOURCE_NAMES,
				WildcardMode.SURROUND);

			_assertDataExist("data", _POSTFIX_NEW_CLASS_NAME, true);
			_assertDataExist("data", _PREFIX_NEW_RESOURCE_NAME, true);

			// Test preventDuplicate

			runSQL(
				StringBundler.concat(
					"insert into UpgradeKernelPackageTest values(",
					String.valueOf(RandomTestUtil.nextLong()), ", 'PREFIX_",
					_OLD_CLASS_NAME, "_POSTFIX', '')"));

			upgradeTable(
				"UpgradeKernelPackageTest", "data", _TEST_CLASS_NAMES,
				WildcardMode.SURROUND, true);

			_assertDataExist("data", _PREFIX_NEW_CLASS_NAME, false);
			_assertDataExist("data", _POSTFIX_NEW_CLASS_NAME, false);
			_assertDataExist(
				"data",
				StringBundler.concat("PREFIX_", _NEW_CLASS_NAME, "_POSTFIX"),
				true);

			// Test Exception

			try {
				upgradeTable(
					"ResourcePermission", "name", null, WildcardMode.SURROUND,
					true);

				Assert.fail("Should throw NullPointerException");
			}
			catch (NullPointerException npe) {
			}
		}
		finally {
			runSQL(
				"delete from UpgradeKernelPackageTest where data like '%" +
					_OLD_CLASS_NAME + "%'");
			runSQL(
				"delete from UpgradeKernelPackageTest where data like '%" +
					_NEW_CLASS_NAME + "%'");
			runSQL(
				"delete from UpgradeKernelPackageTest where data like '%" +
					_OLD_RESOURCE_NAME + "%'");
			runSQL(
				"delete from UpgradeKernelPackageTest where data like '%" +
					_NEW_RESOURCE_NAME + "%'");
		}
	}

	private void _assertDataExist(
			String columnName, String value, boolean expected)
		throws Exception {

		StringBundler selectSB = new StringBundler(7);

		selectSB.append("select ");
		selectSB.append(columnName);
		selectSB.append(
			" from UpgradeKernelPackageTest  where CAST_CLOB_TEXT(");
		selectSB.append(columnName);
		selectSB.append(") = '");
		selectSB.append(value);
		selectSB.append("'");

		try (PreparedStatement ps = connection.prepareStatement(
				SQLTransformer.transform(selectSB.toString()));
			ResultSet rs = ps.executeQuery()) {

			String errorMessage = " does contain value ";

			if (expected) {
				errorMessage = " does not contain value ";
			}

			Assert.assertEquals(
				StringBundler.concat(
					"Column ", columnName, errorMessage, value),
				expected, rs.next());
		}
	}

	private void _assertTableAndColumn(
			DBInspector dbInspector, String tableName, String... columnNames)
		throws Exception {

		Assert.assertTrue(
			StringBundler.concat("Table \"", tableName, "\" does not exist"),
			dbInspector.hasTable(tableName));

		for (String columnName : columnNames) {
			Assert.assertTrue(
				StringBundler.concat(
					"Table \"", tableName, "\" does not have column \"",
					columnName, "\""),
				dbInspector.hasColumn(tableName, columnName));
		}
	}

	private static final String _NEW_CLASS_NAME = "UPDATED_CLASS_NAME";

	private static final String _NEW_RESOURCE_NAME = "UPDATED_RESOURCE_NAME";

	private static final String _OLD_CLASS_NAME = "ORIGINAL_CLASS_NAME";

	private static final String _OLD_RESOURCE_NAME = "ORIGINAL_RESOURCE_NAME";

	private static final String _POSTFIX_NEW_CLASS_NAME =
		_NEW_CLASS_NAME + "_POSTFIX";

	private static final String _POSTFIX_NEW_RESOURCE_NAME =
		_NEW_RESOURCE_NAME + "_POSTFIX";

	private static final String _POSTFIX_OLD_CLASS_NAME =
		_OLD_CLASS_NAME + "_POSTFIX";

	private static final String _POSTFIX_OLD_RESOURCE_NAME =
		_OLD_RESOURCE_NAME + "_POSTFIX";

	private static final String _PREFIX_NEW_CLASS_NAME =
		"PREFIX_" + _NEW_CLASS_NAME;

	private static final String _PREFIX_NEW_RESOURCE_NAME =
		"PREFIX_" + _NEW_RESOURCE_NAME;

	private static final String _PREFIX_OLD_CLASS_NAME =
		"PREFIX_" + _OLD_CLASS_NAME;

	private static final String _PREFIX_OLD_RESOURCE_NAME =
		"PREFIX_" + _OLD_RESOURCE_NAME;

	private static final String[][] _TEST_CLASS_NAMES = {
		{_OLD_CLASS_NAME, _NEW_CLASS_NAME}
	};

	private static final String[][] _TEST_RESOURCE_NAMES = {
		{_OLD_RESOURCE_NAME, _NEW_RESOURCE_NAME}
	};

	private static Connection _connection;
	private static DB _db;

}