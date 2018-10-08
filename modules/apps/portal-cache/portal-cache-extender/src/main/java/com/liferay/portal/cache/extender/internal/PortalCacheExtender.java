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

package com.liferay.portal.cache.extender.internal;

import com.liferay.osgi.felix.util.AbstractExtender;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.cache.PortalCacheManager;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.cache.configurator.PortalCacheConfiguratorSettings;
import com.liferay.portal.kernel.util.HashMapDictionary;

import java.util.Dictionary;

import org.apache.felix.utils.extender.Extension;
import org.apache.felix.utils.log.Logger;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Tina Tian
 */
@Component(immediate = true, service = {})
public class PortalCacheExtender extends AbstractExtender {

	@Activate
	protected void activate(BundleContext bundleContext) throws Exception {
		_logger = new Logger(bundleContext);

		start(bundleContext);
	}

	@Deactivate
	protected void deactivate(BundleContext bundleContext) throws Exception {
		stop(bundleContext);
	}

	@Override
	protected void debug(Bundle bundle, String message) {
		_logger.log(
			Logger.LOG_DEBUG, StringBundler.concat("[", bundle, "] ", message));
	}

	@Override
	protected Extension doCreateExtension(Bundle bundle) throws Exception {
		BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

		ClassLoader classLoader = bundleWiring.getClassLoader();

		PortalCacheConfiguratorSettings multiVMPortalCacheConfiguratorSettings =
			null;

		if (bundle.getEntry("/META-INF/module-multi-vm-clustered.xml") !=
				null) {

			multiVMPortalCacheConfiguratorSettings =
				new PortalCacheConfiguratorSettings(
					classLoader, "/META-INF/module-multi-vm-clustered.xml");
		}

		PortalCacheConfiguratorSettings
			singleVMPortalCacheConfiguratorSettings = null;

		if (bundle.getEntry("/META-INF/module-single-vm-clustered.xml") !=
				null) {

			singleVMPortalCacheConfiguratorSettings =
				new PortalCacheConfiguratorSettings(
					classLoader, "/META-INF/module-single-vm-clustered.xml");
		}

		if ((multiVMPortalCacheConfiguratorSettings == null) &&
			(singleVMPortalCacheConfiguratorSettings == null)) {

			return null;
		}

		return new EhcacheExtension(
			getBundleContext(), multiVMPortalCacheConfiguratorSettings,
			singleVMPortalCacheConfiguratorSettings);
	}

	@Override
	protected void error(String message, Throwable throwable) {
		_logger.log(Logger.LOG_ERROR, message, throwable);
	}

	@Override
	protected void warn(Bundle bundle, String message, Throwable throwable) {
		_logger.log(
			Logger.LOG_WARNING,
			StringBundler.concat("[", bundle, "] ", message));
	}

	private Logger _logger;

	private static class EhcacheExtension implements Extension {

		@Override
		public void destroy() throws Exception {
			if (_multiVMServiceRegistration != null) {
				_multiVMServiceRegistration.unregister();
			}

			if (_singleVMServiceRegistration != null) {
				_singleVMServiceRegistration.unregister();
			}
		}

		@Override
		public void start() throws Exception {
			if (_multiVMPortalCacheConfiguratorSettings != null) {
				Dictionary<String, Object> properties =
					new HashMapDictionary<>();

				properties.put(
					PortalCacheManager.PORTAL_CACHE_MANAGER_NAME,
					PortalCacheManagerNames.MULTI_VM);

				_multiVMServiceRegistration = _bundleContext.registerService(
					PortalCacheConfiguratorSettings.class,
					_multiVMPortalCacheConfiguratorSettings, properties);
			}

			if (_singleVMPortalCacheConfiguratorSettings != null) {
				Dictionary<String, Object> properties =
					new HashMapDictionary<>();

				properties.put(
					PortalCacheManager.PORTAL_CACHE_MANAGER_NAME,
					PortalCacheManagerNames.SINGLE_VM);

				_singleVMServiceRegistration = _bundleContext.registerService(
					PortalCacheConfiguratorSettings.class,
					_singleVMPortalCacheConfiguratorSettings, properties);
			}
		}

		private EhcacheExtension(
			BundleContext bundleContext,
			PortalCacheConfiguratorSettings
				multiVMPortalCacheConfiguratorSettings,
			PortalCacheConfiguratorSettings
				singleVMPortalCacheConfiguratorSettings) {

			_bundleContext = bundleContext;
			_multiVMPortalCacheConfiguratorSettings =
				multiVMPortalCacheConfiguratorSettings;
			_singleVMPortalCacheConfiguratorSettings =
				singleVMPortalCacheConfiguratorSettings;
		}

		private final BundleContext _bundleContext;
		private final PortalCacheConfiguratorSettings
			_multiVMPortalCacheConfiguratorSettings;
		private ServiceRegistration<PortalCacheConfiguratorSettings>
			_multiVMServiceRegistration;
		private final PortalCacheConfiguratorSettings
			_singleVMPortalCacheConfiguratorSettings;
		private ServiceRegistration<PortalCacheConfiguratorSettings>
			_singleVMServiceRegistration;

	}

}