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

package com.liferay.portal.profile;

import com.liferay.petra.string.StringPool;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

/**
 * @author Hai Yu
 */
public abstract class BaseEnterpriseDSModulePortalProfile
	extends BaseDSModulePortalProfile {

	@Override
	public void activate() {
		BundleContext bundleContext = componentContext.getBundleContext();

		Bundle bundle = bundleContext.getBundle();

		Dictionary<String, String> headers = bundle.getHeaders(
			StringPool.BLANK);

		if (headers.get("Liferay-Enterprise-App") == null) {
			return;
		}

		componentContext.enableComponent(null);
	}

	protected void init(ComponentContext componentContext) {
		Set<String> supportedPortalProfileNames = new HashSet<>();

		supportedPortalProfileNames.add(PortalProfile.PORTAL_PROFILE_NAME_CE);
		supportedPortalProfileNames.add(PortalProfile.PORTAL_PROFILE_NAME_DXP);

		init(componentContext, supportedPortalProfileNames, null);
	}

}