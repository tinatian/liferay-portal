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

package com.liferay.taglib.servlet;

import com.liferay.portal.kernel.util.ServerDetector;

import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.PageContext;

/**
 * @author Shuyang Zhou
 */
public class JspFactorySwapper {

	public static void swap() {
		if (!ServerDetector.isTomcat() || ServerDetector.isWebSphere()) {
			return;
		}

		JspFactory jspFactory = JspFactory.getDefaultFactory();

		if (jspFactory instanceof JspFactoryWrapper) {
			return;
		}

		synchronized (JspFactorySwapper.class) {
			if (_jspFactoryWrapper == null) {
				if (ServerDetector.isWebSphere()) {
					_jspFactoryWrapper = new WebsphereJspFactory(jspFactory);
				}
				else {
					_jspFactoryWrapper = new JspFactoryWrapper(jspFactory);
				}
			}

			JspFactory.setDefaultFactory(_jspFactoryWrapper);
		}
	}

	private static JspFactoryWrapper _jspFactoryWrapper;

	private static class WebsphereJspFactory extends JspFactoryWrapper {

		@Override
		public PageContext getPageContext(
			Servlet servlet, ServletRequest servletRequest,
			ServletResponse servletResponse, String errorPageURL,
			boolean needsSession, int buffer, boolean autoflush) {

			return _jspFactory.getPageContext(
				servlet, servletRequest, servletResponse, errorPageURL,
				needsSession, buffer, autoflush);
		}

		@Override
		public void releasePageContext(PageContext pageContext) {
			_jspFactory.releasePageContext(pageContext);
		}

		private WebsphereJspFactory(JspFactory jspFactory) {
			super(jspFactory);

			_jspFactory = jspFactory;
		}

		private final JspFactory _jspFactory;

	}

}