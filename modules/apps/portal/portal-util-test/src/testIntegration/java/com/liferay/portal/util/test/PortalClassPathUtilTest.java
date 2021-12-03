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

package com.liferay.portal.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.process.ProcessConfig;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PortalClassPathUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Dante Wang
 */
@RunWith(Arquillian.class)
public class PortalClassPathUtilTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	public static ProcessConfig processConfig;

	@BeforeClass
	public static void setUpClass() {
		processConfig = PortalClassPathUtil.getPortalProcessConfig();
	}

	@Test
	public void testBoostrapClassPathManifest() {
		List<String> boostrapClassPathEntries = StringUtil.split(
			processConfig.getBootstrapClassPath(), File.pathSeparatorChar);

		Set<String> headerEntries = new HashSet<>();

		for (String bootstrapClassPathEntry : boostrapClassPathEntries) {
			if (bootstrapClassPathEntry.contains("petra")) {
				URL url;

				try {
					url = new URL(
						"jar:file:" + bootstrapClassPathEntry +
							"!/META-INF/MANIFEST.MF");

					InputStream urlInputStream = url.openStream();

					Scanner manifestScanner = new Scanner(urlInputStream);

					while (manifestScanner.hasNext()) {
						String manifestNextLine = manifestScanner.nextLine();

						if (manifestNextLine.contains("Liferay-Releng")) {
							headerEntries.add(bootstrapClassPathEntry);

							break;
						}
					}
				}
				catch (IOException ioException) {
					_log.error(
						"Unable to resolve local URL from bundle", ioException);
				}
			}
		}

		Assert.assertTrue(
			"Bootstrap packages containing petra should not contain" +
				" header entries in their Manifest.MF file: " + headerEntries,
			headerEntries.isEmpty());
	}

	@Test
	public void testBoostrapClassPathPetra() {
		List<String> boostrapClassPathEntries = StringUtil.split(
			processConfig.getBootstrapClassPath(), File.pathSeparatorChar);

		Set<String> nonpetraEntries = new HashSet<>();

		for (String bootstrapClassPathEntry : boostrapClassPathEntries) {
			if (!bootstrapClassPathEntry.contains("petra")) {
				nonpetraEntries.add(bootstrapClassPathEntry);
			}
		}

		Assert.assertTrue(
			"Bootstrap packages should only contain petra packages: " +
				nonpetraEntries,
			nonpetraEntries.isEmpty());
	}

	@Test
	public void testGetPortalProcessConfig() {
		List<String> runtimeClassPathEntries = StringUtil.split(
			processConfig.getRuntimeClassPath(), File.pathSeparatorChar);

		Set<String> runtimeClassPathEntrySet = new HashSet<>();

		List<String> duplicateEntries = new ArrayList<>();

		for (String runtimeClassPathEntry : runtimeClassPathEntries) {
			if (!runtimeClassPathEntrySet.add(runtimeClassPathEntry)) {
				duplicateEntries.add(runtimeClassPathEntry);
			}
		}

		Assert.assertTrue(
			"Portal process config runtime class path should not contain " +
				"duplicate entries: " + duplicateEntries,
			duplicateEntries.isEmpty());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortalClassPathUtilTest.class);

}