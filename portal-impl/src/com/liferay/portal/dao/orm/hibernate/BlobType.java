/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.hibernate;

import java.io.Serializable;

import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.usertype.UserType;

/**
 * @author Brian Wing Shun Chan
 */
public class BlobType implements Serializable, UserType<Blob> {

	@Override
	public Blob assemble(Serializable cached, Object owner) {
		return null;
	}

	@Override
	public Blob deepCopy(Blob value) {
		return value;
	}

	@Override
	public Serializable disassemble(Blob value) {
		return null;
	}

	@Override
	public boolean equals(Blob x, Blob y) {
		if (x == y) {
			return true;
		}

		return false;
	}

	@Override
	public int getSqlType() {
		return Types.BLOB;
	}

	@Override
	public int hashCode(Blob x) {
		return x.hashCode();
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Blob nullSafeGet(
			ResultSet resultSet, int index, WrapperOptions wrapperOptions)
		throws SQLException {

		return resultSet.getBlob(index);
	}

	@Override
	public void nullSafeSet(
			PreparedStatement preparedStatement, Blob target, int index,
			WrapperOptions wrapperOptions)
		throws SQLException {

		if (target == null) {
			preparedStatement.setNull(index, Types.BLOB);
		}
		else {
			preparedStatement.setBlob(index, target);
		}
	}

	@Override
	public Blob replace(Blob original, Blob target, Object owner) {
		return original;
	}

	@Override
	public Class<Blob> returnedClass() {
		return Blob.class;
	}

}