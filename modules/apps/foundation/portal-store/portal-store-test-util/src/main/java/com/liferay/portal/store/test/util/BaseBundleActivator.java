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

package com.liferay.portal.store.test.util;

import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringBundler;

import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.service.component.runtime.ServiceComponentRuntime;
import org.osgi.service.component.runtime.dto.ComponentDescriptionDTO;
import org.osgi.util.promise.Promise;

/**
 * @author Tina Tian
 */
public abstract class BaseBundleActivator implements BundleActivator {

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		_initTargetBundle(bundleContext);

		ServiceReference<ServiceComponentRuntime> serviceReference =
			bundleContext.getServiceReference(ServiceComponentRuntime.class);

		try {
			if (serviceReference == null) {
				throw new Exception("Unable to find ServiceComponentRuntime");
			}

			ServiceComponentRuntime serviceComponentRuntime =
				bundleContext.getService(serviceReference);

			if (serviceComponentRuntime == null) {
				throw new Exception("Unable to find ServiceComponentRuntime");
			}

			String dlStoreImpl = PropsUtil.get(PropsKeys.DL_STORE_IMPL);

			for (String componentName : getComponentNames()) {
				if (!dlStoreImpl.equals(componentName)) {
					_enableComponent(serviceComponentRuntime, componentName);
				}
			}
		}
		finally {
			bundleContext.ungetService(serviceReference);
		}
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		ServiceReference<ServiceComponentRuntime> serviceReference =
			bundleContext.getServiceReference(ServiceComponentRuntime.class);

		try {
			if (serviceReference == null) {
				throw new Exception("Unable to find ServiceComponentRuntime");
			}

			ServiceComponentRuntime serviceComponentRuntime =
				bundleContext.getService(serviceReference);

			if (serviceComponentRuntime == null) {
				throw new Exception("Unable to find ServiceComponentRuntime");
			}

			String dlStoreImpl = PropsUtil.get(PropsKeys.DL_STORE_IMPL);

			for (String componentName : getComponentNames()) {
				if (!dlStoreImpl.equals(componentName)) {
					_disableComponent(serviceComponentRuntime, componentName);
				}
			}
		}
		finally {
			bundleContext.ungetService(serviceReference);
		}
	}

	protected abstract Set<String> getComponentNames();

	protected abstract String getTargetBundleName();

	private void _disableComponent(
			ServiceComponentRuntime serviceComponentRuntime,
			String componentName)
		throws Exception {

		ComponentDescriptionDTO componentDescriptionDTO =
			serviceComponentRuntime.getComponentDescriptionDTO(
				_targetBundle, componentName);

		if (componentDescriptionDTO == null) {
			return;
		}

		Promise<Void> promise = serviceComponentRuntime.disableComponent(
			componentDescriptionDTO);

		promise.getValue();
	}

	private void _enableComponent(
			ServiceComponentRuntime serviceComponentRuntime,
			String componentName)
		throws Exception {

		ComponentDescriptionDTO componentDescriptionDTO =
			serviceComponentRuntime.getComponentDescriptionDTO(
				_targetBundle, componentName);

		if (componentDescriptionDTO == null) {
			StringBundler sb = new StringBundler(9);

			sb.append("Unable to find component description ");
			sb.append(componentName);
			sb.append(" in targetBundle {id:");
			sb.append(_targetBundle.getBundleId());
			sb.append(" name:");
			sb.append(_targetBundle.getSymbolicName());
			sb.append(" version:");
			sb.append(_targetBundle.getVersion());
			sb.append("}");

			throw new Exception(sb.toString());
		}

		Promise<Void> promise = serviceComponentRuntime.enableComponent(
			componentDescriptionDTO);

		promise.getValue();
	}

	private void _initTargetBundle(BundleContext bundleContext) {
		String targetBundleName = getTargetBundleName();

		Bundle targetBundle = null;

		for (Bundle bundle : bundleContext.getBundles()) {
			if (targetBundleName.equals(bundle.getSymbolicName())) {
				if (targetBundle == null) {
					targetBundle = bundle;

					continue;
				}

				Version targetBundleVersion = targetBundle.getVersion();

				if (targetBundleVersion.compareTo(bundle.getVersion()) < 0) {
					targetBundle = bundle;
				}
			}
		}

		if (targetBundle == null) {
			throw new IllegalStateException("Unable to find target bundle");
		}

		_targetBundle = targetBundle;
	}

	private Bundle _targetBundle;

}