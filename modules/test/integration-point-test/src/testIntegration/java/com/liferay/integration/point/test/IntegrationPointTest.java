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

package com.liferay.integration.point.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceReference;
import com.liferay.registry.ServiceTracker;
import com.liferay.registry.ServiceTrackerCustomizer;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Leon Chi
 */
@RunWith(Arquillian.class)
public class IntegrationPointTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testIntegrationPoint() {
		Registry registry = RegistryUtil.getRegistry();

		_testIntegrationPointWithServiceTracker(
			registry.trackServices(IntegrationPoint.class));

		_testIntegrationPointWithServiceTracker(
			registry.trackServices(
				IntegrationPoint.class,
				new IntegrationPointTrackerCustomizer()));
	}

	private void _testIntegrationPointWithServiceTracker(
		ServiceTracker<IntegrationPoint, IntegrationPoint> serviceTracker) {

		serviceTracker.open();

		try {
			Assert.assertSame(
				_expectedIntegrationPoint, serviceTracker.getService());
		}
		finally {
			serviceTracker.close();
		}
	}

	@Inject
	private IntegrationPoint _expectedIntegrationPoint;

	private static class IntegrationPointTrackerCustomizer
		implements ServiceTrackerCustomizer
			<IntegrationPoint, IntegrationPoint> {

		@Override
		public IntegrationPoint addingService(
			ServiceReference<IntegrationPoint> serviceReference) {

			Registry registry = RegistryUtil.getRegistry();

			return registry.getService(serviceReference);
		}

		@Override
		public void modifiedService(
			ServiceReference<IntegrationPoint> serviceReference,
			IntegrationPoint integrationPoint) {
		}

		@Override
		public void removedService(
			ServiceReference<IntegrationPoint> serviceReference,
			IntegrationPoint integrationPoint) {

			Registry registry = RegistryUtil.getRegistry();

			registry.ungetService(serviceReference);
		}

	}

}