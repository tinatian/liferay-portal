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
public class MockHelperUtil {

	public static final ParameterFilter ANY_LONG = new ParameterFilter(
		long.class);

	public static <T> int getInteractionTimes(T targetMock) throws Exception {
		MockInvocationHandler mockInvocationHandler =
			_getValidMockInvcocationHandler(targetMock);

		int result = mockInvocationHandler._getInteractionTimes();

		mockInvocationHandler._setInteractionTimes(0);

		return result;
	}

	public static <T> T initMock(Class<T> clazz) {
		return (T)ProxyUtil.newProxyInstance(
			clazz.getClassLoader(), new Class<?>[] {clazz},
			new MockInvocationHandler(clazz));
	}

	public static <T> T setMethodAlwaysReturnExpected(
			Class<T> clazz, Object expectedResult, String methodName,
			Object... parameters)
		throws Exception {

		T mock = initMock(clazz);

		setMethodAlwaysReturnExpected(
			mock, expectedResult, methodName, parameters);

		return mock;
	}

	public static <T, R> void setMethodAlwaysReturnExpected(
			T targetMock, R expectedResult, String methodName,
			Object... parameters)
		throws Exception {

		MockInvocationHandler mockInvocationHandler =
			_getValidMockInvcocationHandler(targetMock);

		mockInvocationHandler._registerMethodReturnExpected(
			expectedResult, methodName, parameters);
	}

	public static class ParameterFilter {

		public ParameterFilter(Class<?> clazz) {
			_clazz = clazz;
		}

		private final Class<?> _clazz;

	}

	private static <T> MockInvocationHandler _getValidMockInvcocationHandler(
			T targetMock)
		throws Exception {

		InvocationHandler invocationHandler = ProxyUtil.getInvocationHandler(
			targetMock);

		if (invocationHandler instanceof MockInvocationHandler) {
			return (MockInvocationHandler)invocationHandler;
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

			_interactionTimes++;

			InvocationHandler invocationHandler = _invocationHandlers.get(
				method);

			if (invocationHandler == null) {
				Class<?> returnType = method.getReturnType();

				if (_defaultInvokeValue.get(returnType) != null) {
					return _defaultInvokeValue.get(returnType);
				}

				return method.getDefaultValue();
			}

			return invocationHandler.invoke(proxy, method, args);
		}

		private MockInvocationHandler(Class<?> clazz) {
			_clazz = clazz;
			_interactionTimes = 0;
			_invocationHandlers = new HashMap<>();
		}

		private int _getInteractionTimes() {
			return _interactionTimes;
		}

		private void _registerMethodReturnExpected(
				Object expectedResult, String methodName, Object... parameters)
			throws Exception {

			Class<?>[] parameterTypes;

			if (parameters.length == 0) {
				parameterTypes = new Class<?>[0];
			}
			else {
				int parameterNumber = parameters.length;

				parameterTypes = new Class<?>[parameterNumber];

				for (int i = 0; i < parameterNumber; i++) {
					parameterTypes[i] = ((ParameterFilter)parameters[i])._clazz;
				}
			}

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

		private void _setInteractionTimes(int times) {
			_interactionTimes = times;
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
		private int _interactionTimes;
		private final Map<Method, MethodInvocationHandler> _invocationHandlers;

	}

}