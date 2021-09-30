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

package com.liferay.portal.web.internal.session.replication;

import com.liferay.portal.kernel.servlet.WrapHttpServletRequestFilter;
import com.liferay.portal.servlet.filters.BasePortalFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Dante Wang
 */
public class SessionReplicationFilter
	extends BasePortalFilter implements WrapHttpServletRequestFilter {

	@Override
	public HttpServletRequest getWrappedHttpServletRequest(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		HttpServletRequest wrappedHttpServletRequest = httpServletRequest;

		while (wrappedHttpServletRequest instanceof HttpServletRequestWrapper) {
			if (wrappedHttpServletRequest instanceof
					SessionReplicationHttpServletRequest) {

				return httpServletRequest;
			}

			HttpServletRequestWrapper httpServletRequestWrapper =
				(HttpServletRequestWrapper)wrappedHttpServletRequest;

			wrappedHttpServletRequest =
				(HttpServletRequest)httpServletRequestWrapper.getRequest();
		}

		return new SessionReplicationHttpServletRequest(httpServletRequest);
	}

}