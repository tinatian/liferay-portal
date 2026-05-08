/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.hibernate;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.Order;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.util.ListUtil;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.AbstractQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import jakarta.persistence.criteria.Subquery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.hibernate.Session;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.type.BasicTypeReference;

/**
 * @author Brian Wing Shun Chan
 */
public class HibernateTypedQueryUtil {

	public static TypedQuery<?> buildTypedQuery(
		DynamicQueryImpl dynamicQueryImpl, Session session) {

		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

		CriteriaQuery<?> criteriaQuery = criteriaBuilder.createQuery(
			dynamicQueryImpl.getClazz());

		_apply(criteriaQuery, criteriaBuilder, dynamicQueryImpl);

		return session.createQuery(criteriaQuery);
	}

	private static void _apply(
		AbstractQuery<?> abstractQuery, CriteriaBuilder criteriaBuilder,
		DynamicQueryImpl dynamicQueryImpl) {

		String alias = dynamicQueryImpl.getAlias();

		Root<?> from = abstractQuery.from(dynamicQueryImpl.getClazz());

		if (alias != null) {
			from.alias(alias);
		}

		List<Criterion> criterions = dynamicQueryImpl.getCriterions();

		if (!criterions.isEmpty()) {
			Predicate[] predicates = new Predicate[criterions.size()];

			for (int i = 0; i < criterions.size(); i++) {
				predicates[i] = _toPredicate(
					criteriaBuilder, abstractQuery, from, alias,
					criterions.get(i));
			}

			abstractQuery.where(predicates);
		}

		List<Expression<?>> groupBy = new ArrayList<>();

		Selection<?> selection = _toSelection(
			criteriaBuilder, abstractQuery, from, alias,
			dynamicQueryImpl.getProjection(), groupBy);

		if (abstractQuery instanceof CriteriaQuery) {
			CriteriaQuery criteriaQuery = (CriteriaQuery)abstractQuery;

			criteriaQuery.select(selection);
		}
		else {
			Subquery subquery = (Subquery)abstractQuery;

			subquery.select((Expression)selection);
		}

		if (!groupBy.isEmpty()) {
			abstractQuery.groupBy(groupBy);
		}

		List<Order> orders = dynamicQueryImpl.getOrders();

		if ((orders != null) && !orders.isEmpty() &&
			(abstractQuery instanceof CriteriaQuery)) {

			List<jakarta.persistence.criteria.Order> jpaOrders =
				new ArrayList<>(orders.size());

			for (Order order : orders) {
				OrderImpl orderImpl = (OrderImpl)order;

				Expression<?> expression = _getPath(
					orderImpl.getPropertyName(), abstractQuery, from, alias);

				if (orderImpl.isAscending()) {
					jpaOrders.add(criteriaBuilder.asc(expression));
				}
				else {
					jpaOrders.add(criteriaBuilder.desc(expression));
				}
			}

			CriteriaQuery<?> criteriaQuery = (CriteriaQuery<?>)abstractQuery;

			criteriaQuery.orderBy(jpaOrders);
		}
	}

	private static Path<?> _getPath(
		String name, AbstractQuery<?> abstractQuery, From<?, ?> from,
		String alias) {

		List<String> results = StringUtil.split(name, CharPool.PERIOD);

		if (results.size() == 1) {
			return from.get(name);
		}

		if (results.size() != 2) {
			throw new IllegalArgumentException("Unable to parse name " + name);
		}

		String parsedAlias = results.get(0);
		String columnName = results.get(1);

		if (Objects.equals(parsedAlias, alias)) {
			return from.get(columnName);
		}

		while (abstractQuery instanceof Subquery<?>) {
			Subquery<?> subquery = (Subquery<?>)abstractQuery;

			abstractQuery = subquery.getParent();

			List<Root<?>> roots = ListUtil.fromCollection(
				abstractQuery.getRoots());

			if (roots.size() != 1) {
				throw new IllegalStateException();
			}

			Root<?> root = roots.get(0);

			if (Objects.equals(parsedAlias, root.getAlias())) {
				return root.get(columnName);
			}
		}

		throw new IllegalArgumentException(
			StringBundler.concat(
				"Unable to resolve alias ", parsedAlias, " in: ", name));
	}

