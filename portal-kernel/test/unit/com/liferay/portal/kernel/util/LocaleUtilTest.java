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

package com.liferay.portal.kernel.util;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.test.CaptureHandler;
import com.liferay.portal.kernel.test.JDKLoggerTestUtil;
import com.liferay.portal.kernel.test.ProxyTestUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;

import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Wesley Gong
 */
public class LocaleUtilTest {

	@Test
	public void testFromLanguageId() {
		ReflectionTestUtil.setFieldValue(
			LanguageUtil.class, "_language",
			ProxyTestUtil.getProxy(
				Language.class,
				ProxyTestUtil.getProxyMethod(
					"isAvailableLocale",
					(Object[] args) -> {
						if (Locale.US.equals(args[0])) {
							return true;
						}

						return false;
					})));

		try (CaptureHandler captureHandler =
				JDKLoggerTestUtil.configureJDKLogger(
					LocaleUtil.class.getName(), Level.WARNING)) {

			List<LogRecord> logRecords = captureHandler.getLogRecords();

			Assert.assertEquals(Locale.US, LocaleUtil.fromLanguageId("en_US"));
			Assert.assertEquals(logRecords.toString(), 0, logRecords.size());

			logRecords.clear();

			LocaleUtil.fromLanguageId("en");

			Assert.assertEquals(logRecords.toString(), 1, logRecords.size());

			LogRecord logRecord = logRecords.get(0);

			Assert.assertEquals(
				"en is not a valid language id", logRecord.getMessage());
		}
	}

}