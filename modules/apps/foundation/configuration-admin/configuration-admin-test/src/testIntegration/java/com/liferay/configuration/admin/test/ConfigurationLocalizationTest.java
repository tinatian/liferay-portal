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
import com.liferay.portal.kernel.util.ResourceBundleLoader;
import com.liferay.portal.kernel.util.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

import org.junit.Assert;
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

	@Test
	public void testBundleLocalizationTest() {
		Bundle currentBundle = FrameworkUtil.getBundle(
			ConfigurationLocalizationTest.class);

		BundleContext bundleContext = currentBundle.getBundleContext();

		StringBuilder errorMessageBuilder = new StringBuilder();

		for (Bundle bundle : bundleContext.getBundles()) {
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

			StringBuilder bundleErrorBuilder = new StringBuilder();

			ResourceBundleLoader resourceBundleLoader =
				ResourceBundleLoaderUtil.
					getResourceBundleLoaderByBundleSymbolicName(
						currentBundleSymbolicName);

			if (resourceBundleLoader == null) {
				bundleErrorBuilder.append("\n\tResource Bundle Error:");
				bundleErrorBuilder.append("\n\t\tNo resource bundle");
				_printBundleError(
					bundle, errorMessageBuilder, bundleErrorBuilder);

				continue;
			}

			ResourceBundle resourceBundle =
				resourceBundleLoader.loadResourceBundle(Locale.getDefault());

			ResourceBundle koResourceBundle =
				resourceBundleLoader.loadResourceBundle(Locale.KOREA);

			if (Objects.equals(resourceBundle, koResourceBundle)) {
				bundleErrorBuilder.append("\n\tResource Bundle Error:");

				bundleErrorBuilder.append(
					"\n\t\tMissing generated resource files");
			}

			for (String pid : pids) {
				String metaInfoErrorMessage = _collectMetaInfoError(
					pid, extendedMetaTypeInformation, resourceBundle);

				if (!metaInfoErrorMessage.isEmpty()) {
					bundleErrorBuilder.append("\n\tConfiguration {pid:");
					bundleErrorBuilder.append(pid);
					bundleErrorBuilder.append(", missingLocalization:");
					bundleErrorBuilder.append(metaInfoErrorMessage);
					bundleErrorBuilder.append("\n\t}");
				}
			}

			_printBundleError(bundle, errorMessageBuilder, bundleErrorBuilder);
		}

		if (errorMessageBuilder.length() > 0) {
			Assert.fail(errorMessageBuilder.toString());
		}
	}

	private String _collectMetaInfoError(
		String pid, ExtendedMetaTypeInformation extendedMetaTypeInformation,
		ResourceBundle resourceBundle) {

		StringBuilder metaInfoErrorBuilder = new StringBuilder();

		ExtendedObjectClassDefinition extendedObjectClassDefinition =
			extendedMetaTypeInformation.getObjectClassDefinition(
				pid, Locale.getDefault().getLanguage());

		String objectClassDefinitionName = ResourceBundleUtil.getString(
			resourceBundle, extendedObjectClassDefinition.getName());

		if (objectClassDefinitionName == null) {
			metaInfoErrorBuilder.append("\n\t\tObjectClassDefinition {name: ");
			metaInfoErrorBuilder.append(
				extendedObjectClassDefinition.getName());
			metaInfoErrorBuilder.append("}");
		}

		ExtendedAttributeDefinition[] extendedAttributeDefinitions =
			extendedObjectClassDefinition.getAttributeDefinitions(
				ObjectClassDefinition.ALL);

		for (ExtendedAttributeDefinition extendedAttributeDefinition :
				extendedAttributeDefinitions) {

			String attributeDefinitionName =
				extendedAttributeDefinition.getName();

			String attributeDefinitionString = ResourceBundleUtil.getString(
				resourceBundle, attributeDefinitionName);

			if (attributeDefinitionString == null) {
				metaInfoErrorBuilder.append(
					"\n\t\tAttributeDefinition {name: ");
				metaInfoErrorBuilder.append(attributeDefinitionName);
				metaInfoErrorBuilder.append("}");
			}
		}

		return metaInfoErrorBuilder.toString();
	}

	private void _printBundleError(
		Bundle bundle, StringBuilder errorMessageBuilder,
		StringBuilder bundleErrorBuilder) {

		errorMessageBuilder.append("\nBundle {id: ");
		errorMessageBuilder.append(bundle.getBundleId());
		errorMessageBuilder.append(", name: ");
		errorMessageBuilder.append(bundle.getSymbolicName());
		errorMessageBuilder.append(", version: ");
		errorMessageBuilder.append(bundle.getVersion());
		errorMessageBuilder.append(", errors: ");
		errorMessageBuilder.append(bundleErrorBuilder);
		errorMessageBuilder.append("\n}\n");
	}

	@Inject
	private static ExtendedMetaTypeService _extendedMetaTypeService;

}