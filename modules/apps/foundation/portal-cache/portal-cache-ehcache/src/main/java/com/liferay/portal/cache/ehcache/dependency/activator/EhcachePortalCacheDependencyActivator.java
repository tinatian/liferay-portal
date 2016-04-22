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

package com.liferay.portal.cache.ehcache.dependency.activator;

import com.liferay.portal.cache.PortalCacheBootstrapLoaderFactory;
import com.liferay.portal.cache.PortalCacheListenerFactory;
import com.liferay.portal.cache.PortalCacheManagerListenerFactory;
import com.liferay.portal.cache.PortalCacheReplicatorFactory;
import com.liferay.portal.cache.ehcache.internal.configurator.MultiVMEhcachePortalCacheManagerConfigurator;
import com.liferay.portal.cache.ehcache.internal.configurator.SingleVMEhcachePortalCacheManagerConfigurator;
import com.liferay.portal.cache.ehcache.internal.event.EhcachePortalCacheListenerFactory;
import com.liferay.portal.kernel.cache.PortalCacheManager;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.util.StringPool;

import java.util.Dictionary;

import javax.management.MBeanServer;

import org.apache.felix.dm.Component;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.apache.felix.dm.ServiceDependency;

import org.osgi.framework.BundleContext;

/**
 * @author Tina Tian
 */
public class EhcachePortalCacheDependencyActivator
	extends DependencyActivatorBase {

	@Override
	public void init(
			BundleContext bundleContext, DependencyManager dependencyManager)
		throws Exception {

		dependencyManager.add(getPortalCacheListenerFactoryComponent());
		dependencyManager.add(getMultiVMEhcachePortalCacheManagerComponent());
		dependencyManager.add(getSingleVMEhcachePortalCacheManagerComponent());
	}

	protected String getDynamicFilterString() {
		String dynamicFilterString = StringPool.BLANK;

		if (!ReleaseInfo.getName().contains("Community")) {
			dynamicFilterString = "(EE=true)";
		}

		return dynamicFilterString;
	}

	protected Component getMultiVMEhcachePortalCacheManagerComponent() {
		Component component = createComponent();

		Dictionary<String, String> dictionary = new HashMapDictionary<>();

		dictionary.put(
			PortalCacheManager.PORTAL_CACHE_MANAGER_NAME,
			PortalCacheManagerNames.MULTI_VM);

		component.setInterface(PortalCacheManager.class.getName(), dictionary);
		component.setCallbacks(null, "activate", "deactivate", "deactivate");

		component.add(
			getServiceDependency(MBeanServer.class, "setMBeanServer"));
		component.add(
			getServiceDependency(
				PortalCacheListenerFactory.class,
				"setPortalCacheListenerFactory"));
		component.add(
			getServiceDependency(
				PortalCacheManagerListenerFactory.class,
				"setPortalCacheManagerListenerFactory"));
		component.add(getServiceDependency(Props.class, "setProps"));
		component.add(
			getServiceDependency(
				MultiVMEhcachePortalCacheManagerConfigurator.class,
				"setMultiVMEhcachePortalCacheManagerConfigurator"));

		ServiceDependency serviceDependency = createServiceDependency();

		serviceDependency.setService(
			PortalCacheBootstrapLoaderFactory.class, getDynamicFilterString());
		serviceDependency.setRequired(true);
		serviceDependency.setCallbacks(
			"setPortalCacheBootstrapLoaderFactory", null);

		component.add(serviceDependency);

		return component;
	}

	protected Component getPortalCacheListenerFactoryComponent() {
		Component component = createComponent();

		component.setInterface(
			PortalCacheListenerFactory.class.getName(), null);
		component.setImplementation(EhcachePortalCacheListenerFactory.class);

		ServiceDependency serviceDependency = createServiceDependency();

		serviceDependency.setService(
			PortalCacheReplicatorFactory.class, getDynamicFilterString());
		serviceDependency.setRequired(true);
		serviceDependency.setCallbacks("setPortalCacheReplicatorFactory", null);

		component.add(serviceDependency);

		return component;
	}

	protected ServiceDependency getServiceDependency(
		Class<?> targetClass, String callback) {

		ServiceDependency serviceDependency = createServiceDependency();

		serviceDependency.setService(targetClass);
		serviceDependency.setRequired(true);
		serviceDependency.setCallbacks(callback, null);

		return serviceDependency;
	}

	protected Component getSingleVMEhcachePortalCacheManagerComponent() {
		Component component = createComponent();

		Dictionary<String, String> dictionary = new HashMapDictionary<>();

		dictionary.put(
			PortalCacheManager.PORTAL_CACHE_MANAGER_NAME,
			PortalCacheManagerNames.SINGLE_VM);

		component.setInterface(PortalCacheManager.class.getName(), dictionary);
		component.setCallbacks(null, "activate", "deactivate", "deactivate");

		component.add(
			getServiceDependency(MBeanServer.class, "setMBeanServer"));
		component.add(
			getServiceDependency(
				PortalCacheListenerFactory.class,
				"setPortalCacheListenerFactory"));
		component.add(
			getServiceDependency(
				PortalCacheManagerListenerFactory.class,
				"setPortalCacheManagerListenerFactory"));
		component.add(getServiceDependency(Props.class, "setProps"));
		component.add(
			getServiceDependency(
				SingleVMEhcachePortalCacheManagerConfigurator.class,
				"setSingleVMEhcachePortalCacheManagerConfigurator"));

		return component;
	}

}