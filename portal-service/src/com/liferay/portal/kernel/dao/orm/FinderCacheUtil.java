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

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.security.pacl.permission.PortalRuntimePermission;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceTracker;

/**
 * @author Brian Wing Shun Chan
 */
public class FinderCacheUtil {

	public static void clearCache() {
		_finderCache.clearCache();
	}

	public static void clearCache(String className) {
		_finderCache.clearCache(className);
	}

	public static void clearLocalCache() {
		_finderCache.clearLocalCache();
	}

	public static FinderCache getFinderCache() {
		PortalRuntimePermission.checkGetBeanProperty(FinderCacheUtil.class);

		return _finderCache;
	}

	public static Object getResult(
		FinderPath finderPath, Object[] args,
		BasePersistenceImpl<? extends BaseModel<?>> basePersistenceImpl) {

		return _finderCache.getResult(finderPath, args, basePersistenceImpl);
	}

	/**
	 * @deprecated As of 6.1.0
	 */
	@Deprecated
	public static Object getResult(
		String className, String methodName, String[] params, Object[] args,
		SessionFactory sessionFactory) {

		_log.error(
			"Regenerate " + className +
				" via \"ant build-service\" or else caching will not work");

		return null;
	}

	public static void invalidate() {
		getFinderCache().invalidate();
	}

	/**
	 * @deprecated As of 6.1.0
	 */
	@Deprecated
	public static void putResult(
		boolean classNameCacheEnabled, String className, String methodName,
		String[] params, Object[] args, Object result) {

		_log.error(
			"Regenerate " + className +
				" via \"ant build-service\" or else caching will not work");
	}

	public static void putResult(
		FinderPath finderPath, Object[] args, Object result) {

		_finderCache.putResult(finderPath, args, result);
	}

	public static void putResult(
		FinderPath finderPath, Object[] args, Object result, boolean quiet) {

		_finderCache.putResult(finderPath, args, result, quiet);
	}

	public static void removeCache(String className) {
		_finderCache.removeCache(className);
	}

	public static void removeResult(FinderPath finderPath, Object[] args) {
		_finderCache.removeResult(finderPath, args);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FinderCacheUtil.class);

	private static final FinderCache _finderCache = new FinderCacheWrapper();

	private static class FinderCacheWrapper implements FinderCache {

		@Override
		public void clearCache() {
			FinderCache finderCache = _serviceTracker.getService();

			if (finderCache != null) {
				finderCache.clearCache();
			}
		}

		@Override
		public void clearCache(String className) {
			FinderCache finderCache = _serviceTracker.getService();

			if (finderCache != null) {
				finderCache.clearCache(className);
			}
		}

		@Override
		public void clearLocalCache() {
			FinderCache finderCache = _serviceTracker.getService();

			if (finderCache != null) {
				finderCache.clearLocalCache();
			}
		}

		@Override
		public Object getResult(
			FinderPath finderPath, Object[] args,
			BasePersistenceImpl<? extends BaseModel<?>> basePersistenceImpl) {

			FinderCache finderCache = _serviceTracker.getService();

			if (finderCache != null) {
				return finderCache.getResult(
					finderPath, args, basePersistenceImpl);
			}

			return null;
		}

		@Override
		public void invalidate() {
			FinderCache finderCache = _serviceTracker.getService();

			if (finderCache != null) {
				finderCache.invalidate();
			}
		}

		@Override
		public void putResult(
			FinderPath finderPath, Object[] args, Object result) {

			FinderCache finderCache = _serviceTracker.getService();

			if (finderCache != null) {
				finderCache.putResult(finderPath, args, result);
			}
		}

		@Override
		public void putResult(
			FinderPath finderPath, Object[] args, Object result,
			boolean quiet) {

			FinderCache finderCache = _serviceTracker.getService();

			if (finderCache != null) {
				finderCache.putResult(finderPath, args, result, quiet);
			}
		}

		@Override
		public void removeCache(String className) {
			FinderCache finderCache = _serviceTracker.getService();

			if (finderCache != null) {
				finderCache.removeCache(className);
			}
		}

		@Override
		public void removeResult(FinderPath finderPath, Object[] args) {
			FinderCache finderCache = _serviceTracker.getService();

			if (finderCache != null) {
				finderCache.removeResult(finderPath, args);
			}
		}

		private FinderCacheWrapper() {
			Registry registry = RegistryUtil.getRegistry();

			_serviceTracker = registry.trackServices(FinderCache.class);

			_serviceTracker.open();
		}

		private final ServiceTracker<FinderCache, FinderCache> _serviceTracker;

	}

}