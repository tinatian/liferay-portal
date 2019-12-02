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

package com.liferay.change.tracking.internal.aop;

import com.liferay.change.tracking.internal.CTServiceThreadLocal;
import com.liferay.petra.lang.SafeClosable;
import com.liferay.portal.kernel.aop.AopMethodInvocation;
import com.liferay.portal.kernel.aop.ChainableMethodAdvice;
import com.liferay.portal.kernel.service.change.tracking.CTService;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Preston Crary
 */
@Component(immediate = true, service = ChainableMethodAdvice.class)
public class CTServiceAdvice extends ChainableMethodAdvice {

	@Override
	public Object createMethodContext(
		Class<?> targetClass, Method method,
		Map<Class<? extends Annotation>, Annotation> annotations) {

		if (CTService.class.isAssignableFrom(targetClass)) {
			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}

	@Override
	public Object invoke(
			AopMethodInvocation aopMethodInvocation, Object[] arguments)
		throws Throwable {

		try (SafeClosable safeClosable = CTServiceThreadLocal.setCTService(
				aopMethodInvocation.getAdviceMethodContext())) {

			return super.invoke(aopMethodInvocation, arguments);
		}
	}

}