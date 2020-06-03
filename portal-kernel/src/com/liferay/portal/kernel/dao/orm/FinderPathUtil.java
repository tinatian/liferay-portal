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

package com.liferay.portal.kernel.dao.orm;

import com.liferay.petra.string.StringPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Tina Tian
 */
public class FinderPathUtil {

	public static FinderPath create(
		boolean entityCacheEnabled, boolean finderCacheEnabled,
		Class<?> modelImplClass, Class<?> resultClass, String cacheName,
		String methodName, String[] params) {

		return create(
			entityCacheEnabled, finderCacheEnabled, modelImplClass, resultClass,
			cacheName, methodName, params, -1);
	}

	public static FinderPath create(
		boolean entityCacheEnabled, boolean finderCacheEnabled,
		Class<?> modelImplClass, Class<?> resultClass, String cacheName,
		String methodName, String[] params, long columnBitmask) {

		FinderPath finderPath = new FinderPath(
			entityCacheEnabled, finderCacheEnabled, modelImplClass, resultClass,
			cacheName, methodName, params, columnBitmask);

		String finderCacheName =
			FinderCache.class.getName() + StringPool.PERIOD +
				finderPath.getCacheName();

		_finderPaths.compute(
			finderCacheName,
			(key, value) -> {
				if (value == null) {
					value = new ArrayList<>();
				}

				value.add(finderPath);

				return value;
			});

		return finderPath;
	}

	public static List<FinderPath> getFinderPath(String cacheName) {
		return _finderPaths.get(cacheName);
	}

	private static final Map<String, List<FinderPath>> _finderPaths =
		new ConcurrentHashMap<>();

}