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

import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPInstanceOptionValueRelLocalService;
import com.liferay.commerce.product.service.persistence.CPInstancePersistence;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lily Chi
 */
@Component(enabled = false, service = InactiveCPInstanceHelper.class)
public class InactiveCPInstanceHelper {

	public void inactivateCPDefinitionOptionRelCPInstances(
			long userId, long cpDefinitionId, long cpDefinitionOptionRelId)
		throws PortalException {

		_inactivateCPDefinitionOptionRelCPInstances(
			userId, cpDefinitionOptionRelId,
			_cpInstancePersistence.findByCPDefinitionId(
				cpDefinitionId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null));
	}

	public void inactivateCPDefinitionOptionValueRelCPInstances(
			long userId, long cpDefinitionId, long cpDefinitionOptionValueRelId)
		throws PortalException {

		_inactivateCPDefinitionOptionValueRelCPInstances(
			userId, cpDefinitionOptionValueRelId,
			_cpInstancePersistence.findByCPDefinitionId(
				cpDefinitionId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null));
	}

	public void inactivateIncompatibleCPInstances(
			long userId, long cpDefinitionId)
		throws PortalException {

		List<CPInstance> cpInstances = _cpInstancePersistence.findByC_ST(
			cpDefinitionId, WorkflowConstants.STATUS_APPROVED);

		for (CPInstance curCPInstance : cpInstances) {
			if (_cpInstanceOptionValueRelLocalService.
					matchesCPDefinitionOptionRels(
						cpDefinitionId, curCPInstance.getCPInstanceId())) {

				continue;
			}

			_updateCPInstanceStatusHelper.updateStatus(
				userId, curCPInstance.getCPInstanceId(),
				WorkflowConstants.STATUS_INACTIVE);
		}
	}

	private void _inactivateCPDefinitionOptionRelCPInstances(
			long userId, long cpDefinitionOptionRelId,
			List<CPInstance> cpInstances)
		throws PortalException {

		for (CPInstance cpInstance : cpInstances) {
			if (cpInstance.isInactive() ||
				!_cpInstanceOptionValueRelLocalService.
					hasCPInstanceCPDefinitionOptionRel(
						cpDefinitionOptionRelId,
						cpInstance.getCPInstanceId())) {

				continue;
			}

			if (userId <= 0) {
				userId = cpInstance.getUserId();
			}

			_updateCPInstanceStatusHelper.updateStatus(
				userId, cpInstance.getCPInstanceId(),
				WorkflowConstants.STATUS_INACTIVE);
		}
	}

	private void _inactivateCPDefinitionOptionValueRelCPInstances(
			long userId, long cpDefinitionOptionValueRelId,
			List<CPInstance> cpInstances)
		throws PortalException {

		for (CPInstance cpInstance : cpInstances) {
			if (cpInstance.isInactive() ||
				!_cpInstanceOptionValueRelLocalService.
					hasCPInstanceCPDefinitionOptionValueRel(
						cpDefinitionOptionValueRelId,
						cpInstance.getCPInstanceId())) {

				continue;
			}

			if (userId <= 0) {
				userId = cpInstance.getUserId();
			}

			_updateCPInstanceStatusHelper.updateStatus(
				userId, cpInstance.getCPInstanceId(),
				WorkflowConstants.STATUS_INACTIVE);
		}
	}

	@Reference
	private CPInstanceOptionValueRelLocalService
		_cpInstanceOptionValueRelLocalService;

	@Reference
	private CPInstancePersistence _cpInstancePersistence;

	@Reference
	private UpdateCPInstanceStatusHelper _updateCPInstanceStatusHelper;

}