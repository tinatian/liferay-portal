/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.ee.license.EntitlementConstants;
import com.liferay.portal.ee.license.EntitlementManager;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PropsValues;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * @author Kevin Lee
 */
public abstract class BaseEntitlementTestCase extends BaseLicenseTestCase {

	public static void assume() {
		Assume.assumeTrue(
			isReleaseBundle() && EntitlementManager.isEntitlementRelease());
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
		File tempDir = FileUtil.createTempFolder();

		AutoCloseable autoCloseable =
			ReflectionTestUtil.setFieldValueWithAutoCloseable(
				PropsValues.class, "AUTO_DEPLOY_DEPLOY_DIR",
				tempDir.getAbsolutePath());

		_safeCloseable = () -> {
			try {
				autoCloseable.close();
			}
			catch (Exception exception) {
				throw new IllegalStateException(exception);
			}

			FileUtil.deltree(tempDir);
		};
	}

	@AfterClass
	public static void tearDownClass() {
		_safeCloseable.close();
	}

	@Before
	public void setUp() {
		FileUtil.delete(getEntitlementMarkerFile());

		setEntitlementState(EntitlementConstants.STATE_NOT_ACTIVATED);
	}

	protected void assertEntitlementActivated() throws Exception {
		String response = hitHomePage("localhost", getLocalPort());

		Assert.assertFalse(
			response.contains("<strong class=\"lead\">Error:</strong>"));
		Assert.assertFalse(
			response.contains("<strong class=\"lead\">Warning:</strong>"));

		Assert.assertEquals(
			EntitlementConstants.STATE_GOOD,
			EntitlementManager.getEntitlementState());
	}

	protected void assertEntitlementNotActivated() throws Exception {
		String response = hitHomePage("localhost", getLocalPort());

		Assert.assertTrue(response.contains(_NOT_ACTIVATED_KEY));

		Assert.assertEquals(
			EntitlementConstants.STATE_NOT_ACTIVATED,
			EntitlementManager.getEntitlementState());
	}

	protected void assertEntitlementOfflineInvalidFile() throws Exception {
		String response = hitHomePage("localhost", getLocalPort());

		Assert.assertTrue(response.contains(_OFFLINE_INVALID_FILE_KEY));

		Assert.assertEquals(
			EntitlementConstants.STATE_OFFLINE_INVALID_FILE,
			EntitlementManager.getEntitlementState());
	}

	protected File getEntitlementMarkerFile() {
		return ReflectionTestUtil.getFieldValue(
			EntitlementManager.class, "_ENTITLEMENT_FILE");
	}

	protected void setEntitlementState(int state) {
		ReflectionTestUtil.setFieldValue(
			EntitlementManager.class, "_entitlementState", state);
	}

	private static final String _NOT_ACTIVATED_KEY =
		"This installation is not activated.";

	private static final String _OFFLINE_INVALID_FILE_KEY =
		"This file could not be verified. It may be damaged or modified. " +
			"Download it again from one.liferay.com.";

	private static SafeCloseable _safeCloseable;

}