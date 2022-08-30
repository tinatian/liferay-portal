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

import com.liferay.commerce.product.exception.CPDefinitionIgnoreSKUCombinationsException;
import com.liferay.commerce.product.exception.CPInstanceSkuException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceOptionValueRel;
import com.liferay.commerce.product.service.CPInstanceOptionValueRelLocalService;
import com.liferay.commerce.product.service.persistence.CPDefinitionOptionRelPersistence;
import com.liferay.commerce.product.service.persistence.CPDefinitionPersistence;
import com.liferay.commerce.product.service.persistence.CPInstancePersistence;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lily Chi
 */
@Component(enabled = false, service = CheckCPInstancesHelper.class)
public class CheckCPInstancesHelper {

	public void checkCPInstances(
			long userId, long cpDefinitionId, boolean ignoreSKUCombinations)
		throws PortalException {

		if (ignoreSKUCombinations) {
			int cpInstancesCount = _cpInstancePersistence.countByC_ST(
				cpDefinitionId, WorkflowConstants.STATUS_APPROVED);

			if (cpInstancesCount <= 1) {
				return;
			}

			throw new CPDefinitionIgnoreSKUCombinationsException();
		}

		int cpDefinitionOptionRelsCount =
			_cpDefinitionOptionRelPersistence.countByC_SC(cpDefinitionId, true);

		if (cpDefinitionOptionRelsCount == 0) {
			return;
		}

		List<CPInstance> cpInstances = _cpInstancePersistence.findByC_ST(
			cpDefinitionId, WorkflowConstants.STATUS_APPROVED,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		for (CPInstance cpInstance : cpInstances) {
			if (!_cpInstanceOptionValueRelLocalService.
					hasCPInstanceOptionValueRel(cpInstance.getCPInstanceId())) {

				_updateCPInstanceStatusHelper.updateStatus(
					userId, cpInstance.getCPInstanceId(),
					WorkflowConstants.STATUS_INACTIVE);
			}
		}
	}

	public void checkCPInstancesByDisplayDate(long cpDefinitionId)
		throws PortalException {

		List<CPInstance> cpInstances = null;

		if (cpDefinitionId > 0) {
			cpInstances = _cpInstancePersistence.findByC_LtD_S(
				cpDefinitionId, new Date(), WorkflowConstants.STATUS_SCHEDULED);
		}
		else {
			cpInstances = _cpInstancePersistence.findByLtD_S(
				new Date(), WorkflowConstants.STATUS_SCHEDULED);
		}

		for (CPInstance cpInstance : cpInstances) {
			long userId = _portal.getValidUserId(
				cpInstance.getCompanyId(), cpInstance.getUserId());

			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setCommand(Constants.UPDATE);
			serviceContext.setScopeGroupId(cpInstance.getGroupId());
			serviceContext.setUserId(userId);
			serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

			CPDefinition cpDefinition =
				_cpDefinitionPersistence.findByPrimaryKey(
					cpInstance.getCPDefinitionId());

			if (cpDefinition.isIgnoreSKUCombinations()) {
				expireApprovedSiblingCPInstances(
					cpInstance.getCPDefinitionId(),
					cpInstance.getCPInstanceId(), serviceContext);
			}
			else {
				_expireApprovedSiblingMatchingCPInstances(
					cpInstance.getCPDefinitionId(),
					cpInstance.getCPInstanceId(), serviceContext);
			}

			_updateCPInstanceStatusHelper.updateStatus(
				userId, cpInstance.getCPInstanceId(),
				WorkflowConstants.STATUS_APPROVED);
		}
	}

	public void expireApprovedSiblingCPInstances(
			long cpDefinitionId, long siblingCPInstanceId,
			ServiceContext serviceContext)
		throws PortalException {

		List<CPInstance> cpInstances = _cpInstancePersistence.findByC_ST(
			cpDefinitionId, WorkflowConstants.STATUS_APPROVED);

		for (CPInstance cpInstance : cpInstances) {
			if (cpInstance.getCPInstanceId() == siblingCPInstanceId) {
				continue;
			}

			_updateCPInstanceStatusHelper.updateStatus(
				serviceContext.getUserId(), cpInstance.getCPInstanceId(),
				WorkflowConstants.STATUS_EXPIRED);
		}
	}

	public void validateSku(long cpDefinitionId, long cpInstanceId, String sku)
		throws CPInstanceSkuException {

		if (Validator.isNull(sku)) {
			throw new CPInstanceSkuException(
				"SKU value required for product definition ID " +
					cpDefinitionId);
		}

		CPInstance cpInstance = _cpInstancePersistence.fetchByCPDI_SKU(
			cpDefinitionId, sku);

		if ((cpInstance == null) ||
			(cpInstanceId == cpInstance.getCPInstanceId())) {

			return;
		}

		throw new CPInstanceSkuException(
			"Duplicate SKU value for product definition ID " + cpDefinitionId);
	}

	private void _expireApprovedSiblingMatchingCPInstances(
			long cpDefinitionId, long cpInstanceId,
			ServiceContext serviceContext)
		throws PortalException {

		List<CPInstance> cpInstances = _cpInstancePersistence.findByC_ST(
			cpDefinitionId, WorkflowConstants.STATUS_APPROVED);

		List<CPInstanceOptionValueRel> cpInstanceCPInstanceOptionValueRels =
			_cpInstanceOptionValueRelLocalService.
				getCPInstanceCPInstanceOptionValueRels(cpInstanceId);

		for (CPInstance curCPInstance : cpInstances) {
			if (!_cpInstanceOptionValueRelLocalService.
					matchesCPInstanceOptionValueRels(
						curCPInstance.getCPInstanceId(),
						cpInstanceCPInstanceOptionValueRels)) {

				continue;
			}

			_updateCPInstanceStatusHelper.updateStatus(
				serviceContext.getUserId(), curCPInstance.getCPInstanceId(),
				WorkflowConstants.STATUS_EXPIRED);
		}
	}

	@Reference
	private CPDefinitionOptionRelPersistence _cpDefinitionOptionRelPersistence;

	@Reference
	private CPDefinitionPersistence _cpDefinitionPersistence;

	@Reference
	private CPInstanceOptionValueRelLocalService
		_cpInstanceOptionValueRelLocalService;

	@Reference
	private CPInstancePersistence _cpInstancePersistence;

	@Reference
	private Portal _portal;

	@Reference
	private UpdateCPInstanceStatusHelper _updateCPInstanceStatusHelper;

}