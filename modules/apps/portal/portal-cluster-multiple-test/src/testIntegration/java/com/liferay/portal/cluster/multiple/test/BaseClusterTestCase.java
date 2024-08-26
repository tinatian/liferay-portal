/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cluster.multiple.test;

import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;
import java.util.concurrent.FutureTask;

/**
 * @author Tina Tian
 */
public class BaseClusterTestCase {

	@BeforeClass
	public static void setUpClass() throws IOException {
		_testFolder = FileUtil.createTempFolder();

		_testFolder.mkdirs();
	}

	@AfterClass
	public static void tearDownClass() {
		FileUtil.deltree(_testFolder);
	}

	@Before
	public void setUp() {


	}

	protected void setupServer(String serverDir) {

	}

	protected void setupTomcat(String targetDir) throws Exception {
		FileUtil.copyDirectory(PropsUtil.get(PropsKeys.LIFERAY_HOME), targetDir);

		String content = FileUtil.read(
			_getTomcatBase(targetDir) + "/conf/server.xml");

		content = content.replace("8005", "8006");
		content = content.replace("8080", "8081");
		content = content.replace("8443", "8444");

		Files.write(
			Paths.get(_getTomcatBase(targetDir), "/conf/server.xml"),
			content.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);

		File file = new File(
			PropsUtil.get(PropsKeys.LIFERAY_HOME) +
				"/portal-setup-wizard.properties");

		Properties properties = new Properties();

		if (file.exists()) {
			properties.load(new FileReader(file));
		}

		properties.setProperty(
			"module.framework.properties.osgi.console", "11312");
		properties.setProperty("liferay.home", targetDir);

		try (FileWriter fileWriter = new FileWriter(targetDir + "/portal-setup-wizard.properties")) {
			properties.store(fileWriter, "Configuration Settings");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void removeTomcat(String target) {
		FileUtil.deltree(target);
	}

	protected Process startTomcat(String target)
		throws Exception {

		try (BufferedOutputStream bufferedOutputStream =
				 new BufferedOutputStream(
					 Files.newOutputStream(
						 Paths.get(
							 _getTomcatBase(target), "/logs/catalina.out"),
						 StandardOpenOption.CREATE,
						 StandardOpenOption.TRUNCATE_EXISTING))) {

			return startTomcat(
				_getTomcatBase(target) + "/bin",
				new String[] {"sh", "catalina.sh", "run"},
				new PrintStream(bufferedOutputStream));
		}
	}

	private String _getTomcatBase(String target) {
		String liferayHome = PropsUtil.get(PropsKeys.LIFERAY_HOME);

		if (!_TOMCAT_BASE.startsWith(liferayHome)) {
			throw new IllegalStateException("");
		}

		return target + _TOMCAT_BASE.substring(liferayHome.length());
	}


	protected void shutdownTomcat(Process process) throws Exception {
		process.destroy();

		process.waitFor();
	}

	protected Process startTomcat(
			String tomcatBin, String[] command, PrintStream printStream)
		throws Exception {

		ProcessBuilder processBuilder = new ProcessBuilder(command);

		processBuilder.directory(new File(tomcatBin));

		Process process = processBuilder.start();

		BufferedReader stdErrBufferedReader = new BufferedReader(
			new InputStreamReader(process.getErrorStream()));

		FutureTask<Long> stdErrTask = new FutureTask<>(
			() -> {
				String line = null;

				while ((line = stdErrBufferedReader.readLine()) != null) {
					int startIndex = line.indexOf(_STARTED_LINE);

					if (startIndex == -1) {
						printStream.print("*");
					}
					else {
						printStream.println(line);

						startIndex += _STARTED_LINE.length();

						int endIndex = line.indexOf(']', startIndex);

						String time = line.substring(startIndex, endIndex);

						return GetterUtil.getLong(time.replace(",", ""));
					}
				}

				throw new IllegalStateException(
					"Unable to find tomcat startup line");
			});

		Thread stdErrThread = new Thread(stdErrTask, "Std Err Thread");

		stdErrThread.start();

		BufferedReader stdOutBufferedReader = new BufferedReader(
			new InputStreamReader(process.getInputStream()));

		Thread stdOutThread = new Thread(
			() -> {
				String line = null;

				boolean error = false;

				try {
					while ((line = stdOutBufferedReader.readLine()) != null) {
						if (error || line.contains("ERROR")) {
							printStream.println(line);

							error = true;
						}
						else {
							printStream.print(".");
						}
					}
				}
				catch (IOException ioException) {
				}
			},
			"Std out Thread");

		stdOutThread.start();

		stdErrThread.join();

		return process;
	}

	private static final String _STARTED_LINE =
		"org.apache.catalina.startup.Catalina.start Server startup in [";

	private static final String _TOMCAT_BASE = System.getProperty("catalina.base");

	private static File _testFolder;


	public static class TestNode {

		public int getPort() {
			return _port;
		}

		public String getName () {
			return _name;
		}

		private TestNode(String name, int port) {
			_name = name;
			_port = port;
		}

		private final String _name;
		private final int _port;
	}

	private void _initalizeTestNode(TestNode testNode) {

	}

	private void _setupNode()

}
