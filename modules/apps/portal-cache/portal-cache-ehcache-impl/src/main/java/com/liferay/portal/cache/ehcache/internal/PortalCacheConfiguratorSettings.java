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

import java.net.URL;

/**
 * @author Tina Tian
 */
public class PortalCacheConfiguratorSettings {

	public PortalCacheConfiguratorSettings(
		ClassLoader classLoader, URL configurationURL) {

		_classLoader = classLoader;
		_configurationURL = configurationURL;
	}

	public ClassLoader getClassLoader() {
		return _classLoader;
	}

	public URL getConfigurationURL() {
		return _configurationURL;
	}

	private final ClassLoader _classLoader;
	private final URL _configurationURL;

}