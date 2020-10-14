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

package com.liferay.portal.tools.service.builder.test.service.persistence.impl.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.model.DSLQueryEntry;
import com.liferay.portal.tools.service.builder.test.model.DSLQueryEntryTable;
import com.liferay.portal.tools.service.builder.test.model.DSLQueryStatusEntry;
import com.liferay.portal.tools.service.builder.test.model.DSLQueryStatusEntryTable;
import com.liferay.portal.tools.service.builder.test.service.persistence.DSLQueryEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.DSLQueryStatusEntryPersistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tina Tian
 */
@RunWith(Arquillian.class)
public class DSLQueryEntryPersistenceImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.tools.service.builder.test.service"));

	@Before
	public void setUp() {
		_testDSLQueryEntry1 = _addDSLQueryEntry(_TEST_NAME_1);
		_testDSLQueryEntry2 = _addDSLQueryEntry(_TEST_NAME_2);
		_testDSLQueryEntry3 = _addDSLQueryEntry(_TEST_NAME_3);
	}

	@Test
	public void testDSLQuery() {
		DSLQuery dslQuery = DSLQueryFactoryUtil.select(
			DSLQueryEntryTable.INSTANCE.name
		).from(
			DSLQueryEntryTable.INSTANCE
		).where(
			DSLQueryEntryTable.INSTANCE.dslQueryEntryId.neq(
				_testDSLQueryEntry1.getDslQueryEntryId())
		);

		List<String> result = _dslQueryEntryPersistence.dslQuery(dslQuery);

		_assertResult(result, new String[] {_TEST_NAME_2, _TEST_NAME_3});

		_testDSLQueryEntry2.setName("updated.test.name.2");

		_testDSLQueryEntry2 = _dslQueryEntryPersistence.updateImpl(
			_testDSLQueryEntry2);

		result = _dslQueryEntryPersistence.dslQuery(dslQuery);

		_assertResult(
			result, new String[] {"updated.test.name.2", _TEST_NAME_3});
	}

	@Test
	public void testDSLQueryWithMultiTables() {
		long currentTime = System.currentTimeMillis();

		_addDSLQueryStatusEntry(
			_testDSLQueryEntry1, _TEST_STATUS_1, currentTime - 1);
		_addDSLQueryStatusEntry(
			_testDSLQueryEntry1, _TEST_STATUS_2, currentTime);
		_addDSLQueryStatusEntry(
			_testDSLQueryEntry2, _TEST_STATUS_1, currentTime - 1);
		_addDSLQueryStatusEntry(
			_testDSLQueryEntry3, _TEST_STATUS_2, currentTime - 1);

		DSLQuery dslQuery = DSLQueryFactoryUtil.select(
			DSLQueryEntryTable.INSTANCE.name
		).from(
			DSLQueryEntryTable.INSTANCE
		).leftJoinOn(
			DSLQueryStatusEntryTable.INSTANCE,
			DSLQueryEntryTable.INSTANCE.dslQueryEntryId.eq(
				DSLQueryStatusEntryTable.INSTANCE.dslQueryEntryId)
		).where(
			DSLQueryStatusEntryTable.INSTANCE.status.eq(
				_TEST_STATUS_2
			).and(
				DSLQueryStatusEntryTable.INSTANCE.statusDate.gte(
					new Date(currentTime))
			)
		);

		List<String> result = _dslQueryEntryPersistence.dslQuery(dslQuery);

		_assertResult(result, new String[] {_TEST_NAME_1});

		_testDSLQueryEntry1.setName("update.test.name.1");

		_testDSLQueryEntry1 = _dslQueryEntryPersistence.updateImpl(
			_testDSLQueryEntry1);

		result = _dslQueryEntryPersistence.dslQuery(dslQuery);

		_assertResult(result, new String[] {"update.test.name.1"});

		_addDSLQueryStatusEntry(
			_testDSLQueryEntry2, _TEST_STATUS_2, currentTime);

		result = _dslQueryEntryPersistence.dslQuery(dslQuery);

		_assertResult(
			result, new String[] {"update.test.name.1", _TEST_NAME_2});
	}

	private DSLQueryEntry _addDSLQueryEntry(String name) {
		DSLQueryEntry dslQueryEntry = _dslQueryEntryPersistence.create(
			RandomTestUtil.nextLong());

		dslQueryEntry.setName(name);

		dslQueryEntry = _dslQueryEntryPersistence.updateImpl(dslQueryEntry);

		_dslQueryEntries.add(dslQueryEntry);

		return dslQueryEntry;
	}

	private DSLQueryStatusEntry _addDSLQueryStatusEntry(
		DSLQueryEntry dslQueryEntry, String status, long time) {

		DSLQueryStatusEntry dslQueryStatusEntry =
			_dslQueryStatusEntryPersistence.create(RandomTestUtil.nextLong());

		dslQueryStatusEntry.setDslQueryEntryId(
			dslQueryEntry.getDslQueryEntryId());
		dslQueryStatusEntry.setStatus(status);
		dslQueryStatusEntry.setStatusDate(new Date(time));

		dslQueryStatusEntry = _dslQueryStatusEntryPersistence.updateImpl(
			dslQueryStatusEntry);

		_dslQueryStatusEntries.add(dslQueryStatusEntry);

		return dslQueryStatusEntry;
	}

	private void _assertResult(List<String> result, String[] names) {
		for (String name : names) {
			Assert.assertTrue(result.toString(), result.contains(name));
		}

		Assert.assertEquals(result.toString(), names.length, result.size());
	}

	private static final String _TEST_NAME_1 = "test.name.1";

	private static final String _TEST_NAME_2 = "test.name.2";

	private static final String _TEST_NAME_3 = "test.name.3";

	private static final String _TEST_STATUS_1 = "test.status.1";

	private static final String _TEST_STATUS_2 = "test.status.2";

	@DeleteAfterTestRun
	private final List<DSLQueryEntry> _dslQueryEntries = new ArrayList<>();

	@Inject
	private DSLQueryEntryPersistence _dslQueryEntryPersistence;

	@DeleteAfterTestRun
	private final List<DSLQueryStatusEntry> _dslQueryStatusEntries =
		new ArrayList<>();

	@Inject
	private DSLQueryStatusEntryPersistence _dslQueryStatusEntryPersistence;

	private DSLQueryEntry _testDSLQueryEntry1;
	private DSLQueryEntry _testDSLQueryEntry2;
	private DSLQueryEntry _testDSLQueryEntry3;

}