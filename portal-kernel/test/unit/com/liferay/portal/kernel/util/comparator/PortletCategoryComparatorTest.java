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

package com.liferay.portal.kernel.util.comparator;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.PortletCategory;
import com.liferay.portal.kernel.test.ProxyTestUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsUtil;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Eduardo Garcia
 */
public class PortletCategoryComparatorTest {

	@Test
	public void testCompareLocalized() {
		PropsUtil.setProps(ProxyTestUtil.getDummyProxy(Props.class));

		ReflectionTestUtil.setFieldValue(
			LanguageUtil.class, "_language",
			ProxyTestUtil.getProxy(
				Language.class,
				ProxyTestUtil.getProxyMethod(
					"get",
					(Object[] args) -> {
						if ((args.length == 2) &&
							LocaleUtil.SPAIN.equals(args[0])) {

							if ("area".equals(args[1])) {
								return "Area";
							}
							else if ("zone".equals(args[1])) {
								return "Zona";
							}
						}

						return null;
					})));

		PortletCategory portletCategory1 = new PortletCategory("area");
		PortletCategory portletCategory2 = new PortletCategory("zone");

		PortletCategoryComparator portletCategoryComparator =
			new PortletCategoryComparator(LocaleUtil.SPAIN);

		int value = portletCategoryComparator.compare(
			portletCategory1, portletCategory2);

		Assert.assertTrue(value < 0);
	}

}