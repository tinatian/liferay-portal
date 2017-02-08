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

package com.liferay.portal.template.freemarker.internal;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.template.StringTemplateResource;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateException;
import com.liferay.portal.kernel.template.TemplateResource;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.template.AbstractSingleResourceTemplate;
import com.liferay.portal.template.TemplateContextHelper;
import com.liferay.portal.template.TemplateResourceThreadLocal;

import freemarker.core.ParseException;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateModelException;

import java.io.Writer;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Mika Koivisto
 * @author Tina Tian
 */
public class FreeMarkerTemplate extends AbstractSingleResourceTemplate {

	public FreeMarkerTemplate(
		TemplateResource templateResource,
		TemplateResource errorTemplateResource, Map<String, Object> context,
		Configuration configuration,
		TemplateContextHelper templateContextHelper, boolean privileged,
		long interval) {

		super(
			templateResource, errorTemplateResource, context,
			templateContextHelper, TemplateConstants.LANG_TYPE_FTL, interval);

		_configuration = configuration;
		_privileged = privileged;
	}

	@Override
	protected void handleException(Exception exception, Writer writer)
		throws TemplateException {

		if ((exception instanceof ParseException) ||
			(exception instanceof freemarker.template.TemplateException)) {

			put("exception", exception.getMessage());

			if (templateResource instanceof StringTemplateResource) {
				StringTemplateResource stringTemplateResource =
					(StringTemplateResource)templateResource;

				put("script", stringTemplateResource.getContent());
			}

			if (exception instanceof ParseException) {
				ParseException pe = (ParseException)exception;

				put("column", pe.getColumnNumber());
				put("line", pe.getLineNumber());
			}

			try {
				processTemplate(errorTemplateResource, writer);
			}
			catch (Exception e) {
				throw new TemplateException(
					"Unable to process FreeMarker template " +
						errorTemplateResource.getTemplateId(),
					e);
			}
		}
		else {
			throw new TemplateException(
				"Unable to process FreeMarker template " +
					templateResource.getTemplateId(),
				exception);
		}
	}

	@Override
	protected void processTemplate(
			TemplateResource templateResource, Writer writer)
		throws Exception {

		TemplateResourceThreadLocal.setTemplateResource(
			TemplateConstants.LANG_TYPE_FTL, templateResource);

		try {
			Template template = null;

			if (_privileged) {
				template = AccessController.doPrivileged(
					new TemplatePrivilegedExceptionAction(templateResource));
			}
			else {
				template = _configuration.getTemplate(
					getTemplateResourceUUID(templateResource),
					TemplateConstants.DEFAUT_ENCODING);
			}

			template.process(context, writer);
		}
		catch (PrivilegedActionException pae) {
			throw pae.getException();
		}
		finally {
			TemplateResourceThreadLocal.setTemplateResource(
				TemplateConstants.LANG_TYPE_FTL, null);
		}
	}

	private void _initializeContext() throws TemplateModelException {
		boolean isSetupComplete = false;

		Object userObject = context.get("user");

		if (userObject != null) {
			User user = (User)userObject;

			isSetupComplete = user.isSetupComplete();
		}

		String cssMainFile = StringPool.BLANK;
		boolean isSignedIn = false;
		String jsMainFile = StringPool.BLANK;

		Object themeDisplayObject = context.get("themeDisplay");
		HttpServletRequest httpServletRequest = (HttpServletRequest)context.get(
			"request");

		if (themeDisplayObject != null) {
			ThemeDisplay themeDisplay = (ThemeDisplay)themeDisplayObject;

			cssMainFile = HtmlUtil.escape(
				PortalUtil.getStaticResourceURL(
					httpServletRequest,
					themeDisplay.getPathThemeCss() + "/main.css"));

			isSignedIn = themeDisplay.isSignedIn();

			jsMainFile = HtmlUtil.escape(
				PortalUtil.getStaticResourceURL(
					httpServletRequest,
					themeDisplay.getPathThemeJavaScript() + "/main.js"));
		}

		context.put("css_main_file", cssMainFile);
		context.put("is_setup_complete", isSetupComplete);
		context.put("is_signed_in", isSignedIn);
		context.put("js_main_file", jsMainFile);
	}

	private final Configuration _configuration;
	private final boolean _privileged;

	private class TemplatePrivilegedExceptionAction
		implements PrivilegedExceptionAction<Template> {

		public TemplatePrivilegedExceptionAction(
			TemplateResource templateResource) {

			_templateResource = templateResource;
		}

		@Override
		public Template run() throws Exception {
			return _configuration.getTemplate(
				getTemplateResourceUUID(_templateResource),
				TemplateConstants.DEFAUT_ENCODING);
		}

		private final TemplateResource _templateResource;

	}

}