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

package com.liferay.portal.cache.internal.dao.orm;

import com.liferay.petra.lang.CentralizedThreadLocal;

import java.io.Serializable;

import java.util.Map;

import org.apache.commons.collections.map.LRUMap;

/**
 * @author Tina Tian
 */
public class ObjectValueLocalCache {

	public ObjectValueLocalCache(String name, int maxSize) {
		_maxSize = maxSize;
		_localCache = new CentralizedThreadLocal<>(name);
	}

	public void clear() {
		Map<Serializable, Serializable> localCache = _localCache.get();

		if (localCache == null) {
			return;
		}

		localCache.clear();
	}

	public Serializable get(Serializable key) {
		Map<Serializable, Serializable> localCache = _localCache.get();

		if (localCache == null) {
			return null;
		}

		return localCache.get(key);
	}

	public void initialize() {
		_localCache.set(new LRUMap(_maxSize));
	}

	public void put(Serializable key, Serializable value) {
		Map<Serializable, Serializable> localCache = _localCache.get();

		if (localCache == null) {
			return;
		}

		localCache.put(key, key);
	}

	public void remove(Serializable key) {
		Map<Serializable, Serializable> localCache = _localCache.get();

		if (localCache == null) {
			return;
		}

		localCache.remove(key);
	}

	private final ThreadLocal<LRUMap> _localCache;
	private final int _maxSize;

}