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

	public static <T> T initMock(Class<T> clazz) {
		return (T)ProxyUtil.newProxyInstance(
			clazz.getClassLoader(), new Class<?>[] {clazz},
			new MockInvocationHandler(clazz));
	}

	public static <T> T setMethodReturnExpected(
			Class<T> clazz, Object expectedResult, String methodName)
		throws Exception {

		T mock = initMock(clazz);

		setMethodReturnExpected(mock, expectedResult, methodName);

		return mock;
	}

	public static <T, R> void setMethodReturnExpected(
			T mock, R expectedResult, String methodName)
		throws Exception {

		InvocationHandler invocationHandler = ProxyUtil.getInvocationHandler(
			mock);

		if (invocationHandler instanceof MockInvocationHandler) {
			MockInvocationHandler mockInvocationHandler =
				(MockInvocationHandler)invocationHandler;

			mockInvocationHandler._registerMethod(expectedResult, methodName);
		}
		else {
			throw new UnsupportedOperationException("Not a valid mock");
		}
	}

	private static class MethodInvocationHandler implements InvocationHandler {

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

			if (_expectedResult instanceof Exception) {
				throw (Exception)_expectedResult;
			}
			else {
				return _expectedResult;
			}
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
				method.getName());

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
			_invocationHandlers = new HashMap<>();
		}

		private void _registerMethod(Object expectedResult, String methodName)
			throws Exception {

			MethodInvocationHandler methodInvocationHandler =
				_invocationHandlers.get(methodName);

			if (methodInvocationHandler != null) {
				methodInvocationHandler._setExpectedResult(expectedResult);
			}
			else {
				Method[] methods = _clazz.getMethods();

				for (Method method : methods) {
					if (methodName.equals(method.getName())) {
						methodInvocationHandler = new MethodInvocationHandler(
							expectedResult);

						_invocationHandlers.put(
							methodName, methodInvocationHandler);

						return;
					}
				}
			}

			throw new NoSuchMethodException(
				"There is no method with name: " + methodName + " exist in " +
					_clazz.getName());
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
		private final Map<String, MethodInvocationHandler> _invocationHandlers;

	}

}