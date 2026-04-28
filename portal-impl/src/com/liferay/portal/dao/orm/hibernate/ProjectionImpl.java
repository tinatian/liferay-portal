/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.hibernate;

import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.dao.orm.Type;

/**
 * @author Brian Wing Shun Chan
 */
public class ProjectionImpl implements Projection {

	public ProjectionImpl(
		Projection projection, ProjectionType projectionType) {

		_projection = projection;
		_projectionType = projectionType;
	}

	public ProjectionImpl(
		Projection projection, ProjectionType projectionType, String alias) {

		_projection = projection;
		_projectionType = projectionType;
		_alias = alias;
	}

	public ProjectionImpl(ProjectionType projectionType, String propertyName) {
		_projectionType = projectionType;
		_propertyName = propertyName;
	}

	public ProjectionImpl(
		ProjectionType projectionType, String sql, String groupBy,
		String[] columnAliases, Type[] types) {

		_projectionType = projectionType;
		_sql = sql;
		_groupBy = groupBy;
		_columnAliases = columnAliases;
		_types = types;
	}

	public String getAlias() {
		return _alias;
	}

	public String[] getColumnAliases() {
		return _columnAliases;
	}

	public String getGroupBy() {
		return _groupBy;
	}

	public Projection getProjection() {
		return _projection;
	}

	public ProjectionType getProjectionType() {
		return _projectionType;
	}

	public String getPropertyName() {
		return _propertyName;
	}

	public String getSQL() {
		return _sql;
	}

	public Type[] getTypes() {
		return _types;
	}

	@Override
	public String toString() {
		if ((_projectionType == ProjectionType.PROPERTY) ||
			(_projectionType == ProjectionType.GROUP_PROPERTY)) {

			return _propertyName;
		}
		else if (_projectionType == ProjectionType.AVG) {
			return "avg(" + _propertyName + ")";
		}
		else if (_projectionType == ProjectionType.COUNT) {
			return "count(" + _propertyName + ")";
		}
		else if (_projectionType == ProjectionType.COUNT_DISTINCT) {
			return "distinct count(" + _propertyName + ")";
		}
		else if (_projectionType == ProjectionType.MAX) {
			return "max(" + _propertyName + ")";
		}
		else if (_projectionType == ProjectionType.MIN) {
			return "min(" + _propertyName + ")";
		}
		else if (_projectionType == ProjectionType.SUM) {
			return "sum(" + _propertyName + ")";
		}
		else if (_projectionType == ProjectionType.ROW_COUNT) {
			return "count(*)";
		}
		else if (_projectionType == ProjectionType.DISTINCT) {
			return "distinct " + _projection;
		}
		else if (_projectionType == ProjectionType.ALIAS) {
			return _projection + " as " + _alias;
		}
		else if ((_projectionType == ProjectionType.SQL_PROJECTION) ||
				 (_projectionType == ProjectionType.SQL_GROUP_PROJECTION)) {

			return _sql;
		}

		return "{projectionType=" + _projectionType + "}";
	}

	private String _alias;
	private String[] _columnAliases;
	private String _groupBy;
	private Projection _projection;
	private final ProjectionType _projectionType;
	private String _propertyName;
	private String _sql;
	private Type[] _types;

}