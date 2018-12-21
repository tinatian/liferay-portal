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

package com.liferay.portal.cache.ehcache.internal.extender;

import com.liferay.osgi.felix.util.AbstractExtender;
import com.liferay.portal.kernel.cache.PortalCacheManager;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.cache.configurator.PortalCacheConfiguratorSettings;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.configuration.ConfigurationFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.Validator;

import java.util.Dictionary;

import org.apache.felix.utils.extender.Extension;

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
public class EhcachePortalCacheExtender extends AbstractExtender {

	@Activate
	protected void activate(BundleContext bundleContext) throws Exception {
		start(bundleContext);
	}

	@Deactivate
	protected void deactivate(BundleContext bundleContext) throws Exception {
		stop(bundleContext);
	}

	@Override
	protected void debug(Bundle bundle, String message) {
		if (_log.isDebugEnabled()) {
			_log.debug(message);
		}
	}

	@Override
	protected Extension doCreateExtension(Bundle bundle) throws Exception {
		BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

		ClassLoader classLoader = bundleWiring.getClassLoader();

		String multiVMConfigurationFile = null;
		String singleVMConfigurationFile = null;

		if (classLoader.getResource("portlet.properties") != null) {
			Configuration configuration =
				ConfigurationFactoryUtil.getConfiguration(
					classLoader, "portlet");

			multiVMConfigurationFile = configuration.get(
				PropsKeys.EHCACHE_MULTI_VM_CONFIG_LOCATION);
			singleVMConfigurationFile = configuration.get(
				PropsKeys.EHCACHE_SINGLE_VM_CONFIG_LOCATION);
		}

		if (Validator.isNull(multiVMConfigurationFile)) {
			multiVMConfigurationFile =
				"/META-INF/module-multi-vm-clustered.xml";
		}

		if (Validator.isNull(singleVMConfigurationFile)) {
			singleVMConfigurationFile = "/META-INF/module-single-vm.xml";
		}

		if ((classLoader.getResource(multiVMConfigurationFile) == null) &&
			(classLoader.getResource(singleVMConfigurationFile) == null)) {

			return null;
		}

		return new EhcachePortalCacheExtension(
			bundle.getBundleContext(), classLoader, multiVMConfigurationFile,
			singleVMConfigurationFile);
	}

	@Override
	protected void error(String message, Throwable throwable) {
		_log.error(message, throwable);
	}

	@Override
	protected void warn(Bundle bundle, String message, Throwable throwable) {
		if (_log.isWarnEnabled()) {
			_log.warn(message, throwable);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EhcachePortalCacheExtender.class);

	private class EhcachePortalCacheExtension implements Extension {

		@Override
		public void destroy() {
			if (_multiVMServiceRegistration != null) {
				_multiVMServiceRegistration.unregister();
			}

			if (_singleVMServiceRegistration != null) {
				_singleVMServiceRegistration.unregister();
			}
		}

		@Override
		public void start() throws Exception {
			_multiVMServiceRegistration =
				_registerPortalCacheConfiguratorSettings(
					_bundleContext, PortalCacheManagerNames.MULTI_VM,
					_classLoader, _multiVMConfigurationFile);
			_singleVMServiceRegistration =
				_registerPortalCacheConfiguratorSettings(
					_bundleContext, PortalCacheManagerNames.SINGLE_VM,
					_classLoader, _singleVMConfigurationFile);
		}

		private EhcachePortalCacheExtension(
			BundleContext bundleContext, ClassLoader classLoader,
			String multiVMConfigurationFile, String singleVMConfigurationFile) {

			_bundleContext = bundleContext;
			_classLoader = classLoader;
			_multiVMConfigurationFile = multiVMConfigurationFile;
			_singleVMConfigurationFile = singleVMConfigurationFile;
		}

		private ServiceRegistration<PortalCacheConfiguratorSettings>
			_registerPortalCacheConfiguratorSettings(
				BundleContext bundleContext, String portalCacheManagerName,
				ClassLoader classLoader,
				String portalCacheConfigrationLocation) {

			if (classLoader.getResource(portalCacheConfigrationLocation) ==
					null) {

				return null;
			}

			Dictionary<String, Object> properties = new HashMapDictionary<>();

			properties.put(
				PortalCacheManager.PORTAL_CACHE_MANAGER_NAME,
				portalCacheManagerName);

			return bundleContext.registerService(
				PortalCacheConfiguratorSettings.class,
				new PortalCacheConfiguratorSettings(
					classLoader, portalCacheConfigrationLocation),
				properties);
		}

		private final BundleContext _bundleContext;
		private final ClassLoader _classLoader;
		private final String _multiVMConfigurationFile;
		private ServiceRegistration<PortalCacheConfiguratorSettings>
			_multiVMServiceRegistration;
		private final String _singleVMConfigurationFile;
		private ServiceRegistration<PortalCacheConfiguratorSettings>
			_singleVMServiceRegistration;

	}

}