/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchUniqueIndexWithLongTypeEntryException;
import com.liferay.portal.tools.service.builder.test.model.UniqueIndexWithLongTypeEntry;
import com.liferay.portal.tools.service.builder.test.service.UniqueIndexWithLongTypeEntryLocalServiceUtil;
import com.liferay.portal.tools.service.builder.test.service.persistence.UniqueIndexWithLongTypeEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.UniqueIndexWithLongTypeEntryUtil;

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
public class UniqueIndexWithLongTypeEntryPersistenceTest {

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
		_persistence = UniqueIndexWithLongTypeEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<UniqueIndexWithLongTypeEntry> iterator =
			_uniqueIndexWithLongTypeEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		UniqueIndexWithLongTypeEntry uniqueIndexWithLongTypeEntry =
			_persistence.create(pk);

		Assert.assertNotNull(uniqueIndexWithLongTypeEntry);

		Assert.assertEquals(uniqueIndexWithLongTypeEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		UniqueIndexWithLongTypeEntry newUniqueIndexWithLongTypeEntry =
			addUniqueIndexWithLongTypeEntry();

		_persistence.remove(newUniqueIndexWithLongTypeEntry);

		UniqueIndexWithLongTypeEntry existingUniqueIndexWithLongTypeEntry =
			_persistence.fetchByPrimaryKey(
				newUniqueIndexWithLongTypeEntry.getPrimaryKey());

		Assert.assertNull(existingUniqueIndexWithLongTypeEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addUniqueIndexWithLongTypeEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		UniqueIndexWithLongTypeEntry newUniqueIndexWithLongTypeEntry =
			_persistence.create(pk);

		newUniqueIndexWithLongTypeEntry.setName(RandomTestUtil.randomString());

		newUniqueIndexWithLongTypeEntry.setLongTypeId(
			RandomTestUtil.nextLong());

		_uniqueIndexWithLongTypeEntries.add(
			_persistence.update(newUniqueIndexWithLongTypeEntry));

		UniqueIndexWithLongTypeEntry existingUniqueIndexWithLongTypeEntry =
			_persistence.findByPrimaryKey(
				newUniqueIndexWithLongTypeEntry.getPrimaryKey());

		Assert.assertEquals(
			existingUniqueIndexWithLongTypeEntry.
				getUniqueIndexWithLongTypeEntryId(),
			newUniqueIndexWithLongTypeEntry.
				getUniqueIndexWithLongTypeEntryId());
		Assert.assertEquals(
			existingUniqueIndexWithLongTypeEntry.getName(),
			newUniqueIndexWithLongTypeEntry.getName());
		Assert.assertEquals(
			existingUniqueIndexWithLongTypeEntry.getLongTypeId(),
			newUniqueIndexWithLongTypeEntry.getLongTypeId());
	}

	@Test
	public void testCountByName_LongTypeId() throws Exception {
		_persistence.countByName_LongTypeId("", (Long)null);

		_persistence.countByName_LongTypeId("null", (Long)null);

		_persistence.countByName_LongTypeId((String)null, (Long)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		UniqueIndexWithLongTypeEntry newUniqueIndexWithLongTypeEntry =
			addUniqueIndexWithLongTypeEntry();

		UniqueIndexWithLongTypeEntry existingUniqueIndexWithLongTypeEntry =
			_persistence.findByPrimaryKey(
				newUniqueIndexWithLongTypeEntry.getPrimaryKey());

		Assert.assertEquals(
			existingUniqueIndexWithLongTypeEntry,
			newUniqueIndexWithLongTypeEntry);
	}

	@Test(expected = NoSuchUniqueIndexWithLongTypeEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<UniqueIndexWithLongTypeEntry>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"UniqueIndexWithLongTypeEntry", "uniqueIndexWithLongTypeEntryId",
			true, "name", true, "longTypeId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		UniqueIndexWithLongTypeEntry newUniqueIndexWithLongTypeEntry =
			addUniqueIndexWithLongTypeEntry();

		UniqueIndexWithLongTypeEntry existingUniqueIndexWithLongTypeEntry =
			_persistence.fetchByPrimaryKey(
				newUniqueIndexWithLongTypeEntry.getPrimaryKey());

		Assert.assertEquals(
			existingUniqueIndexWithLongTypeEntry,
			newUniqueIndexWithLongTypeEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		UniqueIndexWithLongTypeEntry missingUniqueIndexWithLongTypeEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingUniqueIndexWithLongTypeEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		UniqueIndexWithLongTypeEntry newUniqueIndexWithLongTypeEntry1 =
			addUniqueIndexWithLongTypeEntry();
		UniqueIndexWithLongTypeEntry newUniqueIndexWithLongTypeEntry2 =
			addUniqueIndexWithLongTypeEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newUniqueIndexWithLongTypeEntry1.getPrimaryKey());
		primaryKeys.add(newUniqueIndexWithLongTypeEntry2.getPrimaryKey());

		Map<Serializable, UniqueIndexWithLongTypeEntry>
			uniqueIndexWithLongTypeEntries = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, uniqueIndexWithLongTypeEntries.size());
		Assert.assertEquals(
			newUniqueIndexWithLongTypeEntry1,
			uniqueIndexWithLongTypeEntries.get(
				newUniqueIndexWithLongTypeEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newUniqueIndexWithLongTypeEntry2,
			uniqueIndexWithLongTypeEntries.get(
				newUniqueIndexWithLongTypeEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, UniqueIndexWithLongTypeEntry>
			uniqueIndexWithLongTypeEntries = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(uniqueIndexWithLongTypeEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		UniqueIndexWithLongTypeEntry newUniqueIndexWithLongTypeEntry =
			addUniqueIndexWithLongTypeEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newUniqueIndexWithLongTypeEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, UniqueIndexWithLongTypeEntry>
			uniqueIndexWithLongTypeEntries = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, uniqueIndexWithLongTypeEntries.size());
		Assert.assertEquals(
			newUniqueIndexWithLongTypeEntry,
			uniqueIndexWithLongTypeEntries.get(
				newUniqueIndexWithLongTypeEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, UniqueIndexWithLongTypeEntry>
			uniqueIndexWithLongTypeEntries = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(uniqueIndexWithLongTypeEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		UniqueIndexWithLongTypeEntry newUniqueIndexWithLongTypeEntry =
			addUniqueIndexWithLongTypeEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newUniqueIndexWithLongTypeEntry.getPrimaryKey());

		Map<Serializable, UniqueIndexWithLongTypeEntry>
			uniqueIndexWithLongTypeEntries = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, uniqueIndexWithLongTypeEntries.size());
		Assert.assertEquals(
			newUniqueIndexWithLongTypeEntry,
			uniqueIndexWithLongTypeEntries.get(
				newUniqueIndexWithLongTypeEntry.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			UniqueIndexWithLongTypeEntryLocalServiceUtil.
				getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<UniqueIndexWithLongTypeEntry>() {

				@Override
				public void performAction(
					UniqueIndexWithLongTypeEntry uniqueIndexWithLongTypeEntry) {

					Assert.assertNotNull(uniqueIndexWithLongTypeEntry);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		UniqueIndexWithLongTypeEntry newUniqueIndexWithLongTypeEntry =
			addUniqueIndexWithLongTypeEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			UniqueIndexWithLongTypeEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"uniqueIndexWithLongTypeEntryId",
				newUniqueIndexWithLongTypeEntry.
					getUniqueIndexWithLongTypeEntryId()));

		List<UniqueIndexWithLongTypeEntry> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		UniqueIndexWithLongTypeEntry existingUniqueIndexWithLongTypeEntry =
			result.get(0);

		Assert.assertEquals(
			existingUniqueIndexWithLongTypeEntry,
			newUniqueIndexWithLongTypeEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			UniqueIndexWithLongTypeEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"uniqueIndexWithLongTypeEntryId", RandomTestUtil.nextLong()));

		List<UniqueIndexWithLongTypeEntry> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		UniqueIndexWithLongTypeEntry newUniqueIndexWithLongTypeEntry =
			addUniqueIndexWithLongTypeEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			UniqueIndexWithLongTypeEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("uniqueIndexWithLongTypeEntryId"));

		Object newUniqueIndexWithLongTypeEntryId =
			newUniqueIndexWithLongTypeEntry.getUniqueIndexWithLongTypeEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"uniqueIndexWithLongTypeEntryId",
				new Object[] {newUniqueIndexWithLongTypeEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingUniqueIndexWithLongTypeEntryId = result.get(0);

		Assert.assertEquals(
			existingUniqueIndexWithLongTypeEntryId,
			newUniqueIndexWithLongTypeEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			UniqueIndexWithLongTypeEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("uniqueIndexWithLongTypeEntryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"uniqueIndexWithLongTypeEntryId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		UniqueIndexWithLongTypeEntry newUniqueIndexWithLongTypeEntry =
			addUniqueIndexWithLongTypeEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newUniqueIndexWithLongTypeEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValuesWithDynamicQueryLoadFromDatabase()
		throws Exception {

		_testResetOriginalValuesWithDynamicQuery(true);
	}

	@Test
	public void testResetOriginalValuesWithDynamicQueryLoadFromSession()
		throws Exception {

		_testResetOriginalValuesWithDynamicQuery(false);
	}

	private void _testResetOriginalValuesWithDynamicQuery(boolean clearSession)
		throws Exception {

		UniqueIndexWithLongTypeEntry newUniqueIndexWithLongTypeEntry =
			addUniqueIndexWithLongTypeEntry();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			UniqueIndexWithLongTypeEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"uniqueIndexWithLongTypeEntryId",
				newUniqueIndexWithLongTypeEntry.
					getUniqueIndexWithLongTypeEntryId()));

		List<UniqueIndexWithLongTypeEntry> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		UniqueIndexWithLongTypeEntry uniqueIndexWithLongTypeEntry) {

		Assert.assertEquals(
			uniqueIndexWithLongTypeEntry.getName(),
			ReflectionTestUtil.invoke(
				uniqueIndexWithLongTypeEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));
		Assert.assertEquals(
			Long.valueOf(uniqueIndexWithLongTypeEntry.getLongTypeId()),
			ReflectionTestUtil.<Long>invoke(
				uniqueIndexWithLongTypeEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "longTypeId"));
	}

	protected UniqueIndexWithLongTypeEntry addUniqueIndexWithLongTypeEntry()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		UniqueIndexWithLongTypeEntry uniqueIndexWithLongTypeEntry =
			_persistence.create(pk);

		uniqueIndexWithLongTypeEntry.setName(RandomTestUtil.randomString());

		uniqueIndexWithLongTypeEntry.setLongTypeId(RandomTestUtil.nextLong());

		_uniqueIndexWithLongTypeEntries.add(
			_persistence.update(uniqueIndexWithLongTypeEntry));

		return uniqueIndexWithLongTypeEntry;
	}

	private List<UniqueIndexWithLongTypeEntry> _uniqueIndexWithLongTypeEntries =
		new ArrayList<UniqueIndexWithLongTypeEntry>();
	private UniqueIndexWithLongTypeEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}