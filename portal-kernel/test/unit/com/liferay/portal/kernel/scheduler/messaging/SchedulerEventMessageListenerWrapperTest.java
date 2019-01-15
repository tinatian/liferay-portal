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

package com.liferay.portal.kernel.scheduler.messaging;

import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.test.rule.NewEnv;
import com.liferay.portal.kernel.test.rule.NewEnvTestRule;
import com.liferay.portal.kernel.test.util.PropsTestUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.registry.BasicRegistryImpl;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Tina Tian
 */
@NewEnv(type = NewEnv.Type.CLASSLOADER)
public class SchedulerEventMessageListenerWrapperTest {

	@ClassRule
	@Rule
	public static final NewEnvTestRule newEnvTestRule = NewEnvTestRule.INSTANCE;

	@Before
	public void setUp() {
		RegistryUtil.setRegistry(new BasicRegistryImpl());
	}

	@Test
	public void testConcurrentReceiveWithoutTimeout() throws Exception {
		PropsTestUtil.setProps(
			PropsKeys.SCHEDULER_EVENT_MESSAGE_LISTENER_LOCK_TIMEOUT, "0");

		SchedulerEventMessageListenerWrapper
			schedulerEventMessageListenerWrapper =
				new SchedulerEventMessageListenerWrapper();

		TestMessageListener testMessageListener = new TestMessageListener();

		schedulerEventMessageListenerWrapper.setMessageListener(
			testMessageListener);

		Message message1 = new Message();

		FutureTask<Void> futureTask1 = _startThread(
			schedulerEventMessageListenerWrapper, "Thread1", message1);

		testMessageListener.waitUntilBlock();

		Message message2 = new Message();

		FutureTask<Void> futureTask2 = _startThread(
			schedulerEventMessageListenerWrapper, "Thread2", message2);

		try {
			futureTask2.get(1000, TimeUnit.MICROSECONDS);

			Assert.fail();
		}
		catch (Exception e) {
			Assert.assertTrue(e instanceof TimeoutException);
		}

		testMessageListener.unblock();

		futureTask1.get();
		futureTask2.get();

		Assert.assertSame(
			"Message is not processed", message1, message1.getResponse());
		Assert.assertSame(
			"Message is not processed", message2, message2.getResponse());
	}

	@Test
	public void testConcurrentReceiveWithTimeout() throws Exception {
		PropsTestUtil.setProps(
			PropsKeys.SCHEDULER_EVENT_MESSAGE_LISTENER_LOCK_TIMEOUT, "1");

		SchedulerEventMessageListenerWrapper
			schedulerEventMessageListenerWrapper =
				new SchedulerEventMessageListenerWrapper();

		TestMessageListener testMessageListener = new TestMessageListener();

		schedulerEventMessageListenerWrapper.setMessageListener(
			testMessageListener);

		Registry registry = RegistryUtil.getRegistry();

		TestMessageBusInvocationHandler testMessageBusInvocationHandler =
			new TestMessageBusInvocationHandler(
				schedulerEventMessageListenerWrapper);

		registry.registerService(
			MessageBus.class,
			(MessageBus)ProxyUtil.newProxyInstance(
				MessageBus.class.getClassLoader(),
				new Class<?>[] {MessageBus.class},
				testMessageBusInvocationHandler));

		Message message1 = new Message();

		FutureTask<Void> futureTask1 = _startThread(
			schedulerEventMessageListenerWrapper, "Thread1", message1);

		testMessageListener.waitUntilBlock();

		Message message2 = new Message();

		FutureTask<Void> futureTask2 = _startThread(
			schedulerEventMessageListenerWrapper, "Thread2", message2);

		futureTask2.get();

		testMessageListener.unblock();

		FutureTask<Void> resendFutureTask =
			testMessageBusInvocationHandler.getFutureTask();

		futureTask1.get();
		resendFutureTask.get();

		Assert.assertSame(
			"Message is not processed", message1, message1.getResponse());
		Assert.assertSame(
			"Message is not processed", message2, message2.getResponse());
	}

	private FutureTask<Void> _startThread(
		SchedulerEventMessageListenerWrapper
			schedulerEventMessageListenerWrapper,
		String threadName, Message message) {

		FutureTask<Void> futureTask = new FutureTask<>(
			() -> {
				schedulerEventMessageListenerWrapper.receive(message);

				return null;
			});

		Thread thread = new Thread(
			futureTask,
			"SchedulerEventMessageListenerWrapperTest_startThread_" +
				threadName);

		thread.start();

		return futureTask;
	}

	private class TestMessageBusInvocationHandler implements InvocationHandler {

		public FutureTask<Void> getFutureTask() {
			return _futureTask;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

			String methodName = method.getName();

			if (methodName.equals("sendMessage")) {
				_futureTask = _startThread(
					_schedulerEventMessageListenerWrapper, "Resend",
					(Message)args[1]);
			}

			return null;
		}

		private TestMessageBusInvocationHandler(
			SchedulerEventMessageListenerWrapper
				schedulerEventMessageListenerWrapper) {

			_schedulerEventMessageListenerWrapper =
				schedulerEventMessageListenerWrapper;
		}

		private FutureTask<Void> _futureTask;
		private final SchedulerEventMessageListenerWrapper
			_schedulerEventMessageListenerWrapper;

	}

	private class TestMessageListener implements MessageListener {

		@Override
		public void receive(Message message) {
			if (_lock.tryLock()) {
				try {
					_waitCountDownLatch.countDown();

					_blockCountDownLatch.await();

					message.setResponse(message);
				}
				catch (InterruptedException ie) {
				}
				finally {
					_lock.unlock();
				}
			}
		}

		public void unblock() {
			_blockCountDownLatch.countDown();
		}

		public void waitUntilBlock() throws InterruptedException {
			_waitCountDownLatch.await();
		}

		private final CountDownLatch _blockCountDownLatch = new CountDownLatch(
			1);
		private final Lock _lock = new ReentrantLock();
		private final CountDownLatch _waitCountDownLatch = new CountDownLatch(
			1);

	}

}