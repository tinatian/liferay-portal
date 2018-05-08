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

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.spring.aop.AdvisedSupport;
import com.liferay.portal.kernel.spring.aop.AopProxy;
import com.liferay.portal.kernel.spring.aop.AopProxyFactory;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author Shuyang Zhou
 */
public class ServiceBeanAopProxy
	implements AdvisedSupportProxy, AopProxy, InvocationHandler {

	public static AdvisedSupport getAdvisedSupport(Object proxy)
		throws Exception {

		InvocationHandler invocationHandler = ProxyUtil.getInvocationHandler(
			proxy);

		if (invocationHandler instanceof AdvisedSupportProxy) {
			AdvisedSupportProxy advisableSupportProxy =
				(AdvisedSupportProxy)invocationHandler;

			return advisableSupportProxy.getAdvisedSupport();
		}

		return null;
	}

	public ServiceBeanAopProxy(
		AdvisedSupport advisedSupport, AopProxyFactory aopProxyFactory) {

		_advisedSupport = advisedSupport;

		_aopProxyFactory = aopProxyFactory;
	}

	@Override
	public AdvisedSupport getAdvisedSupport() {
		return _advisedSupport;
	}

	@Override
	public Object getProxy(ClassLoader classLoader) {
		InvocationHandler invocationHandler = _pacl.getInvocationHandler(
			this, _advisedSupport);

		return ProxyUtil.newProxyInstance(
			classLoader, _advisedSupport.getProxiedInterfaces(),
			invocationHandler);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] arguments)
		throws Throwable {

		ServiceBeanMethodInvocation serviceBeanMethodInvocation =
			new ServiceBeanMethodInvocation(
				_advisedSupport.getTarget(), method, arguments);

		ServiceBeanAopCacheManager serviceBeanAopCacheManager =
			ServiceBeanAopCacheManagerUtil.getServiceBeanAopCacheManager(
				_aopProxyFactory);

		serviceBeanMethodInvocation.setMethodInterceptors(
			serviceBeanAopCacheManager.getMethodInterceptors(
				serviceBeanMethodInvocation));

		return serviceBeanMethodInvocation.proceed();
	}

	public interface PACL {

		public InvocationHandler getInvocationHandler(
			InvocationHandler invocationHandler, AdvisedSupport advisedSupport);

	}

	private static final Log _log = LogFactoryUtil.getLog(
		ServiceBeanAopProxy.class);

	private static final PACL _pacl = new NoPACL();

	private final AdvisedSupport _advisedSupport;
	private final AopProxyFactory _aopProxyFactory;

	private static class NoPACL implements PACL {

		@Override
		public InvocationHandler getInvocationHandler(
			InvocationHandler invocationHandler,
			AdvisedSupport advisedSupport) {

			return invocationHandler;
		}

	}

}