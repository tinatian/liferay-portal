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

package com.liferay.portal.messaging.internal.sender;

import com.liferay.portal.kernel.concurrent.ThreadPoolExecutor;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.executor.PortalExecutorManager;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.messaging.MessageBusException;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.messaging.SerialDestination;
import com.liferay.portal.kernel.messaging.SynchronousDestination;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.messaging.internal.DefaultMessageBus;
import com.liferay.registry.BasicRegistryImpl;
import com.liferay.registry.RegistryUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class DefaultSynchronousMessageSenderTest {

	@Before
	public void setUp() {
		RegistryUtil.setRegistry(null);
		RegistryUtil.setRegistry(new BasicRegistryImpl());

		_messageBus = new DefaultMessageBus();

		SynchronousDestination synchronousDestination =
			new SynchronousDestination();

		synchronousDestination.setName(
			DestinationNames.MESSAGE_BUS_DEFAULT_RESPONSE);

		_messageBus.addDestination(synchronousDestination);

		_defaultSynchronousMessageSender =
			new DefaultSynchronousMessageSender();

		ReflectionTestUtil.setFieldValue(
			_defaultSynchronousMessageSender, "_entityCache",
			ProxyFactory.newDummyInstance(EntityCache.class));
		ReflectionTestUtil.setFieldValue(
			_defaultSynchronousMessageSender, "_finderCache",
			ProxyFactory.newDummyInstance(FinderCache.class));
		ReflectionTestUtil.setFieldValue(
			_defaultSynchronousMessageSender, "_messageBus", _messageBus);
		ReflectionTestUtil.setFieldValue(
			_defaultSynchronousMessageSender, "_timeout", 10000);

		synchronousDestination.open();
	}

	@After
	public void tearDown() {
		_messageBus.shutdown(true);
	}

	@Test
	public void testSendToAsyncDestination() throws MessageBusException {
		SerialDestination serialDestination = new SerialDestination() {

			@Override
			public void open() {
				portalExecutorManager = new PortalExecutorManager() {

					@Override
					public ThreadPoolExecutor getPortalExecutor(String name) {
						return new ThreadPoolExecutor(1, 1);
					}

					@Override
					public ThreadPoolExecutor getPortalExecutor(
						String name, boolean createIfAbsent) {

						return null;
					}

					@Override
					public ThreadPoolExecutor registerPortalExecutor(
						String name, ThreadPoolExecutor threadPoolExecutor) {

						return null;
					}

					@Override
					public void shutdown() {
					}

					@Override
					public void shutdown(boolean interrupt) {
					}

				};

				super.open();
			}

		};

		serialDestination.setName("testSerialDestination");

		serialDestination.afterPropertiesSet();

		serialDestination.open();

		doTestSend(serialDestination);
	}

	@Test
	public void testSendToSynchronousDestination() throws MessageBusException {
		SynchronousDestination synchronousDestination =
			new SynchronousDestination();

		synchronousDestination.setName("testSynchronousDestination");

		synchronousDestination.afterPropertiesSet();

		synchronousDestination.open();

		doTestSend(synchronousDestination);
	}

	protected void doTestSend(Destination destination)
		throws MessageBusException {

		Object response = new Object();

		destination.register(new ReplayMessageListener(response));

		_messageBus.addDestination(destination);

		try {
			Assert.assertSame(
				response,
				_defaultSynchronousMessageSender.send(
					destination.getName(), new Message()));
		}
		finally {
			_messageBus.removeDestination(destination.getName());

			destination.close(true);
		}
	}

	private DefaultSynchronousMessageSender _defaultSynchronousMessageSender;
	private MessageBus _messageBus;

	private class ReplayMessageListener implements MessageListener {

		public ReplayMessageListener(Object response) {
			_response = response;
		}

		@Override
		public void receive(Message message) {
			Message responseMessage = new Message();

			responseMessage.setDestinationName(
				message.getResponseDestinationName());
			responseMessage.setResponseId(message.getResponseId());

			responseMessage.setPayload(_response);

			_messageBus.sendMessage(
				message.getResponseDestinationName(), responseMessage);
		}

		private final Object _response;

	}

}