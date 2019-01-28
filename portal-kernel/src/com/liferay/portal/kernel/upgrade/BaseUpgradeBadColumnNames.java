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

import com.liferay.petra.string.StringPool;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * @author Tina Tian
 */
public abstract class BaseUpgradeBadColumnNames extends UpgradeProcess {

	protected void upgradeBadColumnNames(
			Class<?> tableClass, String... badColumnNames)
		throws Exception {

		Stream<String> badColumnNamesStream = Arrays.stream(badColumnNames);

		Stream<AlterColumnName> alterColumnNamesStream =
			badColumnNamesStream.map(
				columnName ->
					new AlterColumnName(
						columnName, columnName.concat(StringPool.UNDERLINE)));

		alter(tableClass, (AlterColumnName[])alterColumnNamesStream.toArray());
	}

}