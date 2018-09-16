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

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;
import com.liferay.portal.kernel.test.util.MockHelperUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.search.web.internal.facet.display.builder.AssetCategoriesSearchFacetDisplayBuilder;
import com.liferay.portal.search.web.internal.facet.display.builder.AssetCategoryPermissionChecker;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author André de Oliveira
 */
public class AssetCategoriesSearchFacetDisplayContextTest {

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		MockHelperUtil.setMethodAlwaysReturnExpected(
			_facet, "getFacetCollector", _facetCollector);
	}

	@Test
	public void testEmptySearchResults() throws Exception {
		String facetParam = StringPool.BLANK;

		AssetCategoriesSearchFacetDisplayContext
			assetCategoriesSearchFacetDisplayContext = createDisplayContext(
				facetParam);

		List<AssetCategoriesSearchFacetTermDisplayContext>
			assetCategoriesSearchFacetTermDisplayContexts =
				assetCategoriesSearchFacetDisplayContext.
					getTermDisplayContexts();

		Assert.assertEquals(
			assetCategoriesSearchFacetTermDisplayContexts.toString(), 0,
			assetCategoriesSearchFacetTermDisplayContexts.size());

		Assert.assertEquals(
			facetParam,
			assetCategoriesSearchFacetDisplayContext.getParameterValue());
		Assert.assertTrue(
			assetCategoriesSearchFacetDisplayContext.isNothingSelected());
		Assert.assertTrue(
			assetCategoriesSearchFacetDisplayContext.isRenderNothing());
	}

	@Test
	public void testEmptySearchResultsWithPreviousSelection() throws Exception {
		long assetCategoryId = RandomTestUtil.randomLong();

		setUpAssetCategory(assetCategoryId);

		String facetParam = String.valueOf(assetCategoryId);

		AssetCategoriesSearchFacetDisplayContext
			assetCategoriesSearchFacetDisplayContext = createDisplayContext(
				facetParam);

		List<AssetCategoriesSearchFacetTermDisplayContext>
			assetCategoriesSearchFacetTermDisplayContexts =
				assetCategoriesSearchFacetDisplayContext.
					getTermDisplayContexts();

		Assert.assertEquals(
			assetCategoriesSearchFacetTermDisplayContexts.toString(), 1,
			assetCategoriesSearchFacetTermDisplayContexts.size());

		AssetCategoriesSearchFacetTermDisplayContext
			assetCategoriesSearchFacetTermDisplayContext =
				assetCategoriesSearchFacetTermDisplayContexts.get(0);

		Assert.assertEquals(
			assetCategoryId,
			assetCategoriesSearchFacetTermDisplayContext.getAssetCategoryId());
		Assert.assertEquals(
			String.valueOf(assetCategoryId),
			assetCategoriesSearchFacetTermDisplayContext.getDisplayName());
		Assert.assertEquals(
			0, assetCategoriesSearchFacetTermDisplayContext.getFrequency());
		Assert.assertTrue(
			assetCategoriesSearchFacetTermDisplayContext.isFrequencyVisible());
		Assert.assertTrue(
			assetCategoriesSearchFacetTermDisplayContext.isSelected());

		Assert.assertEquals(
			facetParam,
			assetCategoriesSearchFacetDisplayContext.getParameterValue());
		Assert.assertFalse(
			assetCategoriesSearchFacetDisplayContext.isNothingSelected());
		Assert.assertFalse(
			assetCategoriesSearchFacetDisplayContext.isRenderNothing());
	}

	@Test
	public void testOneTerm() throws Exception {
		long assetCategoryId = RandomTestUtil.randomLong();

		setUpAssetCategory(assetCategoryId);

		int frequency = RandomTestUtil.randomInt();

		setUpOneTermCollector(assetCategoryId, frequency);

		String facetParam = StringPool.BLANK;

		AssetCategoriesSearchFacetDisplayContext
			assetCategoriesSearchFacetDisplayContext = createDisplayContext(
				facetParam);

		List<AssetCategoriesSearchFacetTermDisplayContext>
			assetCategoriesSearchFacetTermDisplayContexts =
				assetCategoriesSearchFacetDisplayContext.
					getTermDisplayContexts();

		Assert.assertEquals(
			assetCategoriesSearchFacetTermDisplayContexts.toString(), 1,
			assetCategoriesSearchFacetTermDisplayContexts.size());

		AssetCategoriesSearchFacetTermDisplayContext
			assetCategoriesSearchFacetTermDisplayContext =
				assetCategoriesSearchFacetTermDisplayContexts.get(0);

		Assert.assertEquals(
			assetCategoryId,
			assetCategoriesSearchFacetTermDisplayContext.getAssetCategoryId());
		Assert.assertEquals(
			String.valueOf(assetCategoryId),
			assetCategoriesSearchFacetTermDisplayContext.getDisplayName());
		Assert.assertEquals(
			frequency,
			assetCategoriesSearchFacetTermDisplayContext.getFrequency());
		Assert.assertTrue(
			assetCategoriesSearchFacetTermDisplayContext.isFrequencyVisible());
		Assert.assertFalse(
			assetCategoriesSearchFacetTermDisplayContext.isSelected());

		Assert.assertEquals(
			facetParam,
			assetCategoriesSearchFacetDisplayContext.getParameterValue());
		Assert.assertTrue(
			assetCategoriesSearchFacetDisplayContext.isNothingSelected());
		Assert.assertFalse(
			assetCategoriesSearchFacetDisplayContext.isRenderNothing());
	}

	@Test
	public void testOneTermWithPreviousSelection() throws Exception {
		long assetCategoryId = RandomTestUtil.randomLong();

		setUpAssetCategory(assetCategoryId);

		int frequency = RandomTestUtil.randomInt();

		setUpOneTermCollector(assetCategoryId, frequency);

		AssetCategoriesSearchFacetDisplayContext
			assetCategoriesSearchFacetDisplayContext = createDisplayContext(
				String.valueOf(assetCategoryId));

		List<AssetCategoriesSearchFacetTermDisplayContext>
			assetCategoriesSearchFacetTermDisplayContexts =
				assetCategoriesSearchFacetDisplayContext.
					getTermDisplayContexts();

		Assert.assertEquals(
			assetCategoriesSearchFacetTermDisplayContexts.toString(), 1,
			assetCategoriesSearchFacetTermDisplayContexts.size());

		AssetCategoriesSearchFacetTermDisplayContext
			assetCategoriesSearchFacetTermDisplayContext =
				assetCategoriesSearchFacetTermDisplayContexts.get(0);

		Assert.assertEquals(
			assetCategoryId,
			assetCategoriesSearchFacetTermDisplayContext.getAssetCategoryId());
		Assert.assertEquals(
			String.valueOf(assetCategoryId),
			assetCategoriesSearchFacetTermDisplayContext.getDisplayName());
		Assert.assertEquals(
			frequency,
			assetCategoriesSearchFacetTermDisplayContext.getFrequency());
		Assert.assertTrue(
			assetCategoriesSearchFacetTermDisplayContext.isFrequencyVisible());
		Assert.assertTrue(
			assetCategoriesSearchFacetTermDisplayContext.isSelected());

		Assert.assertEquals(
			assetCategoryId,
			GetterUtil.getLong(
				assetCategoriesSearchFacetDisplayContext.getParameterValue()));
		Assert.assertFalse(
			assetCategoriesSearchFacetDisplayContext.isNothingSelected());
		Assert.assertFalse(
			assetCategoriesSearchFacetDisplayContext.isRenderNothing());
	}

	@Test
	public void testUnauthorized() throws Exception {
		long assetCategoryId = RandomTestUtil.randomLong();

		setUpAssetCategoryUnauthorized(assetCategoryId);

		int frequency = RandomTestUtil.randomInt();

		setUpOneTermCollector(assetCategoryId, frequency);

		String facetParam = StringPool.BLANK;

		AssetCategoriesSearchFacetDisplayContext
			assetCategoriesSearchFacetDisplayContext = createDisplayContext(
				facetParam);

		List<AssetCategoriesSearchFacetTermDisplayContext>
			assetCategoriesSearchFacetTermDisplayContexts =
				assetCategoriesSearchFacetDisplayContext.
					getTermDisplayContexts();

		Assert.assertEquals(
			assetCategoriesSearchFacetTermDisplayContexts.toString(), 0,
			assetCategoriesSearchFacetTermDisplayContexts.size());

		Assert.assertEquals(
			facetParam,
			assetCategoriesSearchFacetDisplayContext.getParameterValue());
		Assert.assertTrue(
			assetCategoriesSearchFacetDisplayContext.isNothingSelected());
		Assert.assertTrue(
			assetCategoriesSearchFacetDisplayContext.isRenderNothing());
	}

	@Test
	public void testUnauthorizedWithPreviousSelection() throws Exception {
		long assetCategoryId = RandomTestUtil.randomLong();

		setUpAssetCategoryUnauthorized(assetCategoryId);

		String facetParam = String.valueOf(assetCategoryId);

		AssetCategoriesSearchFacetDisplayContext
			assetCategoriesSearchFacetDisplayContext = createDisplayContext(
				facetParam);

		List<AssetCategoriesSearchFacetTermDisplayContext>
			assetCategoriesSearchFacetTermDisplayContexts =
				assetCategoriesSearchFacetDisplayContext.
					getTermDisplayContexts();

		Assert.assertEquals(
			assetCategoriesSearchFacetTermDisplayContexts.toString(), 0,
			assetCategoriesSearchFacetTermDisplayContexts.size());

		Assert.assertEquals(
			facetParam,
			assetCategoriesSearchFacetDisplayContext.getParameterValue());
		Assert.assertFalse(
			assetCategoriesSearchFacetDisplayContext.isNothingSelected());
		Assert.assertFalse(
			assetCategoriesSearchFacetDisplayContext.isRenderNothing());
	}

	protected AssetCategory createAssetCategory(long assetCategoryId)
		throws Exception {

		AssetCategory assetCategory =
			MockHelperUtil.setMethodAlwaysReturnExpected(
				AssetCategory.class, "getCategoryId", assetCategoryId);

		MockHelperUtil.setMethodAlwaysReturnExpected(
			assetCategory, "getTitle", String.valueOf(assetCategoryId),
			Locale.class);

		Mockito.doReturn(
			assetCategory
		).when(
			_assetCategoryLocalService
		).fetchAssetCategory(
			assetCategoryId
		);

		return assetCategory;
	}

	protected AssetCategoriesSearchFacetDisplayContext createDisplayContext(
		String parameterValue) {

		AssetCategoriesSearchFacetDisplayBuilder
			assetCategoriesSearchFacetDisplayBuilder =
				new AssetCategoriesSearchFacetDisplayBuilder();

		assetCategoriesSearchFacetDisplayBuilder.setAssetCategoryLocalService(
			_assetCategoryLocalService);
		assetCategoriesSearchFacetDisplayBuilder.
			setAssetCategoryPermissionChecker(_assetCategoryPermissionChecker);
		assetCategoriesSearchFacetDisplayBuilder.setDisplayStyle("cloud");
		assetCategoriesSearchFacetDisplayBuilder.setFacet(_facet);
		assetCategoriesSearchFacetDisplayBuilder.setFrequenciesVisible(true);
		assetCategoriesSearchFacetDisplayBuilder.setFrequencyThreshold(0);
		assetCategoriesSearchFacetDisplayBuilder.setMaxTerms(0);
		assetCategoriesSearchFacetDisplayBuilder.setParameterName(
			_facet.getFieldId());
		assetCategoriesSearchFacetDisplayBuilder.setParameterValue(
			parameterValue);

		AssetCategoriesSearchFacetDisplayContext
			assetCategoriesSearchFacetDisplayContext =
				assetCategoriesSearchFacetDisplayBuilder.build();

		return assetCategoriesSearchFacetDisplayContext;
	}

	protected TermCollector createTermCollector(
			long assetCategoryId, int frequency)
		throws Exception {

		TermCollector termCollector =
			MockHelperUtil.setMethodAlwaysReturnExpected(
				TermCollector.class, "getFrequency", frequency);

		MockHelperUtil.setMethodAlwaysReturnExpected(
			termCollector, "getTerm", String.valueOf(assetCategoryId));

		return termCollector;
	}

	protected void setUpAssetCategory(long assetCategoryId) throws Exception {
		AssetCategory assetCategory = createAssetCategory(assetCategoryId);

		Mockito.doReturn(
			true
		).when(
			_assetCategoryPermissionChecker
		).hasPermission(
			assetCategory
		);
	}

	protected void setUpAssetCategoryUnauthorized(long assetCategoryId)
		throws Exception {

		AssetCategory assetCategory = createAssetCategory(assetCategoryId);

		Mockito.doReturn(
			false
		).when(
			_assetCategoryPermissionChecker
		).hasPermission(
			assetCategory
		);
	}

	protected void setUpOneTermCollector(long assetCategoryId, int frequency)
		throws Exception {

		MockHelperUtil.setMethodAlwaysReturnExpected(
			_facetCollector, "getTermCollectors",
			Collections.singletonList(
				createTermCollector(assetCategoryId, frequency)));
	}

	@Mock
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Mock
	private AssetCategoryPermissionChecker _assetCategoryPermissionChecker;

	private final Facet _facet = MockHelperUtil.initMock(Facet.class);
	private final FacetCollector _facetCollector = MockHelperUtil.initMock(
		FacetCollector.class);

}