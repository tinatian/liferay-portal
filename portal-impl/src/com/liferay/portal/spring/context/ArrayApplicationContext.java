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

package com.liferay.portal.spring.context;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.AutoResetThreadLocal;
import com.liferay.portal.kernel.util.ReflectionUtil;

import java.io.FileNotFoundException;

import java.lang.reflect.Field;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * @author Brian Wing Shun Chan
 */
public class ArrayApplicationContext extends ClassPathXmlApplicationContext {

	public ArrayApplicationContext(String[] configLocations) {
		super(configLocations);
	}

	@Override
	protected void loadBeanDefinitions(XmlBeanDefinitionReader reader) {
		String[] configLocations = getConfigLocations();

		if (configLocations == null) {
			return;
		}

		for (String configLocation : configLocations) {
			try {
				reader.loadBeanDefinitions(configLocation);
			}
			catch (Exception e) {
				Throwable cause = e.getCause();

				if (cause instanceof FileNotFoundException) {
					if (_log.isWarnEnabled()) {
						_log.warn(cause.getMessage());
					}
				}
				else {
					_log.error(e, e);
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ArrayApplicationContext.class);

	static {
		try {
			Field resourcesField = ReflectionUtil.getDeclaredField(
				TransactionSynchronizationManager.class, "resources");
			Field synchronizationsField = ReflectionUtil.getDeclaredField(
				TransactionSynchronizationManager.class, "synchronizations");
			Field currentTransactionNameField = ReflectionUtil.getDeclaredField(
				TransactionSynchronizationManager.class,
				"currentTransactionName");
			Field currentTransactionReadOnlyField =
				ReflectionUtil.getDeclaredField(
					TransactionSynchronizationManager.class,
					"currentTransactionReadOnly");
			Field currentTransactionIsolationLevelField =
				ReflectionUtil.getDeclaredField(
					TransactionSynchronizationManager.class,
					"currentTransactionIsolationLevel");
			Field actualTransactionActiveField =
				ReflectionUtil.getDeclaredField(
					TransactionSynchronizationManager.class,
					"actualTransactionActive");

			resourcesField.set(
				null,
				new AutoResetThreadLocal<Map<Object, Object>>("resources"));
			synchronizationsField.set(
				null,
				new AutoResetThreadLocal<Set<TransactionSynchronization>>(
					"synchronizations"));
			currentTransactionNameField.set(
				null,
				new AutoResetThreadLocal<String>("currentTransactionName"));
			currentTransactionReadOnlyField.set(
				null,
				new AutoResetThreadLocal<Boolean>(
					"currentTransactionReadOnly"));
			currentTransactionIsolationLevelField.set(
				null,
				new AutoResetThreadLocal<Integer>(
					"currentTransactionIsolationLevel"));
			actualTransactionActiveField.set(
				null,
				new AutoResetThreadLocal<Boolean>("actualTransactionActive"));
		}
		catch (Exception e) {
			_log.error("Unable to apply AutoResetThreadLocal ", e);
		}
	}

}