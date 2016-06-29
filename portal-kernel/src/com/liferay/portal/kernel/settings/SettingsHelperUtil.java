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

package com.liferay.portal.kernel.settings;

import com.liferay.registry.collections.ServiceTrackerCollections;
import com.liferay.registry.collections.ServiceTrackerList;

/**
 * @author Tina Tian
 */
public class SettingsHelperUtil {

	public static Settings getCompanyPortletPreferencesSettings(
		long companyId, String settingsId,
		SettingsLocator parentSettingsLocator) {

		return getSettingsHelper().getCompanyPortletPreferencesSettings(
			companyId, settingsId, parentSettingsLocator);
	}

	public static SettingsHelper getSettingsHelper() {
		return _settingsHelpers.get(0);
	}

	public Settings getConfigurationBeanSettings(
		String settingsId, SettingsLocator parentSettingsLocator) {

		return getSettingsHelper().getConfigurationBeanSettings(
			settingsId, parentSettingsLocator);
	}

	public Settings getGroupPortletPreferencesSettings(
		long groupId, String settingsId,
		SettingsLocator parentSettingsLocator) {

		return getSettingsHelper().getGroupPortletPreferencesSettings(
			groupId, settingsId, parentSettingsLocator);
	}

	public Settings getPortalPreferencesSettings(
		long companyId, SettingsLocator parentSettingsLocator) {

		return getSettingsHelper().getPortalPreferencesSettings(
			companyId, parentSettingsLocator);
	}

	public Settings getPortalPropertiesSettings() {
		return getSettingsHelper().getPortalPropertiesSettings();
	}

	public Settings getPortletInstancePortletPreferencesSettings(
		long companyId, long plid, String portletId,
		SettingsLocator parentSettingsLocator) {

		return getSettingsHelper().getPortletInstancePortletPreferencesSettings(
			companyId, plid, portletId, parentSettingsLocator);
	}

	private static final ServiceTrackerList<SettingsHelper> _settingsHelpers =
		ServiceTrackerCollections.openList(SettingsHelper.class);

}