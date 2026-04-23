/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

/**
 * The table class for the &quot;UniqueIndexWithLongTypeEntry&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see UniqueIndexWithLongTypeEntry
 * @generated
 */
public class UniqueIndexWithLongTypeEntryTable
	extends BaseTable<UniqueIndexWithLongTypeEntryTable> {

	public static final UniqueIndexWithLongTypeEntryTable INSTANCE =
		new UniqueIndexWithLongTypeEntryTable();

	public final Column<UniqueIndexWithLongTypeEntryTable, Long>
		uniqueIndexWithLongTypeEntryId = createColumn(
			"uniqueIndexWithLongTypeEntryId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<UniqueIndexWithLongTypeEntryTable, String> name =
		createColumn("name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<UniqueIndexWithLongTypeEntryTable, Long> longTypeId =
		createColumn(
			"longTypeId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);

	private UniqueIndexWithLongTypeEntryTable() {
		super(
			"UniqueIndexWithLongTypeEntry",
			UniqueIndexWithLongTypeEntryTable::new);
	}

}