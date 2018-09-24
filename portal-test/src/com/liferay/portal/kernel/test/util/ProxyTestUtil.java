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

package com.liferay.portal.kernel.test.util;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Lance Ji
 */
public class ProxyTestUtil {

	public static <T> T setMethodDoExpected(
			Class<T> clazz, Object expectedResult, String methodName)
		throws Exception {

		ProxyInvocationHandler proxyInvocationHandler =
			new ProxyInvocationHandler(clazz);

		T proxy = (T)ProxyUtil.newProxyInstance(
			clazz.getClassLoader(), new Class<?>[] {clazz},
			proxyInvocationHandler);

		proxyInvocationHandler._registerMethod(expectedResult, methodName);

		return proxy;
	}

	private static class ProxyInvocationHandler implements InvocationHandler {

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

			Object result = _results.get(method.getName());

			if (result == null) {
				Class<?> returnType = method.getReturnType();

				if (_defaultInvokeValue.get(returnType) != null) {
					return _defaultInvokeValue.get(returnType);
				}

				return method.getDefaultValue();
			}

			if (result instanceof Exception) {
				throw (Exception)result;
			}
			else {
				return result;
			}
		}

		private ProxyInvocationHandler(Class<?> clazz) {
			_clazz = clazz;
			_results = new HashMap<>();
		}

		private void _registerMethod(Object expectedResult, String methodName)
			throws Exception {

			Object result = _results.get(methodName);

			if (result != null) {
				_results.put(methodName, expectedResult);
			}
			else {
				Method[] methods = _clazz.getMethods();

				for (Method method : methods) {
					if (methodName.equals(method.getName())) {
						_results.put(methodName, expectedResult);

						return;
					}
				}

				throw new NoSuchMethodException(
					"There is no method with name: " + methodName +
						" exist in " + _clazz.getName());
			}
		}

		private static final Map<Class<?>, Object> _defaultInvokeValue =
			new HashMap<Class<?>, Object>() {
				{
					put(boolean.class, GetterUtil.DEFAULT_BOOLEAN);
					put(Boolean.class, GetterUtil.DEFAULT_BOOLEAN);
					put(int.class, GetterUtil.DEFAULT_INTEGER);
					put(Integer.class, GetterUtil.DEFAULT_INTEGER);
					put(long.class, GetterUtil.DEFAULT_LONG);
					put(Long.class, GetterUtil.DEFAULT_LONG);
					put(String.class, GetterUtil.DEFAULT_STRING);
					put(List.class, Collections.emptyList());
					put(Map.class, Collections.emptyMap());
					put(Set.class, Collections.emptySet());
				}
			};

		private final Class<?> _clazz;
		private final Map<String, Object> _results;

	}

}