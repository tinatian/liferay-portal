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

package com.liferay.portal.kernel.dao.db;

import com.liferay.portal.kernel.test.ProxyTestUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Mariano Alvaro Saiz
 */
public class DBInspectorUnitTest {

	@Test
	public void testArgumentMetaDataIsUsedToNormalizeName() throws Exception {
		_connection = ProxyTestUtil.getProxy(Connection.class);

		DBInspector dbInspector = new DBInspector(_connection);

		DatabaseMetaData databaseMetaData = ProxyTestUtil.getProxy(
			DatabaseMetaData.class,
			ProxyTestUtil.getProxyMethod(
				"storesLowerCaseIdentifiers", (Object[] args) -> true));

		dbInspector.normalizeName(_TABLE_NAME, databaseMetaData);

		ProxyTestUtil.assertAction(
			_connection,
			ProxyTestUtil.getProxyAction("getMetaData", new Object[0]),
			times -> times == 0);

		ProxyTestUtil.assertAction(
			databaseMetaData,
			ProxyTestUtil.getProxyAction(
				"storesLowerCaseIdentifiers", new Object[0]),
			times -> times == 1);
	}

	@Test
	public void testHasColumnIsCaseInsensitive() throws Exception {
		_mockTableWithColumn(_TABLE_NAME, StringUtil.toLowerCase(_COLUMN_NAME));

		DBInspector dbInspector = new DBInspector(_connection);

		Assert.assertTrue(
			dbInspector.hasColumn(
				_TABLE_NAME, StringUtil.toUpperCase(_COLUMN_NAME)));
	}

	@Test
	public void testHasColumnReturnsFalseWithoutExistingColumn()
		throws Exception {

		_mockTableWithoutColumn(_TABLE_NAME, _COLUMN_NAME);

		DBInspector dbInspector = new DBInspector(_connection);

		Assert.assertFalse(dbInspector.hasColumn(_TABLE_NAME, _COLUMN_NAME));
	}

	@Test
	public void testHasColumnReturnsTrueWithExistingColumn() throws Exception {
		_mockTableWithColumn(_TABLE_NAME, _COLUMN_NAME);

		DBInspector dbInspector = new DBInspector(_connection);

		Assert.assertTrue(dbInspector.hasColumn(_TABLE_NAME, _COLUMN_NAME));
	}

	@Test
	public void testHasColumnSkipsQueryWithExistingColumn() throws Exception {
		_mockTableWithColumn(_TABLE_NAME, _COLUMN_NAME);

		DBInspector dbInspector = new DBInspector(_connection);

		dbInspector.hasColumn(_TABLE_NAME, _COLUMN_NAME);

		ProxyTestUtil.assertAction(
			_preparedStatement,
			ProxyTestUtil.getProxyAction("executeQuery", new Object[0]),
			times -> times == 0);
	}

	private void _mockTableWithColumn(String tableName, String columnName)
		throws SQLException {

		_mockTableWithOrWithoutColumn(tableName, columnName, true);
	}

	private void _mockTableWithOrWithoutColumn(
			String tableName, String columnName, boolean hasColumn)
		throws SQLException {

		ResultSet resultSet = ProxyTestUtil.getProxy(
			ResultSet.class,
			ProxyTestUtil.getProxyMethod("next", (Object[] args) -> hasColumn));

		DatabaseMetaData databaseMetaData = ProxyTestUtil.getProxy(
			DatabaseMetaData.class,
			ProxyTestUtil.getProxyMethod(
				"storesLowerCaseIdentifiers", (Object[] args) -> true),
			ProxyTestUtil.getProxyMethod(
				"getColumns",
				(Object[] args) -> {
					if (args[2].equals(StringUtil.toLowerCase(tableName)) &&
						args[3].equals(columnName)) {

						return resultSet;
					}

					return null;
				}));

		_preparedStatement = ProxyTestUtil.getProxy(
			PreparedStatement.class,
			ProxyTestUtil.getProxyMethod(
				"executeQuery", (Object[] args) -> resultSet));

		_connection = ProxyTestUtil.getProxy(
			Connection.class,
			ProxyTestUtil.getProxyMethod(
				"getMetaData", (Object[] args) -> databaseMetaData),
			ProxyTestUtil.getProxyMethod(
				"prepareStatement", (Object[] args) -> _preparedStatement));
	}

	private void _mockTableWithoutColumn(String tableName, String columnName)
		throws SQLException {

		_mockTableWithOrWithoutColumn(tableName, columnName, false);
	}

	private static final String _COLUMN_NAME = "column_name";

	private static final String _TABLE_NAME = "table_name";

	private Connection _connection;
	private PreparedStatement _preparedStatement;

}