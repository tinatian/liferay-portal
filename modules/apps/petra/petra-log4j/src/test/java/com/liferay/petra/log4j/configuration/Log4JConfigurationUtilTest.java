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

package com.liferay.petra.log4j.configuration;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.log.Log4jLogFactoryImpl;
import com.liferay.portal.util.FileImpl;
import com.liferay.portal.util.PropsImpl;

import java.util.Enumeration;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Hai Yu
 */
public class Log4JConfigurationUtilTest {

	@ClassRule
	public static final CodeCoverageAssertor codeCoverageAssertor =
		CodeCoverageAssertor.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		PropsUtil.setProps(new PropsImpl());

		LogFactoryUtil.setLogFactory(new Log4jLogFactoryImpl());

		FileUtil fileUtil = new FileUtil();

		fileUtil.setFile(new FileImpl());
	}

	@Before
	public void setUp() throws Exception {
		Log4JConfigurationUtil.configureLog4JXml(
			new String(
				FileUtil.getBytes(
					getClass(), "dependencies/portal-log4j.xml")));
	}

	@Test
	public void testConfigureLog4JXml() throws Exception {
		_assertLog4JLevel(
			"ERROR",
			LogFactoryUtil.getLog(Log4JConfigurationUtilTest.class.getName()));

		_assertLog4JLevel("ALL", LogFactoryUtil.getLog(_LOGGER_ALL));
		_assertLog4JLevel("OFF", LogFactoryUtil.getLog(_LOGGER_OFF));
		_assertLog4JLevel("FATAL", LogFactoryUtil.getLog(_LOGGER_FATAL));
		_assertLog4JLevel("ERROR", LogFactoryUtil.getLog(_LOGGER_ERROR));
		_assertLog4JLevel("WARN", LogFactoryUtil.getLog(_LOGGER_WARN));
		_assertLog4JLevel("INFO", LogFactoryUtil.getLog(_LOGGER_INFO));
		_assertLog4JLevel("DEBUG", LogFactoryUtil.getLog(_LOGGER_DEBUG));
		_assertLog4JLevel("TRACE", LogFactoryUtil.getLog(_LOGGER_TRACE));

		Log4JConfigurationUtil.configureLog4JXml(
			new String(
				FileUtil.getBytes(
					getClass(), "dependencies/portal-log4j-ext.xml")));

		_assertLog4JLevel(
			"WARN",
			LogFactoryUtil.getLog(Log4JConfigurationUtilTest.class.getName()));
	}

	@Test
	public void testConstructor() {
		new Log4JConfigurationUtil();
	}

	@Test
	public void testGetOriginalLevel() {
		Assert.assertEquals(
			"The original level should be WARN by configuration", "ERROR",
			Log4JConfigurationUtil.getOriginalLevel(
				Log4JConfigurationUtilTest.class.getName()));

		Assert.assertEquals(
			"The original level should be WARN by configuration", "WARN",
			Log4JConfigurationUtil.getOriginalLevel(_LOGGER_WARN));

		Assert.assertEquals(
			"The original level should be ALL for Logger not configured or " +
				"created",
			"ALL",
			Log4JConfigurationUtil.getOriginalLevel(StringUtil.randomString()));
	}

	@Test
	public void testSetLevel() {
		Log log = LogFactoryUtil.getLog(_LOGGER_WARN);

		_assertLog4JLevel("WARN", log);

		Log4JConfigurationUtil.setLevel(_LOGGER_WARN, "DEBUG");

		_assertLog4JLevel("DEBUG", log);

		Log childLog = LogFactoryUtil.getLog("com.test.parent.child");

		_assertLog4JLevel("INFO", childLog);

		Log4JConfigurationUtil.setLevel("com.test.parent", "DEBUG");

		_assertLog4JLevel("DEBUG", childLog);
	}

	@Test
	public void testShutdownLog4J() {
		Logger logger = Logger.getRootLogger();

		Enumeration<Appender> appendersEnumeration = logger.getAllAppenders();

		Assert.assertTrue(
			"The root logger should include appenders",
			appendersEnumeration.hasMoreElements());

		Log4JConfigurationUtil.shutdownLog4J();

		Assert.assertFalse(
			"The root logger should not own appenders after shutting down",
			appendersEnumeration.hasMoreElements());
	}

	private void _assertLog4JLevel(String expectedLevel, Log log) {
		if (expectedLevel.equals("ALL")) {
			Assert.assertTrue(
				"TRACE should be enabled if logging level is ALL",
				log.isTraceEnabled());

			return;
		}

		String actualLevel = null;

		if (log.isTraceEnabled()) {
			actualLevel = "TRACE";
		}
		else if (log.isDebugEnabled()) {
			actualLevel = "DEBUG";
		}
		else if (log.isInfoEnabled()) {
			actualLevel = "INFO";
		}
		else if (log.isWarnEnabled()) {
			actualLevel = "WARN";
		}
		else if (log.isErrorEnabled()) {
			actualLevel = "ERROR";
		}
		else if (log.isFatalEnabled()) {
			actualLevel = "FATAL";
		}
		else {
			actualLevel = "OFF";
		}

		Assert.assertEquals(
			"Logging level is wrong", expectedLevel, actualLevel);
	}

	private static final String _LOGGER_ALL = "logger.all";

	private static final String _LOGGER_DEBUG = "logger.debug";

	private static final String _LOGGER_ERROR = "logger.error";

	private static final String _LOGGER_FATAL = "logger.fatal";

	private static final String _LOGGER_INFO = "logger.info";

	private static final String _LOGGER_OFF = "logger.off";

	private static final String _LOGGER_TRACE = "logger.trace";

	private static final String _LOGGER_WARN = "logger.warn";

}