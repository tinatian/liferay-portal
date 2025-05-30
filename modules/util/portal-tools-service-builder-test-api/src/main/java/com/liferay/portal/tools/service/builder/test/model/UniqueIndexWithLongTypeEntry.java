/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the UniqueIndexWithLongTypeEntry service. Represents a row in the &quot;UniqueIndexWithLongTypeEntry&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see UniqueIndexWithLongTypeEntryModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.portal.tools.service.builder.test.model.impl.UniqueIndexWithLongTypeEntryImpl"
)
@ProviderType
public interface UniqueIndexWithLongTypeEntry
	extends PersistedModel, UniqueIndexWithLongTypeEntryModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.portal.tools.service.builder.test.model.impl.UniqueIndexWithLongTypeEntryImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<UniqueIndexWithLongTypeEntry, Long>
		UNIQUE_INDEX_WITH_LONG_TYPE_ENTRY_ID_ACCESSOR =
			new Accessor<UniqueIndexWithLongTypeEntry, Long>() {

				@Override
				public Long get(
					UniqueIndexWithLongTypeEntry uniqueIndexWithLongTypeEntry) {

					return uniqueIndexWithLongTypeEntry.
						getUniqueIndexWithLongTypeEntryId();
				}

				@Override
				public Class<Long> getAttributeClass() {
					return Long.class;
				}

				@Override
				public Class<UniqueIndexWithLongTypeEntry> getTypeClass() {
					return UniqueIndexWithLongTypeEntry.class;
				}

			};

}