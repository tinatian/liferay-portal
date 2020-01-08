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

package com.liferay.portal.search.elasticsearch7.internal.sidecar;

import com.liferay.petra.process.ProcessCallable;
import com.liferay.petra.process.ProcessException;
import com.liferay.petra.process.local.LocalProcessLauncher;
import com.liferay.petra.reflect.ReflectionUtil;

import java.io.Serializable;

import java.lang.reflect.Method;

/**
 * @author Tina Tian
 */
public class SidecarProcessCallable implements ProcessCallable<Serializable> {

	public SidecarProcessCallable(long heartbeatInterval) {
		_heartbeatInterval = heartbeatInterval;
	}

	@Override
	public Serializable call() throws ProcessException {
		Thread currentThread = Thread.currentThread();

		ClassLoader classLoader = currentThread.getContextClassLoader();

		LocalProcessLauncher.ProcessContext.attach(
			"Sidecar-SidecarProcessCallable", _heartbeatInterval,
			(shutdownCode, shutdownThrowable) -> {
				try {
					Method stopMethod = ReflectionUtil.getDeclaredMethod(
						classLoader.loadClass(
							"org.elasticsearch.bootstrap.Bootstrap"),
						"stop");

					stopMethod.invoke(null);
				}
				catch (Exception e) {
					System.setSecurityManager(null);

					System.exit(shutdownCode);
				}

				return true;
			});

		try {
			Class<?> elasticsearchClass = classLoader.loadClass(
				"org.elasticsearch.bootstrap.Elasticsearch");

			Method mainMethod = ReflectionUtil.getDeclaredMethod(
				elasticsearchClass, "main", String[].class);

			mainMethod.invoke(null, new Object[] {new String[0]});
		}
		catch (Exception e) {
			throw new ProcessException(
				"Unable to start Elasticsearch server", e);
		}

		return null;
	}

	private static final long serialVersionUID = 1L;

	private final long _heartbeatInterval;

}