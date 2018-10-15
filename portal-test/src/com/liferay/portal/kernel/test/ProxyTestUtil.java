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

package com.liferay.portal.kernel.test;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.junit.Assert;

/**
 * @author Lance Ji
 */
public class ProxyTestUtil {

	public static void assertAction(
		Object proxy, Function<Integer, Boolean> timesFunction) {

		ProxyTestInvocationHandler proxyTestInvocationHandler =
			_getInvocationHandler(proxy);

		List<ProxyAction> proxyActions =
			proxyTestInvocationHandler.getProxyActions();

		Assert.assertTrue(timesFunction.apply(proxyActions.size()));
	}

	public static void assertAction(
		Object proxy, List<ProxyAction> proxyActions) {

		ProxyTestInvocationHandler proxyTestInvocationHandler =
			_getInvocationHandler(proxy);

		Assert.assertTrue(
			Collections.indexOfSubList(
				proxyTestInvocationHandler.getProxyActions(),
				proxyActions) != -1);
	}

	public static void assertAction(
		Object proxy, ProxyAction targetAction,
		Function<Long, Boolean> timesFunction) {

		ProxyTestInvocationHandler proxyTestInvocationHandler =
			_getInvocationHandler(proxy);

		Predicate<ProxyAction> predicate = proxyAction ->
			targetAction.getMethodName().equals(proxyAction.getMethodName());

		Object[] arguments = targetAction.getArguments();

		if ((arguments != null) && (arguments.length > 0)) {
			predicate.and(
				proxyAction -> Arrays.deepEquals(
					proxyAction.getArguments(), arguments));
		}

		Function<List<ProxyAction>, Long> proxyActionFunction =
			(List<ProxyAction> proxyActions) -> {
				Stream<ProxyAction> proxyActionStream = proxyActions.stream();

				return proxyActionStream.filter(predicate).count();
			};

		Assert.assertTrue(
			timesFunction.apply(
				proxyActionFunction.apply(
					proxyTestInvocationHandler.getProxyActions())));
	}

	public static <T> T getDummyProxy(Class<T> clazz) {
		return (T)ProxyUtil.newProxyInstance(
			_getClassLoader(clazz), new Class<?>[] {clazz},
			(proxy, method, args) -> method.getDefaultValue());
	}

	public static <T> T getProxy(Class<T> clazz) {
		return (T)ProxyUtil.newProxyInstance(
			_getClassLoader(clazz), new Class<?>[] {clazz},
			new ProxyTestInvocationHandler());
	}

	public static <T> T getProxy(Class<T> clazz, ProxyMethod... proxyMethods) {
		ProxyTestInvocationHandler proxyTestInvocationHandler =
			new ProxyTestInvocationHandler();

		for (ProxyMethod proxyMethod : proxyMethods) {
			proxyTestInvocationHandler.registerMethod(proxyMethod);
		}

		return (T)ProxyUtil.newProxyInstance(
			_getClassLoader(clazz), new Class<?>[] {clazz},
			proxyTestInvocationHandler);
	}

	public static ProxyAction getProxyAction(
		String methodName, Object[] arguments) {

		return new ProxyAction(methodName, arguments);
	}

	public static ProxyMethod getProxyMethod(
		String methodName, Function<Object[], Object> function) {

		return new ProxyMethod(methodName, function);
	}

	public static void updateProxy(Object proxy, ProxyMethod... proxyMethods) {
		ProxyTestInvocationHandler proxyTestInvocationHandler =
			_getInvocationHandler(proxy);

		for (ProxyMethod proxyMethod : proxyMethods) {
			proxyTestInvocationHandler.registerMethod(proxyMethod);
		}
	}

	public static class ProxyAction {

		@Override
		public boolean equals(Object object) {
			if (this == object) {
				return true;
			}

			if ((object == null) || (getClass() != object.getClass())) {
				return false;
			}

			ProxyAction proxyAction = (ProxyAction)object;

			if (!Arrays.deepEquals(_arguments, proxyAction._arguments)) {
				return false;
			}

			return _methodName.equals(proxyAction._methodName);
		}

		public Object[] getArguments() {
			return _arguments;
		}

		public String getMethodName() {
			return _methodName;
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(_arguments) ^ _methodName.hashCode();
		}

		private ProxyAction(String methodName, Object[] arguments) {
			_methodName = methodName;
			_arguments = arguments;
		}

		private final Object[] _arguments;
		private final String _methodName;

	}

	public static class ProxyMethod {

		public Function<Object[], Object> getFunction() {
			return _function;
		}

		public String getMethodName() {
			return _methodName;
		}

		private ProxyMethod(
			String methodName, Function<Object[], Object> function) {

			_methodName = methodName;
			_function = function;
		}

		private final Function<Object[], Object> _function;
		private final String _methodName;

	}

	private static ClassLoader _getClassLoader(Class<?> clazz) {
		ClassLoader classLoader = clazz.getClassLoader();

		if (classLoader == null) {
			classLoader = ProxyTestUtil.class.getClassLoader();
		}

		return classLoader;
	}

	private static ProxyTestInvocationHandler _getInvocationHandler(
		Object proxy) {

		InvocationHandler invocationHandler = ProxyUtil.getInvocationHandler(
			proxy);

		if (invocationHandler instanceof ProxyTestInvocationHandler) {
			return (ProxyTestInvocationHandler)invocationHandler;
		}

		throw new IllegalArgumentException("Not a proxy test instance");
	}

	private static class ProxyTestInvocationHandler
		implements InvocationHandler {

		public List<ProxyAction> getProxyActions() {
			return _proxyActions;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

			String methodName = method.getName();

			if (!"toString".equals(methodName)) {
				_proxyActions.add(new ProxyAction(methodName, args));
			}

			Function<Object[], Object> expectedFunction = _results.get(
				methodName);

			if (expectedFunction != null) {
				return expectedFunction.apply(args);
			}

			Class<?> returnType = method.getReturnType();

			if (returnType.equals(boolean.class)) {
				return GetterUtil.DEFAULT_BOOLEAN;
			}
			else if (returnType.equals(byte.class)) {
				return GetterUtil.DEFAULT_BYTE;
			}
			else if (returnType.equals(double.class)) {
				return GetterUtil.DEFAULT_DOUBLE;
			}
			else if (returnType.equals(float.class)) {
				return GetterUtil.DEFAULT_FLOAT;
			}
			else if (returnType.equals(int.class)) {
				return GetterUtil.DEFAULT_INTEGER;
			}
			else if (returnType.equals(long.class)) {
				return GetterUtil.DEFAULT_LONG;
			}
			else if (returnType.equals(short.class)) {
				return GetterUtil.DEFAULT_SHORT;
			}

			return method.getDefaultValue();
		}

		public void registerMethod(ProxyMethod proxyMethod) {
			_results.put(
				proxyMethod.getMethodName(), proxyMethod.getFunction());
		}

		private ProxyTestInvocationHandler() {
			_proxyActions = new ArrayList<>();
			_results = new HashMap<>();
		}

		private final List<ProxyAction> _proxyActions;
		private final Map<String, Function<Object[], Object>> _results;

	}

}