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
import com.liferay.portal.tools.service.builder.test.model.DSLQueryLinkEntry;
import com.liferay.portal.tools.service.builder.test.model.DSLQueryLinkEntryTable;
import com.liferay.portal.tools.service.builder.test.service.persistence.DSLQueryEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.DSLQueryLinkEntryPersistence;

import java.util.ArrayList;
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
		_testDSLQueryEntry1 = _createDSLQueryEntry(_TEST_NAME_1);
		_testDSLQueryEntry2 = _createDSLQueryEntry(_TEST_NAME_2);
		_testDSLQueryEntry3 = _createDSLQueryEntry(_TEST_NAME_3);

		_dslQueryEntries.add(_testDSLQueryEntry1);
		_dslQueryEntries.add(_testDSLQueryEntry2);
		_dslQueryEntries.add(_testDSLQueryEntry3);
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
		DSLQueryLinkEntry dslQueryLinkEntry = _createDSLQueryLinkEntry(
			_testDSLQueryEntry1.getDslQueryEntryId(),
			_testDSLQueryEntry1.getDslQueryEntryId());

		_dslQueryLinkEntries.add(dslQueryLinkEntry);

		_dslQueryLinkEntries.add(
			_createDSLQueryLinkEntry(
				_testDSLQueryEntry1.getDslQueryEntryId(),
				_testDSLQueryEntry2.getDslQueryEntryId()));
		_dslQueryLinkEntries.add(
			_createDSLQueryLinkEntry(
				_testDSLQueryEntry1.getDslQueryEntryId(),
				_testDSLQueryEntry3.getDslQueryEntryId()));

		DSLQuery dslQuery = DSLQueryFactoryUtil.select(
			DSLQueryEntryTable.INSTANCE.name
		).from(
			DSLQueryEntryTable.INSTANCE
		).where(
			DSLQueryEntryTable.INSTANCE.dslQueryEntryId.in(
				DSLQueryFactoryUtil.select(
					DSLQueryLinkEntryTable.INSTANCE.childDSLQueryEntryId
				).from(
					DSLQueryLinkEntryTable.INSTANCE
				).where(
					DSLQueryLinkEntryTable.INSTANCE.parentDSLQueryEntryId.eq(
						_testDSLQueryEntry1.getDslQueryEntryId())
				))
		);

		List<String> result = _dslQueryEntryPersistence.dslQuery(dslQuery);

		_assertResult(
			result, new String[] {_TEST_NAME_1, _TEST_NAME_2, _TEST_NAME_3});

		_testDSLQueryEntry1.setName("update.test.name.1");

		_testDSLQueryEntry1 = _dslQueryEntryPersistence.updateImpl(
			_testDSLQueryEntry1);

		result = _dslQueryEntryPersistence.dslQuery(dslQuery);

		_assertResult(
			result,
			new String[] {"update.test.name.1", _TEST_NAME_2, _TEST_NAME_3});

		dslQueryLinkEntry.setParentDSLQueryEntryId(
			_testDSLQueryEntry2.getDslQueryEntryId());

		_dslQueryLinkEntryPersistence.updateImpl(dslQueryLinkEntry);

		result = _dslQueryEntryPersistence.dslQuery(dslQuery);

		_assertResult(result, new String[] {_TEST_NAME_2, _TEST_NAME_3});
	}

	private void _assertResult(List<String> result, String[] names) {
		for (String name : names) {
			Assert.assertTrue(result.toString(), result.contains(name));
		}

		Assert.assertEquals(result.toString(), names.length, result.size());
	}

	private DSLQueryEntry _createDSLQueryEntry(String name) {
		DSLQueryEntry dslQueryEntry = _dslQueryEntryPersistence.create(
			RandomTestUtil.nextLong());

		dslQueryEntry.setName(name);

		return _dslQueryEntryPersistence.updateImpl(dslQueryEntry);
	}

	private DSLQueryLinkEntry _createDSLQueryLinkEntry(
		long parentDSLQueryEntryId, long childDSLQueryEntryId) {

		DSLQueryLinkEntry dslQueryLinkEntry =
			_dslQueryLinkEntryPersistence.create(RandomTestUtil.nextLong());

		dslQueryLinkEntry.setParentDSLQueryEntryId(parentDSLQueryEntryId);
		dslQueryLinkEntry.setChildDSLQueryEntryId(childDSLQueryEntryId);

		return _dslQueryLinkEntryPersistence.updateImpl(dslQueryLinkEntry);
	}

	private static final String _TEST_NAME_1 = "test.name.1";

	private static final String _TEST_NAME_2 = "test.name.2";

	private static final String _TEST_NAME_3 = "test.name.3";

	@DeleteAfterTestRun
	private final List<DSLQueryEntry> _dslQueryEntries = new ArrayList<>();

	@Inject
	private DSLQueryEntryPersistence _dslQueryEntryPersistence;

	@DeleteAfterTestRun
	private final List<DSLQueryLinkEntry> _dslQueryLinkEntries =
		new ArrayList<>();

	@Inject
	private DSLQueryLinkEntryPersistence _dslQueryLinkEntryPersistence;

	private DSLQueryEntry _testDSLQueryEntry1;
	private DSLQueryEntry _testDSLQueryEntry2;
	private DSLQueryEntry _testDSLQueryEntry3;

}