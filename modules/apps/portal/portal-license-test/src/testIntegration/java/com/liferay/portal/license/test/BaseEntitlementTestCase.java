/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.portal.ee.license.EntitlementConstants;
import com.liferay.portal.ee.license.EntitlementManager;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PropsValues;

import java.io.File;

import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;

/**
 * @author Kevin Lee
 */
public abstract class BaseEntitlementTestCase extends BaseLicenseTestCase {

	public static void assume() {
		Assume.assumeTrue(
			isReleaseBundle() && EntitlementManager.isEntitlementRelease());
	}

	public void assertEntitlementOnlineInvalidActivationCode()
		throws Exception {

		String response = hitHomePage("localhost", getLocalPort());

		Assert.assertTrue(
			response.contains(_ONLINE_INVALID_ACTIVATION_CODE_KEY));

		Assert.assertEquals(
			EntitlementConstants.STATE_ONLINE_INVALID_ACTIVATION_CODE,
			EntitlementManager.getEntitlementState());
	}

	public void assertEntitlementOnlineNoConnection() throws Exception {
		String response = hitHomePage("localhost", getLocalPort());

		Assert.assertTrue(response.contains(_ONLINE_NO_CONNECTION_KEY));

		Assert.assertEquals(
			EntitlementConstants.STATE_ONLINE_NO_CONNECTION,
			EntitlementManager.getEntitlementState());
	}

	public void assertEntitlementOnlineUnavailable() throws Exception {
		String response = hitHomePage("localhost", getLocalPort());

		Assert.assertTrue(response.contains(_ONLINE_UNAVAILABLE_KEY));

		Assert.assertEquals(
			EntitlementConstants.STATE_ONLINE_UNAVAILABLE,
			EntitlementManager.getEntitlementState());
	}

	@Before
	public void setUp() {
		FileUtil.delete(getEntitlementMarkerFile());

		setEntitlementState(EntitlementConstants.STATE_NOT_ACTIVATED);
	}

	@After
	public void tearDown() {
		for (String fileName :
				FileUtil.listFiles(PropsValues.AUTO_DEPLOY_DEPLOY_DIR)) {

			FileUtil.delete(fileName);
		}

		for (String fileName :
				FileUtil.listFiles(
					PropsValues.MODULE_FRAMEWORK_MARKETPLACE_DIR)) {

			FileUtil.delete(fileName);
		}
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

	private static final String _ONLINE_INVALID_ACTIVATION_CODE_KEY =
		"The activation code is not valid. Check it on one.liferay.com and " +
			"try again.";

	private static final String _ONLINE_NO_CONNECTION_KEY =
		"We could not connect to Liferay to activate this installation. If " +
			"this server has no outbound connection, activate it offline.";

	private static final String _ONLINE_UNAVAILABLE_KEY =
		"Liferay could not retrieve your entitlement. Try again, or contact " +
			"Liferay Support.";

}