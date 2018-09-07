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

import com.liferay.counter.kernel.service.CounterLocalServiceUtil;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.WildcardMode;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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

	@Before
	public void setUp() throws Exception {
		connection = DataAccess.getConnection();
	}

	@After
	public void tearDown() throws Exception {
		if (connection != null) {
			connection.close();
		}
	}

	@Test
	public void testDeprecatedUpgradeLongTextTable() throws Exception {
		try {
			upgradeLongTextTable(
				"UserNotificationEvent", "payload", _TEST_CLASS_NAMES,
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
				"payload", "selectSQL", "updateSQL", _TEST_CLASS_NAMES[0]);

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
			_insertUserNotificationEventValues("PREFIX_" + _OLD_CLASS_NAME);

			upgradeLongTextTable(
				"UserNotificationEvent", "payload", "userNotificationEventId",
				_TEST_CLASS_NAMES, WildcardMode.SURROUND);

			_assertDataExist(
				"UserNotificationEvent", "payload", "PREFIX_" + _NEW_CLASS_NAME,
				true);
		}
		finally {
			runSQL(
				"delete from UserNotificationEvent where payload like '%" +
					_OLD_CLASS_NAME + "%'");
			runSQL(
				"delete from UserNotificationEvent where payload like '%" +
					_NEW_CLASS_NAME + "%'");
		}
	}

	@Test
	public void testUpgradeLongTextTableWithSelectAndUpdateSQL()
		throws Exception {

		try {
			StringBundler updateSB = new StringBundler(2);

			updateSB.append("update UserNotificationEvent set payload = ? ");
			updateSB.append("where userNotificationEventId = ?");

			StringBundler selectSB = new StringBundler(4);

			selectSB.append("select payload, userNotificationEventId from ");
			selectSB.append("UserNotificationEvent where payload like '%");
			selectSB.append(_OLD_CLASS_NAME);
			selectSB.append("%'");

			_insertUserNotificationEventValues("PREFIX_" + _OLD_CLASS_NAME);
			_insertUserNotificationEventValues(_OLD_CLASS_NAME + "_POSTFIX");

			upgradeLongTextTable(
				"payload", "userNotificationEventId", selectSB.toString(),
				updateSB.toString(), _TEST_CLASS_NAMES[0]);

			_assertDataExist(
				"UserNotificationEvent", "payload", "PREFIX_" + _NEW_CLASS_NAME,
				true);
			_assertDataExist(
				"UserNotificationEvent", "payload",
				_NEW_CLASS_NAME + "_POSTFIX", true);
		}
		finally {
			runSQL(
				"delete from UserNotificationEvent where payload like '%" +
					_OLD_CLASS_NAME + "%'");
			runSQL(
				"delete from UserNotificationEvent where payload like '%" +
					_NEW_CLASS_NAME + "%'");
		}
	}

	@Test
	public void testUpgradeTable() throws Exception {
		try {
			runSQL(
				StringBundler.concat(
					"insert into ClassName_ values(0, ",
					String.valueOf(increment(ClassName.class)), ", 'PREFIX_",
					_OLD_CLASS_NAME, "')"));

			upgradeTable(
				"ClassName_", "value", _TEST_CLASS_NAMES,
				WildcardMode.SURROUND);

			_assertDataExist(
				"ClassName_", "value", "PREFIX_" + _NEW_CLASS_NAME, true);

			// Test preventDuplicate

			runSQL(
				StringBundler.concat(
					"insert into ClassName_ values(0, ",
					String.valueOf(increment(ClassName.class)), ", 'PREFIX_",
					_OLD_RESOURCE_NAME, "')"));
			runSQL(
				StringBundler.concat(
					"insert into ClassName_ values(0, ",
					String.valueOf(increment(ClassName.class)), ", 'PREFIX_",
					_NEW_RESOURCE_NAME, "')"));

			upgradeTable(
				"ClassName_", "value", _TEST_RESOURCE_NAMES,
				WildcardMode.SURROUND, true);

			_assertDataExist(
				"ClassName_", "value", "PREFIX_" + _OLD_RESOURCE_NAME, false);
			_assertDataExist(
				"ClassName_", "value", "PREFIX_" + _NEW_RESOURCE_NAME, true);
			_assertDataExist(
				"ClassName_", "value", _NEW_RESOURCE_NAME + "_POSTFIX", false);

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
				"delete from ClassName_ where value like '%" + _OLD_CLASS_NAME +
					"%'");
			runSQL(
				"delete from ClassName_ where value like '%" + _NEW_CLASS_NAME +
					"%'");
			runSQL(
				"delete from ClassName_ where value like '%" +
					_OLD_RESOURCE_NAME + "%'");
			runSQL(
				"delete from ClassName_ where value like '%" +
					_NEW_RESOURCE_NAME + "%'");
		}
	}

	protected long increment(Class<?> clazz) throws Exception {
		return CounterLocalServiceUtil.increment(clazz.getName());
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

	private void _assertDataExist(
			String tableName, String columnName, String value, boolean expected)
		throws Exception {

		StringBundler selectSB = new StringBundler(9);

		selectSB.append("select ");
		selectSB.append(columnName);
		selectSB.append(" from ");
		selectSB.append(tableName);
		selectSB.append(" where CAST_CLOB_TEXT(");
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
					"Table ", tableName, " and column ", columnName,
					errorMessage, value),
				expected, rs.next());
		}
	}

	private void _insertUserNotificationEventValues(String value)
		throws Exception {

		StringBundler sb = new StringBundler(5);

		sb.append("insert into UserNotificationEvent (mvccVersion, uuid_, ");
		sb.append("userNotificationEventId, companyId, userId, type_, ");
		sb.append("timestamp, deliveryType, deliverBy, delivered, payload, ");
		sb.append("actionRequired, archived) values (?, ?, ?, ?, ?, ?, ?, ?, ");
		sb.append("?, ?, ?, ?, ?)");

		PreparedStatement ps = connection.prepareStatement(sb.toString());

		ps.setLong(1, 0L);
		ps.setString(2, _UUID);
		ps.setLong(3, RandomTestUtil.nextLong());
		ps.setLong(4, TestPropsValues.getCompanyId());
		ps.setLong(5, TestPropsValues.getUserId());
		ps.setString(6, "TYPE_");
		ps.setLong(7, RandomTestUtil.nextLong());
		ps.setInt(8, 0);
		ps.setLong(9, 0);
		ps.setBoolean(10, true);
		ps.setString(11, value);
		ps.setBoolean(12, true);
		ps.setBoolean(13, true);

		ps.executeUpdate();
	}

	private static final String _NEW_CLASS_NAME =
		"com.liferay.class.path.kernel.Test";

	private static final String _NEW_RESOURCE_NAME =
		"com.liferay.resource.path.kernel.Test";

	private static final String _OLD_CLASS_NAME =
		"com.liferay.portlet.classpath.Test";

	private static final String _OLD_RESOURCE_NAME =
		"com.liferay.portlet.resourcepath.Test";

	private static final String[][] _TEST_CLASS_NAMES = {
		{_OLD_CLASS_NAME, _NEW_CLASS_NAME}
	};

	private static final String[][] _TEST_RESOURCE_NAMES = {
		{_OLD_RESOURCE_NAME, _NEW_RESOURCE_NAME}
	};

	private static final String _UUID = "theUuid";

}