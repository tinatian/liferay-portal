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

package com.liferay.depot.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.GroupProvider;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.depot.web.internal.constants.DepotPortletKeys;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	immediate = true,
	property = {
		"panel.app.order:Integer=100",
		"panel.category.key=" + PanelCategoryKeys.APPLICATIONS_MENU_APPLICATIONS_CONTENT
	},
	service = PanelApp.class
)
public class DepotAdminPanelApp extends BasePanelApp {

	@Override
	public String getPortletId() {
		return DepotPortletKeys.DEPOT_ADMIN;
	}

	@Override
	public PortletURL getPortletURL(HttpServletRequest httpServletRequest) {
		return _portal.getControlPanelPortletURL(
			httpServletRequest, getGroup(httpServletRequest), getPortletId(), 0,
			0, PortletRequest.RENDER_PHASE);
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		init(
			_resourceActions, _portlet, _groupProvider, _portletLocalService,
			properties);
	}

	@Override
	protected Group getGroup(HttpServletRequest httpServletRequest) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return themeDisplay.getControlPanelGroup();
	}

	@Reference
	private GroupProvider _groupProvider;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(javax.portlet.name=" + DepotPortletKeys.DEPOT_ADMIN + ")"
	)
	private Portlet _portlet;

	@Reference
	private PortletLocalService _portletLocalService;

	@Reference
	private ResourceActions _resourceActions;

}