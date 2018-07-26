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

package com.liferay.sharing.document.library.internal.cache;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	immediate = true,
	service = SharingEntryDLFileEntryModelResourcePermissionCache.class
)
public class SharingEntryDLFileEntryModelResourcePermissionCache {

	public Boolean get(
		long userId, String name, long fileEntryId, String actionId) {

		return _portalCache.get(_getKey(userId, name, fileEntryId, actionId));
	}

	public void put(
		long userId, String name, long fileEntryId, String actionId,
		boolean contains) {

		_portalCache.put(
			_getKey(userId, name, fileEntryId, actionId), contains);
	}

	public void remove(
		long userId, String name, long fileEntryId, String actionId) {

		_portalCache.remove(_getKey(userId, name, fileEntryId, actionId));
	}

	@Reference(unbind = "-")
	protected void setMultiVMPool(MultiVMPool multiVMPool) {
		_portalCache = (PortalCache<String, Boolean>)multiVMPool.getPortalCache(
			SharingEntryDLFileEntryModelResourcePermissionCache.class.
				getName());
	}

	private String _getKey(
		long userId, String name, long fileEntryId, String actionId) {

		return StringBundler.concat(userId, name, fileEntryId, actionId);
	}

	private PortalCache<String, Boolean> _portalCache;

}