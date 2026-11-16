/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.ee.license.EntitlementManager;
import com.liferay.portal.kernel.events.LifecycleEvent;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.license.util.App;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;

import java.net.InetSocketAddress;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import java.security.PublicKey;
import java.security.Signature;

import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Kevin Lee
 */
@RunWith(Arquillian.class)
public class OnlineActivationEntitlementTest extends BaseEntitlementTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		BaseEntitlementTestCase.setUpTestCaseClass();

		_autoCloseable = ReflectionTestUtil.setFieldValueWithAutoCloseable(
			EntitlementManager.class, "_ONE_LIFERAY_LOCATION", _URL);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_autoCloseable.close();

		BaseEntitlementTestCase.tearDownTestCaseClass();
	}

	@Test
	public void testActivation() throws Exception {
		try (SafeCloseable safeCloseable1 = disableValidateWithSafeCloseable();
			SafeCloseable safeCloseable2 = resetLicenseDataWithSafeCloseble();
			MockOneLiferayHttpServer mockOneLiferayHttpServer =
				new MockOneLiferayHttpServer(true, true, 0)) {

			_activateInstallation(_ACTIVATION_CODE);

			assertEntitlementActivated();
		}
	}

	@Test
	public void testActivationWithAddOnDownloadRetry() throws Exception {
		try (SafeCloseable safeCloseable1 = disableValidateWithSafeCloseable();
			SafeCloseable safeCloseable2 = resetLicenseDataWithSafeCloseble();
			MockOneLiferayHttpServer mockOneLiferayHttpServer =
				new MockOneLiferayHttpServer(true, true, 1)) {

			_activateInstallation(_ACTIVATION_CODE);

			assertEntitlementActivated();
		}
	}

	@Test
	public void testActivationWithChecksumMismatch() throws Exception {
		try (MockOneLiferayHttpServer mockOneLiferayHttpServer =
				new MockOneLiferayHttpServer(true, false, 0)) {

			_activateInstallation(_ACTIVATION_CODE);

			assertEntitlementOnlineUnavailable();
		}
	}

	@Test
	public void testActivationWithDifferentInstallationId() throws Exception {
		try (MockOneLiferayHttpServer mockOneLiferayHttpServer =
				new MockOneLiferayHttpServer(false, false, 0);
			SafeCloseable safeCloseable = setInstallationIdWithSafeCloseable(
				RandomTestUtil.randomString())) {

			_activateInstallation(_ACTIVATION_CODE);

			assertEntitlementNotActivated();
		}
	}

	@Test
	public void testActivationWithExhaustedAddOnDownloadRetries()
		throws Exception {

		try (MockOneLiferayHttpServer mockOneLiferayHttpServer =
				new MockOneLiferayHttpServer(true, true, 2)) {

			_activateInstallation(_ACTIVATION_CODE);

			assertEntitlementOnlineUnavailable();
		}
	}

	@Test
	public void testActivationWithInvalidActivationCode() throws Exception {
		try (MockOneLiferayHttpServer mockOneLiferayHttpServer =
				new MockOneLiferayHttpServer(false, false, 0)) {

			_activateInstallation(RandomTestUtil.randomString());

			assertEntitlementOnlineInvalidActivationCode();
		}
	}

	@Test
	public void testActivationWithNoConnection() throws Exception {
		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					EntitlementManager.class, "_ONE_LIFERAY_LOCATION", _URL)) {

			_activateInstallation(_ACTIVATION_CODE);

			assertEntitlementOnlineNoConnection();
		}
	}

	@Test
	public void testActivationWithNoManifestJSON() throws Exception {
		try (MockOneLiferayHttpServer mockOneLiferayHttpServer =
				new MockOneLiferayHttpServer(true, true, 0)) {

			EntitlementManager.registerInstallation(_ACTIVATION_CODE);
		}

		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					EntitlementManager.class, "_ONE_LIFERAY_LOCATION", _URL)) {

			EntitlementManager.installEntitlementOnline(0);

			assertEntitlementOnlineUnavailable();
		}
	}

	@Test
	public void testActivationWithoutAddOns() throws Exception {
		try (SafeCloseable safeCloseable1 = disableValidateWithSafeCloseable();
			SafeCloseable safeCloseable2 = resetLicenseDataWithSafeCloseble();
			MockOneLiferayHttpServer mockOneLiferayHttpServer =
				new MockOneLiferayHttpServer(false, false, 0)) {

			_activateInstallation(_ACTIVATION_CODE);

			assertEntitlementActivated();
		}
	}

	private void _activateInstallation(String activationCode) throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter(
			Constants.CMD, "activateInstallation");
		mockHttpServletRequest.setParameter("activationCode", activationCode);
		mockHttpServletRequest.setRequestURI(
			PortalUtil.getPathContext() + "/c/portal/license_activation");

		lifecycleAction.processLifecycleEvent(
			new LifecycleEvent(
				mockHttpServletRequest, new MockHttpServletResponse()));
	}

	private static final String _ACTIVATION_CODE =
		RandomTestUtil.randomString();

	private static final int _HTTP_SERVER_PORT = 9000;

	private static final String _URL = "http://127.0.0.1:" + _HTTP_SERVER_PORT;

	private static AutoCloseable _autoCloseable;

	private static class MockOneLiferayHttpServer implements AutoCloseable {

		public MockOneLiferayHttpServer(
				boolean addOn, boolean addOnChecksumValid,
				int addOnDownloadFailureCount)
			throws IOException {

			_addOn = addOn;
			_addOnChecksumValid = addOnChecksumValid;
			_addOnDownloadFailureCount = new AtomicInteger(
				addOnDownloadFailureCount);

			_manifestJSON = _buildManifestJSON();

			_httpServer = HttpServer.create(
				new InetSocketAddress("127.0.0.1", _HTTP_SERVER_PORT), 0);

			_httpServer.start();

			_httpServer.createContext(
				"/add-on",
				httpExchange -> {
					if (_addOnDownloadFailureCount.getAndDecrement() > 0) {
						httpExchange.close();

						return;
					}

					_getJWTPayloadJSONObject(httpExchange, true);

					_sendResponse(
						httpExchange, HttpServletResponse.SC_OK, _addOnContent);
				});

			_httpServer.createContext(
				"/dxp/environments/activation",
				httpExchange -> {
					JSONObject jsonObject = _getJWTPayloadJSONObject(
						httpExchange, false);

					int statusCode = HttpServletResponse.SC_FORBIDDEN;

					if (Objects.equals(
							jsonObject.getString("activationCode"),
							_ACTIVATION_CODE)) {

						statusCode = HttpServletResponse.SC_OK;
					}

					_sendResponse(httpExchange, statusCode, new byte[0]);
				});
			_httpServer.createContext(
				"/dxp/environments/manifest",
				httpExchange -> {
					_getJWTPayloadJSONObject(httpExchange, true);

					_sendResponse(
						httpExchange, HttpServletResponse.SC_OK,
						_manifestJSON.getBytes(StandardCharsets.UTF_8));
				});
		}

		@Override
		public void close() {
			_httpServer.stop(0);
		}

		private JSONArray _buildAddOnsJSONArray() {
			if (!_addOn) {
				return JSONFactoryUtil.createJSONArray();
			}

			String sha256Checksum = DigesterUtil.digestHex(
				DigesterUtil.SHA_256, ByteBuffer.wrap(_addOnContent));

			if (!_addOnChecksumValid) {
				sha256Checksum = RandomTestUtil.randomString();
			}

			return JSONUtil.put(
				JSONUtil.put(
					"downloadURL", _URL + "/add-on"
				).put(
					"productId", getProductId(App.CMP)
				).put(
					"productName", RandomTestUtil.randomString()
				).put(
					"sha256Checksum", sha256Checksum
				));
		}

		private String _buildLicenseXML() {
			StringBundler sb = new StringBundler(4);

			sb.append("<licenses>");
			sb.append(
				buildEnterprisePortalLicenseXML(
					RandomTestUtil.randomString(), ENTERPRISE_LICENSE_TYPE, 0,
					getEnterpriseProductName(), Time.HOUR, "7",
					EntitlementManager.getInstallationId()));

			if (_addOn) {
				sb.append(
					buildAppLicenseXML(
						App.CMP, System.currentTimeMillis(), Time.HOUR));
			}

			sb.append("</licenses>");

			return sb.toString();
		}

		private String _buildManifestJSON() {
			Base64.Encoder encoder = Base64.getEncoder();

			return JSONUtil.put(
				"add-ons", _buildAddOnsJSONArray()
			).put(
				"licenseXML",
				encoder.encodeToString(
					_buildLicenseXML().getBytes(StandardCharsets.UTF_8))
			).toString();
		}

		private JSONObject _getJWTPayloadJSONObject(
				HttpExchange httpExchange, boolean signed)
			throws IOException {

			String[] parts = StringUtil.split(
				new String(
					FileUtil.getBytes(httpExchange.getRequestBody()),
					StandardCharsets.UTF_8),
				CharPool.PERIOD);

			try {
				Base64.Decoder decoder = Base64.getUrlDecoder();

				if (signed) {
					Signature signature = Signature.getInstance(
						"SHA256withRSA");

					signature.initVerify(
						(PublicKey)ReflectionTestUtil.invoke(
							EntitlementManager.class, "_getPublicKey",
							new Class<?>[0]));

					String data = parts[0] + "." + parts[1];

					signature.update(data.getBytes(StandardCharsets.UTF_8));

					Assert.assertTrue(
						signature.verify(decoder.decode(parts[2])));
				}

				return JSONFactoryUtil.createJSONObject(
					new String(
						decoder.decode(parts[1]), StandardCharsets.UTF_8));
			}
			catch (Exception exception) {
				throw new IOException(exception);
			}
		}

		private void _sendResponse(
				HttpExchange httpExchange, int statusCode, byte[] bytes)
			throws IOException {

			Headers headers = httpExchange.getResponseHeaders();

			headers.add("Connection", "close");

			if (bytes.length == 0) {
				httpExchange.sendResponseHeaders(statusCode, -1);

				httpExchange.close();

				return;
			}

			httpExchange.sendResponseHeaders(statusCode, bytes.length);

			try (OutputStream outputStream = httpExchange.getResponseBody()) {
				outputStream.write(bytes);
			}
		}

		private final boolean _addOn;
		private final boolean _addOnChecksumValid;
		private final byte[] _addOnContent = RandomTestUtil.randomBytes();
		private final AtomicInteger _addOnDownloadFailureCount;
		private final HttpServer _httpServer;
		private final String _manifestJSON;

	}

}