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

package com.liferay.portal.search.elasticsearch7.internal.connection;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.search.elasticsearch7.internal.sidecar.Sidecar;
import com.liferay.portal.search.elasticsearch7.internal.util.ClassLoaderUtil;

import org.apache.http.HttpHost;

import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * @author Michael C. Han
 */
public class SidecarElasticsearchConnection
	extends BaseElasticsearchConnection {

	public SidecarElasticsearchConnection(Sidecar sidecar) {
		_sidecar = sidecar;
	}

	@Override
	public void close() {
		super.close();

		_sidecar.stop();
	}

	@Override
	public void connect() {
		_sidecar.start();

		super.connect();
	}

	@Override
	public String getConnectionId() {
		return String.valueOf(OperationMode.EMBEDDED);
	}

	@Override
	public OperationMode getOperationMode() {
		return OperationMode.REMOTE;
	}

	@Override
	protected RestHighLevelClient createRestHighLevelClient() {
		String networkHostAddress;

		try {
			networkHostAddress = _sidecar.getPublishedNetworkHostAddress();

			if (_log.isInfoEnabled()) {
				_log.info("Sidecar is fully started");
			}
		}
		catch (Exception e) {
			throw new IllegalStateException(
				"Unable to get published network host address", e);
		}

		RestClientBuilder restClientBuilder = RestClient.builder(
			HttpHost.create(networkHostAddress));

		Class<? extends SidecarElasticsearchConnection> clazz = getClass();

		return ClassLoaderUtil.getWithContextClassLoader(
			() -> new RestHighLevelClient(restClientBuilder), clazz);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SidecarElasticsearchConnection.class);

	private final Sidecar _sidecar;

}