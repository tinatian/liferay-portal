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

package com.liferay.portal.kernel.settings;

import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.resource.ResourceRetriever;
import com.liferay.portal.kernel.resource.manager.ResourceManager;
import com.liferay.portal.kernel.test.ProxyTestUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Iván Zaera
 */
public class ConfigurationBeanSettingsTest {

	@Before
	public void setUp() {
		_configurationBean = new ConfigurationBean();

		_mockLocationVariableResolver = new LocationVariableResolver(
			null, (SettingsLocatorHelper)null);

		_configurationBeanSettings = new ConfigurationBeanSettings(
			_mockLocationVariableResolver, _configurationBean, null);
	}

	@Test
	public void testGetValuesWithExistingKey() {
		Assert.assertArrayEquals(
			_configurationBean.stringArrayValue(),
			_configurationBeanSettings.getValues(
				"stringArrayValue", new String[] {"defaultValue"}));
	}

	@Test
	public void testGetValuesWithMissingKey() {
		String[] defaultValue = {"defaultValue"};

		Assert.assertArrayEquals(
			defaultValue,
			_configurationBeanSettings.getValues("missingKey", defaultValue));
	}

	@Test
	public void testGetValueWithExistingBooleanValue() {
		Assert.assertEquals(
			String.valueOf(_configurationBean.booleanValue()),
			_configurationBeanSettings.getValue(
				"booleanValue", "defaultValue"));
	}

	@Test
	public void testGetValueWithExistingLocalizedValuesMapValue() {
		LocalizedValuesMap localizedValuesMap =
			_configurationBean.localizedValuesMap();

		Assert.assertEquals(
			localizedValuesMap.getDefaultValue(),
			_configurationBeanSettings.getValue("localizedValuesMap", null));
	}

	@Test
	public void testGetValueWithExistingStringValue() {
		Assert.assertEquals(
			_configurationBean.stringValue(),
			_configurationBeanSettings.getValue("stringValue", "defaultValue"));
	}

	@Test
	public void testGetValueWithLocationVariable() {
		String expectedValue = "Once upon a time...";

		ReflectionTestUtil.setFieldValue(
			_mockLocationVariableResolver, "_resourceManager",
			ProxyTestUtil.getProxy(
				ResourceManager.class,
				ProxyTestUtil.getProxyMethod(
					"getResourceRetriever",
					(Object[] arguments) -> {
						if ("template.ftl".equals(arguments[0])) {
							return ProxyTestUtil.getProxy(
								ResourceRetriever.class,
								ProxyTestUtil.getProxyMethod(
									"getInputStream",
									(Object[] args) ->
										new UnsyncByteArrayInputStream(
											expectedValue.getBytes())));
						}

						return null;
					})));

		Assert.assertEquals(
			expectedValue,
			_configurationBeanSettings.getValue(
				"locationVariableValue", "defaultValue"));
	}

	@Test
	public void testGetValueWithMissingKey() {
		Assert.assertEquals(
			"defaultValue",
			_configurationBeanSettings.getValue("missingKey", "defaultValue"));
	}

	@Test
	public void testGetValueWithNullConfigurationBean() {
		try {
			_configurationBeanSettings = new ConfigurationBeanSettings(
				_mockLocationVariableResolver, null, null);

			Assert.fail();
		}
		catch (NullPointerException npe) {
			Assert.assertEquals("Configuration bean is null", npe.getMessage());
		}
	}

	@Test
	public void testGetValueWithNullLocationVariableResolver() {
		try {
			_configurationBeanSettings = new ConfigurationBeanSettings(
				null, null, null);

			Assert.fail();
		}
		catch (NullPointerException npe) {
			Assert.assertEquals(
				"Location variable resolver is null", npe.getMessage());
		}
	}

	private ConfigurationBean _configurationBean;
	private ConfigurationBeanSettings _configurationBeanSettings;
	private LocationVariableResolver _mockLocationVariableResolver;

	private static class ConfigurationBean {

		public boolean booleanValue() {
			return true;
		}

		public LocalizedValuesMap localizedValuesMap() {
			return new LocalizedValuesMap("default localized value");
		}

		public String locationVariableValue() {
			return "${resource:template.ftl}";
		}

		public String[] stringArrayValue() {
			return new String[] {
				"string value 0", "string value 1", "string value 2"
			};
		}

		public String stringValue() {
			return "a string value";
		}

	}

}