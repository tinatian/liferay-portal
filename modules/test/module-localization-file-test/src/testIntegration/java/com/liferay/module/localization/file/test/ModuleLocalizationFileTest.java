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

package com.liferay.module.localization.file.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ResourceBundleLoader;
import com.liferay.portal.kernel.util.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

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

/**
 * @author Lance Ji
 */
@RunWith(Arquillian.class)
public class ModuleLocalizationFileTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testBundleLocalizationFiles() {
		Bundle currentBundle = FrameworkUtil.getBundle(
			ModuleLocalizationFileTest.class);

		BundleContext bundleContext = currentBundle.getBundleContext();

		StringBundler sb = new StringBundler();

		for (Bundle bundle : bundleContext.getBundles()) {
			ResourceBundleLoader resourceBundleLoader =
				ResourceBundleLoaderUtil.
					getResourceBundleLoaderByBundleSymbolicName(
						bundle.getSymbolicName());

			if (resourceBundleLoader == null) {
				continue;
			}

			for (Locale locale : LanguageUtil.getAvailableLocales()) {
				if (locale.equals(LocaleUtil.getDefault())) {
					continue;
				}

				ResourceBundle defaultLocaleResourceBundle =
					resourceBundleLoader.loadResourceBundle(
						LocaleUtil.getDefault());

				ResourceBundle localeResourceBundle =
					resourceBundleLoader.loadResourceBundle(locale);

				if ((localeResourceBundle == null) ||
					Objects.equals(
						defaultLocaleResourceBundle, localeResourceBundle)) {

					sb.append(StringPool.NEW_LINE);
					sb.append(
						"Missing generated localization files in bundle ");
					sb.append(StringPool.OPEN_CURLY_BRACE);
					sb.append("id: ");
					sb.append(bundle.getBundleId());
					sb.append(", name: ");
					sb.append(bundle.getSymbolicName());
					sb.append(", version: ");
					sb.append(bundle.getVersion());
					sb.append(StringPool.CLOSE_CURLY_BRACE);
					sb.append(StringPool.NEW_LINE);
				}

				break;
			}
		}

		Assert.assertEquals(sb.toString(), 0, sb.index());
	}

}