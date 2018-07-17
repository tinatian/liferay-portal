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

package com.liferay.portal.custom.sql.extender.internal;

import com.liferay.portal.dao.orm.custom.sql.CustomSQL;

import org.apache.felix.utils.extender.Extension;
import org.apache.felix.utils.log.Logger;

import org.osgi.framework.Bundle;

/**
 * @author Lance Ji
 */
public class CustomSQLExtension implements Extension {

	public CustomSQLExtension(
		Bundle bundle, CustomSQL customSQL, Logger logger) {

		_bundle = bundle;
		_customSQL = customSQL;
		_logger = logger;
	}

	@Override
	public void destroy() throws Exception {
		_customSQL.removeSQL(_bundle);
	}

	@Override
	public void start() throws Exception {
		_logger.log(
			Logger.LOG_INFO,
			"start extension for bundle: " + _bundle.getSymbolicName());
	}

	private final Bundle _bundle;
	private final CustomSQL _customSQL;
	private final Logger _logger;

}