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

package com.liferay.portal.cache.multiple.internal.cluster.link.messaging;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.cache.multiple.internal.PortalCacheClusterEvent;
import com.liferay.portal.cache.multiple.internal.PortalCacheClusterEventType;
import com.liferay.portal.cache.multiple.internal.constants.PortalCacheDestinationNames;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManager;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.util.SerializableUtil;

import java.io.Serializable;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shuyang Zhou
 */
@Component(
	enabled = false, immediate = true,
	property = "destination.name=" + PortalCacheDestinationNames.CACHE_REPLICATION,
	service = MessageListener.class
)
public class ClusterLinkPortalCacheClusterListener extends BaseMessageListener {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext,
			(Class<PortalCacheManager<? extends Serializable, ?>>)
				(Class<?>)PortalCacheManager.class,
			null,
			(serviceReference, emitter) -> {
				PortalCacheManager<? extends Serializable, ?>
					portalCacheManager = bundleContext.getService(
						serviceReference);

				emitter.emit(portalCacheManager.getPortalCacheManagerName());
			});
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	@Override
	protected void doReceive(Message message) throws Exception {
		PortalCacheClusterEvent portalCacheClusterEvent =
			(PortalCacheClusterEvent)SerializableUtil.deserialize(
				(byte[])message.getPayload(),
				ClusterLinkPortalCacheClusterListener.class.getClassLoader());

		if (portalCacheClusterEvent == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("Payload is null");
			}

			return;
		}

		_handlePortalCacheClusterEvent(portalCacheClusterEvent);
	}

	@Reference(
		target = "(destination.name=" + PortalCacheDestinationNames.CACHE_REPLICATION + ")",
		unbind = "-"
	)
	protected void setDestination(Destination destination) {
	}

	private void _handlePortalCacheClusterEvent(
		PortalCacheClusterEvent portalCacheClusterEvent) {

		PortalCacheManager<? extends Serializable, ?> portalCacheManager =
			_serviceTrackerMap.getService(
				portalCacheClusterEvent.getPortalCacheManagerName());

		if (portalCacheManager == null) {
			return;
		}

		String portalCacheName = portalCacheClusterEvent.getPortalCacheName();

		int index = portalCacheName.indexOf(
			PortalCache.SHARDED_CACHE_NAME_SEPARATOR);

		if (index > 0) {
			portalCacheName = portalCacheName.substring(0, index);
		}

		PortalCache<Serializable, Serializable> portalCache =
			(PortalCache<Serializable, Serializable>)
				portalCacheManager.fetchPortalCache(portalCacheName);

		if (portalCache == null) {
			return;
		}

		if (!portalCache.isSharded()) {
			_handlePortalCacheClusterEvent(
				portalCacheClusterEvent, portalCache);

			return;
		}

		long companyId = GetterUtil.getLong(
			portalCacheName.substring(
				index + PortalCache.SHARDED_CACHE_NAME_SEPARATOR.length()));

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setWithSafeCloseable(companyId)) {

			_handlePortalCacheClusterEvent(
				portalCacheClusterEvent, portalCache);
		}
	}

	private void _handlePortalCacheClusterEvent(
		PortalCacheClusterEvent portalCacheClusterEvent,
		PortalCache<Serializable, Serializable> portalCache) {

		PortalCacheClusterEventType portalCacheClusterEventType =
			portalCacheClusterEvent.getEventType();

		if (portalCacheClusterEventType.equals(
				PortalCacheClusterEventType.REMOVE_ALL)) {

			PortalCacheHelperUtil.removeAllWithoutReplicator(portalCache);
		}
		else if (portalCacheClusterEventType.equals(
					PortalCacheClusterEventType.PUT) ||
				 portalCacheClusterEventType.equals(
					 PortalCacheClusterEventType.UPDATE)) {

			Serializable key = portalCacheClusterEvent.getElementKey();
			Serializable value = portalCacheClusterEvent.getElementValue();

			if (value == null) {
				PortalCacheHelperUtil.removeWithoutReplicator(portalCache, key);
			}
			else {
				PortalCacheHelperUtil.putWithoutReplicator(
					portalCache, key, value,
					portalCacheClusterEvent.getTimeToLive());
			}
		}
		else {
			PortalCacheHelperUtil.removeWithoutReplicator(
				portalCache, portalCacheClusterEvent.getElementKey());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ClusterLinkPortalCacheClusterListener.class);

	private ServiceTrackerMap
		<String, PortalCacheManager<? extends Serializable, ?>>
			_serviceTrackerMap;

}