	private static Predicate _toPredicate(
		CriteriaBuilder criteriaBuilder, AbstractQuery<?> abstractQuery,
		From<?, ?> from, String alias, Criterion criterion) {

		CriterionImpl criterionImpl = (CriterionImpl)criterion;

		CriterionType criterionType = criterionImpl.getCriterionType();
		String propertyName = criterionImpl.getPropertyName();
		Object[] values = criterionImpl.getValues();

		if (criterionType == CriterionType.ALL_EQ) {
			Map<String, Criterion> map = criterionImpl.getPropertyNameValues();

			if ((map == null) || map.isEmpty()) {
				return criteriaBuilder.conjunction();
			}

			Predicate[] predicates = new Predicate[map.size()];
			int i = 0;

			for (Map.Entry<String, Criterion> entry : map.entrySet()) {
				predicates[i++] = criteriaBuilder.equal(
					_getPath(entry.getKey(), abstractQuery, from, alias),
					entry.getValue());
			}

			return criteriaBuilder.and(predicates);
		}
		else if (criterionType == CriterionType.AND) {
			List<Criterion> children = criterionImpl.getCriterions();

			return criteriaBuilder.and(
				_toPredicate(
					criteriaBuilder, abstractQuery, from, alias,
					children.get(0)),
				_toPredicate(
					criteriaBuilder, abstractQuery, from, alias,
					children.get(1)));
		}
		else if (criterionType == CriterionType.BETWEEN) {
			return criteriaBuilder.between(
				(Expression)_getPath(propertyName, abstractQuery, from, alias),
				(Comparable)values[0], (Comparable)values[1]);
		}
		else if (criterionType == CriterionType.CONJUNCTION) {
			List<Criterion> children = criterionImpl.getCriterions();

			if (children.isEmpty()) {
				return criteriaBuilder.conjunction();
			}

			Predicate[] predicates = new Predicate[children.size()];

			for (int i = 0; i < children.size(); i++) {
				predicates[i] = _toPredicate(
					criteriaBuilder, abstractQuery, from, alias,
					children.get(i));
			}

			return criteriaBuilder.and(predicates);
		}
		else if (criterionType == CriterionType.DISJUNCTION) {
			List<Criterion> children = criterionImpl.getCriterions();

			if (children.isEmpty()) {
				return criteriaBuilder.disjunction();
			}

			Predicate[] predicates = new Predicate[children.size()];

			for (int i = 0; i < children.size(); i++) {
				predicates[i] = _toPredicate(
					criteriaBuilder, abstractQuery, from, alias,
					children.get(i));
			}

			return criteriaBuilder.or(predicates);
		}
		else if (criterionType == CriterionType.EQ) {
			return criteriaBuilder.equal(
				_getPath(propertyName, abstractQuery, from, alias), values[0]);
		}
		else if (criterionType == CriterionType.EQ_PROPERTY) {
			return criteriaBuilder.equal(
				_getPath(propertyName, abstractQuery, from, alias),
				_getPath((String)values[0], abstractQuery, from, alias));
		}
		else if (criterionType == CriterionType.GE) {
			return criteriaBuilder.greaterThanOrEqualTo(
				(Expression)_getPath(propertyName, abstractQuery, from, alias),
				(Comparable)values[0]);
		}
		else if (criterionType == CriterionType.GE_PROPERTY) {
			return criteriaBuilder.greaterThanOrEqualTo(
				(Expression)_getPath(propertyName, abstractQuery, from, alias),
				(Expression)_getPath(
					(String)values[0], abstractQuery, from, alias));
		}
		else if (criterionType == CriterionType.GT) {
			return criteriaBuilder.greaterThan(
				(Expression)_getPath(propertyName, abstractQuery, from, alias),
				(Comparable)values[0]);
		}
		else if (criterionType == CriterionType.GT_PROPERTY) {
			return criteriaBuilder.greaterThan(
				(Expression)_getPath(propertyName, abstractQuery, from, alias),
				(Expression)_getPath(
					(String)values[0], abstractQuery, from, alias));
		}
		else if (criterionType == CriterionType.ILIKE) {
			Expression<String> stringExpression = _getPath(
				propertyName, abstractQuery, from, alias
			).as(
				String.class
			);

			return criteriaBuilder.like(
				criteriaBuilder.lower(stringExpression),
				com.liferay.portal.kernel.util.StringUtil.toLowerCase(
					String.valueOf(values[0])));
		}
		else if (criterionType == CriterionType.IN) {
			CriteriaBuilder.In in = criteriaBuilder.in(
				_getPath(propertyName, abstractQuery, from, alias));

			for (Object value : values) {
				in.value(value);
			}

			return in;
		}
		else if (criterionType == CriterionType.IS_EMPTY) {
			return criteriaBuilder.isEmpty(
				(Expression)_getPath(propertyName, abstractQuery, from, alias));
		}
		else if (criterionType == CriterionType.IS_NOT_EMPTY) {
			return criteriaBuilder.isNotEmpty(
				(Expression)_getPath(propertyName, abstractQuery, from, alias));
		}
		else if (criterionType == CriterionType.IS_NOT_NULL) {
			return criteriaBuilder.isNotNull(
				_getPath(propertyName, abstractQuery, from, alias));
		}
		else if (criterionType == CriterionType.IS_NULL) {
			return criteriaBuilder.isNull(
				_getPath(propertyName, abstractQuery, from, alias));
		}
		else if (criterionType == CriterionType.LE) {
			return criteriaBuilder.lessThanOrEqualTo(
				(Expression)_getPath(propertyName, abstractQuery, from, alias),
				(Comparable)values[0]);
		}
		else if (criterionType == CriterionType.LE_PROPERTY) {
			return criteriaBuilder.lessThanOrEqualTo(
				(Expression)_getPath(propertyName, abstractQuery, from, alias),
				(Expression)_getPath(
					(String)values[0], abstractQuery, from, alias));
		}
		else if (criterionType == CriterionType.LIKE) {
			Expression<String> stringExpression = _getPath(
				propertyName, abstractQuery, from, alias
			).as(
				String.class
			);

			return criteriaBuilder.like(
				stringExpression, String.valueOf(values[0]));
		}
		else if (criterionType == CriterionType.LT) {
			return criteriaBuilder.lessThan(
				(Expression)_getPath(propertyName, abstractQuery, from, alias),
				(Comparable)values[0]);
		}
		else if (criterionType == CriterionType.LT_PROPERTY) {
			return criteriaBuilder.lessThan(
				(Expression)_getPath(propertyName, abstractQuery, from, alias),
				(Expression)_getPath(
					(String)values[0], abstractQuery, from, alias));
		}
		else if (criterionType == CriterionType.NE) {
			return criteriaBuilder.notEqual(
				_getPath(propertyName, abstractQuery, from, alias), values[0]);
		}
		else if (criterionType == CriterionType.NE_PROPERTY) {
			return criteriaBuilder.notEqual(
				_getPath(propertyName, abstractQuery, from, alias),
				_getPath((String)values[0], abstractQuery, from, alias));
		}
		else if (criterionType == CriterionType.NOT) {
			List<Criterion> children = criterionImpl.getCriterions();

			return criteriaBuilder.not(
				_toPredicate(
					criteriaBuilder, abstractQuery, from, alias,
					children.get(0)));
		}
		else if (criterionType == CriterionType.OR) {
			List<Criterion> children = criterionImpl.getCriterions();

			return criteriaBuilder.or(
				_toPredicate(
					criteriaBuilder, abstractQuery, from, alias,
					children.get(0)),
				_toPredicate(
					criteriaBuilder, abstractQuery, from, alias,
					children.get(1)));
		}
		else if (criterionType == CriterionType.SIZE_EQ) {
			Integer size = (Integer)values[0];

			return criteriaBuilder.equal(
				criteriaBuilder.size(
					(Expression)_getPath(
						propertyName, abstractQuery, from, alias)),
				size.intValue());
		}
		else if (criterionType == CriterionType.SIZE_GE) {
			Integer size = (Integer)values[0];

			return criteriaBuilder.greaterThanOrEqualTo(
				criteriaBuilder.size(
					(Expression)_getPath(
						propertyName, abstractQuery, from, alias)),
				size.intValue());
		}
		else if (criterionType == CriterionType.SIZE_GT) {
			Integer size = (Integer)values[0];

			return criteriaBuilder.greaterThan(
				criteriaBuilder.size(
					(Expression)_getPath(
						propertyName, abstractQuery, from, alias)),
				size.intValue());
		}
		else if (criterionType == CriterionType.SIZE_LE) {
			Integer size = (Integer)values[0];

			return criteriaBuilder.lessThanOrEqualTo(
				criteriaBuilder.size(
					(Expression)_getPath(
						propertyName, abstractQuery, from, alias)),
				size.intValue());
		}
		else if (criterionType == CriterionType.SIZE_LT) {
			Integer size = (Integer)values[0];

			return criteriaBuilder.lessThan(
				criteriaBuilder.size(
					(Expression)_getPath(
						propertyName, abstractQuery, from, alias)),
				size.intValue());
		}
		else if (criterionType == CriterionType.SIZE_NE) {
			Integer size = (Integer)values[0];

			return criteriaBuilder.notEqual(
				criteriaBuilder.size(
					(Expression)_getPath(
						propertyName, abstractQuery, from, alias)),
				size.intValue());
		}
		else if (criterionType == CriterionType.SQL_RESTRICTION) {
			HibernateCriteriaBuilder hibernateCriteriaBuilder =
				(HibernateCriteriaBuilder)criteriaBuilder;

			Expression<?>[] arguments;

			if ((values == null) || (values.length == 0)) {
				arguments = new Expression<?>[0];
			}
			else {
				arguments = new Expression<?>[values.length];

				for (int i = 0; i < values.length; i++) {
					arguments[i] = criteriaBuilder.literal(values[i]);
				}
			}

			return hibernateCriteriaBuilder.wrap(
				hibernateCriteriaBuilder.sql(
					criterionImpl.getSQL(), Boolean.class, arguments));
		}
		else if (criterionType == CriterionType.SUBQUERY_EQ) {
			return criteriaBuilder.equal(
				_getPath(propertyName, abstractQuery, from, alias),
				_toSubquery(
					criteriaBuilder, abstractQuery,
					(DynamicQueryImpl)values[0]));
		}
		else if (criterionType == CriterionType.SUBQUERY_EQ_ALL) {
			return criteriaBuilder.equal(
				_getPath(propertyName, abstractQuery, from, alias),
				criteriaBuilder.all(
					_toSubquery(
						criteriaBuilder, abstractQuery,
						(DynamicQueryImpl)values[0])));
		}
		else if (criterionType == CriterionType.SUBQUERY_GE) {
			return criteriaBuilder.greaterThanOrEqualTo(
				(Expression)_getPath(propertyName, abstractQuery, from, alias),
				(Expression)_toSubquery(
					criteriaBuilder, abstractQuery,
					(DynamicQueryImpl)values[0]));
		}
		else if (criterionType == CriterionType.SUBQUERY_GE_ALL) {
			return criteriaBuilder.greaterThanOrEqualTo(
				(Expression)_getPath(propertyName, abstractQuery, from, alias),
				(Expression)criteriaBuilder.all(
					_toSubquery(
						criteriaBuilder, abstractQuery,
						(DynamicQueryImpl)values[0])));
		}
		else if (criterionType == CriterionType.SUBQUERY_GE_SOME) {
			return criteriaBuilder.greaterThanOrEqualTo(
				(Expression)_getPath(propertyName, abstractQuery, from, alias),
				(Expression)criteriaBuilder.some(
					_toSubquery(
						criteriaBuilder, abstractQuery,
						(DynamicQueryImpl)values[0])));
		}
		else if (criterionType == CriterionType.SUBQUERY_GT) {
			return criteriaBuilder.greaterThan(
				(Expression)_getPath(propertyName, abstractQuery, from, alias),
				(Expression)_toSubquery(
					criteriaBuilder, abstractQuery,
					(DynamicQueryImpl)values[0]));
		}
		else if (criterionType == CriterionType.SUBQUERY_GT_ALL) {
			return criteriaBuilder.greaterThan(
				(Expression)_getPath(propertyName, abstractQuery, from, alias),
				(Expression)criteriaBuilder.all(
					_toSubquery(
						criteriaBuilder, abstractQuery,
						(DynamicQueryImpl)values[0])));
		}
		else if (criterionType == CriterionType.SUBQUERY_GT_SOME) {
			return criteriaBuilder.greaterThan(
				(Expression)_getPath(propertyName, abstractQuery, from, alias),
				(Expression)criteriaBuilder.some(
					_toSubquery(
						criteriaBuilder, abstractQuery,
						(DynamicQueryImpl)values[0])));
		}
		else if (criterionType == CriterionType.SUBQUERY_IN) {
			return _getPath(
				propertyName, abstractQuery, from, alias
			).in(
				_toSubquery(
					criteriaBuilder, abstractQuery, (DynamicQueryImpl)values[0])
			);
		}
		else if (criterionType == CriterionType.SUBQUERY_LE) {
			return criteriaBuilder.lessThanOrEqualTo(
				(Expression)_getPath(propertyName, abstractQuery, from, alias),
				(Expression)_toSubquery(
					criteriaBuilder, abstractQuery,
					(DynamicQueryImpl)values[0]));
		}
		else if (criterionType == CriterionType.SUBQUERY_LE_ALL) {
			return criteriaBuilder.lessThanOrEqualTo(
				(Expression)_getPath(propertyName, abstractQuery, from, alias),
				(Expression)criteriaBuilder.all(
					_toSubquery(
						criteriaBuilder, abstractQuery,
						(DynamicQueryImpl)values[0])));
		}
		else if (criterionType == CriterionType.SUBQUERY_LE_SOME) {
			return criteriaBuilder.lessThanOrEqualTo(
				(Expression)_getPath(propertyName, abstractQuery, from, alias),
				(Expression)criteriaBuilder.some(
					_toSubquery(
						criteriaBuilder, abstractQuery,
						(DynamicQueryImpl)values[0])));
		}
		else if (criterionType == CriterionType.SUBQUERY_LT) {
			return criteriaBuilder.lessThan(
				(Expression)_getPath(propertyName, abstractQuery, from, alias),
				(Expression)_toSubquery(
					criteriaBuilder, abstractQuery,
					(DynamicQueryImpl)values[0]));
		}
		else if (criterionType == CriterionType.SUBQUERY_LT_ALL) {
			return criteriaBuilder.lessThan(
				(Expression)_getPath(propertyName, abstractQuery, from, alias),
				(Expression)criteriaBuilder.all(
					_toSubquery(
						criteriaBuilder, abstractQuery,
						(DynamicQueryImpl)values[0])));
		}
		else if (criterionType == CriterionType.SUBQUERY_LT_SOME) {
			return criteriaBuilder.lessThan(
				(Expression)_getPath(propertyName, abstractQuery, from, alias),
				(Expression)criteriaBuilder.some(
					_toSubquery(
						criteriaBuilder, abstractQuery,
						(DynamicQueryImpl)values[0])));
		}
		else if (criterionType == CriterionType.SUBQUERY_NE) {
			return criteriaBuilder.notEqual(
				_getPath(propertyName, abstractQuery, from, alias),
				_toSubquery(
					criteriaBuilder, abstractQuery,
					(DynamicQueryImpl)values[0]));
		}
		else if (criterionType == CriterionType.SUBQUERY_NOT_IN) {
			return _getPath(
				propertyName, abstractQuery, from, alias
			).in(
				_toSubquery(
					criteriaBuilder, abstractQuery, (DynamicQueryImpl)values[0])
			).not();
		}

		throw new IllegalStateException(
			"Unexpected criterion type: " + criterionType);
	}

