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

package com.liferay.portal.search.elasticsearch6.internal.sidecar;

import com.liferay.petra.concurrent.NoticeableFuture;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.process.ProcessChannel;
import com.liferay.petra.process.ProcessConfig;
import com.liferay.petra.process.ProcessExecutor;
import com.liferay.petra.process.ProcessLog;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.elasticsearch6.internal.util.ResourceUtil;
import com.liferay.portal.util.PropsValues;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;

import java.net.URL;

import java.security.CodeSource;
import java.security.ProtectionDomain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tina Tian
 */
public class Sidecar {

	public Sidecar(
		ProcessExecutor processExecutor, SidecarConfig sidecarConfig) {

		_processExecutor = processExecutor;
		_sidecarConfig = sidecarConfig;
	}

	public synchronized String getNetworkHostAddress() throws Exception {
		if (_processChannel == null) {
			throw new IllegalStateException("Unable to find sidecar process");
		}

		NoticeableFuture<String> noticeableFuture = _processChannel.write(
			new GetAddressProcessCallable());

		return noticeableFuture.get();
	}

	public synchronized void start() {
		if (_processChannel != null) {
			return;
		}

		try {
			_processChannel = _processExecutor.execute(
				_createProcessConfig(),
				new SidecarProcessCallable(_getSidecarArguments()));

			NoticeableFuture<Serializable> noticeableFuture =
				_processChannel.getProcessNoticeableFuture();

			noticeableFuture.addFutureListener(
				future -> {
					try {
						future.get();

						if (_log.isWarnEnabled()) {
							_log.warn(
								"Sidecar process is finished, will restart");
						}
					}
					catch (Exception e) {
						if (_log.isWarnEnabled()) {
							_log.warn(
								"Sidecar process is aborted, will restart", e);
						}
					}

					stop();

					start();
				});
		}
		catch (Exception e) {
			_log.error("Unable to start sidecar", e);
		}
	}

	public synchronized void stop() {
		if (_processChannel != null) {
			NoticeableFuture<Serializable> noticeableFuture =
				_processChannel.getProcessNoticeableFuture();

			noticeableFuture.cancel(true);
		}

		_processChannel = null;
	}

	private String _createClasspath() throws Exception {
		StringBundler sb = new StringBundler();

		ProtectionDomain protectionDomain = Sidecar.class.getProtectionDomain();

		CodeSource codeSource = protectionDomain.getCodeSource();

		URL url = codeSource.getLocation();

		File file = new File(url.toURI());

		sb.append(file.getAbsolutePath());

		sb.append(File.pathSeparator);

		File libFolder = _sidecarConfig.getLibFolder();

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

	private ProcessConfig _createProcessConfig() throws Exception {
		List<String> arguments = new ArrayList<>();

		_parseJVMArguments(arguments);

		arguments.add("-Des.path.home=" + _sidecarConfig.getHomeFolder());
		arguments.add("-Des.path.conf=" + _sidecarConfig.getConfigFolder());
		arguments.add("-Des.distribution.flavor=default");
		arguments.add("-Des.distribution.type=tar");
		arguments.add("-Des.bundled_jdk=true");

		File policyFile = ResourceUtil.getResourceAsTempFile(
			getClass(), "/META-INF/sidecar.policy");

		arguments.add("-Djava.security.policy=" + policyFile.getAbsolutePath());

		Map<String, String> environments = new HashMap<>();

		environments.putAll(System.getenv());

		String localHostAddress = _sidecarConfig.getLocalHostAddress();

		if (localHostAddress == null) {
			localHostAddress = "localhost";
		}

		environments.put("HOSTNAME", localHostAddress);

		String classpath = _createClasspath();

		ProcessConfig.Builder processConfigBuilder =
			new ProcessConfig.Builder();

		processConfigBuilder.setArguments(arguments);
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

	private String[] _getSidecarArguments() {
		List<String> args = new ArrayList<>();

		String localNodeName = _sidecarConfig.getLocalNodeName();

		if (localNodeName != null) {
			args.add("-E");
			args.add("node.name=" + localNodeName);
		}

		String localHostAddress = _sidecarConfig.getLocalHostAddress();

		if (localHostAddress != null) {
			args.add("-E");
			args.add("network.host=" + localHostAddress);
		}

		String discoverySeedHosts = _sidecarConfig.getDiscoverySeedHosts();

		if (discoverySeedHosts != null) {
			args.add("-E");
			args.add("discovery.seed_hosts=" + discoverySeedHosts);
		}

		String initialMasterNodes = _sidecarConfig.getInitialMasterNodes();

		if (initialMasterNodes != null) {
			args.add("-E");
			args.add("cluster.initial_master_nodes=" + initialMasterNodes);
		}

		return args.toArray(new String[0]);
	}

	private void _parseJVMArguments(List<String> arguments) throws Exception {
		File libFolder = _sidecarConfig.getLibFolder();

		File jvmOptions = new File(
			_sidecarConfig.getConfigFolder(), "jvm.options");

		List<String> command = new ArrayList<>();

		command.add("java");
		command.add("-cp");
		command.add(
			StringBundler.concat(
				libFolder.getAbsolutePath(), File.separator, StringPool.STAR));
		command.add("org.elasticsearch.tools.launchers.JvmOptionsParser");
		command.add(jvmOptions.getAbsolutePath());

		ProcessBuilder processBuilder = new ProcessBuilder();

		processBuilder.command(command);
		processBuilder.directory(_sidecarConfig.getHomeFolder());

		Process process = null;

		try {
			process = processBuilder.start();

			try (InputStream inputStream = process.getInputStream();
				InputStream errorStream = process.getErrorStream()) {

				String output = StringUtil.replace(
					StreamUtil.toString(inputStream), "${ES_TMPDIR}",
					System.getProperty("java.io.tmpdir"));

				if (_log.isDebugEnabled()) {
					_log.debug(output);
				}

				Collections.addAll(
					arguments, StringUtil.split(output, StringPool.SPACE));

				String errorMessage = StreamUtil.toString(errorStream);

				if (Validator.isNotNull(errorMessage) && _log.isWarnEnabled()) {
					_log.warn(errorMessage);
				}
			}
		}
		finally {
			if (process != null) {
				try {
					int exitCode = process.waitFor();

					if ((exitCode != 0) && _log.isWarnEnabled()) {
						_log.warn(
							"Subprocess JvmOptionsParser terminated with " +
								"exit code " + exitCode);
					}
				}
				catch (InterruptedException ie) {
					process.destroy();

					if (_log.isWarnEnabled()) {
						_log.warn(
							"Forcibly killed subprocess JvmOptionsParser on " +
								"interruption",
							ie);
					}
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(Sidecar.class);

	private ProcessChannel<Serializable> _processChannel;
	private final ProcessExecutor _processExecutor;
	private final SidecarConfig _sidecarConfig;

}