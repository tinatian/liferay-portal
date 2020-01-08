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

package com.liferay.portal.search.elasticsearch7.internal.sidecar;

import com.liferay.petra.concurrent.FutureListener;
import com.liferay.petra.concurrent.NoticeableFuture;
import com.liferay.petra.process.ProcessChannel;
import com.liferay.petra.process.ProcessConfig;
import com.liferay.petra.process.ProcessExecutor;
import com.liferay.petra.process.ProcessLog;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cluster.ClusterExecutor;
import com.liferay.portal.kernel.cluster.ClusterNode;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.elasticsearch7.configuration.ElasticsearchConfiguration;
import com.liferay.portal.search.elasticsearch7.internal.settings.SettingsBuilder;
import com.liferay.portal.search.elasticsearch7.internal.util.ResourceUtil;
import com.liferay.portal.util.PropsValues;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import java.net.InetAddress;
import java.net.URL;

import java.nio.file.Path;

import java.security.CodeSource;
import java.security.ProtectionDomain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import org.elasticsearch.cluster.ClusterModule;
import org.elasticsearch.cluster.coordination.CoordinationMetaData;
import org.elasticsearch.cluster.metadata.Manifest;
import org.elasticsearch.cluster.metadata.MetaData;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.env.NodeEnvironment;
import org.elasticsearch.gateway.MetaDataStateFormat;

/**
 * @author Tina Tian
 */
public class Sidecar {

	public Sidecar(
		ClusterExecutor clusterExecutor, ProcessExecutor processExecutor,
		ElasticsearchConfiguration elasticsearchConfiguration,
		String liferayHome, FutureListener<Serializable> futureListener) {

		_clusterExecutor = clusterExecutor;
		_processExecutor = processExecutor;
		_elasticsearchConfiguration = elasticsearchConfiguration;
		_liferayHome = liferayHome;
		_futureListener = futureListener;

		_pathHome = _liferayHome.concat("/data/elasticsearch7");

		File sidecarHomeFolder = new File(
			liferayHome, elasticsearchConfiguration.sidecarHome());

		if (!sidecarHomeFolder.exists() || !sidecarHomeFolder.isDirectory()) {
			sidecarHomeFolder = new File(
				elasticsearchConfiguration.sidecarHome());

			if (!sidecarHomeFolder.exists() ||
				!sidecarHomeFolder.isDirectory()) {

				throw new IllegalStateException(
					"Sidecar home " + elasticsearchConfiguration.sidecarHome() +
						" does not exist");
			}
		}

		_sidecarHomeFolder = sidecarHomeFolder;
	}

	public synchronized String getPublishedNetworkHostAddress()
		throws Exception {

		if (_processChannel == null) {
			throw new IllegalStateException(
				"Unable to find sidecar process, it may not be started");
		}

		NoticeableFuture<String> noticeableFuture = _processChannel.write(
			new SidecarStatusProcessCallable());

		return noticeableFuture.get();
	}

	public synchronized void start() {
		if (_processChannel != null) {
			return;
		}

		if (_clusterExecutor.isEnabled()) {
			try {
				_cleanUpClusterMetaData();
			}
			catch (Exception e) {
				_log.error("Unable to clean up cluster meta data", e);
			}
		}

		try {
			_processChannel = _processExecutor.execute(
				_createProcessConfig(),
				new SidecarProcessCallable(
					_elasticsearchConfiguration.sidecarHeartbeatInterval()));

			NoticeableFuture<Serializable> noticeableFuture =
				_processChannel.getProcessNoticeableFuture();

			noticeableFuture.addFutureListener(_futureListener);
		}
		catch (Exception e) {
			_log.error("Unable to start sidecar", e);
		}
	}

	public synchronized void stop() {
		if (_processChannel == null) {
			return;
		}

		NoticeableFuture<Serializable> noticeableFuture =
			_processChannel.getProcessNoticeableFuture();

		noticeableFuture.removeFutureListener(_futureListener);

		noticeableFuture.cancel(true);

		_processChannel = null;
	}

