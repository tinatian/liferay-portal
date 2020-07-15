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

package com.liferay.portal.enterprise.app.gate.keeper.internal;

import com.liferay.osgi.util.BundleUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.license.LicenseInfo;
import com.liferay.portal.kernel.license.util.LicenseManager;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.lpkg.deployer.LPKGDeployer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import jdk.nashorn.internal.ir.annotations.Reference;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Tina Tian
 */
@Component(immediate = true, service = {})
public class EnterpriseAppGateKeeper {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_bundleContext.addBundleListener(_bundleListener);

		List<Bundle> uninstalledBundles = new ArrayList<>();

		for (Bundle bundle : bundleContext.getBundles()) {
			if ((bundle.getState() != Bundle.UNINSTALLED) &&
				_processBundle(bundle)) {

				uninstalledBundles.add(bundle);
			}
		}

		if (!uninstalledBundles.isEmpty()) {
			BundleUtil.refreshBundles(bundleContext, uninstalledBundles);
		}

		_serviceTracker = new ServiceTracker<>(
			bundleContext, LicenseInfo.class,
			new LicenseInfoServiceTrackerCustomizer());

		_serviceTracker.open();
	}

	@Deactivate
	protected synchronized void deactivate() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}

		_bundleContext.removeBundleListener(_bundleListener);

		_serviceTracker.close();
	}

	private String _getProductId(String enterpriseAppHeader) {
		int index = enterpriseAppHeader.indexOf(_PRODUCT_ID);

		if (index == -1) {
			return null;
		}

		int endIndex = enterpriseAppHeader.indexOf(index, CharPool.SEMICOLON);

		if (endIndex == -1) {
			return enterpriseAppHeader.substring(index + _PRODUCT_ID.length());
		}

		return enterpriseAppHeader.substring(
			index + _PRODUCT_ID.length(), endIndex);
	}

	private boolean _processBundle(Bundle bundle) {
		Dictionary<String, String> headers = bundle.getHeaders(
			StringPool.BLANK);

		String enterpriseAppHeader = headers.get("Liferay-Enterprise-App");

		if (enterpriseAppHeader == null) {
			return false;
		}

		String productId = _getProductId(enterpriseAppHeader);

		synchronized (this) {
			Set<BlockedBundleData> blockedBundleDataSet =
				_blockedBundleDataMap.get(productId);

			if (blockedBundleDataSet == _nullHolder) {
				return false;
			}

			if (blockedBundleDataSet == null) {
				if (_verifyLicense(productId)) {
					_blockedBundleDataMap.put(productId, _nullHolder);

					if (_serviceRegistration == null) {
						_serviceRegistration = _bundleContext.registerService(
							LifecycleAction.class,
							new EnterpriseAppServicePreAction(_verifiedBundles),
							new HashMapDictionary<String, String>() {
								{
									put(
										"key",
										PropsKeys.SERVLET_SERVICE_EVENTS_PRE);
								}
							});
					}

					_verifiedBundles.compute(
						productId,
						(key, value) -> {
							if (value == null) {
								value = Collections.newSetFromMap(
									new ConcurrentHashMap<>());
							}

							value.add(bundle);

							return value;
						});

					return false;
				}

				blockedBundleDataSet = new HashSet<>();

				_blockedBundleDataMap.put(productId, blockedBundleDataSet);
			}

			BundleStartLevel bundleStartLevel = bundle.adapt(
				BundleStartLevel.class);

			int startLevel = bundleStartLevel.getStartLevel();

			try {
				bundle.uninstall();

				blockedBundleDataSet.add(
					new BlockedBundleData(bundle.getLocation(), startLevel));

				return true;
			}
			catch (Exception exception) {
				_log.error(
					"Unable to uninstall bundle " + bundle.getSymbolicName(),
					exception);
			}

			return false;
		}
	}

	private boolean _verifyLicense(String productId) {
		String name = ReleaseInfo.getName();

		if (name.contains("Community")) {
			return true;
		}

		if (LicenseManagerUtil.getLicenseState(productId) !=
				LicenseManager.STATE_GOOD) {

			_log.error(
				"This product " + productId + " does not have a valid license");

			return false;
		}

		Map<String, String> portalLicenseProperties =
			LicenseManagerUtil.getLicenseProperties("Portal");

		String portalLicenseType = portalLicenseProperties.get("type");

		if (portalLicenseType == null) {
			_log.error(
				"This product " + productId +
					" requires a valid Liferay DXP license");

			return false;
		}

		Map<String, String> appLicenseProperties =
			LicenseManagerUtil.getLicenseProperties(productId);

		String appLicenseType = portalLicenseProperties.get("type");

		if (appLicenseType.equals(portalLicenseType)) {
			if (appLicenseType.equals("trial") &&
				!Objects.equals(
					appLicenseProperties.get("lifetime"),
					portalLicenseProperties.get("lifetime"))) {

				_log.error(
					StringBundler.concat(
						"This product ", productId,
						" does not have same lifetime with the Liferay DXP ",
						"trial license"));

				return false;
			}
		}
		else {
			if (appLicenseType.startsWith("developer")) {
				_log.error(
					StringBundler.concat(
						"This product ", productId,
						" must not use a developer license when the Liferay ",
						"DXP license is not a developer license"));

				return false;
			}
			else if (portalLicenseType.startsWith("developer")) {
				_log.error(
					StringBundler.concat(
						"This product ", productId,
						" requires a developer license when the Liferay DXP ",
						"license is a developer license"));

				return false;
			}
		}

		return true;
	}

	private static final String _PRODUCT_ID = "product.id";

	private static final Log _log = LogFactoryUtil.getLog(
		EnterpriseAppGateKeeper.class);

	private static final Set<BlockedBundleData> _nullHolder = new HashSet<>();

	private final Map<String, Set<BlockedBundleData>> _blockedBundleDataMap =
		new HashMap<>();
	private BundleContext _bundleContext;

	private BundleListener _bundleListener = new SynchronousBundleListener() {

		@Override
		public void bundleChanged(BundleEvent bundleEvent) {
			if (bundleEvent.getType() == BundleEvent.INSTALLED) {
				_processBundle(bundleEvent.getBundle());
			}
		}

	};

	@Reference
	private LPKGDeployer _lpkgDeployer;

	private ServiceRegistration<LifecycleAction> _serviceRegistration;
	private ServiceTracker<LicenseInfo, Void> _serviceTracker;
	private final Map<String, Set<Bundle>> _verifiedBundles =
		new ConcurrentHashMap<>();

	private class LicenseInfoServiceTrackerCustomizer
		implements ServiceTrackerCustomizer<LicenseInfo, Void> {

		@Override
		public Void addingService(
			ServiceReference<LicenseInfo> serviceReference) {

			synchronized (EnterpriseAppGateKeeper.this) {
				for (Map.Entry<String, Set<BlockedBundleData>> entry :
						_blockedBundleDataMap.entrySet()) {

					if (entry.getValue() == _nullHolder) {
						continue;
					}

					if (_verifyLicense(entry.getKey())) {
						for (BlockedBundleData blockedBundleData :
								entry.getValue()) {

							try {
								BundleUtil.installBundle(
									_bundleContext, _lpkgDeployer,
									blockedBundleData.getLocation(),
									blockedBundleData.getStartLevel());
							}
							catch (Exception exception) {
							}
						}

						entry.setValue(_nullHolder);
					}
				}
			}

			return null;
		}

		@Override
		public void modifiedService(
			ServiceReference<LicenseInfo> serviceReference, Void aVoid) {
		}

		@Override
		public void removedService(
			ServiceReference<LicenseInfo> serviceReference, Void aVoid) {
		}

	}

}