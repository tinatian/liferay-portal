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
import com.liferay.portal.kernel.util.StringBundler;
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

		StringBundler errorMessageBundeler = new StringBundler();

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

			StringBundler bundleErrorBundeler = new StringBundler();

			ResourceBundleLoader resourceBundleLoader =
				ResourceBundleLoaderUtil.
					getResourceBundleLoaderByBundleSymbolicName(
						currentBundleSymbolicName);

			if (resourceBundleLoader == null) {
				bundleErrorBundeler.append("\n\tResource Bundle Error:");
				bundleErrorBundeler.append("\n\t\tNo resource bundle");
				_printBundleError(
					bundle, errorMessageBundeler, bundleErrorBundeler);

				continue;
			}

			ResourceBundle resourceBundle =
				resourceBundleLoader.loadResourceBundle(Locale.getDefault());

			ResourceBundle koResourceBundle =
				resourceBundleLoader.loadResourceBundle(Locale.KOREA);

			if (Objects.equals(resourceBundle, koResourceBundle)) {
				bundleErrorBundeler.append("\n\tResource Bundle Error:");

				bundleErrorBundeler.append(
					"\n\t\tMissing generated resource files");
			}

			for (String pid : pids) {
				String metaInfoErrorMessage = _collectMetaInfoError(
					pid, extendedMetaTypeInformation, resourceBundle);

				if (!metaInfoErrorMessage.isEmpty()) {
					bundleErrorBundeler.append("\n\tConfiguration {pid:");
					bundleErrorBundeler.append(pid);
					bundleErrorBundeler.append(", missingLocalization:");
					bundleErrorBundeler.append(metaInfoErrorMessage);
					bundleErrorBundeler.append("\n\t}");
				}
			}

			_printBundleError(
				bundle, errorMessageBundeler, bundleErrorBundeler);
		}

		if (errorMessageBundeler.length() > 0) {
			Assert.fail(errorMessageBundeler.toString());
		}
	}

	private String _collectMetaInfoError(
		String pid, ExtendedMetaTypeInformation extendedMetaTypeInformation,
		ResourceBundle resourceBundle) {

		StringBundler metaInfoErrorBundeler = new StringBundler();

		ExtendedObjectClassDefinition extendedObjectClassDefinition =
			extendedMetaTypeInformation.getObjectClassDefinition(
				pid, Locale.getDefault().getLanguage());

		String objectClassDefinitionName = ResourceBundleUtil.getString(
			resourceBundle, extendedObjectClassDefinition.getName());

		if (objectClassDefinitionName == null) {
			metaInfoErrorBundeler.append("\n\t\tObjectClassDefinition {name: ");
			metaInfoErrorBundeler.append(
				extendedObjectClassDefinition.getName());
			metaInfoErrorBundeler.append("}");
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
				metaInfoErrorBundeler.append(
					"\n\t\tAttributeDefinition {name: ");
				metaInfoErrorBundeler.append(attributeDefinitionName);
				metaInfoErrorBundeler.append("}");
			}
		}

		return metaInfoErrorBundeler.toString();
	}

	private void _printBundleError(
		Bundle bundle, StringBundler errorMessageBundeler,
		StringBundler bundleErrorBundeler) {

		errorMessageBundeler.append("\nBundle {id: ");
		errorMessageBundeler.append(bundle.getBundleId());
		errorMessageBundeler.append(", name: ");
		errorMessageBundeler.append(bundle.getSymbolicName());
		errorMessageBundeler.append(", version: ");
		errorMessageBundeler.append(bundle.getVersion());
		errorMessageBundeler.append(", errors: ");
		errorMessageBundeler.append(bundleErrorBundeler);
		errorMessageBundeler.append("\n}\n");
	}

	@Inject
	private static ExtendedMetaTypeService _extendedMetaTypeService;

}