	private void _cleanUpClusterMetaData() throws Exception {
		File pathHomeFolder = new File(_pathHome);

		Path pathHome = pathHomeFolder.toPath();

		Path nodePath = NodeEnvironment.resolveNodePath(
			pathHome.resolve("indices"), 0);

		Path statePath = nodePath.resolve(MetaDataStateFormat.STATE_DIR_NAME);

		File stateFolder = statePath.toFile();

		if (!stateFolder.exists()) {
			return;
		}

		MetaDataStateFormat<MetaData> metaDataMetaDataStateFormat =
			MetaData.FORMAT;
		MetaDataStateFormat<Manifest> manifestMetaDataStateFormat =
			Manifest.FORMAT;

		File globalFile = null;
		File manifestFile = null;

		for (File file : stateFolder.listFiles()) {
			String fileName = file.getName();

			if (fileName.startsWith(metaDataMetaDataStateFormat.getPrefix())) {
				globalFile = file;
			}
			else if (fileName.startsWith(
						manifestMetaDataStateFormat.getPrefix())) {

				manifestFile = file;
			}
		}

		if ((globalFile == null) || (manifestFile == null)) {
			return;
		}

		NamedXContentRegistry namedXContentRegistry = new NamedXContentRegistry(
			ClusterModule.getNamedXWriteables());

		MetaData metaData = metaDataMetaDataStateFormat.read(
			namedXContentRegistry, globalFile.toPath());

		CoordinationMetaData coordinationMetaData =
			metaData.coordinationMetaData();

		CoordinationMetaData.Builder coordinationMetaDataBuilder =
			CoordinationMetaData.builder();

		coordinationMetaDataBuilder.term(coordinationMetaData.term());

		MetaData.Builder metaDataBuilder = MetaData.builder(metaData);

		metaDataBuilder.coordinationMetaData(
			coordinationMetaDataBuilder.build());

		Manifest manifest = manifestMetaDataStateFormat.read(
			namedXContentRegistry, manifestFile.toPath());

		manifestMetaDataStateFormat.write(
			new Manifest(
				manifest.getCurrentTerm(), manifest.getClusterStateVersion(),
				metaDataMetaDataStateFormat.write(
					metaDataBuilder.build(), nodePath),
				manifest.getIndexGenerations()),
			nodePath);
	}

	private File _copyResourceToFile(File targetFolder, String resourceName) {
		File file = new File(targetFolder, resourceName);

		try {
			FileUtils.write(
				file,
				ResourceUtil.getResourceAsString(
					Sidecar.class, "/META-INF/sidecar/" + resourceName),
				"UTF-8");
		}
		catch (IOException ioe) {
			_log.error("Unable to copy " + resourceName, ioe);
		}

		return file;
	}

