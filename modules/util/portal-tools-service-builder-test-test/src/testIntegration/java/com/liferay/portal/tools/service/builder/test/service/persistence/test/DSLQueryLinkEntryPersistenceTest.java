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

package com.liferay.portal.tools.service.builder.test.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchDSLQueryLinkEntryException;
import com.liferay.portal.tools.service.builder.test.model.DSLQueryLinkEntry;
import com.liferay.portal.tools.service.builder.test.service.DSLQueryLinkEntryLocalServiceUtil;
import com.liferay.portal.tools.service.builder.test.service.persistence.DSLQueryLinkEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.DSLQueryLinkEntryUtil;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @generated
 */
@RunWith(Arquillian.class)
public class DSLQueryLinkEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.tools.service.builder.test.service"));

	@Before
	public void setUp() {
		_persistence = DSLQueryLinkEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<DSLQueryLinkEntry> iterator = _dslQueryLinkEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DSLQueryLinkEntry dslQueryLinkEntry = _persistence.create(pk);

		Assert.assertNotNull(dslQueryLinkEntry);

		Assert.assertEquals(dslQueryLinkEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		DSLQueryLinkEntry newDSLQueryLinkEntry = addDSLQueryLinkEntry();

		_persistence.remove(newDSLQueryLinkEntry);

		DSLQueryLinkEntry existingDSLQueryLinkEntry =
			_persistence.fetchByPrimaryKey(
				newDSLQueryLinkEntry.getPrimaryKey());

		Assert.assertNull(existingDSLQueryLinkEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDSLQueryLinkEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DSLQueryLinkEntry newDSLQueryLinkEntry = _persistence.create(pk);

		newDSLQueryLinkEntry.setParentDSLQueryEntryId(
			RandomTestUtil.nextLong());

		newDSLQueryLinkEntry.setChildDSLQueryEntryId(RandomTestUtil.nextLong());

		_dslQueryLinkEntries.add(_persistence.update(newDSLQueryLinkEntry));

		DSLQueryLinkEntry existingDSLQueryLinkEntry =
			_persistence.findByPrimaryKey(newDSLQueryLinkEntry.getPrimaryKey());

		Assert.assertEquals(
			existingDSLQueryLinkEntry.getDslQueryLinkEntryId(),
			newDSLQueryLinkEntry.getDslQueryLinkEntryId());
		Assert.assertEquals(
			existingDSLQueryLinkEntry.getParentDSLQueryEntryId(),
			newDSLQueryLinkEntry.getParentDSLQueryEntryId());
		Assert.assertEquals(
			existingDSLQueryLinkEntry.getChildDSLQueryEntryId(),
			newDSLQueryLinkEntry.getChildDSLQueryEntryId());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		DSLQueryLinkEntry newDSLQueryLinkEntry = addDSLQueryLinkEntry();

		DSLQueryLinkEntry existingDSLQueryLinkEntry =
			_persistence.findByPrimaryKey(newDSLQueryLinkEntry.getPrimaryKey());

		Assert.assertEquals(existingDSLQueryLinkEntry, newDSLQueryLinkEntry);
	}

	@Test(expected = NoSuchDSLQueryLinkEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<DSLQueryLinkEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"DSLQueryLinkEntry", "dslQueryLinkEntryId", true,
			"parentDSLQueryEntryId", true, "childDSLQueryEntryId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		DSLQueryLinkEntry newDSLQueryLinkEntry = addDSLQueryLinkEntry();

		DSLQueryLinkEntry existingDSLQueryLinkEntry =
			_persistence.fetchByPrimaryKey(
				newDSLQueryLinkEntry.getPrimaryKey());

		Assert.assertEquals(existingDSLQueryLinkEntry, newDSLQueryLinkEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DSLQueryLinkEntry missingDSLQueryLinkEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingDSLQueryLinkEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		DSLQueryLinkEntry newDSLQueryLinkEntry1 = addDSLQueryLinkEntry();
		DSLQueryLinkEntry newDSLQueryLinkEntry2 = addDSLQueryLinkEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDSLQueryLinkEntry1.getPrimaryKey());
		primaryKeys.add(newDSLQueryLinkEntry2.getPrimaryKey());

		Map<Serializable, DSLQueryLinkEntry> dslQueryLinkEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, dslQueryLinkEntries.size());
		Assert.assertEquals(
			newDSLQueryLinkEntry1,
			dslQueryLinkEntries.get(newDSLQueryLinkEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newDSLQueryLinkEntry2,
			dslQueryLinkEntries.get(newDSLQueryLinkEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, DSLQueryLinkEntry> dslQueryLinkEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(dslQueryLinkEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		DSLQueryLinkEntry newDSLQueryLinkEntry = addDSLQueryLinkEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDSLQueryLinkEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, DSLQueryLinkEntry> dslQueryLinkEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, dslQueryLinkEntries.size());
		Assert.assertEquals(
			newDSLQueryLinkEntry,
			dslQueryLinkEntries.get(newDSLQueryLinkEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, DSLQueryLinkEntry> dslQueryLinkEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(dslQueryLinkEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		DSLQueryLinkEntry newDSLQueryLinkEntry = addDSLQueryLinkEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDSLQueryLinkEntry.getPrimaryKey());

		Map<Serializable, DSLQueryLinkEntry> dslQueryLinkEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, dslQueryLinkEntries.size());
		Assert.assertEquals(
			newDSLQueryLinkEntry,
			dslQueryLinkEntries.get(newDSLQueryLinkEntry.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			DSLQueryLinkEntryLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<DSLQueryLinkEntry>() {

				@Override
				public void performAction(DSLQueryLinkEntry dslQueryLinkEntry) {
					Assert.assertNotNull(dslQueryLinkEntry);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		DSLQueryLinkEntry newDSLQueryLinkEntry = addDSLQueryLinkEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DSLQueryLinkEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"dslQueryLinkEntryId",
				newDSLQueryLinkEntry.getDslQueryLinkEntryId()));

		List<DSLQueryLinkEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		DSLQueryLinkEntry existingDSLQueryLinkEntry = result.get(0);

		Assert.assertEquals(existingDSLQueryLinkEntry, newDSLQueryLinkEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DSLQueryLinkEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"dslQueryLinkEntryId", RandomTestUtil.nextLong()));

		List<DSLQueryLinkEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		DSLQueryLinkEntry newDSLQueryLinkEntry = addDSLQueryLinkEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DSLQueryLinkEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("dslQueryLinkEntryId"));

		Object newDslQueryLinkEntryId =
			newDSLQueryLinkEntry.getDslQueryLinkEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"dslQueryLinkEntryId", new Object[] {newDslQueryLinkEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingDslQueryLinkEntryId = result.get(0);

		Assert.assertEquals(
			existingDslQueryLinkEntryId, newDslQueryLinkEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DSLQueryLinkEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("dslQueryLinkEntryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"dslQueryLinkEntryId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected DSLQueryLinkEntry addDSLQueryLinkEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DSLQueryLinkEntry dslQueryLinkEntry = _persistence.create(pk);

		dslQueryLinkEntry.setParentDSLQueryEntryId(RandomTestUtil.nextLong());

		dslQueryLinkEntry.setChildDSLQueryEntryId(RandomTestUtil.nextLong());

		_dslQueryLinkEntries.add(_persistence.update(dslQueryLinkEntry));

		return dslQueryLinkEntry;
	}

	private List<DSLQueryLinkEntry> _dslQueryLinkEntries =
		new ArrayList<DSLQueryLinkEntry>();
	private DSLQueryLinkEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}