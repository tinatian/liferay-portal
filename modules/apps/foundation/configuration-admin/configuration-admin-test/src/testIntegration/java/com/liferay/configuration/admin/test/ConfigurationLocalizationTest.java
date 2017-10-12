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

package com.liferay.configuration.admin.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.configuration.metatype.definitions.ExtendedAttributeDefinition;
import com.liferay.portal.configuration.metatype.definitions.ExtendedMetaTypeInformation;
import com.liferay.portal.configuration.metatype.definitions.ExtendedMetaTypeService;
import com.liferay.portal.configuration.metatype.definitions.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.ResourceBundleLoader;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.metatype.ObjectClassDefinition;

/**
 * @author Lance Ji
 */
@RunWith(Arquillian.class)
public class ConfigurationLocalizationTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		Bundle bundle = FrameworkUtil.getBundle(
			ConfigurationLocalizationTest.class);

		_bundleContext = bundle.getBundleContext();

		_resourceBundleLoaders = ServiceTrackerMapFactory.openSingleValueMap(
			_bundleContext, ResourceBundleLoader.class, "bundle.symbolic.name");
	}

	@After
	public void tearDown() {
		_resourceBundleLoaders.close();
	}

	@Test
	public void testBundleLocalizationTest() {
		Bundle[] bundles = _bundleContext.getBundles();

		for (Bundle bundle : bundles) {
			ExtendedMetaTypeInformation extendedMetaTypeInformation =
				_extendedMetaTypeService.getMetaTypeInformation(bundle);

			List<String> pids = new ArrayList<>();

			Collections.addAll(
				pids, extendedMetaTypeInformation.getFactoryPids());

			Collections.addAll(pids, extendedMetaTypeInformation.getPids());

			if (pids.isEmpty()) {
				continue;
			}

			ResourceBundleLoader resourceBundleLoader =
				_resourceBundleLoaders.getService(bundle.getSymbolicName());

			Assert.assertNotNull(resourceBundleLoader);

			ResourceBundle resourceBundle =
				resourceBundleLoader.loadResourceBundle(new Locale("en"));

			for (String pid : pids) {
				ExtendedObjectClassDefinition extendedObjectClassDefinition =
					extendedMetaTypeInformation.getObjectClassDefinition(
						pid, "en");

				Assert.assertNotNull(
					ResourceBundleUtil.getString(
						resourceBundle,
						extendedObjectClassDefinition.getName()));

				ExtendedAttributeDefinition[] extendedAttributeDefinitions =
					extendedObjectClassDefinition.getAttributeDefinitions(
						ObjectClassDefinition.ALL);

				for (ExtendedAttributeDefinition ead :
						extendedAttributeDefinitions) {

					Assert.assertNotNull(
						ResourceBundleUtil.getString(
							resourceBundle, ead.getName()));
				}
			}
		}
	}

	@Inject
	private static ExtendedMetaTypeService _extendedMetaTypeService;

	private BundleContext _bundleContext;
	private ServiceTrackerMap<String, ResourceBundleLoader>
		_resourceBundleLoaders;

}