/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.model.UniqueIndexWithLongTypeEntry;
import com.liferay.portal.tools.service.builder.test.service.persistence.UniqueIndexWithLongTypeEntryPersistence;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tina Tian
 */
@RunWith(Arquillian.class)
public class UniqueIndexWithLongTypeEntryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.tools.service.builder.test.service"));

	@Test
	public void test() {
		String name = RandomTestUtil.randomString();

		UniqueIndexWithLongTypeEntry uniqueIndexWithLongTypeEntry1 = _add(name);

		Assert.assertEquals(name, uniqueIndexWithLongTypeEntry1.getName());
		Assert.assertNull(uniqueIndexWithLongTypeEntry1.getLongTypeId());

		UniqueIndexWithLongTypeEntry uniqueIndexWithLongTypeEntry2 = _add(name);

		Assert.assertEquals(name, uniqueIndexWithLongTypeEntry2.getName());
		Assert.assertNull(uniqueIndexWithLongTypeEntry2.getLongTypeId());

		uniqueIndexWithLongTypeEntry1.setLongTypeId(1L);

		uniqueIndexWithLongTypeEntry1 = _update(uniqueIndexWithLongTypeEntry1);

		Assert.assertEquals(name, uniqueIndexWithLongTypeEntry1.getName());
		Assert.assertEquals(
			Long.valueOf(1L), uniqueIndexWithLongTypeEntry1.getLongTypeId());

		uniqueIndexWithLongTypeEntry2.setLongTypeId(1L);

		try (LogCapture logCapture1 = LoggerTestUtil.configureLog4JLogger(
				"org.hibernate.engine.jdbc.spi.SqlExceptionHelper",
				LoggerTestUtil.OFF);
			 LogCapture logCapture2 = LoggerTestUtil.configureLog4JLogger(
				 "org.hibernate.engine.jdbc.batch.internal.BatchingBatch",
				 LoggerTestUtil.OFF)) {

			_update(uniqueIndexWithLongTypeEntry2);
		}
		catch (Exception exception) {
			Assert.assertEquals(
				"jakarta.persistence.PersistenceException: org.hibernate." +
					"exception.ConstraintViolationException: could not " +
						"execute batch",
				exception.getMessage());
		}
	}

	private UniqueIndexWithLongTypeEntry _add(String name) {
		Session session =
			_uniqueIndexWithLongTypeEntryPersistence.openSession();

		try {
			UniqueIndexWithLongTypeEntry uniqueIndexWithLongTypeEntry1 =
				_uniqueIndexWithLongTypeEntryPersistence.create(
					RandomTestUtil.randomLong());

			uniqueIndexWithLongTypeEntry1.setName(name);

			return _uniqueIndexWithLongTypeEntryPersistence.update(
				uniqueIndexWithLongTypeEntry1);
		}
		finally {
			session.flush();
		}
	}

	private UniqueIndexWithLongTypeEntry _update(
		UniqueIndexWithLongTypeEntry uniqueIndexWithLongTypeEntry) {

		Session session =
			_uniqueIndexWithLongTypeEntryPersistence.openSession();

		try {
			return _uniqueIndexWithLongTypeEntryPersistence.update(
				uniqueIndexWithLongTypeEntry);
		}
		finally {
			session.flush();
		}
	}

	@Inject
	private UniqueIndexWithLongTypeEntryPersistence
		_uniqueIndexWithLongTypeEntryPersistence;

}