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

package com.liferay.commerce.product.internal.helper;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPInstanceOptionValueRelLocalService;
import com.liferay.commerce.product.service.persistence.CPInstancePersistence;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lily Chi
 */
@Component(enabled = false, service = UpdateCPInstanceStatusHelper.class)
public class UpdateCPInstanceStatusHelper {

	public CPInstance updateStatus(long userId, long cpInstanceId, int status)
		throws PortalException {

		User user = _userLocalService.getUser(userId);
		Date date = new Date();

		CPInstance cpInstance = _cpInstancePersistence.findByPrimaryKey(
			cpInstanceId);

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		if (!cpDefinition.isIgnoreSKUCombinations() &&
			!_cpInstanceOptionValueRelLocalService.hasCPInstanceOptionValueRel(
				cpInstance.getCPInstanceId())) {

			status = WorkflowConstants.STATUS_INACTIVE;
		}

		if ((status == WorkflowConstants.STATUS_APPROVED) &&
			(cpInstance.getDisplayDate() != null) &&
			date.before(cpInstance.getDisplayDate())) {

			status = WorkflowConstants.STATUS_SCHEDULED;
		}

		if (status == WorkflowConstants.STATUS_APPROVED) {
			Date expirationDate = cpInstance.getExpirationDate();

			if ((expirationDate != null) && expirationDate.before(date)) {
				cpInstance.setExpirationDate(null);
			}
		}

		if (status == WorkflowConstants.STATUS_EXPIRED) {
			cpInstance.setExpirationDate(date);
		}

		if ((cpInstance.getStatus() == WorkflowConstants.STATUS_APPROVED) &&
			(status != WorkflowConstants.STATUS_APPROVED)) {

			_cpInstanceCPDefinitionOptionValueRelHelper.
				resetCPInstanceCPDefinitionOptionValueRels(
					cpInstance.getCPInstanceUuid());
		}

		cpInstance.setStatus(status);
		cpInstance.setStatusByUserId(user.getUserId());
		cpInstance.setStatusByUserName(user.getFullName());
		cpInstance.setStatusDate(date);

		return _cpInstancePersistence.update(cpInstance);
	}

	@Reference
	private CPInstanceCPDefinitionOptionValueRelHelper
		_cpInstanceCPDefinitionOptionValueRelHelper;

	@Reference
	private CPInstanceOptionValueRelLocalService
		_cpInstanceOptionValueRelLocalService;

	@Reference
	private CPInstancePersistence _cpInstancePersistence;

	@Reference
	private UserLocalService _userLocalService;

}