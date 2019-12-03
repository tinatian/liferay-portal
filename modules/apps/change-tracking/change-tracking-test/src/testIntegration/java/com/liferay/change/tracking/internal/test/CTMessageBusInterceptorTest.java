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

package com.liferay.change.tracking.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTMessageLocalService;
import com.liferay.change.tracking.service.CTProcessLocalService;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.petra.lang.SafeClosable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationConfiguration;
import com.liferay.portal.kernel.messaging.DestinationFactory;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.SubscriptionSender;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Tina Tian
 */
@RunWith(Arquillian.class)
public class CTMessageBusInterceptorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		Bundle bundle = FrameworkUtil.getBundle(
			CTMessageBusInterceptorTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_testMessageListener = new TestMessageListener();

		Destination destination = _destinationFactory.createDestination(
			new DestinationConfiguration(
				DestinationConfiguration.DESTINATION_TYPE_SERIAL,
				DestinationNames.SUBSCRIPTION_SENDER));

		destination.register(_testMessageListener);

		_serviceRegistration = bundleContext.registerService(
			Destination.class, destination,
			MapUtil.singletonDictionary(
				"destination.name", DestinationNames.SUBSCRIPTION_SENDER));
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration.unregister();
	}

	@Before
	public void setUp() throws Exception {
		long ctCollectionId = _counterLocalService.increment(
			CTCollection.class.getName());

		_ctCollection = _ctCollectionLocalService.createCTCollection(
			ctCollectionId);

		_ctCollection.setUserId(TestPropsValues.getUserId());
		_ctCollection.setName(String.valueOf(ctCollectionId));
		_ctCollection.setStatus(WorkflowConstants.STATUS_DRAFT);

		_ctCollection = _ctCollectionLocalService.updateCTCollection(
			_ctCollection);

		_testMessageListener.receive(null);
	}

	@Sync
	@Test
	public void testInterceptMessageToAsyncDestination() throws Exception {
		long companyId = TestPropsValues.getCompanyId();

		SubscriptionSender subscriptionSender = new SubscriptionSender();

		subscriptionSender.setCompanyId(companyId);
		subscriptionSender.setMailId(
			CTMessageBusInterceptorTest.class.getName(), "test");

		try (SafeClosable safeClosable =
				CTCollectionThreadLocal.setCTCollectionId(
					_ctCollection.getCtCollectionId())) {

			_journalArticleLocalService.updateWithUnsafeFunction(
				journalArticleCTPersistence -> {
					subscriptionSender.flushNotificationsAsync();

					return null;
				});

			Assert.assertNull(_testMessageListener.getReceivedMessage());

			subscriptionSender.flushNotificationsAsync();

			Message message = _testMessageListener.getReceivedMessage();

			Assert.assertSame(subscriptionSender, message.getPayload());

			_testMessageListener.receive(null);
		}

		List<Message> messages = _ctMessageLocalService.getMessages(
			_ctCollection.getCtCollectionId());

		Assert.assertSame(messages.toString(), 1, messages.size());

		Message deserializedMessage = messages.get(0);

		Assert.assertEquals(
			DestinationNames.SUBSCRIPTION_SENDER,
			deserializedMessage.getDestinationName());

		SubscriptionSender deserializedSubscriptionSender =
			(SubscriptionSender)deserializedMessage.getPayload();

		Assert.assertEquals(
			companyId, deserializedSubscriptionSender.getCompanyId());

		Assert.assertNull(_testMessageListener.getReceivedMessage());
	}

	@Sync
	@Test
	public void testPublishMessage() throws Exception {
		long companyId = TestPropsValues.getCompanyId();

		Message message = new Message();

		message.setDestinationName(DestinationNames.SUBSCRIPTION_SENDER);

		SubscriptionSender subscriptionSender = new SubscriptionSender();

		subscriptionSender.setCompanyId(companyId);
		subscriptionSender.setMailId(
			CTMessageBusInterceptorTest.class.getName(), "test");

		message.setPayload(subscriptionSender);

		_ctMessageLocalService.addCTMessage(
			_ctCollection.getCtCollectionId(), message);

		Assert.assertNull(_testMessageListener.getReceivedMessage());

		try (SafeClosable safeClosable =
				CTCollectionThreadLocal.setCTCollectionId(
					_ctCollection.getCtCollectionId())) {

			_ctProcessLocalService.addCTProcess(
				_ctCollection.getUserId(), _ctCollection.getCtCollectionId());
		}

		Message receivedMessage = _testMessageListener.getReceivedMessage();

		SubscriptionSender deserializedSubscriptionSender =
			(SubscriptionSender)receivedMessage.getPayload();

		Assert.assertEquals(
			companyId, deserializedSubscriptionSender.getCompanyId());

		List<Message> messages = _ctMessageLocalService.getMessages(
			_ctCollection.getCtCollectionId());

		Assert.assertSame(messages.toString(), 1, messages.size());

		Message deserializedMessage = messages.get(0);

		Assert.assertEquals(
			DestinationNames.SUBSCRIPTION_SENDER,
			deserializedMessage.getDestinationName());

		deserializedSubscriptionSender =
			(SubscriptionSender)receivedMessage.getPayload();

		Assert.assertEquals(
			companyId, deserializedSubscriptionSender.getCompanyId());

		_ctCollectionLocalService.deleteCTCollection(_ctCollection);

		messages = _ctMessageLocalService.getMessages(
			_ctCollection.getCtCollectionId());

		Assert.assertTrue(messages.toString(), messages.isEmpty());

		_ctCollection = null;
	}

	@Inject
	private static CounterLocalService _counterLocalService;

	@Inject
	private static CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private static CTMessageLocalService _ctMessageLocalService;

	@Inject
	private static CTProcessLocalService _ctProcessLocalService;

	@Inject
	private static DestinationFactory _destinationFactory;

	@Inject
	private static JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private static MessageBus _messageBus;

	private static ServiceRegistration<?> _serviceRegistration;
	private static TestMessageListener _testMessageListener;

	@DeleteAfterTestRun
	private CTCollection _ctCollection;

	private static class TestMessageListener implements MessageListener {

		public Message getReceivedMessage() {
			return _message;
		}

		@Override
		public void receive(Message message) {
			_message = message;
		}

		private Message _message;

	}

}