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

package com.liferay.portal.util;

import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ProxyTestUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.SubscriptionSender;
import com.liferay.portal.kernel.uuid.PortalUUID;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Mika Koivisto
 */
public class SubscriptionSenderTest {

	@Before
	public void setUp() throws Exception {
		ReflectionTestUtil.setFieldValue(
			CompanyLocalServiceUtil.class, "_service",
			ProxyTestUtil.getProxy(
				CompanyLocalService.class,
				ProxyTestUtil.getProxyMethod(
					"getCompany",
					(Object[] arguments) -> ProxyTestUtil.getProxy(
						Company.class,
						ProxyTestUtil.getProxyMethod(
							"getPortalURL",
							(Object[] args) -> {
								if (args[0].equals(0L)) {
									return "http://www.portal.com";
								}
								else if (args[0].equals(100L)) {
									return "http://www.virtual.com";
								}

								return null;
							})))));

		ReflectionTestUtil.setFieldValue(
			GroupLocalServiceUtil.class, "_service",
			ProxyTestUtil.getProxy(
				GroupLocalService.class,
				ProxyTestUtil.getProxyMethod(
					"getGroup",
					(Object[] arguments) -> {
						if ((arguments.length == 1) &&
							arguments[0].equals(100L)) {

							return ProxyTestUtil.getProxy(Group.class);
						}

						return null;
					})));

		ReflectionTestUtil.setFieldValue(
			PortalUUIDUtil.class, "_portalUUID",
			ProxyTestUtil.getDummyProxy(PortalUUID.class));

		ReflectionTestUtil.setFieldValue(
			PortalUtil.class, "_portal",
			ProxyTestUtil.getDummyProxy(Portal.class));
	}

	@Test
	public void testGetPortalURLWithGroupId() throws Exception {
		SubscriptionSender subscriptionSender = new SubscriptionSender();

		subscriptionSender.setGroupId(100);
		subscriptionSender.setMailId("test-mail-id");

		subscriptionSender.initialize();

		String portalURL = String.valueOf(
			subscriptionSender.getContextAttribute("[$PORTAL_URL$]"));

		Assert.assertEquals("http://www.virtual.com", portalURL);
	}

	@Test
	public void testGetPortalURLWithoutGroupId() throws Exception {
		SubscriptionSender subscriptionSender = new SubscriptionSender();

		subscriptionSender.setMailId("test-mail-id");

		subscriptionSender.initialize();

		String portalURL = String.valueOf(
			subscriptionSender.getContextAttribute("[$PORTAL_URL$]"));

		Assert.assertEquals("http://www.portal.com", portalURL);
	}

	@Test
	public void testGetPortalURLWithServiceContext() throws Exception {
		SubscriptionSender subscriptionSender = new SubscriptionSender();

		subscriptionSender.setMailId("test-mail-id");

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setScopeGroupId(100L);

		subscriptionSender.setServiceContext(serviceContext);

		subscriptionSender.initialize();

		String portalURL = String.valueOf(
			subscriptionSender.getContextAttribute("[$PORTAL_URL$]"));

		Assert.assertEquals("http://www.virtual.com", portalURL);
	}

}