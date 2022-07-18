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

import com.liferay.portal.cache.BasePortalCache;
import com.liferay.portal.cache.ehcache.internal.event.PortalCacheCacheEventListener;
import com.liferay.portal.kernel.cache.PortalCacheListener;
import com.liferay.portal.kernel.cache.PortalCacheListenerScope;

import java.io.Serializable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.NotificationScope;
import net.sf.ehcache.event.RegisteredEventListeners;

/**
 * @author Brian Wing Shun Chan
 * @author Edward Han
 * @author Shuyang Zhou
 */
public class EhcachePortalCache<K extends Serializable, V>
	extends BasePortalCache<K, V> implements EhcacheWrapper {

	public EhcachePortalCache(
		EhcachePortalCacheManager<K, V> ehcachePortalCacheManager,
		String portalCacheName) {

		super(ehcachePortalCacheManager);

		_portalCacheName = portalCacheName;

		_cacheManager = ehcachePortalCacheManager.getEhcacheManager();
	}

	@Override
	public Ehcache getEhcache() {
		Ehcache ehcache = _ehcache;

		if (ehcache != null) {
			return ehcache;
		}

		synchronized (_cacheManager) {
			if (!_cacheManager.cacheExists(_portalCacheName)) {
				_cacheManager.addCache(_portalCacheName);
			}
		}

		ehcache = _cacheManager.getCache(_portalCacheName);

		RegisteredEventListeners registeredEventListeners =
			ehcache.getCacheEventNotificationService();

		registeredEventListeners.registerListener(
			new PortalCacheCacheEventListener<>(
				aggregatedPortalCacheListener, this),
			NotificationScope.ALL);

		return ehcache;
	}

	@Override
	public List<K> getKeys() {
		Ehcache ehcache = getEhcache();

		return ehcache.getKeys();
	}

	@Override
	public String getPortalCacheName() {
		Ehcache ehcache = getEhcache();

		return ehcache.getName();
	}

	@Override
	public void removeAll() {
		Ehcache ehcache = getEhcache();

		ehcache.removeAll();
	}

	@Override
	protected V doGet(K key) {
		Ehcache ehcache = getEhcache();

		Element element = ehcache.get(key);

		if (element == null) {
			return null;
		}

		return (V)element.getObjectValue();
	}

	@Override
	protected void doPut(K key, V value, int timeToLive) {
		Element element = new Element(key, value);

		if (timeToLive != DEFAULT_TIME_TO_LIVE) {
			element.setTimeToLive(timeToLive);
		}

		Ehcache ehcache = getEhcache();

		ehcache.put(element);
	}

	@Override
	protected V doPutIfAbsent(K key, V value, int timeToLive) {
		Element element = new Element(key, value);

		if (timeToLive != DEFAULT_TIME_TO_LIVE) {
			element.setTimeToLive(timeToLive);
		}

		Ehcache ehcache = getEhcache();

		Element oldElement = ehcache.putIfAbsent(element);

		if (oldElement == null) {
			return null;
		}

		return (V)oldElement.getObjectValue();
	}

	@Override
	protected void doRemove(K key) {
		Ehcache ehcache = getEhcache();

		ehcache.remove(key);
	}

	@Override
	protected boolean doRemove(K key, V value) {
		Element element = new Element(key, value);

		Ehcache ehcache = getEhcache();

		return ehcache.removeElement(element);
	}

	@Override
	protected V doReplace(K key, V value, int timeToLive) {
		Element element = new Element(key, value);

		if (timeToLive != DEFAULT_TIME_TO_LIVE) {
			element.setTimeToLive(timeToLive);
		}

		Ehcache ehcache = getEhcache();

		Element oldElement = ehcache.replace(element);

		if (oldElement == null) {
			return null;
		}

		return (V)oldElement.getObjectValue();
	}

	@Override
	protected boolean doReplace(K key, V oldValue, V newValue, int timeToLive) {
		Element oldElement = new Element(key, oldValue);

		Element newElement = new Element(key, newValue);

		if (timeToLive != DEFAULT_TIME_TO_LIVE) {
			newElement.setTimeToLive(timeToLive);
		}

		Ehcache ehcache = getEhcache();

		return ehcache.replace(oldElement, newElement);
	}

	protected Map<PortalCacheListener<K, V>, PortalCacheListenerScope>
		getPortalCacheListeners() {

		return Collections.unmodifiableMap(
			aggregatedPortalCacheListener.getPortalCacheListeners());
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
	private final String _portalCacheName;

}