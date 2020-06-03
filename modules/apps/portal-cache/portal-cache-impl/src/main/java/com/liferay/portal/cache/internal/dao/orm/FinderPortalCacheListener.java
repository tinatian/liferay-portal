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

import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheException;
import com.liferay.portal.kernel.cache.PortalCacheListener;
import com.liferay.portal.kernel.dao.orm.FinderCache;

import java.io.Serializable;

import org.apache.commons.collections.map.LRUMap;

/**
 * @author Tina Tian
 */
public class FinderPortalCacheListener
	<K extends Serializable, V extends Serializable>
		implements PortalCacheListener<K, V> {

	public FinderPortalCacheListener(
		PortalCache<K, V> entityPortalCache,
		PortalCache<K, V> finderPortalCache, ThreadLocal<LRUMap> localCache,
		FinderCache finderCache) {

		_entityPortalCache = entityPortalCache;
		_finderPortalCache = finderPortalCache;
		_localCache = localCache;
		_finderCache = finderCache;
	}

	@Override
	public void dispose() {
		_finderPortalCache.removeAll();

		if (_localCache != null) {
			_localCache.remove();
		}
	}

	public PortalCache<?, ?> getEntityPortalCache() {
		return _entityPortalCache;
	}

	@Override
	public void notifyEntryEvicted(
			PortalCache<K, V> portalCache, K key, V value, int timeToLive)
		throws PortalCacheException {

		_remove(key, value);
	}

	@Override
	public void notifyEntryExpired(
			PortalCache<K, V> portalCache, K key, V value, int timeToLive)
		throws PortalCacheException {

		_remove(key, value);
	}

	@Override
	public void notifyEntryPut(
			PortalCache<K, V> portalCache, K key, V value, int timeToLive)
		throws PortalCacheException {

		_finderPortalCache.removeAll();

		if (_localCache != null) {
			_localCache.remove();
		}
	}

	@Override
	public void notifyEntryRemoved(
			PortalCache<K, V> portalCache, K key, V value, int timeToLive)
		throws PortalCacheException {

		_remove(key, value);
	}

	@Override
	public void notifyEntryUpdated(
			PortalCache<K, V> portalCache, K key, V value, int timeToLive)
		throws PortalCacheException {

		_remove(key, value);
	}

	@Override
	public void notifyRemoveAll(PortalCache<K, V> portalCache)
		throws PortalCacheException {

		_finderPortalCache.removeAll();

		if (_localCache != null) {
			_localCache.remove();
		}
	}

	private void _remove(K key, V value) {
		_finderPortalCache.removeAll();

		if (_localCache != null) {
			_localCache.remove();
		}
	}

	private final PortalCache<K, V> _entityPortalCache;
	private final FinderCache _finderCache;
	private final PortalCache<K, V> _finderPortalCache;
	private final ThreadLocal<LRUMap> _localCache;

}