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
public class MockHelperUtil {

	public static <T> T initMock(Class<T> clazz) {
		return (T)ProxyUtil.newProxyInstance(
			clazz.getClassLoader(), new Class<?>[] {clazz},
			new MockInvocationHandler(clazz));
	}

	public static <T> T setMethodAlwaysReturnExpected(
			Class<T> clazz, String methodName, Object expectedResult,
			Class<?>... parameterTypes)
		throws Exception {

		T mock = initMock(clazz);

		setMethodAlwaysReturnExpected(
			mock, methodName, expectedResult, parameterTypes);

		return mock;
	}

	public static <T, R> void setMethodAlwaysReturnExpected(
			T targetMock, String methodName, R expectedResult,
			Class<?>... parameterTypes)
		throws Exception {

		InvocationHandler invocationHandler = ProxyUtil.getInvocationHandler(
			targetMock);

		if (invocationHandler instanceof MockInvocationHandler) {
			MockInvocationHandler mockInvocationHandler =
				(MockInvocationHandler)invocationHandler;

			mockInvocationHandler._registerMethodReturnExpected(
				methodName, expectedResult, parameterTypes);
		}
		else {
			throw new UnsupportedOperationException("Not a valid mock");
		}
	}

	private static class MethodInvocationHandler implements InvocationHandler {

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

			return _expectedResult;
		}

		private MethodInvocationHandler(Object expectedResult) {
			_expectedResult = expectedResult;
		}

		private void _setExpectedResult(Object expectedResult) {
			_expectedResult = expectedResult;
		}

		private Object _expectedResult;

	}

	private static class MockInvocationHandler implements InvocationHandler {

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

			InvocationHandler invocationHandler = _invocationHandlers.get(
				method);

			if (invocationHandler == null) {
				Class<?> returnType = method.getReturnType();

				if (_defaultInvokeValue.get(returnType) != null) {
					return _defaultInvokeValue.get(returnType);
				}

				return null;
			}

			return invocationHandler.invoke(proxy, method, args);
		}

		private MockInvocationHandler(Class<?> clazz) {
			_clazz = clazz;
			_invocationHandlers = new HashMap<>();
		}

		private void _registerMethodReturnExpected(
				String methodName, Object expectedResult,
				Class<?>... parameterTypes)
			throws Exception {

			Method method = _clazz.getMethod(methodName, parameterTypes);

			MethodInvocationHandler methodInvocationHandler =
				_invocationHandlers.get(method);

			if (methodInvocationHandler == null) {
				methodInvocationHandler = new MethodInvocationHandler(
					expectedResult);

				_invocationHandlers.put(method, methodInvocationHandler);
			}

			methodInvocationHandler._setExpectedResult(expectedResult);
		}

		private static final Map<Class<?>, Object> _defaultInvokeValue =
			new HashMap<Class<?>, Object>() {
				{
					put(int.class, 0);
					put(Integer.class, 0);
					put(long.class, 0L);
					put(Long.class, 0L);
					put(Boolean.class, false);
					put(boolean.class, false);
					put(String.class, "");
					put(List.class, Collections.emptyList());
					put(Set.class, Collections.emptySet());
					put(Map.class, Collections.emptyMap());
				}
			};

		private final Class<?> _clazz;
		private final Map<Method, MethodInvocationHandler> _invocationHandlers;

	}

}