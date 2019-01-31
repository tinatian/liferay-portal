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

package com.liferay.portal.kernel.upgrade;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

import java.lang.reflect.Field;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tina Tian
 */
public abstract class BaseUpgradeBadColumnNames extends UpgradeProcess {

	protected void upgradeBadColumnNames(
			Class<?> tableClass, String... columnNames)
		throws Exception {

		Map<String, String> columnSQLs = _getTableColumnSQLs(tableClass);

		AlterColumnName[] alterColumnNames =
			new AlterColumnName[columnNames.length];

		for (int i = 0; i < columnNames.length; i++) {
			alterColumnNames[i] = new AlterColumnName(
				columnNames[i],
				columnSQLs.get(columnNames[i].concat(StringPool.UNDERLINE)));
		}

		alter(tableClass, alterColumnNames);
	}

	private Map<String, String> _getTableColumnSQLs(Class<?> tableClass)
		throws Exception {

		Field tableSQLCreateField = tableClass.getField("TABLE_SQL_CREATE");

		String createSQL = (String)tableSQLCreateField.get(null);

		int startIndex = createSQL.indexOf(CharPool.OPEN_PARENTHESIS);
		int endIndex = createSQL.lastIndexOf(CharPool.CLOSE_PARENTHESIS);

		if ((startIndex < 0) || (endIndex < 0) || (startIndex >= endIndex)) {
			throw new IllegalStateException(
				"Unable to retrieve column SQL from " + createSQL);
		}

		Map<String, String> columnSQLs = new HashMap<>();

		for (String columnSQL :
				StringUtil.split(
					createSQL.substring(startIndex + 1, endIndex))) {

			int index = columnSQL.indexOf(CharPool.SPACE);

			if (index <= 0) {
				throw new IllegalStateException(
					"Unable to retrieve column name from " + columnSQL);
			}

			columnSQLs.put(columnSQL.substring(0, index), columnSQL);
		}

		return columnSQLs;
	}

}