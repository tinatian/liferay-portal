/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.db.DBManager;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.orm.Conjunction;
import com.liferay.portal.kernel.dao.orm.Disjunction;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.OrderFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionList;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.tools.service.builder.test.model.DynamicQueryEntry;
import com.liferay.portal.tools.service.builder.test.service.DynamicQueryEntryLocalService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tina Tian
 */
@RunWith(Arquillian.class)
public class DynamicQueryEntryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		long now = System.currentTimeMillis();

		_dynamicQueryEntries.add(
			_addDynamicQueryEntry(
				"alpha", "first", 100, 1, new Date(now - 4000),
				new Date(now - 4000)));
		_dynamicQueryEntries.add(
			_addDynamicQueryEntry(
				"beta", null, 200, 2, new Date(now - 3000),
				new Date(now - 1500)));
		_dynamicQueryEntries.add(
			_addDynamicQueryEntry(
				"gamma", "third", 300, 1, new Date(now - 2000),
				new Date(now - 2000)));
		_dynamicQueryEntries.add(
			_addDynamicQueryEntry(
				"delta", "fourth", 400, 3, new Date(now - 1000),
				new Date(now - 500)));
	}

	@AfterClass
	public static void tearDownClass() {
		for (DynamicQueryEntry dynamicQueryEntry : _dynamicQueryEntries) {
			_dynamicQueryEntryLocalService.deleteDynamicQueryEntry(
				dynamicQueryEntry);
		}
	}

	@Test
	public void testAddOrderAsc() {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.addOrder(OrderFactoryUtil.asc("amount"));

		_assertDynamicQueryResult(
			dynamicQuery, "alpha", "beta", "gamma", "delta");
	}

	@Test
	public void testAddOrderByComparator() {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		OrderFactoryUtil.addOrderByComparator(
			dynamicQuery,
			new OrderByComparator<DynamicQueryEntry>() {

				@Override
				public int compare(
					DynamicQueryEntry dynamicQueryEntry1,
					DynamicQueryEntry dynamicQueryEntry2) {

					return Long.compare(
						dynamicQueryEntry2.getAmount(),
						dynamicQueryEntry1.getAmount());
				}

				@Override
				public String[] getOrderByFields() {
					return new String[] {"amount"};
				}

				@Override
				public boolean isAscending() {
					return false;
				}

				@Override
				public boolean isAscending(String field) {
					return false;
				}

			});

		_assertDynamicQueryResult(
			dynamicQuery, "delta", "gamma", "beta", "alpha");
	}

	@Test
	public void testAddOrderDesc() {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.addOrder(OrderFactoryUtil.desc("amount"));

		_assertDynamicQueryResult(
			dynamicQuery, "delta", "gamma", "beta", "alpha");
	}

	@Test
	public void testAnd() {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(
			RestrictionsFactoryUtil.and(
				RestrictionsFactoryUtil.eq("status", 1),
				RestrictionsFactoryUtil.ge("amount", 200L)));

		_assertDynamicQueryResult(dynamicQuery, "gamma");
	}

	@Test
	public void testBetween() {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(RestrictionsFactoryUtil.between("amount", 150L, 350L));

		_assertDynamicQueryResult(dynamicQuery, "beta", "gamma");
	}

	@Test
	public void testConjunction() {
		Conjunction conjunction = RestrictionsFactoryUtil.conjunction();

		conjunction.add(RestrictionsFactoryUtil.ge("amount", 100L));
		conjunction.add(RestrictionsFactoryUtil.le("amount", 300L));
		conjunction.add(RestrictionsFactoryUtil.eq("status", 1));

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(conjunction);

		_assertDynamicQueryResult(dynamicQuery, "alpha", "gamma");
	}

	@Test
	public void testDisjunction() {
		Disjunction disjunction = RestrictionsFactoryUtil.disjunction();

		disjunction.add(RestrictionsFactoryUtil.eq("name", "alpha"));
		disjunction.add(RestrictionsFactoryUtil.eq("name", "beta"));
		disjunction.add(RestrictionsFactoryUtil.eq("name", "gamma"));

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(disjunction);

		_assertDynamicQueryResult(dynamicQuery, "alpha", "beta", "gamma");
	}

	@Test
	public void testDisjunctionMixedCriterionTypes() {
		Disjunction disjunction = RestrictionsFactoryUtil.disjunction();

		disjunction.add(
			PropertyFactoryUtil.forName(
				"description"
			).isNull());
		disjunction.add(
			PropertyFactoryUtil.forName(
				"name"
			).eq(
				"alpha"
			));
		disjunction.add(
			PropertyFactoryUtil.forName(
				"name"
			).like(
				"g%"
			));

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(disjunction);

		_assertDynamicQueryResult(dynamicQuery, "alpha", "beta", "gamma");
	}

	@Test
	public void testEq() {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(RestrictionsFactoryUtil.eq("name", "alpha"));

		_assertDynamicQueryResult(dynamicQuery, "alpha");
	}

	@Test
	public void testEqProperty() {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(
			RestrictionsFactoryUtil.eqProperty("createDate", "modifiedDate"));

		_assertDynamicQueryResult(dynamicQuery, "alpha", "gamma");
	}

	@Test
	public void testForClassWithAlias() {
		Class<?> clazz = _dynamicQueryEntryLocalService.getClass();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DynamicQueryEntry.class, "e", clazz.getClassLoader());

		dynamicQuery.add(RestrictionsFactoryUtil.eq("e.name", "alpha"));

		_assertDynamicQueryResult(dynamicQuery, "alpha");
	}

	@Test
	public void testGe() {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(RestrictionsFactoryUtil.ge("amount", 300L));

		_assertDynamicQueryResult(dynamicQuery, "gamma", "delta");
	}

	@Test
	public void testGeProperty() {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(
			RestrictionsFactoryUtil.geProperty("modifiedDate", "createDate"));

		_assertDynamicQueryResult(
			dynamicQuery, "alpha", "beta", "gamma", "delta");
	}

	@Test
	public void testGt() {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(RestrictionsFactoryUtil.gt("amount", 200L));

		_assertDynamicQueryResult(dynamicQuery, "gamma", "delta");
	}

	@Test
	public void testGtProperty() {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(
			RestrictionsFactoryUtil.gtProperty("modifiedDate", "createDate"));

		_assertDynamicQueryResult(dynamicQuery, "beta", "delta");
	}

	@Test
	public void testIlike() {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(RestrictionsFactoryUtil.ilike("name", "ALPH%"));

		_assertDynamicQueryResult(dynamicQuery, "alpha");
	}

	@Test
	public void testIn() {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in("status", new Integer[] {1, 3}));

		_assertDynamicQueryResult(dynamicQuery, "alpha", "gamma", "delta");
	}

	@Test
	public void testInLargeCollectionTriggersDisjunction() throws Exception {
		DBManager dbManager = ReflectionTestUtil.getFieldValue(
			DBManagerUtil.class, "_dbManager");

		int dbInMaxParameters = RandomTestUtil.randomInt(10, 100);

		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					dbManager, "_databaseInMaxParameters", dbInMaxParameters)) {

			Assert.assertEquals(
				dbInMaxParameters, DBManagerUtil.getDBInMaxParameters());

			List<Long> ids = new ArrayList<>(dbInMaxParameters + 5);

			for (int i = 0; i < (dbInMaxParameters + 4); i++) {
				ids.add((long)i);
			}

			DynamicQueryEntry dynamicQueryEntry = _dynamicQueryEntries.get(0);

			ids.add(dynamicQueryEntry.getDynamicQueryEntryId());

			DynamicQuery dynamicQuery =
				_dynamicQueryEntryLocalService.dynamicQuery();

			dynamicQuery.add(
				RestrictionsFactoryUtil.in("dynamicQueryEntryId", ids));

			_assertDynamicQueryResult(dynamicQuery, "alpha");
		}
	}

	@Test
	public void testIsNotNull() {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(RestrictionsFactoryUtil.isNotNull("description"));

		_assertDynamicQueryResult(dynamicQuery, "alpha", "gamma", "delta");
	}

	@Test
	public void testIsNull() {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(RestrictionsFactoryUtil.isNull("description"));

		_assertDynamicQueryResult(dynamicQuery, "beta");
	}

	@Test
	public void testLe() {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(RestrictionsFactoryUtil.le("amount", 200L));

		_assertDynamicQueryResult(dynamicQuery, "alpha", "beta");
	}

	@Test
	public void testLeProperty() {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(
			RestrictionsFactoryUtil.leProperty("createDate", "modifiedDate"));

		_assertDynamicQueryResult(
			dynamicQuery, "alpha", "beta", "gamma", "delta");
	}

	@Test
	public void testLike() {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(RestrictionsFactoryUtil.like("name", "g%"));

		_assertDynamicQueryResult(dynamicQuery, "gamma");
	}

	@Test
	public void testLtProperty() {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(
			RestrictionsFactoryUtil.ltProperty("createDate", "modifiedDate"));

		_assertDynamicQueryResult(dynamicQuery, "beta", "delta");
	}

	@Test
	public void testNe() {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(RestrictionsFactoryUtil.ne("status", 1));

		_assertDynamicQueryResult(dynamicQuery, "beta", "delta");
	}

	@Test
	public void testNeProperty() {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(
			RestrictionsFactoryUtil.neProperty("createDate", "modifiedDate"));

		_assertDynamicQueryResult(dynamicQuery, "beta", "delta");
	}

	@Test
	public void testNestedConjunctionInsideDisjunction() {
		Conjunction conjunction = RestrictionsFactoryUtil.conjunction();

		conjunction.add(RestrictionsFactoryUtil.eq("name", "beta"));
		conjunction.add(RestrictionsFactoryUtil.isNull("description"));

		Disjunction disjunction = RestrictionsFactoryUtil.disjunction();

		disjunction.add(RestrictionsFactoryUtil.eq("name", "alpha"));
		disjunction.add(conjunction);

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(disjunction);

		_assertDynamicQueryResult(dynamicQuery, "alpha", "beta");
	}

	@Test
	public void testNestedDisjunctionInsideConjunction() {
		Disjunction disjunction = RestrictionsFactoryUtil.disjunction();

		disjunction.add(RestrictionsFactoryUtil.eq("name", "alpha"));
		disjunction.add(RestrictionsFactoryUtil.eq("name", "gamma"));

		Conjunction conjunction = RestrictionsFactoryUtil.conjunction();

		conjunction.add(RestrictionsFactoryUtil.eq("status", 1));
		conjunction.add(disjunction);

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(conjunction);

		_assertDynamicQueryResult(dynamicQuery, "alpha", "gamma");
	}

	@Test
	public void testNot() {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(
			RestrictionsFactoryUtil.not(
				RestrictionsFactoryUtil.eq("name", "alpha")));

		_assertDynamicQueryResult(dynamicQuery, "beta", "gamma", "delta");
	}

	@Test
	public void testOr() {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(
			RestrictionsFactoryUtil.or(
				RestrictionsFactoryUtil.eq("name", "alpha"),
				RestrictionsFactoryUtil.eq("name", "delta")));

		_assertDynamicQueryResult(dynamicQuery, "alpha", "delta");
	}

	@Test
	public void testProjectionCount() {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.count("dynamicQueryEntryId"));

		List<Long> result = _dynamicQueryEntryLocalService.dynamicQuery(
			dynamicQuery);

		Assert.assertEquals(
			Long.valueOf(_dynamicQueryEntries.size()), result.get(0));
	}

	@Test
	public void testProjectionCountDistinct() {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.countDistinct("status"));

		List<Long> result = _dynamicQueryEntryLocalService.dynamicQuery(
			dynamicQuery);

		Assert.assertEquals(Long.valueOf(3), result.get(0));
	}

	@Test
	public void testProjectionDistinct() {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.addOrder(OrderFactoryUtil.asc("status"));
		dynamicQuery.setProjection(
			ProjectionFactoryUtil.distinct(
				ProjectionFactoryUtil.property("status")));

		Assert.assertEquals(
			Arrays.asList(1, 2, 3),
			_dynamicQueryEntryLocalService.dynamicQuery(dynamicQuery));
	}

	@Test
	public void testProjectionGroupProperty() {
		ProjectionList projectionList = ProjectionFactoryUtil.projectionList();

		projectionList.add(ProjectionFactoryUtil.groupProperty("status"));
		projectionList.add(
			ProjectionFactoryUtil.alias(
				ProjectionFactoryUtil.count("dynamicQueryEntryId"), "c"));

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.addOrder(OrderFactoryUtil.asc("status"));
		dynamicQuery.setProjection(projectionList);

		List<Object[]> rows = _dynamicQueryEntryLocalService.dynamicQuery(
			dynamicQuery);

		Assert.assertEquals(rows.toString(), 3, rows.size());

		Assert.assertArrayEquals(new Object[] {1, 2L}, rows.get(0));
		Assert.assertArrayEquals(new Object[] {2, 1L}, rows.get(1));
		Assert.assertArrayEquals(new Object[] {3, 1L}, rows.get(2));
	}

	@Test
	public void testProjectionProperty() {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("name"));

		_assertDynamicQueryResult(
			dynamicQuery, "alpha", "beta", "gamma", "delta");
	}

	@Test
	public void testProjectionPropertyInSubquery() {
		DynamicQuery subqueryDynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		subqueryDynamicQuery.add(RestrictionsFactoryUtil.eq("status", 1));
		subqueryDynamicQuery.setProjection(
			ProjectionFactoryUtil.distinct(
				ProjectionFactoryUtil.property("amount")));

		Property property = PropertyFactoryUtil.forName("amount");

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(property.in(subqueryDynamicQuery));

		_assertDynamicQueryResult(dynamicQuery, "alpha", "gamma");
	}

	@Test
	public void testProjectionRowCount() {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.setProjection(ProjectionFactoryUtil.rowCount());

		List<Long> rows = _dynamicQueryEntryLocalService.dynamicQuery(
			dynamicQuery);

		Assert.assertEquals(
			Long.valueOf(_dynamicQueryEntries.size()), rows.get(0));
	}

	@Test
	public void testProjectionSqlGroupProjection() {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.addOrder(OrderFactoryUtil.asc("status"));
		dynamicQuery.setProjection(
			ProjectionFactoryUtil.sqlGroupProjection(
				"this_.status AS s, count(*) AS c", "this_.status",
				new String[] {"s", "c"}, new Type[] {Type.INTEGER, Type.LONG}));

		List<Object[]> rows = _dynamicQueryEntryLocalService.dynamicQuery(
			dynamicQuery);

		Assert.assertEquals(rows.toString(), 3, rows.size());

		Assert.assertArrayEquals(new Object[] {1, 2L}, rows.get(0));
		Assert.assertArrayEquals(new Object[] {2, 1L}, rows.get(1));
		Assert.assertArrayEquals(new Object[] {3, 1L}, rows.get(2));
	}

	@Test
	public void testProjectionSqlProjection() {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.addOrder(OrderFactoryUtil.asc("amount"));
		dynamicQuery.setProjection(
			ProjectionFactoryUtil.sqlProjection(
				"this_.name AS sqlName", new String[] {"sqlName"},
				new Type[] {Type.STRING}));

		_assertDynamicQueryResult(
			dynamicQuery, "alpha", "beta", "gamma", "delta");
	}

	@Test
	public void testProjectionSumMaxMinAvg() {
		ProjectionList projectionList = ProjectionFactoryUtil.projectionList();

		projectionList.add(ProjectionFactoryUtil.sum("amount"));
		projectionList.add(ProjectionFactoryUtil.max("amount"));
		projectionList.add(ProjectionFactoryUtil.min("amount"));
		projectionList.add(ProjectionFactoryUtil.avg("amount"));

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.setProjection(projectionList);

		List<Object[]> rows = _dynamicQueryEntryLocalService.dynamicQuery(
			dynamicQuery);

		Object[] row = rows.get(0);

		Assert.assertEquals(1000L, row[0]);
		Assert.assertEquals(400L, row[1]);
		Assert.assertEquals(100L, row[2]);
		Assert.assertEquals(250.0, (Double)row[3], 0.001);
	}

	@Test
	public void testPropertyComparisons() {
		Property property = PropertyFactoryUtil.forName("amount");

		DynamicQuery ltDynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		ltDynamicQuery.add(property.lt(300L));

		_assertDynamicQueryResult(ltDynamicQuery, "alpha", "beta");

		DynamicQuery leDynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		leDynamicQuery.add(property.le(300L));

		_assertDynamicQueryResult(leDynamicQuery, "alpha", "beta", "gamma");

		DynamicQuery gtDynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		gtDynamicQuery.add(property.gt(200L));

		_assertDynamicQueryResult(gtDynamicQuery, "gamma", "delta");

		DynamicQuery geDynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		geDynamicQuery.add(property.ge(200L));

		_assertDynamicQueryResult(geDynamicQuery, "beta", "gamma", "delta");
	}

	@Test
	public void testPropertyEq() {
		Property property = PropertyFactoryUtil.forName("name");

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(property.eq("beta"));

		_assertDynamicQueryResult(dynamicQuery, "beta");
	}

	@Test
	public void testPropertyEqWithAlias() {
		Class<?> clazz = _dynamicQueryEntryLocalService.getClass();

		DynamicQuery subqueryDynamicQuery = DynamicQueryFactoryUtil.forClass(
			DynamicQueryEntry.class, "child", clazz.getClassLoader());

		subqueryDynamicQuery.add(
			RestrictionsFactoryUtil.eqProperty("child.status", "this.status"));
		subqueryDynamicQuery.add(
			RestrictionsFactoryUtil.eq("child.name", "gamma"));
		subqueryDynamicQuery.setProjection(
			ProjectionFactoryUtil.property("child.amount"));

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		Property property = PropertyFactoryUtil.forName("amount");

		dynamicQuery.add(property.eq(subqueryDynamicQuery));

		_assertDynamicQueryResult(dynamicQuery, "gamma");
	}

	@Test
	public void testPropertyInCollection() {
		Property property = PropertyFactoryUtil.forName("name");

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(property.in(Arrays.asList("alpha", "delta")));

		_assertDynamicQueryResult(dynamicQuery, "alpha", "delta");
	}

	@Test
	public void testPropertyIsNotNull() {
		Property property = PropertyFactoryUtil.forName("description");

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(property.isNotNull());

		_assertDynamicQueryResult(dynamicQuery, "alpha", "gamma", "delta");
	}

	@Test
	public void testPropertyIsNull() {
		Property property = PropertyFactoryUtil.forName("description");

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(property.isNull());

		_assertDynamicQueryResult(dynamicQuery, "beta");
	}

	@Test
	public void testPropertyLike() {
		Property property = PropertyFactoryUtil.forName("name");

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(property.like("g%"));

		_assertDynamicQueryResult(dynamicQuery, "gamma");
	}

	@Test
	public void testPropertyNe() {
		Property property = PropertyFactoryUtil.forName("name");

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(property.ne("alpha"));

		_assertDynamicQueryResult(dynamicQuery, "beta", "gamma", "delta");
	}

	@Test
	public void testPropertyNotInCollection() {
		Property property = PropertyFactoryUtil.forName("name");

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(
			RestrictionsFactoryUtil.not(
				property.in(Arrays.asList("alpha", "delta"))));

		_assertDynamicQueryResult(dynamicQuery, "beta", "gamma");
	}

	@Test
	public void testPropertySubqueryEq() {
		DynamicQuery subqueryDynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		subqueryDynamicQuery.setProjection(ProjectionFactoryUtil.max("amount"));

		Property property = PropertyFactoryUtil.forName("amount");

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(property.eq(subqueryDynamicQuery));

		_assertDynamicQueryResult(dynamicQuery, "delta");
	}

	@Test
	public void testPropertySubqueryEqAll() {
		DynamicQuery subqueryDynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		subqueryDynamicQuery.add(RestrictionsFactoryUtil.eq("status", 1));
		subqueryDynamicQuery.setProjection(
			ProjectionFactoryUtil.property("status"));

		Property property = PropertyFactoryUtil.forName("status");

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(property.eqAll(subqueryDynamicQuery));

		_assertDynamicQueryResult(dynamicQuery, "alpha", "gamma");
	}

	@Test
	public void testPropertySubqueryGe() {
		DynamicQuery subqueryDynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		subqueryDynamicQuery.add(RestrictionsFactoryUtil.eq("status", 2));
		subqueryDynamicQuery.setProjection(ProjectionFactoryUtil.max("amount"));

		Property property = PropertyFactoryUtil.forName("amount");

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(property.ge(subqueryDynamicQuery));

		_assertDynamicQueryResult(dynamicQuery, "beta", "gamma", "delta");
	}

	@Test
	public void testPropertySubqueryGeAll() {
		DynamicQuery subqueryDynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		subqueryDynamicQuery.add(RestrictionsFactoryUtil.eq("status", 1));
		subqueryDynamicQuery.setProjection(
			ProjectionFactoryUtil.property("amount"));

		Property property = PropertyFactoryUtil.forName("amount");

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(property.geAll(subqueryDynamicQuery));

		_assertDynamicQueryResult(dynamicQuery, "gamma", "delta");
	}

	@Test
	public void testPropertySubqueryGeSome() {
		DynamicQuery subqueryDynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		subqueryDynamicQuery.add(RestrictionsFactoryUtil.gt("status", 1));
		subqueryDynamicQuery.setProjection(
			ProjectionFactoryUtil.property("amount"));

		Property property = PropertyFactoryUtil.forName("amount");

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(property.geSome(subqueryDynamicQuery));

		_assertDynamicQueryResult(dynamicQuery, "beta", "gamma", "delta");
	}

	@Test
	public void testPropertySubqueryGt() {
		DynamicQuery subqueryDynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		subqueryDynamicQuery.setProjection(ProjectionFactoryUtil.min("amount"));

		Property property = PropertyFactoryUtil.forName("amount");

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(property.gt(subqueryDynamicQuery));

		_assertDynamicQueryResult(dynamicQuery, "beta", "gamma", "delta");
	}

	@Test
	public void testPropertySubqueryGtAll() {
		DynamicQuery subqueryDynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		subqueryDynamicQuery.add(RestrictionsFactoryUtil.eq("status", 1));
		subqueryDynamicQuery.setProjection(
			ProjectionFactoryUtil.property("amount"));

		Property property = PropertyFactoryUtil.forName("amount");

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(property.gtAll(subqueryDynamicQuery));

		_assertDynamicQueryResult(dynamicQuery, "delta");
	}

	@Test
	public void testPropertySubqueryGtSome() {
		DynamicQuery subqueryDynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		subqueryDynamicQuery.add(RestrictionsFactoryUtil.eq("status", 1));
		subqueryDynamicQuery.setProjection(
			ProjectionFactoryUtil.property("amount"));

		Property property = PropertyFactoryUtil.forName("amount");

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(property.gtSome(subqueryDynamicQuery));

		_assertDynamicQueryResult(dynamicQuery, "beta", "gamma", "delta");
	}

	@Test
	public void testPropertySubqueryIn() {
		DynamicQuery subqueryDynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		subqueryDynamicQuery.add(RestrictionsFactoryUtil.eq("status", 1));
		subqueryDynamicQuery.setProjection(
			ProjectionFactoryUtil.property("amount"));

		Property property = PropertyFactoryUtil.forName("amount");

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(property.in(subqueryDynamicQuery));

		_assertDynamicQueryResult(dynamicQuery, "alpha", "gamma");
	}

	@Test
	public void testPropertySubqueryLe() {
		DynamicQuery subqueryDynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		subqueryDynamicQuery.setProjection(ProjectionFactoryUtil.min("amount"));

		Property property = PropertyFactoryUtil.forName("amount");

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(property.le(subqueryDynamicQuery));

		_assertDynamicQueryResult(dynamicQuery, "alpha");
	}

	@Test
	public void testPropertySubqueryLeAll() {
		DynamicQuery subqueryDynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		subqueryDynamicQuery.add(RestrictionsFactoryUtil.eq("status", 1));
		subqueryDynamicQuery.setProjection(
			ProjectionFactoryUtil.property("amount"));

		Property property = PropertyFactoryUtil.forName("amount");

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(property.leAll(subqueryDynamicQuery));

		_assertDynamicQueryResult(dynamicQuery, "alpha");
	}

	@Test
	public void testPropertySubqueryLeSome() {
		DynamicQuery subqueryDynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		subqueryDynamicQuery.add(RestrictionsFactoryUtil.eq("status", 1));
		subqueryDynamicQuery.setProjection(
			ProjectionFactoryUtil.property("amount"));

		Property property = PropertyFactoryUtil.forName("amount");

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(property.leSome(subqueryDynamicQuery));

		_assertDynamicQueryResult(dynamicQuery, "alpha", "beta", "gamma");
	}

	@Test
	public void testPropertySubqueryLt() {
		DynamicQuery subqueryDynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		subqueryDynamicQuery.add(RestrictionsFactoryUtil.eq("status", 1));
		subqueryDynamicQuery.setProjection(ProjectionFactoryUtil.max("amount"));

		Property property = PropertyFactoryUtil.forName("amount");

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(property.lt(subqueryDynamicQuery));

		_assertDynamicQueryResult(dynamicQuery, "alpha", "beta");
	}

	@Test
	public void testPropertySubqueryLtAll() {
		DynamicQuery subqueryDynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		subqueryDynamicQuery.add(RestrictionsFactoryUtil.gt("status", 1));
		subqueryDynamicQuery.setProjection(
			ProjectionFactoryUtil.property("amount"));

		Property property = PropertyFactoryUtil.forName("amount");

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(property.ltAll(subqueryDynamicQuery));

		_assertDynamicQueryResult(dynamicQuery, "alpha");
	}

	@Test
	public void testPropertySubqueryLtSome() {
		DynamicQuery subqueryDynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		subqueryDynamicQuery.add(RestrictionsFactoryUtil.eq("status", 1));
		subqueryDynamicQuery.setProjection(
			ProjectionFactoryUtil.property("amount"));

		Property property = PropertyFactoryUtil.forName("amount");

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(property.ltSome(subqueryDynamicQuery));

		_assertDynamicQueryResult(dynamicQuery, "alpha", "beta");
	}

	@Test
	public void testPropertySubqueryNe() {
		DynamicQuery subqueryDynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		subqueryDynamicQuery.add(RestrictionsFactoryUtil.eq("status", 2));
		subqueryDynamicQuery.setProjection(ProjectionFactoryUtil.max("amount"));

		Property property = PropertyFactoryUtil.forName("amount");

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(property.ne(subqueryDynamicQuery));

		_assertDynamicQueryResult(dynamicQuery, "alpha", "gamma", "delta");
	}

	@Test
	public void testPropertySubqueryNotIn() {
		DynamicQuery subqueryDynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		subqueryDynamicQuery.add(RestrictionsFactoryUtil.eq("status", 1));
		subqueryDynamicQuery.setProjection(
			ProjectionFactoryUtil.property("dynamicQueryEntryId"));

		Property property = PropertyFactoryUtil.forName("dynamicQueryEntryId");

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(property.notIn(subqueryDynamicQuery));

		_assertDynamicQueryResult(dynamicQuery, "beta", "delta");
	}

	@Test
	public void testRangeGeAndLt() {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(RestrictionsFactoryUtil.ge("amount", 200L));
		dynamicQuery.add(RestrictionsFactoryUtil.lt("amount", 400L));

		_assertDynamicQueryResult(dynamicQuery, "beta", "gamma");
	}

	@Test
	public void testSetLimit() {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.setLimit(0, 2);

		_assertDynamicQueryResult(dynamicQuery, "alpha", "beta");
	}

	@Test
	public void testSetLimitAllPos() {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.setLimit(QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		_assertDynamicQueryResult(
			dynamicQuery, "alpha", "beta", "gamma", "delta");
	}

	@Test
	public void testSetLimitBothNegative() {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.setLimit(-5, -3);

		_assertDynamicQueryResult(dynamicQuery);
	}

	@Test
	public void testSetLimitEndAllPos() {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.setLimit(2, QueryUtil.ALL_POS);

		_assertDynamicQueryResult(dynamicQuery, "gamma", "delta");
	}

	@Test
	public void testSetLimitNegativeStart() {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.setLimit(-5, 3);

		_assertDynamicQueryResult(dynamicQuery, "alpha", "beta", "gamma");
	}

	@Test
	public void testSetLimitStartEqualsEnd() {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.setLimit(2, 2);

		_assertDynamicQueryResult(dynamicQuery);
	}

	@Test
	public void testSetLimitStartGreaterThanEnd() {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.setLimit(5, 2);

		_assertDynamicQueryResult(dynamicQuery);
	}

	@Test
	public void testSqlRestriction() {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(
			RestrictionsFactoryUtil.sqlRestriction(
				"name = ?", "alpha", Type.STRING));

		_assertDynamicQueryResult(dynamicQuery, "alpha");
	}

	private static DynamicQueryEntry _addDynamicQueryEntry(
			String name, String description, long amount, int status,
			Date createDate, Date modifiedDate)
		throws Exception {

		DynamicQueryEntry dynamicQueryEntry =
			_dynamicQueryEntryLocalService.createDynamicQueryEntry(
				RandomTestUtil.nextLong());

		dynamicQueryEntry.setCompanyId(TestPropsValues.getCompanyId());
		dynamicQueryEntry.setUserId(TestPropsValues.getUserId());
		dynamicQueryEntry.setCreateDate(createDate);
		dynamicQueryEntry.setModifiedDate(modifiedDate);
		dynamicQueryEntry.setName(name);
		dynamicQueryEntry.setDescription(description);
		dynamicQueryEntry.setAmount(amount);
		dynamicQueryEntry.setStatus(status);

		return _dynamicQueryEntryLocalService.addDynamicQueryEntry(
			dynamicQueryEntry);
	}

	private void _assertDynamicQueryResult(
		DynamicQuery dynamicQuery, String... expectedNames) {

		dynamicQuery.addOrder(OrderFactoryUtil.asc("dynamicQueryEntryId"));

		List<?> results = _dynamicQueryEntryLocalService.dynamicQuery(
			dynamicQuery);

		Assert.assertEquals(
			results.toString(), expectedNames.length, results.size());

		for (int i = 0; i < expectedNames.length; i++) {
			Object result = results.get(i);

			String name;

			if (result instanceof DynamicQueryEntry) {
				DynamicQueryEntry dynamicQueryEntry = (DynamicQueryEntry)result;

				name = dynamicQueryEntry.getName();
			}
			else {
				name = (String)result;
			}

			Assert.assertEquals(results.toString(), expectedNames[i], name);
		}
	}

	private static final List<DynamicQueryEntry> _dynamicQueryEntries =
		new ArrayList<>();

	@Inject
	private static DynamicQueryEntryLocalService _dynamicQueryEntryLocalService;

}