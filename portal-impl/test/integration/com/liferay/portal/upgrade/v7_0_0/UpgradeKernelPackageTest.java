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
import com.liferay.petra.string.StringPool;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.WildcardMode;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
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
				"UserNotificationEvent", "payload", getClassNames(),
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
				"payload", "selectSQL", "updateSQL", getClassNames()[0]);
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
	public void testGetFieldValue() {
		String[][] classNames = ReflectionTestUtil.getFieldValue(
			UpgradeKernelPackage.class, "_CLASS_NAMES");

		Assert.assertArrayEquals(super.getClassNames(), classNames);

		String[][] resourceNames = ReflectionTestUtil.getFieldValue(
			UpgradeKernelPackage.class, "_RESOURCE_NAMES");

		Assert.assertArrayEquals(super.getResourceNames(), resourceNames);
	}

	@Test
	public void testPreventDuplicates() throws Exception {
		try {
			_insertTableValues(
				"Counter",
				StringBundler.concat("'PREFIX_", _NEW_RESOURCE_NAME, "'"),
				"10");
			upgradeTable(
				"Counter", "name", getResourceNames(), WildcardMode.SURROUND,
				true);
			_assertWildcardModeDataExist(
				"Counter", "name", _NEW_RESOURCE_NAME, WildcardMode.SURROUND,
				false);
		}
		finally {
			_clearData("Counter", "name", _NEW_RESOURCE_NAME);
		}
	}

	@Test
	public void testPreventDuplicatesWithNullClassNames() throws Exception {
		_classNames = null;

		try {
			upgradeTable(
				"ResourcePermission", "name", getClassNames(),
				WildcardMode.SURROUND, true);
			Assert.fail("Should throw NullPointerException");
		}
		catch (NullPointerException npe) {
		}
		finally {
			_classNames = new String[][] {{_OLD_CLASS_NAME, _NEW_CLASS_NAME}};
		}
	}

	@Test
	public void testUpgradeLongTextTable() throws Exception {
		try {
			_insertUserNotificationEventValues("PREFIX_" + _OLD_CLASS_NAME);
			upgradeLongTextTable(
				"UserNotificationEvent", "payload", "userNotificationEventId",
				getClassNames(), WildcardMode.SURROUND);
			_assertLongtextDataExist(
				"UserNotificationEvent", "payload", "PREFIX_" + _NEW_CLASS_NAME,
				true);
		}
		finally {
			_clearData("UserNotificationEvent", "payload", _OLD_CLASS_NAME);
			_clearData("UserNotificationEvent", "payload", _NEW_CLASS_NAME);
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

			_insertUserNotificationEventValues(_OLD_CLASS_NAME + "_POSTFIX");

			upgradeLongTextTable(
				"payload", "userNotificationEventId", selectSB.toString(),
				updateSB.toString(), getClassNames()[0]);
			_assertLongtextDataExist(
				"UserNotificationEvent", "payload",
				_NEW_CLASS_NAME + "_POSTFIX", true);
		}
		finally {
			_clearData("UserNotificationEvent", "payload", _OLD_CLASS_NAME);
			_clearData("UserNotificationEvent", "payload", _NEW_CLASS_NAME);
		}
	}

	@Test
	public void testUpgradeTable() throws Exception {
		try {
			_insertTableValues(
				"ClassName_", "0", String.valueOf(increment(ClassName.class)),
				StringBundler.concat("'PREFIX_", _OLD_CLASS_NAME, "'"));
			upgradeTable(
				"ClassName_", "value", getClassNames(), WildcardMode.SURROUND);
			_assertDataExist(
				"ClassName_", "value", "PREFIX_" + _NEW_CLASS_NAME, true);
		}
		finally {
			_clearData("ClassName_", "value", _OLD_CLASS_NAME);
			_clearData("ClassName_", "value", _NEW_CLASS_NAME);
		}
	}

	@Test
	public void testUpgradeTableWithoutMatchData() throws Exception {
		_assertDataExist("ListType", "type_", _OLD_RESOURCE_NAME, false);
		upgradeTable(
			"ListType", "type_", getResourceNames(), WildcardMode.SURROUND);
		_assertDataExist("ListType", "type_", _OLD_RESOURCE_NAME, false);
		_assertDataExist("ListType", "type_", _NEW_RESOURCE_NAME, false);
	}

	@Override
	protected String[][] getClassNames() {
		return _classNames;
	}

	@Override
	protected String[][] getResourceNames() {
		return _RESOURCENAMES;
	}

	protected long increment(Class<?> clazz) throws Exception {
		return CounterLocalServiceUtil.increment(clazz.getName());
	}

	private void _assertDataExist(
			String tableName, String columnName, String value, boolean expected)
		throws Exception {

		_checkDataExistence(
			tableName, columnName, value, null, false, expected);
	}

	private void _assertLongtextDataExist(
			String tableName, String columnName, String value, boolean expected)
		throws Exception {

		_checkDataExistence(tableName, columnName, value, null, true, expected);
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

	private void _assertWildcardModeDataExist(
			String tableName, String columnName, String value,
			WildcardMode mode, boolean expected)
		throws Exception {

		_checkDataExistence(
			tableName, columnName, value, mode, false, expected);
	}

	private void _checkDataExistence(
			String tableName, String columnName, String value,
			WildcardMode mode, boolean ifLongtext, boolean expected)
		throws Exception {

		StringBundler selectSB = new StringBundler(12);

		selectSB.append("select ");
		selectSB.append(columnName);
		selectSB.append(" from ");
		selectSB.append(tableName);

		if (ifLongtext) {
			selectSB.append(" where CAST_CLOB_TEXT(");
			selectSB.append(columnName);
			selectSB.append(")");
		}
		else {
			selectSB.append(" where ");
			selectSB.append(columnName);
		}

		if (mode != null) {
			selectSB.append(" like '");
			selectSB.append(mode.getLeadingWildcard());
			selectSB.append(value);
			selectSB.append(mode.getTrailingWildcard());
		}
		else {
			selectSB.append(" = '");
			selectSB.append(value);
		}

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

	private void _clearData(String tableName, String columnName, String value)
		throws Exception {

		runSQL(
			StringBundler.concat(
				"delete from ", tableName, " where ", columnName, " like '%",
				value, "%'"));
	}

	private void _insertTableValues(String tableName, String... values)
		throws Exception {

		StringBundler sb = new StringBundler(values.length);

		sb.append("insert into ");
		sb.append(tableName);
		sb.append(" values(");

		for (String value : values) {
			sb.append(value);
			sb.append(StringPool.COMMA);
		}

		sb.setIndex(sb.index() - 1);
		sb.append(")");

		runSQL(sb.toString());
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

	private static final String[][] _RESOURCENAMES = {
		{_OLD_RESOURCE_NAME, _NEW_RESOURCE_NAME}
	};

	private static final String _UUID = "theUuid";

	private String[][] _classNames = {{_OLD_CLASS_NAME, _NEW_CLASS_NAME}};

}