	private static Selection<?> _toSelection(
		CriteriaBuilder criteriaBuilder, AbstractQuery<?> abstractQuery,
		From<?, ?> from, String alias, Projection projection,
		List<Expression<?>> groupBy) {

		if (projection == null) {
			return from;
		}

		if (projection instanceof ProjectionListImpl) {
			ProjectionListImpl projectionListImpl =
				(ProjectionListImpl)projection;

			Map<Projection, String> projections =
				projectionListImpl.getProjections();

			List<Selection<?>> selections = new ArrayList<>(projections.size());

			for (Map.Entry<Projection, String> entry : projections.entrySet()) {
				Selection<?> child = _toSelection(
					criteriaBuilder, abstractQuery, from, alias, entry.getKey(),
					groupBy);

				String selectionAlias = entry.getValue();

				if (selectionAlias != null) {
					child.alias(selectionAlias);
				}

				selections.add(child);
			}

			return criteriaBuilder.array(
				selections.toArray(new Selection<?>[0]));
		}

		ProjectionImpl projectionImpl = (ProjectionImpl)projection;

		ProjectionType projectionType = projectionImpl.getProjectionType();
		String propertyName = projectionImpl.getPropertyName();

		if (projectionType == ProjectionType.ALIAS) {
			Selection<?> child = _toSelection(
				criteriaBuilder, abstractQuery, from, alias,
				projectionImpl.getProjection(), groupBy);

			child.alias(projectionImpl.getAlias());

			return child;
		}
		else if (projectionType == ProjectionType.AVG) {
			return criteriaBuilder.avg(
				(Expression)_getPath(propertyName, abstractQuery, from, alias));
		}
		else if (projectionType == ProjectionType.COUNT) {
			return criteriaBuilder.count(
				_getPath(propertyName, abstractQuery, from, alias));
		}
		else if (projectionType == ProjectionType.COUNT_DISTINCT) {
			return criteriaBuilder.countDistinct(
				_getPath(propertyName, abstractQuery, from, alias));
		}
		else if (projectionType == ProjectionType.DISTINCT) {
			abstractQuery.distinct(true);

			return _toSelection(
				criteriaBuilder, abstractQuery, from, alias,
				projectionImpl.getProjection(), groupBy);
		}
		else if (projectionType == ProjectionType.GROUP_PROPERTY) {
			Path<?> path = _getPath(propertyName, abstractQuery, from, alias);

			groupBy.add(path);

			return path;
		}
		else if (projectionType == ProjectionType.MAX) {
			return criteriaBuilder.max(
				(Expression)_getPath(propertyName, abstractQuery, from, alias));
		}
		else if (projectionType == ProjectionType.MIN) {
			return criteriaBuilder.min(
				(Expression)_getPath(propertyName, abstractQuery, from, alias));
		}
		else if (projectionType == ProjectionType.PROPERTY) {
			return _getPath(propertyName, abstractQuery, from, alias);
		}
		else if (projectionType == ProjectionType.ROW_COUNT) {
			return criteriaBuilder.count(from);
		}
		else if ((projectionType == ProjectionType.SQL_GROUP_PROJECTION) ||
				 (projectionType == ProjectionType.SQL_PROJECTION)) {

			HibernateCriteriaBuilder hibernateCriteriaBuilder =
				(HibernateCriteriaBuilder)criteriaBuilder;

			String[] columnAliases = projectionImpl.getColumnAliases();
			Type[] types = projectionImpl.getTypes();

			List<String> columnSqls = _splitTopLevelCommas(
				projectionImpl.getSQL());

			Selection<?>[] selections = new Selection<?>[columnSqls.size()];

			for (int i = 0; i < columnSqls.size(); i++) {
				String columnSql = columnSqls.get(i).trim();

				String columnAlias = null;

				if ((columnAliases != null) && (i < columnAliases.length)) {
					columnAlias = columnAliases[i];
				}

				if (columnAlias != null) {
					columnSql = _stripTrailingAlias(columnSql, columnAlias);
				}

				Class<?> javaType = Object.class;

				if ((types != null) && (i < types.length) &&
					(types[i] != null)) {

					BasicTypeReference<?> basicTypeReference =
						TypeTranslator.translate(types[i]);

					if (basicTypeReference != null) {
						javaType = basicTypeReference.getJavaType();
					}
				}

				Selection<?> sqlSelection = _sqlSelection(
					hibernateCriteriaBuilder, columnSql, javaType);

				if (columnAlias != null) {
					sqlSelection.alias(columnAlias);
				}

				selections[i] = sqlSelection;
			}

			if (projectionType == ProjectionType.SQL_GROUP_PROJECTION) {
				String groupBySql = projectionImpl.getGroupBy();

				if ((groupBySql != null) && !groupBySql.isEmpty()) {
					for (String groupByPart :
							_splitTopLevelCommas(groupBySql)) {

						groupBy.add(
							_sqlSelection(
								hibernateCriteriaBuilder, groupByPart.trim(),
								Object.class));
					}
				}
			}

			if (selections.length == 1) {
				return selections[0];
			}

			return criteriaBuilder.array(selections);
		}
		else if (projectionType == ProjectionType.SUM) {
			return criteriaBuilder.sum(
				(Expression)_getPath(propertyName, abstractQuery, from, alias));
		}

		throw new IllegalStateException(
			"Unexpected projection type: " + projectionType);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static <T> Expression<T> _sqlSelection(
		HibernateCriteriaBuilder hibernateCriteriaBuilder, String sql,
		Class<?> javaType) {

		return hibernateCriteriaBuilder.sql(sql, (Class)javaType);
	}

	private static List<String> _splitTopLevelCommas(String sql) {
		List<String> parts = new ArrayList<>();

		int depth = 0;
		int start = 0;

		for (int i = 0; i < sql.length(); i++) {
			char c = sql.charAt(i);

			if (c == CharPool.OPEN_PARENTHESIS) {
				depth++;
			}
			else if (c == CharPool.CLOSE_PARENTHESIS) {
				depth--;
			}
			else if ((c == CharPool.COMMA) && (depth == 0)) {
				parts.add(sql.substring(start, i));

				start = i + 1;
			}
		}

		parts.add(sql.substring(start));

		return parts;
	}

	private static String _stripTrailingAlias(String sql, String alias) {
		String trimmed = sql.trim();

		String upperCased = trimmed.toUpperCase();

		int index = upperCased.lastIndexOf(" AS ");

		if (index < 0) {
			return trimmed;
		}

		String afterAs = trimmed.substring(index + 4).trim();

		if (afterAs.equalsIgnoreCase(alias)) {
			return trimmed.substring(0, index).trim();
		}

		return trimmed;
	}

	private static Subquery<?> _toSubquery(
		CriteriaBuilder criteriaBuilder, AbstractQuery<?> abstractQuery,
		DynamicQueryImpl dynamicQueryImpl) {

		Subquery<?> subquery = abstractQuery.subquery(
			dynamicQueryImpl.getClazz());

		_apply(subquery, criteriaBuilder, dynamicQueryImpl);

		return subquery;
	}

}