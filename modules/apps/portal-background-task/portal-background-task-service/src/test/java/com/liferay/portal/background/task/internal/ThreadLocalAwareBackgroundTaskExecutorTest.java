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

package com.liferay.portal.background.task.internal;

import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskConstants;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskResult;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskThreadLocalManager;
import com.liferay.portal.kernel.test.CaptureHandler;
import com.liferay.portal.kernel.test.JDKLoggerTestUtil;
import com.liferay.portal.kernel.test.util.ProxyTestUtil;
import com.liferay.portal.kernel.util.ProxyFactory;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author André de Oliveira
 */
public class ThreadLocalAwareBackgroundTaskExecutorTest {

	@Test
	public void testStaleBackgroundTaskIsSkipped() throws Exception {
		BackgroundTaskThreadLocalManager backgroundTaskThreadLocalManager =
			ProxyTestUtil.getProxy(
				BackgroundTaskThreadLocalManager.class,
				"deserializeThreadLocals",
				new StaleBackgroundTaskException("Unable to find company"));

		ThreadLocalAwareBackgroundTaskExecutor
			threadLocalAwareBackgroundTaskExecutor =
				new ThreadLocalAwareBackgroundTaskExecutor(
					ProxyTestUtil.getProxy(
						BackgroundTaskExecutor.class, "execute",
						new BackgroundTaskResult(
							BackgroundTaskConstants.STATUS_FAILED)),
					backgroundTaskThreadLocalManager);

		try (CaptureHandler captureHandler =
				JDKLoggerTestUtil.configureJDKLogger(
					ThreadLocalAwareBackgroundTaskExecutor.class.getName(),
					Level.INFO)) {

			BackgroundTask backgroundTask = ProxyFactory.newDummyInstance(
				BackgroundTask.class);

			BackgroundTaskResult backgroundTaskResult =
				threadLocalAwareBackgroundTaskExecutor.execute(backgroundTask);

			List<LogRecord> logRecords = captureHandler.getLogRecords();

			Assert.assertEquals(logRecords.toString(), 1, logRecords.size());

			LogRecord logRecord = logRecords.get(0);

			Assert.assertEquals(
				"Skipped stale background task " + backgroundTask,
				logRecord.getMessage());

			Assert.assertTrue(
				backgroundTaskResult.getStatusMessage(),
				backgroundTaskResult.isSuccessful());
		}
	}

}