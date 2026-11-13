/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.ee.license.EntitlementConstants;
import com.liferay.portal.ee.license.EntitlementManager;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Time;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Kevin Lee
 */
@RunWith(Arquillian.class)
public class RestartEntitlementTest extends BaseEntitlementTestCase {

	@Test
	public void testRestartWithDifferentInstallationId() throws Exception {
		setEntitlementState(EntitlementConstants.STATE_GOOD);

		try (SafeCloseable safeCloseable = setInstallationIdWithSafeCloseable(
				RandomTestUtil.randomString())) {

			EntitlementManager.saveEntitlementState();
		}

		_restartInstallation();
		_assertInstallationNotActivated();
	}

	@Test
	public void testRestartWithMarkerFile() throws Exception {
		setEntitlementState(EntitlementConstants.STATE_GOOD);

		EntitlementManager.saveEntitlementState();

		_restartInstallation();
		_assertInstallationActivated();
	}

	@Test
	public void testRestartWithModifiedMarkerFile() throws Exception {
		setEntitlementState(EntitlementConstants.STATE_GOOD);

		EntitlementManager.saveEntitlementState();

		File entitlementMarkerFile = getEntitlementMarkerFile();

		Assert.assertTrue(
			entitlementMarkerFile.setLastModified(
				entitlementMarkerFile.lastModified() + Time.DAY));

		_restartInstallation();
		_assertInstallationNotActivated();
	}

	@Test
	public void testRestartWithoutMarkerFile() {
		_restartInstallation();
		_assertInstallationNotActivated();
	}

	private void _assertInstallationActivated() {
		Assert.assertEquals(
			EntitlementConstants.STATE_GOOD,
			EntitlementManager.getEntitlementState());

		File entitlementMarkerFile = getEntitlementMarkerFile();

		Assert.assertTrue(entitlementMarkerFile.exists());
	}

	private void _assertInstallationNotActivated() {
		Assert.assertEquals(
			EntitlementConstants.STATE_NOT_ACTIVATED,
			EntitlementManager.getEntitlementState());

		File entitlementMarkerFile = getEntitlementMarkerFile();

		Assert.assertFalse(entitlementMarkerFile.exists());
	}

	private void _restartInstallation() {
		setEntitlementState(EntitlementConstants.STATE_NOT_ACTIVATED);

		EntitlementManager.init();
	}

}