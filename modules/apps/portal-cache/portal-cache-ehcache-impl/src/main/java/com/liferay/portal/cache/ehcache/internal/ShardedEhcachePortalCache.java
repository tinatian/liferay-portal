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

package com.liferay.portal.cache.ehcache.internal;

import com.liferay.portal.cache.ehcache.internal.event.PortalCacheCacheEventListener;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;

import java.io.Serializable;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.NotificationScope;
import net.sf.ehcache.event.RegisteredEventListeners;

/**
 * @author Tina Tian
 */
public class ShardedEhcachePortalCache<K extends Serializable, V>
	extends BaseEhcachePortalCache<K, V> {

	public static final String SHARDED_SEPARATOR = "_SHARDED_SEPARATOR_";

	public ShardedEhcachePortalCache(
		EhcachePortalCacheManager<K, V> ehcachePortalCacheManager,
		EhcachePortalCacheConfiguration ehcachePortalCacheConfiguration) {

		super(ehcachePortalCacheManager, ehcachePortalCacheConfiguration);

		_cacheManager = ehcachePortalCacheManager.getEhcacheManager();
	}

	@Override
	public void destroy() {
		for (Ehcache ehcache : _ehcaches.values()) {
			_cacheManager.removeCache(ehcache.getName());
		}
	}

	@Override
	public Ehcache getEhcache() {
		Ehcache ehcache = _ehcaches.computeIfAbsent(
			CompanyThreadLocal.getCompanyId(),
			key -> {
				String shardedPortalCacheName =
					getPortalCacheName() + SHARDED_SEPARATOR + key;

				synchronized (_cacheManager) {
					if (!_cacheManager.cacheExists(shardedPortalCacheName)) {
						if (_cacheManager.cacheExists(getPortalCacheName())) {
							Cache cache = _cacheManager.getCache(
								getPortalCacheName());

							CacheConfiguration cacheConfiguration =
								cache.getCacheConfiguration();

							CacheConfiguration clonedCacheConfiguration =
								cacheConfiguration.clone();

							clonedCacheConfiguration.setName(
								shardedPortalCacheName);

							_cacheManager.addCache(
								new Cache(clonedCacheConfiguration));
						}
						else {
							_cacheManager.addCache(shardedPortalCacheName);
						}
					}
				}

				return _cacheManager.getCache(shardedPortalCacheName);
			});

		RegisteredEventListeners registeredEventListeners =
			ehcache.getCacheEventNotificationService();

		registeredEventListeners.registerListener(
			new PortalCacheCacheEventListener<>(
				aggregatedPortalCacheListener, this),
			NotificationScope.ALL);

		return ehcache;
	}

	@Override
	public boolean isSharded() {
		return true;
	}

	public void removeEhcache(long companyId) {
		Ehcache ehcache = _ehcaches.remove(companyId);

		if (ehcache != null) {
			_cacheManager.removeCache(ehcache.getName());
		}
	}

	@Override
	protected void reconfigEhcache(Ehcache ehcache) {
		String cacheName = ehcache.getName();

		int index = cacheName.indexOf(SHARDED_SEPARATOR);

		if (index < 0) {
			_ehcaches.clear();

			return;
		}

		Ehcache oldEhcache = _ehcaches.remove(
			GetterUtil.getLong(
				cacheName.substring(index + SHARDED_SEPARATOR.length())));

		if (oldEhcache == null) {
			return;
		}

		RegisteredEventListeners registeredEventListeners =
			oldEhcache.getCacheEventNotificationService();

		Set<CacheEventListener> cacheEventListeners =
			registeredEventListeners.getCacheEventListeners();

		for (CacheEventListener cacheEventListener : cacheEventListeners) {
			registeredEventListeners.unregisterListener(cacheEventListener);
		}
	}

	private final CacheManager _cacheManager;
	private final Map<Long, Ehcache> _ehcaches = new ConcurrentHashMap<>();

}