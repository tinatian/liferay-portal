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

import com.liferay.petra.process.ProcessExecutor;
import com.liferay.portal.kernel.cluster.ClusterExecutor;
import com.liferay.portal.search.elasticsearch7.internal.configuration.ElasticsearchConnectionConfigurationWrapper;
import com.liferay.portal.search.elasticsearch7.internal.sidecar.Sidecar;
import com.liferay.portal.search.elasticsearch7.internal.sidecar.SidecarConfig;
import com.liferay.portal.search.elasticsearch7.internal.util.ClassLoaderUtil;
import com.liferay.portal.util.PropsValues;

import java.io.File;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.security.KeyStore;

import java.util.Arrays;
import java.util.Objects;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;

import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * @author Michael C. Han
 */
public class RemoteElasticsearchConnection extends BaseElasticsearchConnection {

	public RemoteElasticsearchConnection(
		ProcessExecutor processExecutor, ClusterExecutor clusterExecutor,
		String elasticsearchClusterName) {

		_processExecutor = processExecutor;
		_clusterExecutor = clusterExecutor;
		_elasticsearchClusterName = elasticsearchClusterName;
	}

	@Override
	public void close() {
		super.close();

		if (_sidecar != null) {
			_sidecar.stop();
		}
	}

	@Override
	public void connect() {
		if (Arrays.equals(
				_elasticsearchConnectionConfigurationWrapper.
					getNetworkHostAddresses(),
				new String[] {"http://localhost:9200"}) &&
			Objects.equals(
				_elasticsearchClusterName, "LiferayElasticsearchCluster")) {

			String sidecarHome =
				_elasticsearchConnectionConfigurationWrapper.getSidecarHome();

			File sidecarHomeFolder = new File(
				PropsValues.LIFERAY_HOME, sidecarHome);

			if (!sidecarHomeFolder.exists()) {
				sidecarHomeFolder = new File(sidecarHome);

				if (!sidecarHomeFolder.exists()) {
					throw new IllegalStateException(
						"Sidecar home " + sidecarHome + " does not exist");
				}
			}

			_sidecar = new Sidecar(
				_processExecutor,
				new SidecarConfig(
					sidecarHomeFolder,
					_elasticsearchConnectionConfigurationWrapper.
						getSidecarHeartbeatInterval(),
					_elasticsearchConnectionConfigurationWrapper.
						getSidecarJVMOptions(),
					_clusterExecutor));

			_sidecar.start();
		}

		super.connect();
	}

	@Override
	public OperationMode getOperationMode() {
		return OperationMode.REMOTE;
	}

	public void setElasticsearchConnectionConfigurationWrapper(
		ElasticsearchConnectionConfigurationWrapper
			elasticsearchConnectionConfigurationWrapper) {

		_elasticsearchConnectionConfigurationWrapper =
			elasticsearchConnectionConfigurationWrapper;
	}

	protected void configureSecurity(RestClientBuilder restClientBuilder) {
		restClientBuilder.setHttpClientConfigCallback(
			httpClientBuilder -> {
				httpClientBuilder.setDefaultCredentialsProvider(
					createCredentialsProvider());

				if (_elasticsearchConnectionConfigurationWrapper.
						isHhttpSSLEnabled()) {

					httpClientBuilder.setSSLContext(createSSLContext());
				}

				return httpClientBuilder;
			});
	}

	protected CredentialsProvider createCredentialsProvider() {
		CredentialsProvider credentialsProvider =
			new BasicCredentialsProvider();

		credentialsProvider.setCredentials(
			AuthScope.ANY,
			new UsernamePasswordCredentials(
				_elasticsearchConnectionConfigurationWrapper.getUsername(),
				_elasticsearchConnectionConfigurationWrapper.getPassword()));

		return credentialsProvider;
	}

	@Override
	protected RestHighLevelClient createRestHighLevelClient() {
		String[] networkHostAddresses;

		if (_sidecar == null) {
			networkHostAddresses =
				_elasticsearchConnectionConfigurationWrapper.
					getNetworkHostAddresses();
		}
		else {
			try {
				networkHostAddresses = new String[] {
					_sidecar.getNetworkHostAddress()
				};
			}
			catch (Exception e) {
				throw new RuntimeException(
					"Unable to get network host address", e);
			}
		}

		HttpHost[] httpHosts = new HttpHost[networkHostAddresses.length];

		for (int i = 0; i < networkHostAddresses.length; i++) {
			httpHosts[i] = HttpHost.create(networkHostAddresses[i]);
		}

		RestClientBuilder restClientBuilder = RestClient.builder(httpHosts);

		if (_elasticsearchConnectionConfigurationWrapper.
				isAuthenticationEnabled()) {

			configureSecurity(restClientBuilder);
		}

		Class<? extends RemoteElasticsearchConnection> clazz = getClass();

		return ClassLoaderUtil.getWithContextClassLoader(
			() -> new RestHighLevelClient(restClientBuilder), clazz);
	}

	protected SSLContext createSSLContext() {
		try {
			Path path = Paths.get(
				_elasticsearchConnectionConfigurationWrapper.
					getTruststorePath());

			InputStream is = Files.newInputStream(path);

			KeyStore keyStore = KeyStore.getInstance(
				_elasticsearchConnectionConfigurationWrapper.
					getTruststoreType());
			String truststorePassword =
				_elasticsearchConnectionConfigurationWrapper.
					getTruststorePassword();

			keyStore.load(is, truststorePassword.toCharArray());

			SSLContextBuilder sslContextBuilder = SSLContexts.custom();

			sslContextBuilder.loadKeyMaterial(
				keyStore, truststorePassword.toCharArray());
			sslContextBuilder.loadTrustMaterial(keyStore, null);

			return sslContextBuilder.build();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private final ClusterExecutor _clusterExecutor;
	private final String _elasticsearchClusterName;
	private ElasticsearchConnectionConfigurationWrapper
		_elasticsearchConnectionConfigurationWrapper;
	private final ProcessExecutor _processExecutor;
	private Sidecar _sidecar;

}