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

package com.liferay.portal.cache.ehcache.internal;

import com.liferay.portal.cache.PortalCacheBootstrapLoaderFactory;
import com.liferay.portal.cache.PortalCacheListenerFactory;
import com.liferay.portal.cache.PortalCacheManagerListenerFactory;
import com.liferay.portal.cache.ehcache.internal.configurator.MultiVMEhcachePortalCacheManagerConfigurator;
import com.liferay.portal.kernel.cache.PortalCacheManager;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.cache.configurator.PortalCacheConfiguratorSettings;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.AggregateClassLoader;
import com.liferay.portal.kernel.util.ClassLoaderUtil;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;

import java.io.Serializable;

import javax.management.MBeanServer;

/**
 * @author Tina Tian
 */
public class MultiVMEhcachePortalCacheManager
	<K extends Serializable, V extends Serializable>
		extends EhcachePortalCacheManager<K, V> {

	protected void activate() {
		setClusterAware(true);
		setConfigFile(props.get(PropsKeys.EHCACHE_MULTI_VM_CONFIG_LOCATION));
		setDefaultConfigFile(_DEFAULT_CONFIG_FILE_NAME);
		setMpiOnly(true);
		setPortalCacheManagerName(PortalCacheManagerNames.MULTI_VM);

		Thread currentThread = Thread.currentThread();

		ClassLoader contextClassLoader = currentThread.getContextClassLoader();

		Class<?> clazz = getClass();

		ClassLoaderUtil.setContextClassLoader(
			AggregateClassLoader.getAggregateClassLoader(
				contextClassLoader, clazz.getClassLoader()));

		try {
			initialize();
		}
		finally {
			ClassLoaderUtil.setContextClassLoader(contextClassLoader);
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Activated " + PortalCacheManagerNames.MULTI_VM);
		}
	}

	protected void deactivate() {
		destroy();
	}

	protected void setMBeanServer(MBeanServer mBeanServer) {
		this.mBeanServer = mBeanServer;
	}

	protected void setMultiVMEhcachePortalCacheManagerConfigurator(
		MultiVMEhcachePortalCacheManagerConfigurator
			multiVMEhcachePortalCacheManagerConfigurator) {

		this.baseEhcachePortalCacheManagerConfigurator =
			multiVMEhcachePortalCacheManagerConfigurator;
	}

	protected void setPortalCacheBootstrapLoaderFactory(
		PortalCacheBootstrapLoaderFactory portalCacheBootstrapLoaderFactory) {

		this.portalCacheBootstrapLoaderFactory =
			portalCacheBootstrapLoaderFactory;
	}

//	@Reference(
//		cardinality = ReferenceCardinality.MULTIPLE,
//		policy = ReferencePolicy.DYNAMIC,
//		policyOption = ReferencePolicyOption.GREEDY,
//		target = "(" + PortalCacheManager.PORTAL_CACHE_MANAGER_NAME + "=" + PortalCacheManagerNames.MULTI_VM + ")"
//	)
	protected void setPortalCacheConfiguratorSettings(
		PortalCacheConfiguratorSettings portalCacheConfiguratorSettings) {

		reconfigure(portalCacheConfiguratorSettings);
	}

	protected void setPortalCacheListenerFactory(
		PortalCacheListenerFactory portalCacheListenerFactory) {

		this.portalCacheListenerFactory = portalCacheListenerFactory;
	}

	protected void setPortalCacheManagerListenerFactory(
		PortalCacheManagerListenerFactory<PortalCacheManager<K, V>>
			portalCacheManagerListenerFactory) {

		this.portalCacheManagerListenerFactory =
			portalCacheManagerListenerFactory;
	}

	protected void setProps(Props props) {
		this.props = props;
	}

	protected void unsetPortalCacheConfiguratorSettings(
		PortalCacheConfiguratorSettings portalCacheConfiguratorSettings) {
	}

	private static final String _DEFAULT_CONFIG_FILE_NAME =
		"/ehcache/liferay-multi-vm-clustered.xml";

	private static final Log _log = LogFactoryUtil.getLog(
		MultiVMEhcachePortalCacheManager.class);

}