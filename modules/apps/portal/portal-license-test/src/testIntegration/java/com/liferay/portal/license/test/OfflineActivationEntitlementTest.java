/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.ee.license.EntitlementManager;
import com.liferay.portal.kernel.events.LifecycleEvent;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.license.util.App;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upload.FileItem;
import com.liferay.portal.kernel.upload.UploadServletRequest;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.upload.UploadServletRequestImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Kevin Lee
 */
@RunWith(Arquillian.class)
public class OfflineActivationEntitlementTest extends BaseEntitlementTestCase {

	@Test
	public void testOfflineActivation() throws Exception {
		try (SafeCloseable safeCloseable1 = disableValidateWithSafeCloseable();
			SafeCloseable safeCloseable2 = resetLicenseDataWithSafeCloseble()) {

			String productId = getProductId(App.CMP);

			String addOnContent = RandomTestUtil.randomString();

			_uploadEntitlementZip(
				_createEntitlementZipFile(
					_createManifestJSON(
						_buildLicenseXML(
							EntitlementManager.getInstallationId(), App.CMP),
						JSONUtil.put(
							"productId", productId
						).put(
							"productName", RandomTestUtil.randomString()
						).put(
							"sha256Checksum",
							DigesterUtil.digestHex(
								DigesterUtil.SHA_256,
								ByteBuffer.wrap(
									addOnContent.getBytes(
										StandardCharsets.UTF_8)))
						)),
					Collections.singletonMap(productId, addOnContent)));

			assertEntitlementActivated();
		}
	}

	@Test
	public void testOfflineActivationWithChecksumMismatch() throws Exception {
		String productId = getProductId(App.CMP);

		_uploadEntitlementZip(
			_createEntitlementZipFile(
				_createManifestJSON(
					_buildLicenseXML(
						EntitlementManager.getInstallationId(), App.CMP),
					JSONUtil.put(
						"productId", productId
					).put(
						"productName", RandomTestUtil.randomString()
					).put(
						"sha256Checksum", RandomTestUtil.randomString()
					)),
				Collections.singletonMap(
					productId, RandomTestUtil.randomString())));

		assertEntitlementOfflineInvalidFile();
	}

	@Test
	public void testOfflineActivationWithDifferentInstallationId()
		throws Exception {

		_uploadEntitlementZip(
			_createEntitlementZipFile(
				_createManifestJSON(
					_buildLicenseXML(RandomTestUtil.randomString())),
				Collections.emptyMap()));

		assertEntitlementNotActivated();
	}

	@Test
	public void testOfflineActivationWithRandomZip() throws Exception {
		_uploadEntitlementZip(
			_createZipFile(
				HashMapBuilder.put(
					RandomTestUtil.randomString(), RandomTestUtil.randomString()
				).build()));

		assertEntitlementOfflineInvalidFile();
	}

	@Test
	public void testOfflineActivationWithoutAddOnEntry() throws Exception {
		String productId = RandomTestUtil.randomString();

		_uploadEntitlementZip(
			_createEntitlementZipFile(
				_createManifestJSON(
					_buildLicenseXML(EntitlementManager.getInstallationId()),
					JSONUtil.put("productId", productId)),
				Collections.emptyMap()));

		assertEntitlementOfflineInvalidFile();
	}

	@Test
	public void testOfflineActivationWithoutManifestJSON() throws Exception {
		_uploadEntitlementZip(
			_createEntitlementZipFile(null, Collections.emptyMap()));

		assertEntitlementOfflineInvalidFile();
	}

	private String _buildLicenseXML(String installationId, App... apps) {
		StringBundler sb = new StringBundler();

		sb.append("<licenses>");

		sb.append(
			buildEnterprisePortalLicenseXML(
				StringPool.BLANK, ENTERPRISE_LICENSE_TYPE, 1,
				getEnterpriseProductName(), Time.HOUR, "7", installationId));

		for (App app : apps) {
			sb.append(
				buildAppLicenseXML(app, System.currentTimeMillis(), Time.HOUR));
		}

		sb.append("</licenses>");

		return sb.toString();
	}

	private File _createEntitlementZipFile(
			String manifestJSON, Map<String, String> addOnContents)
		throws IOException {

		Map<String, String> entries = new HashMap<>();

		if (manifestJSON != null) {
			entries.put("manifest.json", manifestJSON);
		}

		for (Map.Entry<String, String> entry : addOnContents.entrySet()) {
			entries.put(
				"add-ons/" + entry.getKey() + ".lpkg", entry.getValue());
		}

		return _createZipFile(entries);
	}

	private String _createManifestJSON(
		String licenseXML, JSONObject... addOnJSONObjects) {

		JSONObject manifestJSONObject = JSONUtil.put(
			"add-ons", JSONUtil.putAll((Object[])addOnJSONObjects));

		if (licenseXML != null) {
			Base64.Encoder encoder = Base64.getEncoder();

			manifestJSONObject.put(
				"licenseXML",
				encoder.encodeToString(
					licenseXML.getBytes(StandardCharsets.UTF_8)));
		}

		return manifestJSONObject.toString();
	}

	private File _createZipFile(Map<String, String> entries)
		throws IOException {

		File file = File.createTempFile(RandomTestUtil.randomString(), ".zip");

		try (ZipOutputStream zipOutputStream = new ZipOutputStream(
				new FileOutputStream(file))) {

			for (Map.Entry<String, String> entry : entries.entrySet()) {
				String value = entry.getValue();

				zipOutputStream.putNextEntry(new ZipEntry(entry.getKey()));
				zipOutputStream.write(value.getBytes(StandardCharsets.UTF_8));
				zipOutputStream.closeEntry();
			}
		}

		return file;
	}

	private void _uploadEntitlementZip(File zipFile) throws Exception {
		FileItem fileItem = ProxyUtil.newDelegateProxyInstance(
			FileItem.class.getClassLoader(), FileItem.class,
			new Object() {

				public void delete() {
				}

				public String getFileName() {
					return zipFile.getName();
				}

				public File getStoreLocation() {
					return zipFile;
				}

				public boolean isInMemory() {
					return false;
				}

			},
			null);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setContentType("multipart/form-data");
		mockHttpServletRequest.setRequestURI(
			PortalUtil.getPathContext() + "/c/portal/license_activation");

		UploadServletRequest uploadServletRequest =
			new UploadServletRequestImpl(
				mockHttpServletRequest,
				Collections.singletonMap(
					"entitlementZIP", new FileItem[] {fileItem}),
				Collections.singletonMap(
					Constants.CMD,
					Collections.singletonList("uploadEntitlement")));

		lifecycleAction.processLifecycleEvent(
			new LifecycleEvent(
				uploadServletRequest, new MockHttpServletResponse()));
	}

}