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

import com.liferay.osgi.felix.util.AbstractExtender;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.dao.orm.custom.sql.CustomSQLPool;

import java.net.URL;

import org.apache.felix.utils.extender.Extension;
import org.apache.felix.utils.log.Logger;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lance Ji
 */
@Component(immediate = true)
public class CustomSQLExtender extends AbstractExtender {

	@Activate
	protected void activate(BundleContext bundleContext) throws Exception {
		_logger = new Logger(bundleContext);

		start(bundleContext);
	}

	@Deactivate
	protected void deactivate(BundleContext bundleContext) throws Exception {
		stop(bundleContext);
	}

	@Override
	protected void debug(Bundle bundle, String s) {
		_logger.log(
			Logger.LOG_DEBUG, StringBundler.concat("[", bundle, "] ", s));
	}

	@Override
	protected Extension doCreateExtension(Bundle bundle) throws Exception {
		if (_validateSQLSource(bundle, _CUSTOM_SQL_SOURCE) ||
			_validateSQLSource(bundle, _META_INF0_CUSTOM_SQL_SOURCE)) {

			return new CustomSQLExtension(bundle, _customSQLPool);
		}

		return null;
	}

	@Override
	protected void error(String s, Throwable throwable) {
		_logger.log(Logger.LOG_ERROR, s, throwable);
	}

	@Override
	protected void warn(Bundle bundle, String s, Throwable throwable) {
		_logger.log(
			Logger.LOG_WARNING, StringBundler.concat("[", bundle, "] ", s));
	}

	private boolean _validateSQLSource(Bundle bundle, String source)
		throws Exception {

		URL url = bundle.getResource(source);

		if (url == null) {
			return false;
		}

		return true;
	}

	private static final String _CUSTOM_SQL_SOURCE = "custom-sql/default.xml";

	private static final String _META_INF0_CUSTOM_SQL_SOURCE =
		"META-INF/custom-sql/default.xml";

	@Reference
	private CustomSQLPool _customSQLPool;

	private Logger _logger;

}