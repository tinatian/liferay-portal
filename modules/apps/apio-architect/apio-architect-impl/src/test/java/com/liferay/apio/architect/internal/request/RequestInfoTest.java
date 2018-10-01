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

package com.liferay.apio.architect.internal.request;

import com.liferay.apio.architect.internal.response.control.Embedded;
import com.liferay.apio.architect.internal.response.control.Fields;
import com.liferay.apio.architect.internal.url.ApplicationURL;
import com.liferay.apio.architect.internal.url.ServerURL;
import com.liferay.apio.architect.language.AcceptLanguage;
import com.liferay.portal.kernel.util.ProxyFactory;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Alejandro Hernández
 */
public class RequestInfoTest {

	@Before
	public void setUp() {
		_httpServletRequest = ProxyFactory.newDummyInstance(
			HttpServletRequest.class);
		_serverURL = () -> null;
		_applicationURL = () -> null;
		_embedded = s -> false;
		_fields = strings -> null;
		_acceptLanguage = () -> null;
	}

	@Test
	public void testBuildingRequestInfoCreatesValidRequestInfo() {
		RequestInfo requestInfo = RequestInfo.create(
			builder -> builder.httpServletRequest(
				_httpServletRequest
			).serverURL(
				_serverURL
			).applicationURL(
				_applicationURL
			).embedded(
				_embedded
			).fields(
				_fields
			).language(
				_acceptLanguage
			).build());

		Assert.assertSame(requestInfo.getEmbedded(), _embedded);
		Assert.assertSame(requestInfo.getFields(), _fields);
		Assert.assertSame(
			requestInfo.getHttpServletRequest(), _httpServletRequest);
		Assert.assertSame(requestInfo.getAcceptLanguage(), _acceptLanguage);
		Assert.assertSame(requestInfo.getServerURL(), _serverURL);
		Assert.assertSame(requestInfo.getApplicationURL(), _applicationURL);
	}

	private AcceptLanguage _acceptLanguage;
	private ApplicationURL _applicationURL;
	private Embedded _embedded;
	private Fields _fields;
	private HttpServletRequest _httpServletRequest;
	private ServerURL _serverURL;

}