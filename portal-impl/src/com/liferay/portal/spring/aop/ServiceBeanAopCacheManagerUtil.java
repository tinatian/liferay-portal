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

import com.liferay.portal.kernel.spring.aop.AopProxyFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Shuyang Zhou
 */
public class ServiceBeanAopCacheManagerUtil {

	public static ServiceBeanAopCacheManager getServiceBeanAopCacheManager(
		AopProxyFactory aopProxyFactory) {

		return _serviceBeanAopCacheManagers.get(aopProxyFactory);
	}

	public static void registerServiceBeanAopCacheManager(
		AopProxyFactory aopProxyFactory,
		ServiceBeanAopCacheManager serviceBeanAopCacheManager) {

		_serviceBeanAopCacheManagers.put(
			aopProxyFactory, serviceBeanAopCacheManager);
	}

	public static void reset() {
		for (ServiceBeanAopCacheManager serviceBeanAopCacheManager :
				_serviceBeanAopCacheManagers.values()) {

			serviceBeanAopCacheManager.reset();
		}
	}

	public static void unregisterServiceBeanAopCacheManager(
		AopProxyFactory aopProxyFactory) {

		_serviceBeanAopCacheManagers.remove(aopProxyFactory);
	}

	private static final Map<AopProxyFactory, ServiceBeanAopCacheManager>
		_serviceBeanAopCacheManagers = new ConcurrentHashMap<>();

}