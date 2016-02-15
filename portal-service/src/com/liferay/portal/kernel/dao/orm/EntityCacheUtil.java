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

import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.security.pacl.permission.PortalRuntimePermission;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceTracker;

import java.io.Serializable;

/**
 * @author Brian Wing Shun Chan
 */
public class EntityCacheUtil {

	public static void clearCache() {
		_entityCache.clearCache();
	}

	public static void clearCache(Class<?> clazz) {
		_entityCache.clearCache(clazz);
	}

	public static void clearLocalCache() {
		_entityCache.clearLocalCache();
	}

	public static EntityCache getEntityCache() {
		PortalRuntimePermission.checkGetBeanProperty(EntityCacheUtil.class);

		return _entityCache;
	}

	public static PortalCache<Serializable, Serializable> getPortalCache(
		Class<?> clazz) {

		return _entityCache.getPortalCache(clazz);
	}

	public static Serializable getResult(
		boolean entityCacheEnabled, Class<?> clazz, Serializable primaryKey) {

		return _entityCache.getResult(entityCacheEnabled, clazz, primaryKey);
	}

	public static void invalidate() {
		_entityCache.invalidate();
	}

	public static Serializable loadResult(
		boolean entityCacheEnabled, Class<?> clazz, Serializable primaryKey,
		SessionFactory sessionFactory) {

		return _entityCache.loadResult(
			entityCacheEnabled, clazz, primaryKey, sessionFactory);
	}

	public static void putResult(
		boolean entityCacheEnabled, Class<?> clazz, Serializable primaryKey,
		Serializable result) {

		_entityCache.putResult(entityCacheEnabled, clazz, primaryKey, result);
	}

	public static void putResult(
		boolean entityCacheEnabled, Class<?> clazz, Serializable primaryKey,
		Serializable result, boolean quiet) {

		_entityCache.putResult(
			entityCacheEnabled, clazz, primaryKey, result, quiet);
	}

	public static void removeCache(String className) {
		_entityCache.removeCache(className);
	}

	public static void removeResult(
		boolean entityCacheEnabled, Class<?> clazz, Serializable primaryKey) {

		_entityCache.removeResult(entityCacheEnabled, clazz, primaryKey);
	}

	private static final EntityCache _entityCache = new EntityCacheWrapper();

	private static class EntityCacheWrapper implements EntityCache {

		@Override
		public void clearCache() {
			EntityCache entityCache = _serviceTracker.getService();

			if (entityCache != null) {
				entityCache.clearCache();
			}
		}

		@Override
		public void clearCache(Class<?> clazz) {
			EntityCache entityCache = _serviceTracker.getService();

			if (entityCache != null) {
				entityCache.clearCache(clazz);
			}
		}

		@Override
		public void clearLocalCache() {
			EntityCache entityCache = _serviceTracker.getService();

			if (entityCache != null) {
				entityCache.clearLocalCache();
			}
		}

		@Override
		public PortalCache<Serializable, Serializable> getPortalCache(
			Class<?> clazz) {

			EntityCache entityCache = _serviceTracker.getService();

			if (entityCache != null) {
				return entityCache.getPortalCache(clazz);
			}

			return null;
		}

		@Override
		public Serializable getResult(
			boolean entityCacheEnabled, Class<?> clazz,
			Serializable primaryKey) {

			EntityCache entityCache = _serviceTracker.getService();

			if (entityCache != null) {
				return entityCache.getResult(
					entityCacheEnabled, clazz, primaryKey);
			}

			return null;
		}

		@Override
		public void invalidate() {
			EntityCache entityCache = _serviceTracker.getService();

			if (entityCache != null) {
				entityCache.invalidate();
			}
		}

		@Override
		public Serializable loadResult(
			boolean entityCacheEnabled, Class<?> clazz, Serializable primaryKey,
			SessionFactory sessionFactory) {

			EntityCache entityCache = _serviceTracker.getService();

			if (entityCache != null) {
				return entityCache.loadResult(
					entityCacheEnabled, clazz, primaryKey, sessionFactory);
			}

			return null;
		}

		@Override
		public void putResult(
			boolean entityCacheEnabled, Class<?> clazz, Serializable primaryKey,
			Serializable result) {

			EntityCache entityCache = _serviceTracker.getService();

			if (entityCache != null) {
				entityCache.putResult(
					entityCacheEnabled, clazz, primaryKey, result);
			}
		}

		@Override
		public void putResult(
			boolean entityCacheEnabled, Class<?> clazz, Serializable primaryKey,
			Serializable result, boolean quiet) {

			EntityCache entityCache = _serviceTracker.getService();

			if (entityCache != null) {
				entityCache.putResult(
					entityCacheEnabled, clazz, primaryKey, result, quiet);
			}
		}

		@Override
		public void removeCache(String className) {
			EntityCache entityCache = _serviceTracker.getService();

			if (entityCache != null) {
				entityCache.removeCache(className);
			}
		}

		@Override
		public void removeResult(
			boolean entityCacheEnabled, Class<?> clazz,
			Serializable primaryKey) {

			EntityCache entityCache = _serviceTracker.getService();

			if (entityCache != null) {
				entityCache.removeResult(entityCacheEnabled, clazz, primaryKey);
			}
		}

		private EntityCacheWrapper() {
			Registry registry = RegistryUtil.getRegistry();

			_serviceTracker = registry.trackServices(EntityCache.class);

			_serviceTracker.open();
		}

		private final ServiceTracker<EntityCache, EntityCache> _serviceTracker;

	}

}