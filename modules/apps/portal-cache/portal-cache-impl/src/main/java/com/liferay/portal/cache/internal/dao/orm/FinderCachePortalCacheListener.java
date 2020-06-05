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

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheException;
import com.liferay.portal.kernel.cache.PortalCacheListener;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;

import java.io.Serializable;

import java.lang.reflect.Field;

import java.util.List;
import java.util.Objects;

/**
 * @author Tina Tian
 */
public class FinderCachePortalCacheListener
	<K extends Serializable, V extends Serializable>
		implements PortalCacheListener<K, V> {

	public FinderCachePortalCacheListener(
		FinderCache finderCache, String cacheName, boolean columnBitmaskEnabled,
		ServiceTrackerMap<String, List<FinderPath>> serviceTrackerMap) {

		_finderCache = finderCache;
		_cacheName = cacheName;
		_columnBitmaskEnabled = columnBitmaskEnabled;
		_serviceTrackerMap = serviceTrackerMap;

		_cacheNameWithPagination = _cacheName.concat(".List1");
		_cacheNameWithoutPagination = _cacheName.concat(".List2");
	}

	@Override
	public void dispose() {
		_finderCache.removeCache(_cacheName);
		_finderCache.removeCache(_cacheNameWithPagination);
		_finderCache.removeCache(_cacheNameWithoutPagination);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FinderCachePortalCacheListener)) {
			return false;
		}

		FinderCachePortalCacheListener finderCachePortalCacheListener =
			(FinderCachePortalCacheListener)object;

		return Objects.equals(
			_cacheName, finderCachePortalCacheListener._cacheName);
	}

	public int hashCode() {
		return _cacheName.hashCode();
	}

	@Override
	public void notifyEntryEvicted(
			PortalCache<K, V> portalCache, K key, V value, int timeToLive)
		throws PortalCacheException {

		_remove(value);
	}

	@Override
	public void notifyEntryExpired(
			PortalCache<K, V> portalCache, K key, V value, int timeToLive)
		throws PortalCacheException {

		_remove(value);
	}

	@Override
	public void notifyEntryPut(
			PortalCache<K, V> portalCache, K key, V value, int timeToLive)
		throws PortalCacheException {

		_update(value, true);
	}

	@Override
	public void notifyEntryRemoved(
			PortalCache<K, V> portalCache, K key, V value, int timeToLive)
		throws PortalCacheException {

		_remove(value);
	}

	@Override
	public void notifyEntryUpdated(
			PortalCache<K, V> portalCache, K key, V value, int timeToLive)
		throws PortalCacheException {

		_update(value, false);
	}

	@Override
	public void notifyRemoveAll(PortalCache<K, V> portalCache)
		throws PortalCacheException {

		_finderCache.clearCache(_cacheName);
		_finderCache.clearCache(_cacheNameWithPagination);
		_finderCache.clearCache(_cacheNameWithoutPagination);
	}

	private Long _getColumnBitmask(BaseModel<?> baseModel) {
		try {
			Field field = ReflectionUtil.getDeclaredField(
				baseModel.getClass(), "columnBitmask");

			return (Long)field.get(baseModel);
		}
		catch (Exception exception) {
			return null;
		}
	}

	private void _remove(V value) {
		Class<?> clazz = value.getClass();

		if (!_cacheName.equals(clazz.getName())) {
			return;
		}

		BaseModel<?> baseModel = (BaseModel<?>)value;

		Long columnBitmask = _getColumnBitmask(baseModel);

		_finderCache.clearCache(_cacheNameWithPagination);
		_finderCache.clearCache(_cacheNameWithoutPagination);

		for (FinderPath finderPath :
				_serviceTrackerMap.getService(_cacheName)) {

			_removeResult(finderPath, finderPath.getArguments(baseModel), null);
			_removeResult(
				finderPath, finderPath.getOriginalArguments(baseModel),
				columnBitmask);
		}
	}

	private void _removeResult(
		FinderPath finderPath, Object[] arguments, Long columnBitmask) {

		if ((arguments == null) ||
			((columnBitmask != null) &&
			 ((columnBitmask & finderPath.getColumnBitmask()) == 0))) {

			return;
		}

		_finderCache.removeResult(finderPath, arguments);
	}

	private void _update(V value, boolean isNew) {
		Class<?> clazz = value.getClass();

		if (!_cacheName.equals(clazz.getName())) {
			return;
		}

		BaseModel<?> baseModel = (BaseModel<?>)value;

		Long columnBitmask = _getColumnBitmask(baseModel);

		_finderCache.clearCache(_cacheNameWithPagination);

		if (_columnBitmaskEnabled) {
			_finderCache.clearCache(_cacheNameWithoutPagination);
		}
		else {
			for (FinderPath finderPath :
					_serviceTrackerMap.getService(
						_cacheNameWithoutPagination)) {

				if (isNew) {
					Object[] arguments = finderPath.getArguments(baseModel);

					if (arguments == null) {
						arguments = _FINDER_ARGS_EMPTY;
					}

					_removeResult(finderPath, arguments, null);
				}
				else {
					_removeResult(
						finderPath, finderPath.getArguments(baseModel),
						columnBitmask);
					_removeResult(
						finderPath, finderPath.getOriginalArguments(baseModel),
						columnBitmask);
				}
			}
		}

		for (FinderPath finderPath :
				_serviceTrackerMap.getService(_cacheName)) {

			_removeResult(
				finderPath, finderPath.getOriginalArguments(baseModel),
				columnBitmask);
		}
	}

	private static final Object[] _FINDER_ARGS_EMPTY = new Object[0];

	private final String _cacheName;
	private final String _cacheNameWithoutPagination;
	private final String _cacheNameWithPagination;
	private final boolean _columnBitmaskEnabled;
	private final FinderCache _finderCache;
	private final ServiceTrackerMap<String, List<FinderPath>>
		_serviceTrackerMap;

}