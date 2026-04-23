/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.tools.service.builder.test.model.UniqueIndexWithLongTypeEntry;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * The cache model class for representing UniqueIndexWithLongTypeEntry in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class UniqueIndexWithLongTypeEntryCacheModel
	implements CacheModel<UniqueIndexWithLongTypeEntry>, Externalizable {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof UniqueIndexWithLongTypeEntryCacheModel)) {
			return false;
		}

		UniqueIndexWithLongTypeEntryCacheModel
			uniqueIndexWithLongTypeEntryCacheModel =
				(UniqueIndexWithLongTypeEntryCacheModel)object;

		if (uniqueIndexWithLongTypeEntryId ==
				uniqueIndexWithLongTypeEntryCacheModel.
					uniqueIndexWithLongTypeEntryId) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, uniqueIndexWithLongTypeEntryId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(7);

		sb.append("{uniqueIndexWithLongTypeEntryId=");
		sb.append(uniqueIndexWithLongTypeEntryId);
		sb.append(", name=");
		sb.append(name);
		sb.append(", longTypeId=");
		sb.append(longTypeId);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public UniqueIndexWithLongTypeEntry toEntityModel() {
		UniqueIndexWithLongTypeEntryImpl uniqueIndexWithLongTypeEntryImpl =
			new UniqueIndexWithLongTypeEntryImpl();

		uniqueIndexWithLongTypeEntryImpl.setUniqueIndexWithLongTypeEntryId(
			uniqueIndexWithLongTypeEntryId);

		if (name == null) {
			uniqueIndexWithLongTypeEntryImpl.setName("");
		}
		else {
			uniqueIndexWithLongTypeEntryImpl.setName(name);
		}

		uniqueIndexWithLongTypeEntryImpl.setLongTypeId(longTypeId);

		uniqueIndexWithLongTypeEntryImpl.resetOriginalValues();

		return uniqueIndexWithLongTypeEntryImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		uniqueIndexWithLongTypeEntryId = objectInput.readLong();
		name = objectInput.readUTF();

		longTypeId = objectInput.readLong();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(uniqueIndexWithLongTypeEntryId);

		if (name == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(name);
		}

		objectOutput.writeLong(longTypeId);
	}

	public long uniqueIndexWithLongTypeEntryId;
	public String name;
	public long longTypeId;

}