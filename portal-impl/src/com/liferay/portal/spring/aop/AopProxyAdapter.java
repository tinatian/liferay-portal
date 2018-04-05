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

package com.liferay.portal.spring.aop;

import com.liferay.portal.kernel.aop.AopProxy;

/**
 * @author Tina Tian
 */
public class AopProxyAdapter
	implements org.springframework.aop.framework.AopProxy {

	public AopProxyAdapter(AopProxy aopProxy, ClassLoader defaultClassLoader) {
		_aopProxy = aopProxy;
		_defaultClassLoader = defaultClassLoader;
	}

	@Override
	public Object getProxy() {
		return _aopProxy.getProxy(_defaultClassLoader);
	}

	@Override
	public Object getProxy(ClassLoader classLoader) {
		return _aopProxy.getProxy(classLoader);
	}

	private final AopProxy _aopProxy;
	private final ClassLoader _defaultClassLoader;

}