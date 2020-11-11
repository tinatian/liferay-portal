/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.portal.workflow.kaleo.forms.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.GroupProvider;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.workflow.kaleo.forms.constants.KaleoFormsPortletKeys;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Inácio Nery
 */
@Component(
	immediate = true,
	property = {
		"panel.category.key=" + PanelCategoryKeys.SITE_ADMINISTRATION_CONTENT,
		"service.ranking:Integer=1200"
	},
	service = PanelApp.class
)
public class KaleoFormsAdminPanelApp extends BasePanelApp {

	@Override
	public String getPortletId() {
		return KaleoFormsPortletKeys.KALEO_FORMS_ADMIN;
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
		target = "(javax.portlet.name=" + KaleoFormsPortletKeys.KALEO_FORMS_ADMIN + ")"
	)
	private Portlet _portlet;

	@Reference
	private PortletLocalService _portletLocalService;

	@Reference
	private ResourceActions _resourceActions;

}