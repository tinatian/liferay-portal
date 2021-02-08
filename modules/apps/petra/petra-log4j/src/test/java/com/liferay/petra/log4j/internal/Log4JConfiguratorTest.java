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

package com.liferay.petra.log4j.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.test.rule.NewEnv;
import com.liferay.portal.kernel.test.rule.NewEnvTestRule;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.impl.Log4jContextFactory;
import org.apache.logging.log4j.core.selector.BasicContextSelector;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Hai Yu
 */
public class Log4JConfiguratorTest {

	@ClassRule
	public static final CodeCoverageAssertor codeCoverageAssertor =
		CodeCoverageAssertor.INSTANCE;

	@Before
	public void setUp() {
		LogManager.setFactory(
			new Log4jContextFactory(new BasicContextSelector()));
	}

	@Test
	public void testConfigureLog4JXmlAppender() {

		// Assert no appender exist

		String loggerName = StringUtil.randomString();

		Log4JConfigurator.configureLog4JXml(
			_generateXMLConfigurationContent(loggerName, _ERROR));

		Logger logger = (Logger)LogManager.getLogger(loggerName);

		_assertAppenders(logger);

		// Assert one appender exist

		Log4JConfigurator.configureLog4JXml(
			_generateXMLConfigurationContent(
				loggerName, _ERROR, ConsoleAppender.class));

		_assertAppenders(logger, ConsoleAppender.class);

		// Assert override the previous appender

		Log4JConfigurator.configureLog4JXml(
			_generateXMLConfigurationContent(
				loggerName, _ERROR, FileAppender.class));

		_assertAppenders(logger, FileAppender.class);

		// Assert two appenders

		Log4JConfigurator.configureLog4JXml(
			_generateXMLConfigurationContent(
				loggerName, _ERROR, ConsoleAppender.class, FileAppender.class));

		_assertAppenders(logger, ConsoleAppender.class, FileAppender.class);
	}

	@Test
	public void testConfigureLog4JXmlLogLevel() {
		String loggerName = StringUtil.randomString();

		Logger logger = (Logger)LogManager.getLogger(loggerName);

		Log4JConfigurator.configureLog4JXml(
			_generateXMLConfigurationContent(loggerName, _ALL));

		_assertLog4JLevel(logger, _ALL);

		Log4JConfigurator.configureLog4JXml(
			_generateXMLConfigurationContent(loggerName, _OFF));

		_assertLog4JLevel(logger, _OFF);

		Log4JConfigurator.configureLog4JXml(
			_generateXMLConfigurationContent(loggerName, _FATAL));

		_assertLog4JLevel(logger, _FATAL);

		Log4JConfigurator.configureLog4JXml(
			_generateXMLConfigurationContent(loggerName, _ERROR));

		_assertLog4JLevel(logger, _ERROR);

		Log4JConfigurator.configureLog4JXml(
			_generateXMLConfigurationContent(loggerName, _WARN));

		_assertLog4JLevel(logger, _WARN);

		Log4JConfigurator.configureLog4JXml(
			_generateXMLConfigurationContent(loggerName, _INFO));

		_assertLog4JLevel(logger, _INFO);

		Log4JConfigurator.configureLog4JXml(
			_generateXMLConfigurationContent(loggerName, _DEBUG));

		_assertLog4JLevel(logger, _DEBUG);

		Log4JConfigurator.configureLog4JXml(
			_generateXMLConfigurationContent(loggerName, _TRACE));

		_assertLog4JLevel(logger, _TRACE);

		Log4JConfigurator.configureLog4JXml(
			_generateXMLConfigurationContent(loggerName, "FAKE_LEVEL"));

		// When level is fake, its level is null and get parent's level,
		// see LoggerConfig.getLevel().

		_assertLog4JLevel(logger, _ERROR);
	}

	@Test
	public void testConstructor() {
		new Log4JConfigurator();
	}

	@Test
	public void testGetOriginalLevel() {
		String loggerName = StringUtil.randomString();

		Assert.assertEquals(
			"The original level should be ALL for Logger not configured or " +
				"created",
			_ALL, Log4JConfigurator.getOriginalLevel(loggerName));

		Log4JConfigurator.configureLog4JXml(
			_generateXMLConfigurationContent(loggerName, _ERROR));

		Assert.assertEquals(
			"The original level should be WARN by configuration", _ERROR,
			Log4JConfigurator.getOriginalLevel(loggerName));
	}

