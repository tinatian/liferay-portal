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

import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.xml.DOMConfigurator;

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
		DOMConfigurator domConfigurator = new DOMConfigurator();

		domConfigurator.doConfigure(
			new UnsyncStringReader(xml), LogManager.getLoggerRepository());
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

		List<Element> categoryElements = rootElement.elements("category");

		Map<String, String> loggersNameMap = new HashMap<>();

		for (Element categoryElement : categoryElements) {
			Element priorityElement = categoryElement.element("priority");

			loggersNameMap.put(
				categoryElement.attributeValue("name"),
				priorityElement.attributeValue("value"));
		}

		return loggersNameMap;
	}

	public static Map<String, String> getLogLevelStrings() {
		Map<String, String> logLevelStrings = new HashMap<>();

		Enumeration<Logger> enumeration = LogManager.getCurrentLoggers();

		while (enumeration.hasMoreElements()) {
			Logger logger = enumeration.nextElement();

			Level level = logger.getLevel();

			if (level != null) {
				logLevelStrings.put(logger.getName(), level.toString());
			}
		}

		return logLevelStrings;
	}

	public static String removeAppender(String content, String appenderName) {
		String startAppenderTag = "<appender";
		String endAppenderTag = "</appender>";

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
			content, "<appender-ref ref=\"" + appenderName + "\" />");
	}

	public static void setLevel(String name, String priority) {
		Logger logger = Logger.getLogger(name);

		logger.setLevel(Level.toLevel(priority));
	}

	public static void shutdownLog4J() {
		LoggerRepository loggerRepository = LogManager.getLoggerRepository();

		loggerRepository.shutdown();
	}

}