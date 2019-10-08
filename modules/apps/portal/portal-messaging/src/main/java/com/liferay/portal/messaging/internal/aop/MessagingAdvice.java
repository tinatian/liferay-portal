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

package com.liferay.portal.messaging.internal.aop;

import com.liferay.portal.kernel.aop.AopMethodInvocation;
import com.liferay.portal.kernel.aop.ChainableMethodAdvice;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.messaging.aop.Messaging;
import com.liferay.portal.kernel.messaging.proxy.MessageValuesThreadLocal;
import com.liferay.portal.kernel.messaging.proxy.MessagingProxy;
import com.liferay.portal.kernel.messaging.proxy.ProxyModeThreadLocal;
import com.liferay.portal.kernel.messaging.proxy.ProxyRequest;
import com.liferay.portal.kernel.messaging.proxy.ProxyResponse;
import com.liferay.portal.kernel.messaging.sender.SynchronousMessageSender;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Preston Crary
 */
@Component(immediate = true, service = ChainableMethodAdvice.class)
public class MessagingAdvice extends ChainableMethodAdvice {

	@Override
	public Object createMethodContext(
		Class<?> targetClass, Method method,
		Map<Class<? extends Annotation>, Annotation> annotations) {

		Messaging messaging = (Messaging)annotations.get(Messaging.class);

		if (messaging == null) {
			return null;
		}

		return messaging.destinationName();
	}

	@Override
	public Object invoke(
			AopMethodInvocation aopMethodInvocation, Object[] arguments)
		throws Throwable {

		String destinationName = aopMethodInvocation.getAdviceMethodContext();

		ProxyRequest proxyRequest = new ProxyRequest(
			aopMethodInvocation.getMethod(), arguments);

		if (proxyRequest.isSynchronous() ||
			ProxyModeThreadLocal.isForceSync()) {

			if (!_messageBus.hasMessageListener(destinationName)) {
				return proxyRequest.execute(this);
			}

			ProxyResponse proxyResponse =
				(ProxyResponse)_directSynchronousMessageSender.send(
					destinationName, _buildMessage(proxyRequest));

			if (proxyResponse == null) {
				return proxyRequest.execute(this);
			}

			if (proxyResponse.hasError()) {
				throw proxyResponse.getException();
			}

			return proxyResponse.getResult();
		}

		_messageBus.sendMessage(destinationName, _buildMessage(proxyRequest));

		return null;
	}

	private Message _buildMessage(ProxyRequest proxyRequest) {
		Message message = new Message();

		message.setPayload(proxyRequest);

		MessageValuesThreadLocal.populateMessageFromThreadLocals(message);

		if (proxyRequest.isLocal()) {
			message.put(MessagingProxy.LOCAL_MESSAGE, Boolean.TRUE);
		}

		return message;
	}

	@Reference(target = "(mode=DIRECT)")
	private SynchronousMessageSender _directSynchronousMessageSender;

	@Reference
	private MessageBus _messageBus;

}