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
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskResult;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.util.MockHelperUtil;

import java.io.Serializable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author André de Oliveira
 */
public class ThreadLocalAwareBackgroundTaskExecutorTest
	extends BaseBackgroundTaskTestCase {

	@Test
	public void testStaleBackgroundTaskIsSkipped() throws Exception {
		CompanyLocalService companyLocalService =
			MockHelperUtil.setMethodAlwaysReturnExpected(
				CompanyLocalService.class, null, "fetchCompany",
				MockHelperUtil.ANY_LONG);

		backgroundTaskThreadLocalManagerImpl.companyLocalService =
			companyLocalService;

		BackgroundTaskExecutor backgroundTaskExecutor = MockHelperUtil.initMock(
			BackgroundTaskExecutor.class);

		ThreadLocalAwareBackgroundTaskExecutor
			threadLocalAwareBackgroundTaskExecutor =
				new ThreadLocalAwareBackgroundTaskExecutor(
					backgroundTaskExecutor,
					backgroundTaskThreadLocalManagerImpl);

		Map<String, Serializable> taskContextMap = Collections.singletonMap(
			BackgroundTaskThreadLocalManagerImpl.KEY_THREAD_LOCAL_VALUES,
			(Serializable)new HashMap<>(
				Collections.singletonMap("companyId", 1)));

		BackgroundTask backgroundTask =
			MockHelperUtil.setMethodAlwaysReturnExpected(
				BackgroundTask.class, taskContextMap, "getTaskContextMap");

		BackgroundTaskResult backgroundTaskResult =
			threadLocalAwareBackgroundTaskExecutor.execute(backgroundTask);

		Assert.assertTrue(backgroundTaskResult.isSuccessful());

		Assert.assertEquals(
			0, MockHelperUtil.getInteractionTimes(backgroundTaskExecutor));
	}

}