	@Test
	public void testSetLevel() {
		String loggerName = StringUtil.randomString();

		String childLoggerName = loggerName + ".child";

		Logger logger = (Logger)LogManager.getLogger(loggerName);

		Logger childLogger = (Logger)LogManager.getLogger(childLoggerName);

		_assertLog4JLevel(logger, _ERROR);

		_assertLog4JLevel(childLogger, _ERROR);

		Log4JConfigurator.configureLog4JXml(
			_generateXMLConfigurationContent(loggerName, _WARN));

		_assertLog4JLevel(logger, _WARN);

		_assertLog4JLevel(childLogger, _WARN);

		Log4JConfigurator.setLevel(loggerName, _DEBUG);

		_assertLog4JLevel(logger, _DEBUG);

		// In the beginning, LogManager.getLogger(childLoggerName) will create
		// the logger to LoggerContext. When Log4JConfigurator.configureLog4JXml
		// initial, since the logger existed, please see _updateLoggers of
		// CentralizedConfiguration, childLogger level will explicitly be set
		// for WARN by using its parent. This is different from log4j1. For
		// log4j1, this process won't set level for childLogger.

		_assertLog4JLevel(childLogger, _WARN);
	}

	@NewEnv(type = NewEnv.Type.JVM)
	@Test
	public void testShutdownLog4J() {
		Logger logger = (Logger)LogManager.getRootLogger();

		Map<String, Appender> appenders = logger.getAppenders();

		Assert.assertTrue(
			"The root logger should include appenders", !appenders.isEmpty());

		Log4JConfigurator.shutdownLog4J();

		Assert.assertFalse(
			"The root logger should not own appenders after shutting down",
			appenders.isEmpty());
	}

	@Rule
	public final NewEnvTestRule newEnvTestRule = NewEnvTestRule.INSTANCE;

	private void _assertAppenders(Logger logger, Class<?>... appenderTypes) {
		Map<String, Appender> appenders = logger.getAppenders();

		List<String> targetAppenderNames = new ArrayList<>();

		for (String appenderName : appenders.keySet()) {
			targetAppenderNames.add(appenderName);
		}

		Assert.assertEquals(targetAppenderNames.size(), appenderTypes.length);

		for (Class<?> appenderType : appenderTypes) {
			Assert.assertTrue(
				"Missing appender " + appenderType.getName(),
				targetAppenderNames.contains(appenderType.getName()));
		}
	}

	private void _assertLog4JLevel(Logger logger, String expectedLevel) {
		if (expectedLevel.equals("ALL")) {
			Assert.assertTrue(
				"TRACE should be enabled if logging level is ALL",
				logger.isTraceEnabled());

			return;
		}

		String actualLevel = null;

		if (logger.isTraceEnabled()) {
			actualLevel = "TRACE";
		}
		else if (logger.isDebugEnabled()) {
			actualLevel = "DEBUG";
		}
		else if (logger.isInfoEnabled()) {
			actualLevel = "INFO";
		}
		else if (logger.isWarnEnabled()) {
			actualLevel = "WARN";
		}
		else if (logger.isErrorEnabled()) {
			actualLevel = "ERROR";
		}
		else if (logger.isFatalEnabled()) {
			actualLevel = "FATAL";
		}
		else {
			actualLevel = "OFF";
		}

		Assert.assertEquals(
			"Logging level is wrong", expectedLevel, actualLevel);
	}

	private String _generateXMLConfigurationContent(
		String loggerName, String level, Class<?>... appenderTypes) {

		StringBundler sb = new StringBundler(10 + (8 * appenderTypes.length));

		sb.append("<?xml version=\"1.0\"?><Configuration>");

		if (appenderTypes.length > 0) {
			sb.append("<Appenders>");

			for (Class<?> appenderType : appenderTypes) {
				if (appenderType.equals(ConsoleAppender.class)) {
					sb.append("<Console name=\"");
					sb.append(appenderType.getName());
					sb.append("\"></Console>");
				}
				else {
					sb.append("<File name=\"");
					sb.append(appenderType.getName());
					sb.append("\" fileName=\"");
					sb.append(appenderType.getName());
					sb.append("\"></File>");
				}
			}

			sb.append("</Appenders>");
		}

		sb.append("<Loggers><Logger level= \"");
		sb.append(level);
		sb.append("\" name=\"");
		sb.append(loggerName);
		sb.append("\">");

		for (Class<?> appenderType : appenderTypes) {
			sb.append("<AppenderRef ref=\"");
			sb.append(appenderType.getName());
			sb.append("\" />");
		}

		sb.append("</Logger></Loggers></Configuration>");

		return sb.toString();
	}

	private static final String _ALL = "ALL";

	private static final String _DEBUG = "DEBUG";

	private static final String _ERROR = "ERROR";

	private static final String _FATAL = "FATAL";

	private static final String _INFO = "INFO";

	private static final String _OFF = "OFF";

	private static final String _TRACE = "TRACE";

	private static final String _WARN = "WARN";

}