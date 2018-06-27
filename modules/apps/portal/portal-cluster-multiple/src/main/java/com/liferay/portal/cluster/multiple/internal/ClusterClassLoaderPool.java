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

package com.liferay.portal.cluster.multiple.internal;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Lance Ji
 */
public class ClusterClassLoaderPool {

	public static ClassLoader getClassLoader(String contextName) {
		ClassLoader classLoader = null;

		if ((contextName != null) && !contextName.equals("null")) {
			classLoader = _classLoaders.get(contextName);

			if (classLoader == null) {
				String[] bundleInfo = _parseContextName(contextName);

				List<VersionedClassLoader> classLoadersInOrder =
					_fallbackClassLoaders.get(bundleInfo[0]);

				if (classLoadersInOrder != null) {
					VersionedClassLoader latestVersionClassLoader =
						classLoadersInOrder.get(0);

					classLoader = latestVersionClassLoader.getClassLoader();

					if (_log.isWarnEnabled()) {
						_log.warn(
							StringBundler.concat(
								"Unable to find ClassLoader for ", contextName,
								", ClassLoader ", bundleInfo[0],
								StringPool.UNDERLINE,
								latestVersionClassLoader.getVersion(),
								" is provided instead"));
					}
				}
			}
		}

		if (classLoader == null) {
			Thread currentThread = Thread.currentThread();

			classLoader = currentThread.getContextClassLoader();
		}

		return classLoader;
	}

	public static void register(String[] bundleInfo, ClassLoader classLoader) {
		String contextName = bundleInfo[0];

		if (bundleInfo[1] != null) {
			contextName = StringBundler.concat(
				contextName, StringPool.UNDERLINE, bundleInfo[1]);
		}

		_classLoaders.put(contextName, classLoader);
		_contextNames.put(classLoader, contextName);

		_registerFallback(bundleInfo, classLoader);
	}

	public static void unregister(ClassLoader classLoader) {
		String contextName = _contextNames.remove(classLoader);

		if (contextName != null) {
			_classLoaders.remove(contextName);
			_unregisterFallback(contextName);
		}
	}

	private static String[] _parseContextName(String contextName) {
		String[] bundleInfo = new String[2];

		int pos = contextName.indexOf(StringPool.UNDERLINE);

		if (pos < 0) {
			bundleInfo[0] = contextName;
			bundleInfo[1] = null;
		}
		else {
			bundleInfo[0] = contextName.substring(0, pos);
			bundleInfo[1] = contextName.substring(pos + 1);
		}

		return bundleInfo;
	}

	private static void _registerFallback(
		String[] bundleInfo, ClassLoader classLoader) {

		if (bundleInfo[1] == null) {
			return;
		}

		List<VersionedClassLoader> versionedClassLoaders =
			_fallbackClassLoaders.get(bundleInfo[0]);

		if (versionedClassLoaders == null) {
			versionedClassLoaders = new CopyOnWriteArrayList<>();
		}

		versionedClassLoaders.add(
			new VersionedClassLoader(classLoader, bundleInfo[1]));

		if (versionedClassLoaders.size() > 1) {
			Collections.sort(versionedClassLoaders);
		}

		_fallbackClassLoaders.put(bundleInfo[0], versionedClassLoaders);
	}

	private static void _unregisterFallback(String contextName) {
		String[] bundleInfo = _parseContextName(contextName);

		List<VersionedClassLoader> classLoadersInOrder =
			_fallbackClassLoaders.get(bundleInfo[0]);

		for (VersionedClassLoader versionedClassLoader : classLoadersInOrder) {
			if (bundleInfo[1].equals(versionedClassLoader.getVersion())) {
				classLoadersInOrder.remove(versionedClassLoader);

				break;
			}
		}
	}

	private static final String _PORTAL_SERVLETCONTEXTNAME = StringPool.BLANK;

	private static final Log _log = LogFactoryUtil.getLog(
		ClusterClassLoaderPool.class);

	private static final Map<String, ClassLoader> _classLoaders =
		new ConcurrentHashMap<>();
	private static final Map<ClassLoader, String> _contextNames =
		new ConcurrentHashMap<>();
	private static final Map<String, List<VersionedClassLoader>>
		_fallbackClassLoaders = new ConcurrentHashMap<>();

	static {
		register(
			new String[] {"GlobalClassLoader", null},
			ClusterClassLoaderPool.class.getClassLoader());
		register(
			new String[] {_PORTAL_SERVLETCONTEXTNAME, null},
			ClusterClassLoaderPool.class.getClassLoader());
	}

	private static class VersionedClassLoader
		implements Comparable<VersionedClassLoader> {

		@Override
		public int compareTo(VersionedClassLoader versionedClassLoader) {
			int[] comparedVersion = versionedClassLoader.getSplitVersion();
			int i = 0;

			while ((i < _splitVersion.length) && (i < comparedVersion.length) &&
				   (_splitVersion[i] == comparedVersion[i])) {

				i++;
			}

			if ((i < _splitVersion.length) && (i < comparedVersion.length)) {
				int diff = comparedVersion[i] - _splitVersion[i];

				return Integer.signum(diff);
			}

			return Integer.signum(
				comparedVersion.length - _splitVersion.length);
		}

		public ClassLoader getClassLoader() {
			return _classLoader;
		}

		public int[] getSplitVersion() {
			return _splitVersion;
		}

		public String getVersion() {
			return _version;
		}

		private VersionedClassLoader(ClassLoader classLoader, String version) {
			_classLoader = classLoader;
			_version = version;

			_splitVersion = _split(version);
		}

		private int[] _split(String s) {
			List<String> array = StringUtil.split(s, CharPool.PERIOD);

			int[] newArray = new int[array.size()];

			for (int i = 0; i < array.size(); i++) {
				int value = 0;

				try {
					value = Integer.parseInt(array.get(i));
				}
				catch (Exception e) {
				}

				newArray[i] = value;
			}

			return newArray;
		}

		private final ClassLoader _classLoader;
		private final int[] _splitVersion;
		private final String _version;

	}

}