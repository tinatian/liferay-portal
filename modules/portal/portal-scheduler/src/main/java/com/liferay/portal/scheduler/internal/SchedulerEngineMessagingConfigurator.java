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

import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.messaging.proxy.ProxyMessageListener;
import com.liferay.portal.kernel.scheduler.SchedulerEngine;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(immediate = true, service = SchedulerEngineMessagingConfigurator.class)
public class SchedulerEngineMessagingConfigurator {

	@Activate
	protected void activate() {
		_proxyMessageListener.setManager(_schedulerEngine);
		_proxyMessageListener.setMessageBus(_messageBus);
	}

	@Reference(
		service = Destination.class,
		target = "(destination.name=" + DestinationNames.SCHEDULER_ENGINE + ")"
	)
	protected void setDestination(Destination destination) {
		destination.register(_proxyMessageListener);
	}

	@Reference(
		service = Destination.class,
		target = "(destination.name=" + DestinationNames.SCHEDULER_DISPATCH + ")"
	)
	protected void setDestination1(Destination destination) {
	}

	@Reference(unbind = "-")
	protected void setMessageBus(MessageBus messageBus) {
		_messageBus = messageBus;
	}

	protected void unsetDestination(Destination destination) {
		destination.unregister(_proxyMessageListener);
	}

	private MessageBus _messageBus;
	private final ProxyMessageListener _proxyMessageListener =
		new ProxyMessageListener();
	private SchedulerEngine _schedulerEngine;

}