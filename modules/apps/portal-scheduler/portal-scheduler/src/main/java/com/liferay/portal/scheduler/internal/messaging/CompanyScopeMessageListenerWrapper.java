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

package com.liferay.portal.scheduler.internal.messaging;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.messaging.MessageListenerException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.scheduler.SchedulerEngine;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;

/**
 * @author Alberto Chaparro
 * @author Tina Tian
 */
public class CompanyScopeMessageListenerWrapper implements MessageListener {

	public CompanyScopeMessageListenerWrapper(
		MessageListener messageListener,
		CompanyLocalService companyLocalService) {

		_messageListener = messageListener;
		_companyLocalService = companyLocalService;
	}

	@Override
	public void receive(Message message) throws MessageListenerException {
		long companyId = message.getLong("companyId");

		if (companyId != CompanyConstants.SYSTEM) {
			_messageListener.receive(message);

			return;
		}

		for (Company company : _companyLocalService.getCompanies(false)) {
			if (!company.isActive()) {
				continue;
			}

			companyId = CompanyThreadLocal.getCompanyId();

			try {
				CompanyThreadLocal.setCompanyId(company.getCompanyId());

				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"Executing ",
							message.get(SchedulerEngine.GROUP_NAME),
							" for company ", company.getCompanyId()));
				}

				_messageListener.receive(message);
			}
			finally {
				CompanyThreadLocal.setCompanyId(companyId);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CompanyScopeMessageListenerWrapper.class);

	private final CompanyLocalService _companyLocalService;
	private final MessageListener _messageListener;

}