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

package com.liferay.portal.kernel.util;

import com.liferay.portal.kernel.servlet.ServletContextClassLoaderPool;

/**
 * @author Raymond Augé
 * @author Shuyang Zhou
 */
public class ClassLoaderUtil {

	public static ClassLoader getAggregatePluginsClassLoader(
		String[] servletContextNames, boolean addContextClassLoader) {

		return _pacl.getAggregatePluginsClassLoader(
			servletContextNames, addContextClassLoader);
	}

	public static ClassLoader getClassLoader(Class<?> clazz) {
		return _pacl.getClassLoader(clazz);
	}

	public static ClassLoader getContextClassLoader() {
		return _pacl.getContextClassLoader();
	}

	public static ClassLoader getPluginClassLoader(String servletContextName) {
		return _pacl.getPluginClassLoader(servletContextName);
	}

	public static ClassLoader getPortalClassLoader() {
		return _pacl.getPortalClassLoader();
	}

	public static void setContextClassLoader(ClassLoader classLoader) {
		_pacl.setContextClassLoader(classLoader);
	}

	public static class NoPACL implements PACL {

		@Override
		public ClassLoader getAggregatePluginsClassLoader(
			String[] servletContextNames, boolean addContextClassLoader) {

			ClassLoader[] classLoaders = null;

			int offset = 0;

			Thread currentThread = Thread.currentThread();

			ClassLoader contextClassLoader =
				currentThread.getContextClassLoader();

			if (addContextClassLoader) {
				classLoaders = new ClassLoader[servletContextNames.length + 1];

				classLoaders[0] = contextClassLoader;

				offset = 1;
			}
			else {
				classLoaders = new ClassLoader[servletContextNames.length];
			}

			for (int i = 0; i < servletContextNames.length; i++) {
				ClassLoader classLoader =
					ServletContextClassLoaderPool.getClassLoader(
						servletContextNames[i]);

				if (classLoader == null) {
					classLoader = contextClassLoader;
				}

				classLoaders[offset + i] = classLoader;
			}

			return AggregateClassLoader.getAggregateClassLoader(classLoaders);
		}

		@Override
		public ClassLoader getClassLoader(Class<?> clazz) {
			return clazz.getClassLoader();
		}

		@Override
		public ClassLoader getContextClassLoader() {
			Thread currentThread = Thread.currentThread();

			return currentThread.getContextClassLoader();
		}

		@Override
		public ClassLoader getPluginClassLoader(String servletContextName) {
			ClassLoader classLoader =
				ServletContextClassLoaderPool.getClassLoader(
					servletContextName);

			if (classLoader == null) {
				Thread currentThread = Thread.currentThread();

				classLoader = currentThread.getContextClassLoader();
			}

			return classLoader;
		}

		@Override
		public ClassLoader getPortalClassLoader() {
			return PortalClassLoaderUtil.getClassLoader();
		}

		@Override
		public void setContextClassLoader(ClassLoader classLoader) {
			Thread thread = Thread.currentThread();

			thread.setContextClassLoader(classLoader);
		}

	}

	public interface PACL {

		public ClassLoader getAggregatePluginsClassLoader(
			String[] servletContextNames, boolean addContextClassLoader);

		public ClassLoader getClassLoader(Class<?> clazz);

		public ClassLoader getContextClassLoader();

		public ClassLoader getPluginClassLoader(String servletContextName);

		public ClassLoader getPortalClassLoader();

		public void setContextClassLoader(ClassLoader classLoader);

	}

	private static final PACL _pacl = new NoPACL();

}