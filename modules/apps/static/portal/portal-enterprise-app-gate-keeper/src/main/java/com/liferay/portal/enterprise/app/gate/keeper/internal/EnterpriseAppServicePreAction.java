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

import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.license.util.LicenseManager;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

/**
 * @author Tina Tian
 */
public class EnterpriseAppServicePreAction extends Action {

	public EnterpriseAppServicePreAction(
		Map<String, Set<Bundle>> verifiedBundles) {

		_verifiedBundles = verifiedBundles;
	}

	@Override
	public void run(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws ActionException {

		ClassLoader targetClassLoader = null;

		try {
			for (Map.Entry<String, Set<Bundle>> entry :
					_verifiedBundles.entrySet()) {

				if (LicenseManagerUtil.getLicenseState(entry.getKey()) !=
						LicenseManager.STATE_EXPIRED) {

					continue;
				}

				for (Bundle bundle : entry.getValue()) {
					BundleWiring bundleWiring = bundle.adapt(
						BundleWiring.class);

					if (targetClassLoader == null) {
						targetClassLoader = _getTargetClassLoader(
							httpServletRequest);
					}

					if (targetClassLoader == bundleWiring.getClassLoader()) {
						httpServletResponse.sendError(
							403,
							StringBundler.concat(
								"Your license for ", entry.getKey(),
								" has expired"));

						return;
					}
				}
			}
		}
		catch (Exception exception) {
			throw new ActionException(exception);
		}
	}

	private ClassLoader _getTargetClassLoader(
		HttpServletRequest httpServletRequest) {

		String portletId = httpServletRequest.getParameter("p_p_id");

		if (Validator.isNotNull(portletId)) {
			Portlet portlet = PortletLocalServiceUtil.getPortletById(
				PortalUtil.getCompanyId(httpServletRequest), portletId);

			if (portlet != null) {
				Class<?> clazz = portlet.getClass();

				return clazz.getClassLoader();
			}
		}

		return null;
	}

	private final Map<String, Set<Bundle>> _verifiedBundles;

}