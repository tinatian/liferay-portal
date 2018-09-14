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

	public static <T> T getProxy(
			Class<T> clazz, String methodName, Object expectedResult)
		throws Exception {

		ProxyTestInvocationHandler proxyTestInvocationHandler =
			new ProxyTestInvocationHandler(clazz);

		proxyTestInvocationHandler.registerMethod(methodName, expectedResult);

		return (T)ProxyUtil.newProxyInstance(
			clazz.getClassLoader(), new Class<?>[] {clazz},
			proxyTestInvocationHandler);
	}

	private static class ProxyTestInvocationHandler
		implements InvocationHandler {

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

			Object result = _results.get(method.getName());

			if (result != null) {
				if (result instanceof Exception) {
					throw (Exception)result;
				}
				else if (_NULL_HOLDER == result) {
					return null;
				}

				return result;
			}

			Class<?> returnType = method.getReturnType();

			if (returnType.equals(boolean.class) ||
				returnType.equals(Boolean.class)) {

				return GetterUtil.DEFAULT_BOOLEAN;
			}
			else if (returnType.equals(byte.class) ||
					 returnType.equals(Byte.class)) {

				return GetterUtil.DEFAULT_BYTE;
			}
			else if (returnType.equals(double.class) ||
					 returnType.equals(Double.class)) {

				return GetterUtil.DEFAULT_DOUBLE;
			}
			else if (returnType.equals(float.class) ||
					 returnType.equals(Float.class)) {

				return GetterUtil.DEFAULT_FLOAT;
			}
			else if (returnType.equals(int.class) ||
					 returnType.equals(Integer.class)) {

				return GetterUtil.DEFAULT_INTEGER;
			}
			else if (returnType.equals(long.class) ||
					 returnType.equals(Long.class)) {

				return GetterUtil.DEFAULT_LONG;
			}
			else if (returnType.equals(short.class) ||
					 returnType.equals(Short.class)) {

				return GetterUtil.DEFAULT_SHORT;
			}
			else if (returnType.equals(String.class)) {
				return GetterUtil.DEFAULT_STRING;
			}
			else if (List.class.isAssignableFrom(returnType)) {
				return Collections.emptyList();
			}
			else if (Map.class.isAssignableFrom(returnType)) {
				return Collections.emptyMap();
			}
			else if (Set.class.isAssignableFrom(returnType)) {
				return Collections.emptySet();
			}

			return method.getDefaultValue();
		}

		public void registerMethod(String methodName, Object expectedResult)
			throws Exception {

			if (expectedResult == null) {
				expectedResult = _NULL_HOLDER;
			}

			Object registeredResult = _results.get(methodName);

			if (registeredResult != null) {
				_results.put(methodName, expectedResult);

				return;
			}

			Method[] methods = _clazz.getMethods();

			for (Method method : methods) {
				if (methodName.equals(method.getName())) {
					_results.put(methodName, expectedResult);

					return;
				}
			}

			throw new NoSuchMethodException(
				"There is no method with name: " + methodName + " exist in " +
					_clazz.getName());
		}

		private ProxyTestInvocationHandler(Class<?> clazz) {
			_clazz = clazz;
			_results = new HashMap<>();
		}

		private static final Object _NULL_HOLDER = new Object();

		private final Class<?> _clazz;
		private final Map<String, Object> _results;

	}

}