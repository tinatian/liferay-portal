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
import com.liferay.portal.configuration.metatype.definitions.ExtendedMetaTypeService;
import com.liferay.portal.kernel.util.ResourceBundleLoader;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * @author Lance Ji
 */
@RunWith(Arquillian.class)
public class ConfigurationLocalizationTest {

	@Before
	public void setUp() {
		Bundle bundle = FrameworkUtil.getBundle(
			ConfigurationLocalizationTest.class);

		_bundleContext = bundle.getBundleContext();

		_extendedMetaTypeServiceServiceReference =
			_bundleContext.getServiceReference(ExtendedMetaTypeService.class);

		_extendedMetaTypeService = _bundleContext.getService(
			_extendedMetaTypeServiceServiceReference);

		_resourceBundleLoaders = ServiceTrackerMapFactory.openSingleValueMap(
			_bundleContext, ResourceBundleLoader.class, "bundle.symbolic.name");
	}

	@After
	public void tearDown() {
		_bundleContext.ungetService(_extendedMetaTypeServiceServiceReference);

		_resourceBundleLoaders.close();
	}

	private BundleContext _bundleContext;
	private ExtendedMetaTypeService _extendedMetaTypeService;
	private ServiceReference<ExtendedMetaTypeService>
		_extendedMetaTypeServiceServiceReference;
	private ServiceTrackerMap<String, ResourceBundleLoader>
		_resourceBundleLoaders;

}