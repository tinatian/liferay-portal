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
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.WildcardMode;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceBlock;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeException;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

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
		_timestamp = new Timestamp(System.currentTimeMillis());
	}

	@After
	public void tearDown() throws Exception {
		for (String className : getClassNames()[0]) {
			_clearData("ClassName_", "value", className);

			_clearData("Counter", "name", className);

			_clearData("Lock_", "className", className);

			_clearData("ResourceAction", "name", className);

			_clearData("ResourceBlock", "name", className);

			_clearData("ResourcePermission", "name", className);

			_clearData("UserNotificationEvent", "payload", className);

			_clearData("ListType", "type_", className);
		}

		for (String resourceName : getResourceNames()[0]) {
			_clearData("ResourceAction", "name", resourceName);

			_clearData("ResourceBlock", "name", resourceName);

			_clearData("ResourcePermission", "name", resourceName);

			_clearData("UserNotificationEvent", "payload", resourceName);
		}

		connection.close();
		_timestamp = null;
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
		_dataPreparation();
		doUpgrade();
		_checkUpgrade();
	}

	@Test
	public void testDoUpgradeFailureWithNullValue() {
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
	public void testFieldInitaliaztion() {
		String[][] classNames = ReflectionTestUtil.getFieldValue(
			UpgradeKernelPackage.class, "_CLASS_NAMES");

		Assert.assertArrayEquals(super.getClassNames(), classNames);

		String[][] resourceNames = ReflectionTestUtil.getFieldValue(
			UpgradeKernelPackage.class, "_RESOURCE_NAMES");

		Assert.assertArrayEquals(super.getResourceNames(), resourceNames);
	}

	@Test
	public void testPreventDuplicates() throws Exception {
		_insertTableValues(
			"Counter",
			StringBundler.concat("'PREFIX_", _NEW_RESOURCE_NAME, "'"), "10");
		upgradeTable(
			"Counter", "name", getResourceNames(), WildcardMode.SURROUND, true);
		_validateWildData(
			"Counter", "name", _NEW_RESOURCE_NAME, WildcardMode.SURROUND,
			false);
	}

	@Test
	public void testUpgradeWithoutMatchData() throws Exception {
		_validateData("ListType", "type_", _OLD_RESOURCE_NAME, false);
		upgradeTable(
			"ListType", "type_", getClassNames(), WildcardMode.SURROUND);
		_validateData("ListType", "type_", _NEW_RESOURCE_NAME, false);
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

	private void _checkUpgrade() throws Exception {
		String prefixNewClassName = "PREFIX_" + _NEW_CLASS_NAME;
		String postfixNewClassName = _NEW_CLASS_NAME + "_POSTFIX";
		String surroundNewClassName = StringBundler.concat(
			"PREFIX_", _NEW_CLASS_NAME, "_POSTFIX");

		String prefixNewResourceName = "PREFIX_" + _NEW_RESOURCE_NAME;
		String postfixNewResourceName = _NEW_RESOURCE_NAME + "_POSTFIX";
		String surroundNewResourceName = StringBundler.concat(
			"PREFIX_", _NEW_RESOURCE_NAME, "_POSTFIX");

		_validateWildData(
			"ClassName_", "value", _OLD_CLASS_NAME, WildcardMode.SURROUND,
			false);
		_validateData("Counter", "name", postfixNewClassName, true);
		_validateData("Lock_", "className", surroundNewClassName, true);
		_validateData("ResourceAction", "name", prefixNewClassName, true);
		_validateData("ResourceBlock", "name", postfixNewClassName, true);
		_validateData("ResourcePermission", "name", surroundNewClassName, true);
		_validateLongtext(
			"UserNotificationEvent", "payload", prefixNewClassName, true);
		_validateData("ListType", "type_", prefixNewClassName, false);
		_validateData("ResourceAction", "name", postfixNewResourceName, false);
		_validateData("ResourceBlock", "name", prefixNewResourceName, true);
		_validateData(
			"ResourcePermission", "name", surroundNewResourceName, false);
		_validateLongtext(
			"UserNotificationEvent", "payload", prefixNewResourceName, true);
	}

	private void _clearData(String tableName, String columnName, String value)
		throws Exception {

		runSQL(
			StringBundler.concat(
				"delete from ", tableName, " where ", columnName, " like '%",
				value, "%'"));
	}

	private void _dataPreparation() throws Exception {
		String prefixOldClassName = StringBundler.concat(
			"'PREFIX_", _OLD_CLASS_NAME, "'");
		String postfixOldClassName = StringBundler.concat(
			"'", _OLD_CLASS_NAME, "_POSTFIX'");
		String surroundOldClassName = StringBundler.concat(
			"'PREFIX_", _OLD_CLASS_NAME, "_POSTFIX'");

		String prefixOldResourceName = StringBundler.concat(
			"'PREFIX_", _OLD_RESOURCE_NAME, "'");
		String postfixOldResourceName = StringBundler.concat(
			"'", _OLD_RESOURCE_NAME, "_POSTFIX'");
		String surroundOldResourceName = StringBundler.concat(
			"'PREFIX_", _OLD_RESOURCE_NAME, "_POSTFIX'");

		_insertTableValues(
			"ClassName_", "0", String.valueOf(increment(ClassName.class)),
			prefixOldClassName);

		_insertTableValues("Counter", postfixOldClassName, "10");

		_insertLockValues(_OLD_CLASS_NAME);

		_insertTableValues(
			"ResourceAction", "0",
			String.valueOf(increment(ResourceAction.class)), prefixOldClassName,
			"'UPDATE'", "64");

		_insertTableValues(
			"ResourceBlock", "0",
			String.valueOf(increment(ResourceBlock.class)),
			String.valueOf(TestPropsValues.getCompanyId()),
			String.valueOf(TestPropsValues.getGroupId()), postfixOldClassName,
			"'HASH'", "1");

		_insertTableValues(
			"ResourcePermission", "0",
			String.valueOf(increment(ResourcePermission.class)),
			String.valueOf(TestPropsValues.getCompanyId()),
			surroundOldClassName, "4", "'PRIM_KEY'", "2", "3", "4", "5",
			"[$TRUE$]");

		_insertUserNotificationEventValues(_OLD_CLASS_NAME);

		_insertTableValues(
			"ListType", "0", String.valueOf(increment(ListType.class)),
			"'email-address'", prefixOldClassName);

		_insertTableValues(
			"ResourceAction", "0",
			String.valueOf(increment(ResourceAction.class)),
			postfixOldResourceName, "'DELETE'", "32");

		_insertTableValues(
			"ResourceBlock", "0",
			String.valueOf(increment(ResourceBlock.class)),
			String.valueOf(TestPropsValues.getCompanyId()),
			String.valueOf(TestPropsValues.getGroupId()), prefixOldResourceName,
			"'HASH'", "1");

		_insertTableValues(
			"ResourcePermission", "0",
			String.valueOf(increment(ResourcePermission.class)),
			String.valueOf(TestPropsValues.getCompanyId()),
			surroundOldResourceName, "4", "'PRIM_KEY2'", "5", "4", "3", "2",
			"[$FALSE$]");

		_insertUserNotificationEventValues(_OLD_RESOURCE_NAME);
	}

	private void _insertLockValues(String value) throws Exception {
		StringBundler sb = new StringBundler(4);

		sb.append("insert into Lock_ (mvccVersion, uuid_, lockId, companyId, ");
		sb.append("userId, userName, createDate, className, key_, owner, ");
		sb.append("inheritable, expirationDate) values (?, ?, ?, ?, ?, ?, ?, ");
		sb.append("?, ?, ?, ?, ?)");

		PreparedStatement ps = connection.prepareStatement(sb.toString());

		ps.setLong(1, 0L);
		ps.setString(2, _UUID);
		ps.setLong(3, RandomTestUtil.nextLong());
		ps.setLong(4, TestPropsValues.getCompanyId());
		ps.setLong(5, TestPropsValues.getUserId());
		ps.setString(6, TestPropsValues.getUser().getFullName());
		ps.setTimestamp(7, _timestamp);
		ps.setString(8, StringBundler.concat("PREFIX_", value, "_POSTFIX"));
		ps.setString(9, "key_");
		ps.setString(10, "owner_");
		ps.setBoolean(11, true);
		ps.setTimestamp(12, _timestamp);

		ps.executeUpdate();
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
		ps.setString(11, "PREFIX_" + value);
		ps.setBoolean(12, true);
		ps.setBoolean(13, true);

		ps.executeUpdate();
	}

	private void _validateData(
			String tableName, String columnName, String value, boolean expected)
		throws Exception {

		_checkDataExistence(
			tableName, columnName, value, null, false, expected);
	}

	private void _validateLongtext(
			String tableName, String columnName, String value, boolean expected)
		throws Exception {

		_checkDataExistence(tableName, columnName, value, null, true, expected);
	}

	private void _validateWildData(
			String tableName, String columnName, String value,
			WildcardMode mode, boolean expected)
		throws Exception {

		_checkDataExistence(
			tableName, columnName, value, mode, false, expected);
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
	private Timestamp _timestamp;

}