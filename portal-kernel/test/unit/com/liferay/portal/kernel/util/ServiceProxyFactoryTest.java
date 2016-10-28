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

package com.liferay.portal.kernel.util;

import com.liferay.registry.BasicRegistryImpl;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Tina Tian
 */
public class ServiceProxyFactoryTest {

	@BeforeClass
	public static void setUpClass() {
		RegistryUtil.setRegistry(new BasicRegistryImpl());
	}

	@Test
	public void testNewServiceTrackedInstanceBlocking() throws Exception {
		final TestService testService =
			TestServiceBlockingClient.blockingTestService;

		Assert.assertTrue(ProxyUtil.isProxyClass(testService.getClass()));
		Assert.assertNotSame(TestServiceImpl.class, testService.getClass());

		Thread thread = new Thread() {

			@Override
			public void run() {
				Assert.assertEquals(
					_TEST_SERVICE_NAME, testService.getTestServiceName());
				Assert.assertEquals(
					_TEST_SERVICE_ID, testService.getTestServiceId());

				TestService newTestService =
					TestServiceBlockingClient.blockingTestService;

				Assert.assertFalse(
					ProxyUtil.isProxyClass(newTestService.getClass()));
				Assert.assertSame(
					TestServiceImpl.class, newTestService.getClass());
			}

		};

		thread.start();

		Registry registry = RegistryUtil.getRegistry();

		ServiceRegistration<TestService> serviceRegistration =
			registry.registerService(TestService.class, new TestServiceImpl());

		thread.join();

		serviceRegistration.unregister();
	}

	@Test
	public void testNewServiceTrackedInstanceNonBlocking() {
		TestService testService =
			TestServiceNonblockingClient.nonblockingTestService;

		Assert.assertTrue(ProxyUtil.isProxyClass(testService.getClass()));
		Assert.assertNotSame(TestServiceImpl.class, testService.getClass());

		Assert.assertEquals(0, testService.getTestServiceId());
		Assert.assertEquals(null, testService.getTestServiceName());

		Registry registry = RegistryUtil.getRegistry();

		ServiceRegistration<TestService> serviceRegistration =
			registry.registerService(TestService.class, new TestServiceImpl());

		TestService newTestService =
			TestServiceBlockingClient.blockingTestService;

		Assert.assertEquals(
			_TEST_SERVICE_NAME, newTestService.getTestServiceName());
		Assert.assertEquals(
			_TEST_SERVICE_ID, newTestService.getTestServiceId());

		Assert.assertFalse(ProxyUtil.isProxyClass(newTestService.getClass()));
		Assert.assertSame(TestServiceImpl.class, newTestService.getClass());

		serviceRegistration.unregister();
	}

	private static final long _TEST_SERVICE_ID = 1234L;

	private static final String _TEST_SERVICE_NAME = "TestServiceName";

	private static class TestServiceBlockingClient {

		public static volatile TestService blockingTestService =
			ServiceProxyFactory.newServiceTrackedInstance(
				TestService.class, TestServiceBlockingClient.class,
				"blockingTestService", true);

	}

	private static class TestServiceNonblockingClient {

		public static volatile TestService nonblockingTestService =
			ServiceProxyFactory.newServiceTrackedInstance(
				TestService.class, TestServiceNonblockingClient.class,
				"nonblockingTestService", false);

	}

	private interface TestService {

		public long getTestServiceId();

		public String getTestServiceName();

	}

	private class TestServiceImpl implements TestService {

		@Override
		public long getTestServiceId() {
			return _TEST_SERVICE_ID;
		}

		@Override
		public String getTestServiceName() {
			return _TEST_SERVICE_NAME;
		}

	}

}