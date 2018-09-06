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
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeException;
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
		connection.close();
	}

	@Test
	public void testColumnExists() throws Exception {
		DBInspector dbInspector = new DBInspector(connection);

		Assert.assertTrue(dbInspector.hasTable("ClassName_"));
		Assert.assertTrue(dbInspector.hasColumn("ClassName_", "value"));

		Assert.assertTrue(dbInspector.hasTable("Counter"));
		Assert.assertTrue(dbInspector.hasColumn("Counter", "name"));

		Assert.assertTrue(dbInspector.hasTable("Lock_"));
		Assert.assertTrue(dbInspector.hasColumn("Lock_", "className"));

		Assert.assertTrue(dbInspector.hasTable("ResourceAction"));
		Assert.assertTrue(dbInspector.hasColumn("ResourceAction", "name"));

		Assert.assertTrue(dbInspector.hasTable("ResourceBlock"));
		Assert.assertTrue(dbInspector.hasColumn("ResourceBlock", "name"));

		Assert.assertTrue(dbInspector.hasTable("ResourcePermission"));
		Assert.assertTrue(dbInspector.hasColumn("ResourcePermission", "name"));

		Assert.assertTrue(dbInspector.hasTable("ListType"));
		Assert.assertTrue(dbInspector.hasColumn("ListType", "type_"));

		Assert.assertTrue(dbInspector.hasTable("UserNotificationEvent"));
		Assert.assertTrue(
			dbInspector.hasColumn("UserNotificationEvent", "payload"));
		Assert.assertTrue(
			dbInspector.hasColumn(
				"UserNotificationEvent", "userNotificationEventId"));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testDeprecatedUpgradeLongTextTable1() throws Throwable {
		upgradeLongTextTable(
			"UserNotificationEvent", "payload", getClassNames(),
			WildcardMode.SURROUND);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testDeprecatedUpgradeLongTextTable2() throws Throwable {
		upgradeLongTextTable(
			"payload", "selectSQL", "updateSQL", getClassNames()[0]);
	}

	@Test
	public void testDoUpgrade() throws Exception {
		try {
			_insertTableValues(
				"ResourceAction", "0",
				String.valueOf(increment(ResourceAction.class)),
				StringBundler.concat("'PREFIX_", _OLD_CLASS_NAME, "'"),
				"'UPDATE'", "64");

			doUpgrade();

			_assertDataExist(
				"ResourceAction", "name", "PREFIX_" + _NEW_CLASS_NAME, true);
		}
		finally {
			_clearData("ResourceAction", "name", _OLD_CLASS_NAME);
			_clearData("ResourceAction", "name", _NEW_CLASS_NAME);
		}
	}

	@Test
	public void testDoUpgradeFailureWithNullArray() {
		_classNames = null;
		_resourceNames = null;

		try {
			doUpgrade();
			Assert.fail();
		}
		catch (UpgradeException ue) {
			Assert.assertEquals(
				"java.lang.NullPointerException", ue.getMessage());
		}
		finally {
			_classNames = new String[][] {{_OLD_CLASS_NAME, _NEW_CLASS_NAME}};
			_resourceNames =
				new String[][] {{_OLD_RESOURCE_NAME, _NEW_RESOURCE_NAME}};
		}
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
		return _resourceNames;
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

	private static final String _UUID = "theUuid";

	private String[][] _classNames = {{_OLD_CLASS_NAME, _NEW_CLASS_NAME}};
	private String[][] _resourceNames = {
		{_OLD_RESOURCE_NAME, _NEW_RESOURCE_NAME}
	};

}