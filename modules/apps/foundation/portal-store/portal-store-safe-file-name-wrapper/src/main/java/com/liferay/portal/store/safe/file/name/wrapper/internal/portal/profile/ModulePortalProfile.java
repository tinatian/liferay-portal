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

package com.liferay.portal.store.safe.file.name.wrapper.internal.portal.profile;

import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.profile.BaseDSModulePortalProfile;
import com.liferay.portal.profile.PortalProfile;
import com.liferay.portal.store.safe.file.name.wrapper.internal.SafeFileNameAdvancedFileSystemStoreWrapper;
import com.liferay.portal.store.safe.file.name.wrapper.internal.SafeFileNameFileSystemStoreWrapper;

import java.util.HashSet;
import java.util.Set;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tina Tian
 */
@Component(immediate = true, service = PortalProfile.class)
public class ModulePortalProfile extends BaseDSModulePortalProfile {

	@Activate
	public void activate(ComponentContext componentContext) {
		Set<String> supportedPortalProfileNames = new HashSet<>();

		supportedPortalProfileNames.add(PortalProfile.PORTAL_PROFILE_NAME_CE);
		supportedPortalProfileNames.add(PortalProfile.PORTAL_PROFILE_NAME_DXP);

		String dlStoreImpl = _props.get(PropsKeys.DL_STORE_IMPL);

		if (dlStoreImpl.equals(
				"com.liferay.portal.store.file.system.FileSystemStore")) {

			init(
				componentContext, supportedPortalProfileNames,
				SafeFileNameFileSystemStoreWrapper.class.getName());
		}
		else if (dlStoreImpl.equals(
					"com.liferay.portal.store.file.system." +
						"AdvancedFileSystemStore")) {

			init(
				componentContext, supportedPortalProfileNames,
				SafeFileNameAdvancedFileSystemStoreWrapper.class.getName());
		}
	}

	@Reference
	private Props _props;

}