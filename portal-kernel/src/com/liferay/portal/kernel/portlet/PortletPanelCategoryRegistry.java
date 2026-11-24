/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Tina Tian
 */
@ProviderType
public interface PortletPanelCategoryRegistry {

	public String getControlPanelEntryCategory(String portletId);

}