	private String _createClasspath() throws Exception {
		StringBundler sb = new StringBundler();

		ProtectionDomain protectionDomain = Sidecar.class.getProtectionDomain();

		CodeSource codeSource = protectionDomain.getCodeSource();

		URL url = codeSource.getLocation();

		File file = new File(url.toURI());

		sb.append(file.getAbsolutePath());

		sb.append(File.pathSeparator);

		File libFolder = new File(_sidecarHomeFolder, "lib");

		for (File libFile : libFolder.listFiles()) {
			sb.append(libFile.getAbsolutePath());
			sb.append(File.pathSeparator);
		}

		File globalLib = new File(PropsValues.LIFERAY_LIB_GLOBAL_DIR);

		for (File libFile : globalLib.listFiles()) {
			String path = libFile.getAbsolutePath();

			if (path.contains("petra")) {
				sb.append(path);
				sb.append(File.pathSeparator);
			}
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	private void _createConfigFile(File configFolder) {
		File configFile = _copyResourceToFile(
			configFolder, "elasticsearch.yml");

		StringBundler sb = new StringBundler();

		sb.append("\nbootstrap.memory_lock: ");
		sb.append(_elasticsearchConfiguration.bootstrapMlockAll());
		sb.append("\ncluster.name: ");
		sb.append(_elasticsearchConfiguration.clusterName());
		sb.append("\nhttp.cors.enabled: ");
		sb.append(_elasticsearchConfiguration.httpCORSEnabled());

		if (_elasticsearchConfiguration.httpCORSEnabled()) {
			sb.append("\nhttp.cors.allow-origin: ");
			sb.append(_elasticsearchConfiguration.httpCORSAllowOrigin());

			SettingsBuilder settingsBuilder = new SettingsBuilder(
				Settings.builder());

			settingsBuilder.loadFromSource(
				_elasticsearchConfiguration.httpCORSConfigurations());

			Settings settings = settingsBuilder.build();

			for (String key : settings.keySet()) {
				String value = settings.get(key);

				if (Validator.isNotNull(value)) {
					sb.append(StringPool.NEW_LINE);
					sb.append(key);
					sb.append(StringPool.COLON);
					sb.append(StringPool.SPACE);
					sb.append(value);
				}
			}
		}

		sb.append("\npath.data: ");
		sb.append(_pathHome);
		sb.append("/indices");

		sb.append("\npath.home: ");
		sb.append(_pathHome);

		sb.append("\npath.logs: ");
		sb.append(_liferayHome);
		sb.append("/logs");

		sb.append("\npath.repo: ");
		sb.append(_pathHome);
		sb.append("/repo");

		if (_clusterExecutor.isEnabled()) {
			ClusterNode localClusterNode =
				_clusterExecutor.getLocalClusterNode();

			sb.append("\nnode.name: ");
			sb.append(_generateNodeName(localClusterNode));

			sb.append("\nnetwork.host: ");
			sb.append(_getHostAddress(localClusterNode));

			List<ClusterNode> clusterNodes = _clusterExecutor.getClusterNodes();

			StringBundler discoverySeedHostsSB = new StringBundler(
				2 * clusterNodes.size() - 1);
			StringBundler initialMasterNodesSB = new StringBundler(
				2 * clusterNodes.size() - 1);

			for (ClusterNode clusterNode : clusterNodes) {
				discoverySeedHostsSB.append(_getHostAddress(clusterNode));
				discoverySeedHostsSB.append(StringPool.COMMA);

				initialMasterNodesSB.append(_generateNodeName(clusterNode));
				initialMasterNodesSB.append(StringPool.COMMA);
			}

			discoverySeedHostsSB.setIndex(discoverySeedHostsSB.index() - 1);
			initialMasterNodesSB.setIndex(initialMasterNodesSB.index() - 1);

			sb.append("\ndiscovery.seed_hosts: ");
			sb.append(discoverySeedHostsSB.toString());

			sb.append("\ncluster.initial_master_nodes: ");
			sb.append(initialMasterNodesSB.toString());
		}
		else {
			sb.append("\ndiscovery.seed_hosts: localhost");
		}

		try {
			FileUtils.write(configFile, sb.toString(), "UTF-8", true);
		}
		catch (IOException ioe) {
			_log.error("Unable to write to " + configFile, ioe);
		}
	}

	private ProcessConfig _createProcessConfig() throws Exception {
		Map<String, String> environments = new HashMap<>();

		environments.putAll(System.getenv());

		String localHostAddress;

		if (_clusterExecutor.isEnabled()) {
			localHostAddress = _getHostAddress(
				_clusterExecutor.getLocalClusterNode());
		}
		else {
			localHostAddress = "localhost";
		}

		environments.put("HOSTNAME", localHostAddress);

		String classpath = _createClasspath();

		ProcessConfig.Builder processConfigBuilder =
			new ProcessConfig.Builder();

		processConfigBuilder.setArguments(_getJVMArguments());
		processConfigBuilder.setBootstrapClassPath(classpath);
		processConfigBuilder.setEnvironment(environments);
		processConfigBuilder.setProcessLogConsumer(
			processLog -> {
				if (ProcessLog.Level.DEBUG == processLog.getLevel()) {
					if (_log.isDebugEnabled()) {
						_log.debug(
							processLog.getMessage(), processLog.getThrowable());
					}
				}
				else if (ProcessLog.Level.INFO == processLog.getLevel()) {
					if (_log.isInfoEnabled()) {
						_log.info(
							processLog.getMessage(), processLog.getThrowable());
					}
				}
				else if (ProcessLog.Level.WARN == processLog.getLevel()) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							processLog.getMessage(), processLog.getThrowable());
					}
				}
				else {
					_log.error(
						processLog.getMessage(), processLog.getThrowable());
				}
			});
		processConfigBuilder.setReactClassLoader(
			Sidecar.class.getClassLoader());
		processConfigBuilder.setRuntimeClassPath(classpath);

