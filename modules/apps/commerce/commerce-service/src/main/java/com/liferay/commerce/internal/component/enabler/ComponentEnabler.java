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

package com.liferay.commerce.internal.component.enabler;

import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.service.impl.CommerceAddressLocalServiceImpl;
import com.liferay.commerce.service.impl.CommerceOrderItemLocalServiceImpl;
import com.liferay.osgi.util.ComponentUtil;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Tina Tian
 */
@Component(immediate = true, service = {})
public class ComponentEnabler {

	@Activate
	protected void activate(ComponentContext componentContext) {
		ComponentUtil.enableComponents(
			CommerceOrderLocalService.class, null, componentContext,
			CommerceOrderItemLocalServiceImpl.class,
			CommerceAddressLocalServiceImpl.class);
	}

}