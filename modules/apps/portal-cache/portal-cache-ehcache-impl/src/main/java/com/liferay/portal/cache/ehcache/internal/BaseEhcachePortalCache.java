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
import com.liferay.portal.kernel.cache.PortalCacheListener;
import com.liferay.portal.kernel.cache.PortalCacheListenerScope;

import java.io.Serializable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

/**
 * @author Brian Wing Shun Chan
 * @author Edward Han
 * @author Shuyang Zhou
 */
public abstract class BaseEhcachePortalCache<K extends Serializable, V>
	extends BasePortalCache<K, V> implements EhcacheWrapper {

	public BaseEhcachePortalCache(
		EhcachePortalCacheManager<K, V> ehcachePortalCacheManager,
		String portalCacheName) {

		super(ehcachePortalCacheManager);

		_portalCacheName = portalCacheName;
	}

	public abstract Ehcache getEhcache();

	@Override
	public List<K> getKeys() {
		Ehcache ehcache = getEhcache();

		return ehcache.getKeys();
	}

	@Override
	public String getPortalCacheName() {
		return _portalCacheName;
	}

	@Override
	public void removeAll() {
		Ehcache ehcache = getEhcache();

		ehcache.removeAll();
	}

	@Override
	protected V doGet(K key) {
		Ehcache ehcache = getEhcache();

		return _getValue(ehcache.get(key));
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

		return _getValue(ehcache.putIfAbsent(element));
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

		return _getValue(ehcache.replace(element));
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

	protected abstract void reconfigEhcache(Ehcache ehcache);

	private V _getValue(Element element) {
		if (element == null) {
			return null;
		}

		return (V)element.getObjectValue();
	}

	private final String _portalCacheName;

}