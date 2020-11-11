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

package com.liferay.product.navigation.applications.menu.web.internal.portlet.action.test.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.GroupProvider;
import com.liferay.application.list.PanelApp;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.product.navigation.applications.menu.web.internal.portlet.action.test.constants.ApplicationsMenuTestPanelCategoryKeys;
import com.liferay.product.navigation.applications.menu.web.internal.portlet.action.test.constants.ApplicationsMenuTestPortletKeys;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	immediate = true,
	property = {
		"panel.app.order:Integer=100",
		"panel.category.key=" + ApplicationsMenuTestPanelCategoryKeys.APPLICATIONS_MENU_TEST_PANEL_CATEGORY
	},
	service = PanelApp.class
)
public class ApplicationsMenuTestPanelApp extends BasePanelApp {

	@Override
	public String getPortletId() {
		return ApplicationsMenuTestPortletKeys.APPLICATIONS_MENU_TEST_PORTLET;
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		init(
			_resourceActions, _portlet, _groupProvider, _portletLocalService,
			properties);
	}

	@Reference
	private GroupProvider _groupProvider;

	@Reference(
		target = "(javax.portlet.name=" + ApplicationsMenuTestPortletKeys.APPLICATIONS_MENU_TEST_PORTLET + ")"
	)
	private Portlet _portlet;

	@Reference
	private PortletLocalService _portletLocalService;

	@Reference
	private ResourceActions _resourceActions;

}