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

package com.liferay.portal.template.freemarker.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;

import freemarker.core.TemplateClassResolver;

import freemarker.template.TemplateException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.util.Dictionary;
import java.util.Hashtable;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Tomas Polesovsky
 * @author Manuel de la Peña
 */
@RunWith(Arquillian.class)
public class LiferayTemplateClassResolverTest {

	@BeforeClass
	public static void setUpClass() throws ClassNotFoundException {
		Bundle bundle = FrameworkUtil.getBundle(
			LiferayTemplateClassResolverTest.class);

		_serviceTracker = new ServiceTracker<>(
			bundle.getBundleContext(), TemplateClassResolver.class, null);

		_serviceTracker.open();

		_liferayTemplateClassResolver = _serviceTracker.getService();

		_freeMarkerEngineConfigurationInvocationHandler =
			new FreeMarkerEngineConfigurationInvocationHandler();

		ClassLoader classLoader =
			_liferayTemplateClassResolver.getClass().getClassLoader();

		Class<?> clazz = classLoader.loadClass(
			"com.liferay.portal.template.freemarker.configuration." +
				"FreeMarkerEngineConfiguration");

		ReflectionTestUtil.setFieldValue(
			_liferayTemplateClassResolver, "_freemarkerEngineConfiguration",
			ProxyUtil.newProxyInstance(
				_liferayTemplateClassResolver.getClass().getClassLoader(),
				new Class<?>[] {clazz},
				_freeMarkerEngineConfigurationInvocationHandler));
	}

	@AfterClass
	public static void tearDownClass() {
		ReflectionTestUtil.setFieldValue(
			_liferayTemplateClassResolver, "_freemarkerEngineConfiguration",
			_freeMarkerEngineConfigurationInvocationHandler.
				getFreeMarkerEngineConfiguration());

		_serviceTracker.close();
	}

	@Before
	public void setUp() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			_liferayTemplateClassResolver.getClass());

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceReference<ConfigurationAdmin> serviceReference =
			bundleContext.getServiceReference(ConfigurationAdmin.class);

		try {
			ConfigurationAdmin configurationAdmin = bundleContext.getService(
				serviceReference);

			_freemarkerTemplateConfiguration =
				configurationAdmin.getConfiguration(
					"com.liferay.portal.template.freemarker.configuration." +
						"FreeMarkerEngineConfiguration",
					null);

			_properties = _freemarkerTemplateConfiguration.getProperties();
		}
		finally {
			bundleContext.ungetService(serviceReference);
		}
	}

	@After
	public void tearDown() throws Exception {
		_freeMarkerEngineConfigurationInvocationHandler.setProperties(null);
	}

	@Test
	public void testResolveAllowedClassByClassName() throws Exception {
		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put(
			"allowedClasses", "freemarker.template.utility.ClassUtil");
		properties.put("restrictedClasses", "");

		_freeMarkerEngineConfigurationInvocationHandler.setProperties(
			properties);

		_liferayTemplateClassResolver.resolve(
			"freemarker.template.utility.ClassUtil", null, null);
	}

	@Test
	public void testResolveAllowedClassByStar() throws Exception {
		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put("allowedClasses", "freemarker.template.utility.*");
		properties.put("restrictedClasses", "");

		_freeMarkerEngineConfigurationInvocationHandler.setProperties(
			properties);

		_liferayTemplateClassResolver.resolve(
			"freemarker.template.utility.ClassUtil", null, null);
	}

	@Test(expected = TemplateException.class)
	public void testResolveAllowedExecuteClass() throws Exception {
		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put("allowedClasses", "freemarker.template.utility.*");
		properties.put("restrictedClasses", "");

		_freeMarkerEngineConfigurationInvocationHandler.setProperties(
			properties);

		_liferayTemplateClassResolver.resolve(
			"freemarker.template.utility.Execute", null, null);
	}

	@Test(expected = TemplateException.class)
	public void testResolveAllowedObjectConstructorClass() throws Exception {
		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put("allowedClasses", "freemarker.template.utility.*");
		properties.put("restrictedClasses", "");

		_freeMarkerEngineConfigurationInvocationHandler.setProperties(
			properties);

		_liferayTemplateClassResolver.resolve(
			"freemarker.template.utility.ObjectConstructor", null, null);
	}

	@Test
	public void testResolveAllowedPortalClass() throws Exception {
		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put(
			"allowedClasses", "com.liferay.portal.kernel.model.User");

		_freeMarkerEngineConfigurationInvocationHandler.setProperties(
			properties);

		_liferayTemplateClassResolver.resolve(
			"com.liferay.portal.kernel.model.User", null, null);
	}

	@Test(expected = TemplateException.class)
	public void testResolveAllowedPortalClassExplicitlyRestricted()
		throws Exception {

		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put(
			"allowedClasses", "com.liferay.portal.kernel.model.User");
		properties.put(
			"restrictedClasses", "com.liferay.portal.kernel.model.*");

		_freeMarkerEngineConfigurationInvocationHandler.setProperties(
			properties);

		_liferayTemplateClassResolver.resolve(
			"com.liferay.portal.kernel.model.User", null, null);
	}

	@Test(expected = TemplateException.class)
	public void testResolveClassClass() throws Exception {
		_liferayTemplateClassResolver.resolve("java.lang.Class", null, null);
	}

	@Test(expected = TemplateException.class)
	public void testResolveClassLoaderClass() throws Exception {
		_liferayTemplateClassResolver.resolve(
			"java.lang.ClassLoader", null, null);
	}

	@Test(expected = TemplateException.class)
	public void testResolveExecuteClass() throws Exception {
		_liferayTemplateClassResolver.resolve(
			"freemarker.template.utility.Execute", null, null);
	}

	@Test(expected = TemplateException.class)
	public void testResolveNotAllowedPortalClass() throws Exception {
		_liferayTemplateClassResolver.resolve(
			"com.liferay.portal.kernel.model.User", null, null);
	}

	@Test(expected = TemplateException.class)
	public void testResolveObjectConstructorClass() throws Exception {
		_liferayTemplateClassResolver.resolve(
			"freemarker.template.utility.ObjectConstructor", null, null);
	}

	@Test(expected = TemplateException.class)
	public void testResolveThreadClass() throws Exception {
		_liferayTemplateClassResolver.resolve("java.lang.Thread", null, null);
	}

	private static FreeMarkerEngineConfigurationInvocationHandler
		_freeMarkerEngineConfigurationInvocationHandler;
	private static TemplateClassResolver _liferayTemplateClassResolver;
	private static ServiceTracker<TemplateClassResolver, TemplateClassResolver>
		_serviceTracker;

	private Configuration _freemarkerTemplateConfiguration;
	private Dictionary<String, Object> _properties;

	private static class FreeMarkerEngineConfigurationInvocationHandler
		implements InvocationHandler {

		public Object getFreeMarkerEngineConfiguration() {
			return _freeMarkerEngineConfiguration;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

			if (_properties != null) {
				String propertyName = method.getName();

				Object value = _properties.get(propertyName);

				if (value != null) {
					if (propertyName.equals("allowedClasses") ||
						propertyName.equals("restrictedClasses")) {

						return StringUtil.split((String)value, '|');
					}

					return value;
				}
			}

			return method.invoke(_freeMarkerEngineConfiguration, args);
		}

		public void setProperties(Dictionary<String, Object> properties) {
			_properties = properties;
		}

		private static final Object _freeMarkerEngineConfiguration =
			ReflectionTestUtil.getFieldValue(
				_liferayTemplateClassResolver,
				"_freemarkerEngineConfiguration");

		private Dictionary<String, Object> _properties;

	}

}