		return processConfigBuilder.build();
	}

	private String _generateNodeName(ClusterNode clusterNode) {
		return _NODE_NAME_PREFIX.concat(clusterNode.getClusterNodeId());
	}

	private String _getHostAddress(ClusterNode clusterNode) {
		InetAddress inetAddress = clusterNode.getBindInetAddress();

		return inetAddress.getHostAddress();
	}

	private List<String> _getJVMArguments() {
		List<String> arguments = new ArrayList<>();

		for (String jvmOption :
				_elasticsearchConfiguration.sidecarJVMOptions()) {

			arguments.add(jvmOption);
		}

		arguments.add("-Des.distribution.flavor=default");
		arguments.add("-Des.distribution.type=tar");

		File tempFolder = new File(
			System.getProperty("java.io.tmpdir"), "sidecar-tmp");

		FileUtils.deleteQuietly(tempFolder);

		tempFolder.mkdir();
		tempFolder.deleteOnExit();

		File configFolder = new File(tempFolder, "config");

		configFolder.mkdir();

		_copyResourceToFile(configFolder, "log4j2.properties");

		_createConfigFile(configFolder);

		arguments.add("-Des.path.conf=" + configFolder.getAbsolutePath());

		arguments.add("-Des.path.home=" + _sidecarHomeFolder.getAbsolutePath());
		arguments.add("-Des.networkaddress.cache.ttl=60");
		arguments.add("-Des.networkaddress.cache.negative.ttl=10");

		arguments.add("-Dlog4j.shutdownHookEnabled=false");
		arguments.add("-Dlog4j2.disable.jmx=true");

		arguments.add("-Dio.netty.allocator.type=unpooled");
		arguments.add("-Dio.netty.allocator.numDirectArenas=0");
		arguments.add("-Dio.netty.noUnsafe=true");
		arguments.add("-Dio.netty.noKeySetOptimization=true");
		arguments.add("-Dio.netty.recycler.maxCapacityPerThread=0");

		arguments.add("-Dfile.encoding=UTF-8");
		arguments.add("-Djava.io.tmpdir=" + tempFolder.getAbsolutePath());

		File policyFile = _copyResourceToFile(configFolder, "security.policy");

		arguments.add("-Djava.security.policy=" + policyFile.getAbsolutePath());

		arguments.add("-Djna.nosys=true");

		return arguments;
	}

	private static final String _NODE_NAME_PREFIX = "NODE_";

	private static final Log _log = LogFactoryUtil.getLog(Sidecar.class);

	private final ClusterExecutor _clusterExecutor;
	private final ElasticsearchConfiguration _elasticsearchConfiguration;
	private final FutureListener<Serializable> _futureListener;
	private final String _liferayHome;
	private final String _pathHome;
	private ProcessChannel<Serializable> _processChannel;
	private final ProcessExecutor _processExecutor;
	private final File _sidecarHomeFolder;

}