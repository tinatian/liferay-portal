/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.hibernate;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.StandardBasicTypes;

public class NullableLongType extends LongType {

	@Override
	public Object nullSafeGet(
			ResultSet resultSet, String[] names,
			SharedSessionContractImplementor sharedSessionContractImplementor,
			Object owner)
		throws SQLException {

		Object value = null;

		try {
			value = StandardBasicTypes.LONG.nullSafeGet(
				resultSet, names[0], sharedSessionContractImplementor);
		}
		catch (SQLException sqlException1) {
			if (_log.isDebugEnabled()) {
				_log.debug(sqlException1);
			}

			// Some JDBC drivers do not know how to convert a VARCHAR column
			// with a blank entry into a BIGINT

			try {
				value = Long.valueOf(
					GetterUtil.getLong(
						StandardBasicTypes.STRING.nullSafeGet(
							resultSet, names[0],
							sharedSessionContractImplementor)));
			}
			catch (SQLException sqlException2) {
				throw sqlException2;
			}
		}

		return value;
	}

	@Override
	public void nullSafeSet(
			PreparedStatement preparedStatement, Object target, int index,
			SharedSessionContractImplementor sharedSessionContractImplementor)
		throws SQLException {

		if (target == null) {
			preparedStatement.setNull(index, -5);
		}
		else {
			preparedStatement.setLong(index, (Long)target);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		NullableLongType.class);

}