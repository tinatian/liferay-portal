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

package com.liferay.portal.scheduler.internal;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.aop.Messaging;
import com.liferay.portal.kernel.scheduler.SchedulerEngine;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.Trigger;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerResponse;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Tina Tian
 */
@Component(
	immediate = true, property = "scheduler.engine.proxy.bean=true",
	service = AopService.class
)
@Messaging(destinationName = DestinationNames.SCHEDULER_ENGINE)
public class SchedulerEngineProxyBean implements AopService, SchedulerEngine {

	@Override
	public void delete(String groupName, StorageType storageType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(
		String jobName, String groupName, StorageType storageType) {

		throw new UnsupportedOperationException();
	}

	@Override
	public SchedulerResponse getScheduledJob(
		String jobName, String groupName, StorageType storageType) {

		throw new UnsupportedOperationException();
	}

	@Override
	public List<SchedulerResponse> getScheduledJobs() {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<SchedulerResponse> getScheduledJobs(StorageType storageType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<SchedulerResponse> getScheduledJobs(
		String groupName, StorageType storageType) {

		throw new UnsupportedOperationException();
	}

	@Override
	public void pause(String groupName, StorageType storageType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void pause(
		String jobName, String groupName, StorageType storageType) {

		throw new UnsupportedOperationException();
	}

	@Override
	public void resume(String groupName, StorageType storageType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void resume(
		String jobName, String groupName, StorageType storageType) {

		throw new UnsupportedOperationException();
	}

	@Override
	public void schedule(
		Trigger trigger, String description, String destinationName,
		Message message, StorageType storageType) {

		throw new UnsupportedOperationException();
	}

	@Override
	public void shutdown() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void start() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void suppressError(
		String jobName, String groupName, StorageType storageType) {

		throw new UnsupportedOperationException();
	}

	@Override
	public void unschedule(String groupName, StorageType storageType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void unschedule(
		String jobName, String groupName, StorageType storageType) {

		throw new UnsupportedOperationException();
	}

	@Override
	public void update(Trigger trigger, StorageType storageType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void validateTrigger(Trigger trigger, StorageType storageType) {
		throw new UnsupportedOperationException();
	}

}