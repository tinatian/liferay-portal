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

package com.liferay.portal.search.elasticsearch6.internal.connection;

import com.liferay.petra.process.ProcessExecutor;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cluster.ClusterExecutor;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.InetAddressUtil;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.elasticsearch6.internal.index.IndexFactory;
import com.liferay.portal.search.elasticsearch6.internal.sidecar.Sidecar;
import com.liferay.portal.search.elasticsearch6.internal.sidecar.SidecarConfig;
import com.liferay.portal.search.elasticsearch6.settings.SettingsContributor;
import com.liferay.portal.search.elasticsearch6.settings.XPackSecuritySettings;
import com.liferay.portal.util.PropsValues;

import java.io.File;

import java.net.InetAddress;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Tina Tian
 */
@Component(
	enabled = false, property = "operation.mode=REMOTE",
	service = ElasticsearchConnection.class
)
public class SidecarElasticsearchConnection
	extends BaseElasticsearchConnection {

	@Override
	public OperationMode getOperationMode() {
		return OperationMode.REMOTE;
	}

	@Override
	@Reference(unbind = "-")
	public void setIndexFactory(IndexFactory indexFactory) {
		super.setIndexFactory(indexFactory);
	}

	@Activate
	protected void activate() {
		elasticsearchConfiguration =
			_remoteConnectionSelector.getElasticsearchConfiguration();

		File sidecarHome = new File(
			PropsValues.LIFERAY_HOME, elasticsearchConfiguration.sidecarHome());

		if (!sidecarHome.exists()) {
			sidecarHome = new File(elasticsearchConfiguration.sidecarHome());

			if (!sidecarHome.exists()) {
				throw new IllegalStateException(
					"Sidecar home does not exist" +
						elasticsearchConfiguration.sidecarHome());
			}
		}

		_sidecar = new Sidecar(
			_processExecutor, new SidecarConfig(sidecarHome, _clusterExecutor));

		_sidecar.start();
	}

	@Override
	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(operation.mode=REMOTE)"
	)
	protected void addSettingsContributor(
		SettingsContributor settingsContributor) {

		super.addSettingsContributor(settingsContributor);
	}

	@Override
	protected Client createClient() {
		Thread thread = Thread.currentThread();

		ClassLoader contextClassLoader = thread.getContextClassLoader();

		Class<?> clazz = getClass();

		thread.setContextClassLoader(clazz.getClassLoader());

		try {
			TransportClient transportClient = createTransportClient();

			String transportAddress = null;

			try {
				transportAddress = _sidecar.getNetworkHostAddress();
			}
			catch (Exception e) {
				if (_log.isWarnEnabled()) {
					_log.warn("Unable to get transport address", e);
				}
			}

			try {
				transportAddress = _sidecar.getNetworkHostAddress();

				String[] transportAddressParts = StringUtil.split(
					transportAddress, StringPool.COLON);

				String host = transportAddressParts[0];

				int port = GetterUtil.getInteger(transportAddressParts[1]);

				InetAddress inetAddress = InetAddressUtil.getInetAddressByName(
					host);

				transportClient.addTransportAddress(
					new TransportAddress(inetAddress, port));
			}
			catch (Exception e) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to add transport address " + transportAddress,
						e);
				}
			}

			return transportClient;
		}
		finally {
			thread.setContextClassLoader(contextClassLoader);
		}
	}

	protected TransportClient createTransportClient() {
		Settings settings = settingsBuilder.build();

		if (_log.isDebugEnabled()) {
			_log.debug("Settings: " + settings.toString());
		}

		if ((xPackSecuritySettings != null) &&
			xPackSecuritySettings.requiresXPackSecurity()) {

			return new PreBuiltXPackTransportClient(settings);
		}

		return new PreBuiltTransportClient(settings);
	}

	@Deactivate
	protected void deactivate() {
		close();

		_sidecar.stop();
	}

	@Override
	protected void loadRequiredDefaultConfigurations() {
		settingsBuilder.put(
			"client.transport.ignore_cluster_name",
			elasticsearchConfiguration.clientTransportIgnoreClusterName());
		settingsBuilder.put(
			"client.transport.nodes_sampler_interval",
			elasticsearchConfiguration.clientTransportNodesSamplerInterval());
		settingsBuilder.put(
			"client.transport.ping_timeout",
			elasticsearchConfiguration.clientTransportPingTimeout());
		settingsBuilder.put(
			"client.transport.sniff",
			elasticsearchConfiguration.clientTransportSniff());
		settingsBuilder.put(
			"cluster.name", elasticsearchConfiguration.clusterName());
		settingsBuilder.put(
			"request.headers.X-Found-Cluster",
			elasticsearchConfiguration.clusterName());
	}

	@Override
	protected void removeSettingsContributor(
		SettingsContributor settingsContributor) {

		super.removeSettingsContributor(settingsContributor);
	}

	@Reference
	protected Props props;

	@Reference(cardinality = ReferenceCardinality.OPTIONAL)
	protected volatile XPackSecuritySettings xPackSecuritySettings;

	private static final Log _log = LogFactoryUtil.getLog(
		SidecarElasticsearchConnection.class);

	@Reference
	private ClusterExecutor _clusterExecutor;

	@Reference
	private ProcessExecutor _processExecutor;

	@Reference
	private RemoteConnectionSelector _remoteConnectionSelector;

	private Sidecar _sidecar;

}