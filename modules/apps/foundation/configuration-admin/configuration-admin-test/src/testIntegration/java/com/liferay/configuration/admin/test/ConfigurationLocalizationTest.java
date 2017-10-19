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
import com.liferay.portal.configuration.metatype.definitions.ExtendedAttributeDefinition;
import com.liferay.portal.configuration.metatype.definitions.ExtendedMetaTypeInformation;
import com.liferay.portal.configuration.metatype.definitions.ExtendedMetaTypeService;
import com.liferay.portal.configuration.metatype.definitions.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ResourceBundleLoader;
import com.liferay.portal.kernel.util.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Ignore;
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

	@Ignore("Waiting for bundle fixes")
	@Test
	public void testBundleLocalizationTest() {
		Bundle currentBundle = FrameworkUtil.getBundle(
			ConfigurationLocalizationTest.class);

		BundleContext bundleContext = currentBundle.getBundleContext();

		Map<String, List<String>> errorMap = new HashMap<>();

		for (Bundle bundle : bundleContext.getBundles()) {
			List<String> errorMessageList = new ArrayList<>();

			String currentBundleSymbolicName = bundle.getSymbolicName();

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
				ResourceBundleLoaderUtil.
					getResourceBundleLoaderByBundleSymbolicName(
						currentBundleSymbolicName);

			if (resourceBundleLoader == null) {
				errorMessageList.add("No resource bundle");
				errorMap.put(currentBundleSymbolicName, errorMessageList);
				continue;
			}

			ResourceBundle resourceBundle =
				resourceBundleLoader.loadResourceBundle(Locale.getDefault());

			ResourceBundle koResourceBundle =
				resourceBundleLoader.loadResourceBundle(Locale.KOREA);

			if (Objects.equals(resourceBundle, koResourceBundle)) {
				errorMessageList.add("Missing generated resource files");
			}

			for (String pid : pids) {
				ExtendedObjectClassDefinition extendedObjectClassDefinition =
					extendedMetaTypeInformation.getObjectClassDefinition(
						pid, Locale.getDefault().getLanguage());

				String objectClassDefinitionName = ResourceBundleUtil.getString(
					resourceBundle, extendedObjectClassDefinition.getName());

				if (objectClassDefinitionName == null) {
					errorMessageList.add(
						"ObjectClassDefinition name value is empty in " +
							"configuration " + pid);
				}

				ExtendedAttributeDefinition[] extendedAttributeDefinitions =
					extendedObjectClassDefinition.getAttributeDefinitions(
						ObjectClassDefinition.ALL);

				for (ExtendedAttributeDefinition extendedAttributeDefinition :
						extendedAttributeDefinitions) {

					String attributeDefinitionName =
						extendedAttributeDefinition.getName();

					String attributeDefinitionString =
						ResourceBundleUtil.getString(
							resourceBundle, attributeDefinitionName);

					if (attributeDefinitionString == null) {
						errorMessageList.add(
							"AttributeDefinition with name: \"" +
								attributeDefinitionName + "\" has no value " +
									"in language.properties file, " +
										"configurationPid: " + pid);
					}
				}
			}

			if (ListUtil.isNotEmpty(errorMessageList)) {
				errorMap.put(currentBundleSymbolicName, errorMessageList);
			}
		}

		if (MapUtil.isNotEmpty(errorMap)) {
			Assert.fail(_printErrorMessage(errorMap));
		}
	}

	private String _printErrorMessage(Map<String, List<String>> errorMap) {
		String errorMessage = "";

		for (String key : errorMap.keySet()) {
			errorMessage += "\nBundle with symbolic name \"" + key + "\":\n";
			List<String> messageList = errorMap.get(key);

			for (String message : messageList) {
				errorMessage += message + "\n";
			}
		}

		return errorMessage;
	}

	@Inject
	private static ExtendedMetaTypeService _extendedMetaTypeService;

}