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

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.IOException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.AbstractConfiguration;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.plugins.util.PluginManager;
import org.apache.logging.log4j.core.config.xml.XmlConfiguration;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * @author Hai Yu
 */
public class Log4JConfigurator {

	public static void configureLog4JXml(String xml) {
		try {
			ConfigurationSource configurationSource = new ConfigurationSource(
				new UnsyncByteArrayInputStream(xml.getBytes(StringPool.UTF8)));

			AbstractConfiguration abstractConfiguration = null;

			if (xml.contains(
					"<!DOCTYPE log4j:configuration SYSTEM \"log4j.dtd\">")) {

				abstractConfiguration =
					new org.apache.log4j.xml.XmlConfiguration(
						_loggerContext, configurationSource, 0);
			}
			else {
				abstractConfiguration = new XmlConfiguration(
					_loggerContext, configurationSource);
			}

			_centralizedConfiguration.addConfiguration(abstractConfiguration);
		}
		catch (IOException ioException) {
			_log.error(ioException, ioException);
		}
	}

	public static Map<String, String> getLoggersNameFromXml(String xml)
		throws Exception {

		SAXReader saxReader = new SAXReader();

		saxReader.setEntityResolver(
			new EntityResolver() {

				@Override
				public InputSource resolveEntity(
					String publicId, String systemId) {

					if (systemId.endsWith("log4j.dtd")) {
						return new InputSource(
							Level.class.getResourceAsStream("xml/log4j.dtd"));
					}

					return null;
				}

			});

		Document document = saxReader.read(new UnsyncStringReader(xml));

		Element rootElement = document.getRootElement();

		Element loggersElement = rootElement.element("Loggers");

		if (loggersElement == null) {
			if (_log.isInfoEnabled()) {
				_log.info("Config file " + xml + " does not include <Loggers>");
			}

			return Collections.emptyMap();
		}

		Map<String, String> loggerLevelStrings = new HashMap<>();

		List<Element> loggerElements = loggersElement.elements("Logger");

		for (Element loggerElement : loggerElements) {
			loggerLevelStrings.put(
				loggerElement.attributeValue("name"),
				loggerElement.attributeValue("level"));
		}

		return loggerLevelStrings;
	}

	public static Map<String, String> getLogLevelStrings() {
		Map<String, String> logLevelStrings = new HashMap<>();

		Map<String, LoggerConfig> loggerLoggerConfigs =
			_centralizedConfiguration.getLoggers();

		for (Map.Entry<String, LoggerConfig> loggerNameEntry :
				loggerLoggerConfigs.entrySet()) {

			LoggerConfig loggerConfig = loggerNameEntry.getValue();

			if (loggerConfig.getParent() != null) {
				logLevelStrings.put(
					loggerNameEntry.getKey(),
					String.valueOf(loggerConfig.getLevel()));
			}
		}

		return logLevelStrings;
	}

	public static String removeAppender(String content, String appenderName) {
		String startAppenderTag = "<RollingFile";
		String endAppenderTag = "</RollingFile>";

		int fromIndex = 0;

		while (content.indexOf(startAppenderTag, fromIndex) > -1) {
			int x = content.indexOf(startAppenderTag, fromIndex);

			int y = content.indexOf(endAppenderTag, x);

			if (y == -1) {
				break;
			}

			String appenderTagContent = content.substring(
				x, y + endAppenderTag.length());

			if (appenderTagContent.contains("name=\"" + appenderName + "\"")) {
				content =
					content.substring(0, x) +
						content.substring(y + endAppenderTag.length());

				break;
			}

			fromIndex = y + endAppenderTag.length();
		}

		return StringUtil.removeSubstring(
			content, "<AppenderRef ref=\"" + appenderName + "\" />");
	}

	public static void setLevel(String name, String priority) {
		LoggerConfig loggerConfig = _centralizedConfiguration.getLogger(name);

		Level level = Level.toLevel(priority);

		if (loggerConfig != null) {
			loggerConfig.setLevel(level);

			Logger logger = _loggerContext.getLogger(name);

			logger.setLevel(level);
		}
		else {
			loggerConfig = new LoggerConfig(name, level, true);

			_centralizedConfiguration.addLogger(name, loggerConfig);
		}
	}

	public static void shutdownLog4J() {
		LogManager.shutdown();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		Log4JConfigurator.class);

	private static final CentralizedConfiguration _centralizedConfiguration;
	private static final LoggerContext _loggerContext;

	static {
		PluginManager.addPackage("com.liferay.petra.log4j");

		LoggerContext loggerContext = (LoggerContext)LogManager.getContext();

		CentralizedConfiguration centralizedConfiguration =
			new CentralizedConfiguration(loggerContext);

		loggerContext.setConfiguration(centralizedConfiguration);

		_loggerContext = loggerContext;

		_centralizedConfiguration = centralizedConfiguration;
	}

}