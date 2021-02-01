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
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.util.xml.XMLSafeReader;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

	public static Map<String, Level> getCurrentLoggersName(String keywords) {
		Map<String, Level> currentLoggersNameMap = new TreeMap<>();

		Enumeration<Logger> enumeration = LogManager.getCurrentLoggers();

		while (enumeration.hasMoreElements()) {
			Logger logger = enumeration.nextElement();

			String loggerName = logger.getName();

			if (Validator.isNull(keywords) || loggerName.contains(keywords)) {
				currentLoggersNameMap.put(loggerName, logger.getLevel());
			}
		}

		return currentLoggersNameMap;
	}

	public static Map<String, String> getLoggersNameFromXml(String xml)
		throws Exception {

		Map<String, String> loggersNameMap = new HashMap<>();

		Document document = _getDocument(xml);

		Element rootElement = document.getRootElement();

		List<Element> categoryElements = rootElement.elements("category");

		for (Element categoryElement : categoryElements) {
			Element priorityElement = categoryElement.element("priority");

			loggersNameMap.put(
				categoryElement.attributeValue("name"),
				priorityElement.attributeValue("value"));
		}

		return loggersNameMap;
	}

	public static String getOriginalPriority(String className) {
		Level level = Level.ALL;

		Enumeration<Logger> enumeration = LogManager.getCurrentLoggers();

		while (enumeration.hasMoreElements()) {
			Logger logger = enumeration.nextElement();

			if (className.equals(logger.getName())) {
				level = logger.getLevel();

				break;
			}
		}

		return level.toString();
	}

	public static String removeAppender(String content, String appenderName) {
		try {
			Document document = _getDocument(content);

			Element rootElement = document.getRootElement();

			List<Element> appenderElements = rootElement.elements("appender");

			for (Element appenderElement : appenderElements) {
				String name = appenderElement.attributeValue("name");

				if (name.equals(appenderName)) {
					rootElement.remove(appenderElement);

					break;
				}
			}

			content = document.asXML();
		}
		catch (Exception exception) {
			_log.error(exception, exception);

			return StringUtil.removeSubstring(
				content,
				"<appender-ref ref=\"" + appenderName + "\"" +
					StringPool.SPACE + "/>");
		}

		return StringUtil.removeSubstring(
			content, "<appender-ref ref=\"" + appenderName + "\"/>");
	}

	public static void setLevel(String name, String priority) {
		Logger logger = Logger.getLogger(name);

		logger.setLevel(Level.toLevel(priority));
	}

	public static void shutdownLog4J() {
		LoggerRepository loggerRepository = LogManager.getLoggerRepository();

		loggerRepository.shutdown();
	}

	private static Document _getDocument(String xml) throws Exception {
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

		return saxReader.read(new XMLSafeReader(xml));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		Log4JConfigurator.class);

}