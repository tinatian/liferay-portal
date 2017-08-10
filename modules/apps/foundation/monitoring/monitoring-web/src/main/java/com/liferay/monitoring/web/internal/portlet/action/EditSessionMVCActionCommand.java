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

package com.liferay.monitoring.web.internal.portlet.action;

import com.liferay.monitoring.web.internal.constants.MonitoringPortletKeys;
import com.liferay.portal.kernel.cluster.ClusterInvokeAcceptor;
import com.liferay.portal.kernel.cluster.ClusterInvokeThreadLocal;
import com.liferay.portal.kernel.cluster.ClusterableInvokerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.framework.service.IdentifiableOSGiService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.servlet.PortalSessionContext;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.lang.reflect.Method;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;

import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Wing Shun Chan
 * @author Philip Jones
 */
@Component(
	property = {
		"javax.portlet.name=" + MonitoringPortletKeys.MONITORING,
		"mvc.command.name=/monitoring/edit_session"
	},
	service = {IdentifiableOSGiService.class, MVCActionCommand.class}
)
public class EditSessionMVCActionCommand
	extends BaseMVCActionCommand implements IdentifiableOSGiService {

	@Override
	public void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		if (!permissionChecker.isCompanyAdmin()) {
			SessionErrors.add(
				actionRequest,
				PrincipalException.MustBeCompanyAdmin.class.getName());

			actionResponse.setRenderParameter("mvcPath", "/error.jsp");

			return;
		}

		invalidateSession(actionRequest);

		sendRedirect(actionRequest, actionResponse);
	}

	@Override
	public String getOSGiServiceIdentifier() {
		return EditSessionMVCActionCommand.class.getName();
	}

	public void invalidateSession(String sessionId) {
		HttpSession userSession = PortalSessionContext.get(sessionId);

		if (userSession != null) {
			userSession.invalidate();
		}
	}

	protected void invalidateSession(ActionRequest actionRequest)
		throws Exception {

		String sessionId = ParamUtil.getString(actionRequest, "sessionId");

		PortletSession portletSession = actionRequest.getPortletSession();

		if (!sessionId.equals(portletSession.getId())) {
			try {
				invalidateSession(sessionId);

				if (ClusterInvokeThreadLocal.isEnabled()) {
					ClusterableInvokerUtil.invokeOnCluster(
						ClusterInvokeAcceptor.class, this,
						_invalidateSessionMethod, new Object[] {sessionId});
				}
			}
			catch (Throwable t) {
				if (_log.isWarnEnabled()) {
					_log.warn("Unable to invalidate session", t);
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditSessionMVCActionCommand.class);

	private static final Method _invalidateSessionMethod;

	static {
		try {
			_invalidateSessionMethod =
				EditSessionMVCActionCommand.class.getMethod(
					"invalidateSession", String.class);
		}
		catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}

}