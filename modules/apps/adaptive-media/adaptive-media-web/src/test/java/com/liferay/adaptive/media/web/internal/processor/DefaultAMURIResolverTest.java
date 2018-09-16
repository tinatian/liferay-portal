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

package com.liferay.adaptive.media.web.internal.processor;

import com.liferay.adaptive.media.AMURIResolver;
import com.liferay.adaptive.media.web.internal.constants.AMWebConstants;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.MockHelperUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;

import java.net.URI;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Adolfo Pérez
 */
public class DefaultAMURIResolverTest {

	@Before
	public void setUp() {
		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(_portal);

		ReflectionTestUtil.setFieldValue(_amURIResolver, "_portal", _portal);
	}

	@Test
	public void testMediaURIWhenPathDoesNotEndInSlash() throws Exception {
		String pathModule = StringPool.SLASH + RandomTestUtil.randomString();

		MockHelperUtil.setMethodAlwaysReturnExpected(
			_portal, "getPathModule", pathModule);

		URI relativeURI = URI.create(RandomTestUtil.randomString());

		URI uri = _amURIResolver.resolveURI(relativeURI);

		String uriString = uri.toString();

		Assert.assertTrue(uriString, uriString.contains(pathModule));
		Assert.assertTrue(
			uriString, uriString.contains(AMWebConstants.SERVLET_PATH));
		Assert.assertTrue(
			uriString, uriString.contains(relativeURI.toString()));
	}

	@Test
	public void testMediaURIWhenPathEndsInSlash() throws Exception {
		String pathModule =
			StringPool.SLASH + RandomTestUtil.randomString() + StringPool.SLASH;

		MockHelperUtil.setMethodAlwaysReturnExpected(
			_portal, "getPathModule", pathModule);

		URI relativeURI = URI.create(RandomTestUtil.randomString());

		URI uri = _amURIResolver.resolveURI(relativeURI);

		String uriString = uri.toString();

		Assert.assertTrue(uriString, uriString.contains(pathModule));
		Assert.assertTrue(
			uriString, uriString.contains(AMWebConstants.SERVLET_PATH));
		Assert.assertTrue(
			uriString, uriString.contains(relativeURI.toString()));
	}

	private final AMURIResolver _amURIResolver = new DefaultAMURIResolver();
	private final Portal _portal = MockHelperUtil.initMock(Portal.class);

}