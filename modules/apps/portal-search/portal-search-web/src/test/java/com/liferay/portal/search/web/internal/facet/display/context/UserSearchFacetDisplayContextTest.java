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

package com.liferay.portal.search.web.internal.facet.display.context;

import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;
import com.liferay.portal.kernel.test.util.MockHelperUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.search.web.internal.facet.display.builder.UserSearchFacetDisplayBuilder;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Lino Alves
 */
public class UserSearchFacetDisplayContextTest {

	@Before
	public void setUp() throws Exception {
		MockHelperUtil.setMethodAlwaysReturnExpected(
			_facet, "getFacetCollector", _facetCollector);
	}

	@Test
	public void testEmptySearchResults() throws Exception {
		String paramValue = "";

		UserSearchFacetDisplayContext userSearchFacetDisplayContext =
			createDisplayContext(paramValue);

		List<UserSearchFacetTermDisplayContext>
			userSearchFacetTermDisplayContexts =
				userSearchFacetDisplayContext.getTermDisplayContexts();

		Assert.assertEquals(
			userSearchFacetTermDisplayContexts.toString(), 0,
			userSearchFacetTermDisplayContexts.size());

		Assert.assertEquals(
			paramValue, userSearchFacetDisplayContext.getParamValue());
		Assert.assertTrue(userSearchFacetDisplayContext.isNothingSelected());
		Assert.assertTrue(userSearchFacetDisplayContext.isRenderNothing());
	}

	@Test
	public void testEmptySearchResultsWithPreviousSelection() throws Exception {
		String userName = RandomTestUtil.randomString();

		String paramValue = userName;

		UserSearchFacetDisplayContext userSearchFacetDisplayContext =
			createDisplayContext(paramValue);

		List<UserSearchFacetTermDisplayContext>
			userSearchFacetTermDisplayContexts =
				userSearchFacetDisplayContext.getTermDisplayContexts();

		Assert.assertEquals(
			userSearchFacetTermDisplayContexts.toString(), 1,
			userSearchFacetTermDisplayContexts.size());

		UserSearchFacetTermDisplayContext userSearchFacetTermDisplayContext =
			userSearchFacetTermDisplayContexts.get(0);

		Assert.assertEquals(
			0, userSearchFacetTermDisplayContext.getFrequency());
		Assert.assertEquals(
			userName, userSearchFacetTermDisplayContext.getUserName());
		Assert.assertTrue(userSearchFacetTermDisplayContext.isSelected());
		Assert.assertTrue(
			userSearchFacetTermDisplayContext.isFrequencyVisible());

		Assert.assertEquals(
			paramValue, userSearchFacetDisplayContext.getParamValue());
		Assert.assertFalse(userSearchFacetDisplayContext.isNothingSelected());
		Assert.assertFalse(userSearchFacetDisplayContext.isRenderNothing());
	}

	@Test
	public void testOneTerm() throws Exception {
		String userName = RandomTestUtil.randomString();

		int count = RandomTestUtil.randomInt();

		setUpOneTermCollector(userName, count);

		String paramValue = "";

		UserSearchFacetDisplayContext userSearchFacetDisplayContext =
			createDisplayContext(paramValue);

		List<UserSearchFacetTermDisplayContext>
			userSearchFacetTermDisplayContexts =
				userSearchFacetDisplayContext.getTermDisplayContexts();

		Assert.assertEquals(
			userSearchFacetTermDisplayContexts.toString(), 1,
			userSearchFacetTermDisplayContexts.size());

		UserSearchFacetTermDisplayContext userSearchFacetTermDisplayContext =
			userSearchFacetTermDisplayContexts.get(0);

		Assert.assertEquals(
			count, userSearchFacetTermDisplayContext.getFrequency());
		Assert.assertEquals(
			userName, userSearchFacetTermDisplayContext.getUserName());
		Assert.assertFalse(userSearchFacetTermDisplayContext.isSelected());
		Assert.assertTrue(
			userSearchFacetTermDisplayContext.isFrequencyVisible());

		Assert.assertEquals(
			paramValue, userSearchFacetDisplayContext.getParamValue());
		Assert.assertTrue(userSearchFacetDisplayContext.isNothingSelected());
		Assert.assertFalse(userSearchFacetDisplayContext.isRenderNothing());
	}

	@Test
	public void testOneTermWithPreviousSelection() throws Exception {
		String userName = RandomTestUtil.randomString();

		int count = RandomTestUtil.randomInt();

		setUpOneTermCollector(userName, count);

		String paramValue = userName;

		UserSearchFacetDisplayContext userSearchFacetDisplayContext =
			createDisplayContext(paramValue);

		List<UserSearchFacetTermDisplayContext>
			userSearchFacetTermDisplayContexts =
				userSearchFacetDisplayContext.getTermDisplayContexts();

		Assert.assertEquals(
			userSearchFacetTermDisplayContexts.toString(), 1,
			userSearchFacetTermDisplayContexts.size());

		UserSearchFacetTermDisplayContext userSearchFacetTermDisplayContext =
			userSearchFacetTermDisplayContexts.get(0);

		Assert.assertEquals(
			count, userSearchFacetTermDisplayContext.getFrequency());
		Assert.assertEquals(
			userName, userSearchFacetTermDisplayContext.getUserName());
		Assert.assertTrue(userSearchFacetTermDisplayContext.isSelected());
		Assert.assertTrue(
			userSearchFacetTermDisplayContext.isFrequencyVisible());

		Assert.assertEquals(
			paramValue, userSearchFacetDisplayContext.getParamValue());
		Assert.assertFalse(userSearchFacetDisplayContext.isNothingSelected());
		Assert.assertFalse(userSearchFacetDisplayContext.isRenderNothing());
	}

	protected UserSearchFacetDisplayContext createDisplayContext(
			String paramValue)
		throws Exception {

		UserSearchFacetDisplayBuilder userSearchFacetDisplayBuilder =
			new UserSearchFacetDisplayBuilder();

		userSearchFacetDisplayBuilder.setFacet(_facet);
		userSearchFacetDisplayBuilder.setParamValue(paramValue);
		userSearchFacetDisplayBuilder.setFrequenciesVisible(true);
		userSearchFacetDisplayBuilder.setFrequencyThreshold(0);
		userSearchFacetDisplayBuilder.setMaxTerms(0);

		return userSearchFacetDisplayBuilder.build();
	}

	protected TermCollector createTermCollector(String userName, int count)
		throws Exception {

		TermCollector termCollector =
			MockHelperUtil.setMethodAlwaysReturnExpected(
				TermCollector.class, "getFrequency", count);

		MockHelperUtil.setMethodAlwaysReturnExpected(
			termCollector, "getTerm", userName);

		return termCollector;
	}

	protected void setUpOneTermCollector(String userName, int count)
		throws Exception {

		MockHelperUtil.setMethodAlwaysReturnExpected(
			_facetCollector, "getTermCollectors",
			Collections.singletonList(createTermCollector(userName, count)));
	}

	private final Facet _facet = MockHelperUtil.initMock(Facet.class);
	private final FacetCollector _facetCollector = MockHelperUtil.initMock(
		FacetCollector.class);

}