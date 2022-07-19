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

import java.io.Serializable;

import java.util.Set;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.NotificationScope;
import net.sf.ehcache.event.RegisteredEventListeners;

/**
 * @author Brian Wing Shun Chan
 * @author Edward Han
 * @author Shuyang Zhou
 */
public class EhcachePortalCache<K extends Serializable, V>
	extends BaseEhcachePortalCache<K, V> {

	public EhcachePortalCache(
		EhcachePortalCacheManager<K, V> ehcachePortalCacheManager,
		EhcachePortalCacheConfiguration ehcachePortalCacheConfiguration) {

		super(ehcachePortalCacheManager, ehcachePortalCacheConfiguration);

		_cacheManager = ehcachePortalCacheManager.getEhcacheManager();
	}

	@Override
	public void destroy() {
		_cacheManager.removeCache(getPortalCacheName());
	}

	@Override
	public Ehcache getEhcache() {
		Ehcache ehcache = _ehcache;

		if (ehcache != null) {
			return ehcache;
		}

		synchronized (_cacheManager) {
			if (!_cacheManager.cacheExists(getPortalCacheName())) {
				_cacheManager.addCache(getPortalCacheName());
			}
		}

		ehcache = _cacheManager.getCache(getPortalCacheName());

		RegisteredEventListeners registeredEventListeners =
			ehcache.getCacheEventNotificationService();

		registeredEventListeners.registerListener(
			new PortalCacheCacheEventListener<>(
				aggregatedPortalCacheListener, this),
			NotificationScope.ALL);

		return ehcache;
	}

	protected void reconfigEhcache(Ehcache ehcache) {
		RegisteredEventListeners registeredEventListeners =
			ehcache.getCacheEventNotificationService();

		registeredEventListeners.registerListener(
			new PortalCacheCacheEventListener<>(
				aggregatedPortalCacheListener, this),
			NotificationScope.ALL);

		Ehcache oldEhcache = _ehcache;

		if (oldEhcache == null) {
			return;
		}

		_ehcache = ehcache;

		registeredEventListeners =
			oldEhcache.getCacheEventNotificationService();

		Set<CacheEventListener> cacheEventListeners =
			registeredEventListeners.getCacheEventListeners();

		for (CacheEventListener cacheEventListener : cacheEventListeners) {
			registeredEventListeners.unregisterListener(cacheEventListener);
		}
	}

	private final CacheManager _cacheManager;
	private volatile Ehcache _ehcache;

}