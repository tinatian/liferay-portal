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

import com.liferay.commerce.product.exception.CPInstanceDisplayDateException;
import com.liferay.commerce.product.exception.CPInstanceExpirationDateException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPInstanceOptionValueRelLocalService;
import com.liferay.commerce.product.service.persistence.CPDefinitionPersistence;
import com.liferay.commerce.product.service.persistence.CPInstancePersistence;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUID;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;

import java.math.BigDecimal;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lily Chi
 */
@Component(enabled = false, service = AddCPInstanceHelper.class)
public class AddCPInstanceHelper {

	public CPInstance addCPInstance(
			String externalReferenceCode, long cpDefinitionId, long groupId,
			String sku, String gtin, String manufacturerPartNumber,
			boolean purchasable,
			Map<Long, List<Long>>
				cpDefinitionOptionRelIdCPDefinitionOptionValueRelIds,
			double width, double height, double depth, double weight,
			BigDecimal price, BigDecimal promoPrice, BigDecimal cost,
			boolean published, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire,
			boolean overrideSubscriptionInfo, boolean subscriptionEnabled,
			int subscriptionLength, String subscriptionType,
			UnicodeProperties subscriptionTypeSettingsUnicodeProperties,
			long maxSubscriptionCycles, boolean deliverySubscriptionEnabled,
			int deliverySubscriptionLength, String deliverySubscriptionType,
			UnicodeProperties deliverySubscriptionTypeSettingsUnicodeProperties,
			long deliveryMaxSubscriptionCycles, String unspsc,
			boolean discontinued, String replacementCPInstanceUuid,
			long replacementCProductId, int discontinuedDateMonth,
			int discontinuedDateDay, int discontinuedDateYear,
			ServiceContext serviceContext)
		throws PortalException {

		// Commerce product instance

		User user = _userLocalService.getUser(serviceContext.getUserId());

		if (Validator.isBlank(externalReferenceCode)) {
			externalReferenceCode = null;
		}

		Date expirationDate = null;
		Date date = new Date();

		Date displayDate = _portal.getDate(
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, user.getTimeZone(),
			CPInstanceDisplayDateException.class);

		if (!neverExpire) {
			expirationDate = _portal.getDate(
				expirationDateMonth, expirationDateDay, expirationDateYear,
				expirationDateHour, expirationDateMinute, user.getTimeZone(),
				CPInstanceExpirationDateException.class);
		}

		long cpInstanceId = _counterLocalService.increment();

		CPInstance cpInstance = _cpInstancePersistence.create(cpInstanceId);

		cpInstance.setExternalReferenceCode(externalReferenceCode);
		cpInstance.setGroupId(groupId);
		cpInstance.setCompanyId(user.getCompanyId());
		cpInstance.setUserId(user.getUserId());
		cpInstance.setUserName(user.getFullName());
		cpInstance.setCPDefinitionId(cpDefinitionId);
		cpInstance.setCPInstanceUuid(_portalUUID.generate());
		cpInstance.setSku(sku);
		cpInstance.setGtin(gtin);
		cpInstance.setManufacturerPartNumber(manufacturerPartNumber);
		cpInstance.setPurchasable(purchasable);
		cpInstance.setWidth(width);
		cpInstance.setHeight(height);
		cpInstance.setDepth(depth);
		cpInstance.setWeight(weight);
		cpInstance.setPrice(price);
		cpInstance.setPromoPrice(promoPrice);
		cpInstance.setCost(cost);
		cpInstance.setPublished(published);
		cpInstance.setDisplayDate(displayDate);
		cpInstance.setExpirationDate(expirationDate);
		cpInstance.setOverrideSubscriptionInfo(overrideSubscriptionInfo);
		cpInstance.setSubscriptionEnabled(subscriptionEnabled);
		cpInstance.setSubscriptionLength(subscriptionLength);
		cpInstance.setSubscriptionType(subscriptionType);
		cpInstance.setSubscriptionTypeSettingsProperties(
			subscriptionTypeSettingsUnicodeProperties);
		cpInstance.setMaxSubscriptionCycles(maxSubscriptionCycles);
		cpInstance.setDeliverySubscriptionEnabled(deliverySubscriptionEnabled);
		cpInstance.setDeliverySubscriptionLength(deliverySubscriptionLength);
		cpInstance.setDeliverySubscriptionType(deliverySubscriptionType);
		cpInstance.setDeliverySubscriptionTypeSettingsProperties(
			deliverySubscriptionTypeSettingsUnicodeProperties);
		cpInstance.setDeliveryMaxSubscriptionCycles(
			deliveryMaxSubscriptionCycles);
		cpInstance.setStatus(WorkflowConstants.STATUS_DRAFT);

		if ((displayDate != null) && date.before(displayDate)) {
			cpInstance.setStatus(WorkflowConstants.STATUS_SCHEDULED);
		}

		if (!neverExpire && expirationDate.before(date)) {
			cpInstance.setStatus(WorkflowConstants.STATUS_EXPIRED);
		}

		cpInstance.setStatusByUserId(user.getUserId());
		cpInstance.setStatusDate(serviceContext.getModifiedDate(date));
		cpInstance.setExpandoBridgeAttributes(serviceContext);
		cpInstance.setUnspsc(unspsc);
		cpInstance.setDiscontinued(discontinued);
		cpInstance.setDiscontinuedDate(
			_portal.getDate(
				discontinuedDateMonth, discontinuedDateDay,
				discontinuedDateYear));
		cpInstance.setReplacementCPInstanceUuid(replacementCPInstanceUuid);
		cpInstance.setReplacementCProductId(replacementCProductId);

		cpInstance = _cpInstancePersistence.update(cpInstance);

		if ((cpDefinitionOptionRelIdCPDefinitionOptionValueRelIds != null) &&
			!cpDefinitionOptionRelIdCPDefinitionOptionValueRelIds.isEmpty()) {

			_cpInstanceOptionValueRelLocalService.
				updateCPInstanceOptionValueRels(
					groupId, user.getCompanyId(), user.getUserId(),
					cpInstanceId,
					cpDefinitionOptionRelIdCPDefinitionOptionValueRelIds);
		}

		_cpDefinitionIndexHelper.reindexCPDefinition(cpDefinitionId);

		if (!isWorkflowActionPublish(serviceContext)) {
			return cpInstance;
		}

		CPDefinition cpDefinition = _cpDefinitionPersistence.findByPrimaryKey(
			cpDefinitionId);

		if (cpDefinition.isIgnoreSKUCombinations()) {
			_checkCPInstancesHelper.expireApprovedSiblingCPInstances(
				cpDefinition.getCPDefinitionId(), cpInstance.getCPInstanceId(),
				serviceContext);
		}
		else {
			if (!_cpInstanceOptionValueRelLocalService.
					hasCPInstanceOptionValueRel(cpInstanceId)) {

				cpInstance = _updateCPInstanceStatusHelper.updateStatus(
					user.getUserId(), cpInstance.getCPInstanceId(),
					WorkflowConstants.STATUS_INACTIVE);
			}

			_expireApprovedSiblingMatchingCPInstances(
				cpDefinitionId,
				cpDefinitionOptionRelIdCPDefinitionOptionValueRelIds,
				serviceContext);

			inactivateNoOptionSiblingCPInstances(
				cpDefinitionId, serviceContext);
		}

		// Workflow

		if (cpInstance.getStatus() == WorkflowConstants.STATUS_DRAFT) {
			cpInstance = WorkflowHandlerRegistryUtil.startWorkflowInstance(
				cpInstance.getCompanyId(), cpInstance.getGroupId(),
				user.getUserId(), CPInstance.class.getName(),
				cpInstance.getCPInstanceId(), cpInstance, serviceContext,
				new HashMap<>());
		}

		return cpInstance;
	}

	public void inactivateNoOptionSiblingCPInstances(
			long cpDefinitionId, ServiceContext serviceContext)
		throws PortalException {

		List<CPInstance> cpInstances = _cpInstancePersistence.findByC_ST(
			cpDefinitionId, WorkflowConstants.STATUS_APPROVED);

		for (CPInstance curCPInstance : cpInstances) {
			if (_cpInstanceOptionValueRelLocalService.
					hasCPInstanceOptionValueRel(
						curCPInstance.getCPInstanceId())) {

				continue;
			}

			_updateCPInstanceStatusHelper.updateStatus(
				serviceContext.getUserId(), curCPInstance.getCPInstanceId(),
				WorkflowConstants.STATUS_INACTIVE);
		}
	}

	public boolean isWorkflowActionPublish(ServiceContext serviceContext) {
		if (serviceContext.getWorkflowAction() ==
				WorkflowConstants.ACTION_PUBLISH) {

			return true;
		}

		return false;
	}

	private void _expireApprovedSiblingMatchingCPInstances(
			long cpDefinitionId,
			Map<Long, List<Long>>
				cpDefinitionOptionRelIdCPDefinitionOptionValueRelIds,
			ServiceContext serviceContext)
		throws PortalException {

		List<CPInstance> cpInstances = _cpInstancePersistence.findByC_ST(
			cpDefinitionId, WorkflowConstants.STATUS_APPROVED);

		for (CPInstance curCPInstance : cpInstances) {
			if (!_cpInstanceOptionValueRelLocalService.
					matchesCPInstanceOptionValueRels(
						curCPInstance.getCPInstanceId(),
						cpDefinitionOptionRelIdCPDefinitionOptionValueRelIds)) {

				continue;
			}

			_updateCPInstanceStatusHelper.updateStatus(
				serviceContext.getUserId(), curCPInstance.getCPInstanceId(),
				WorkflowConstants.STATUS_EXPIRED);
		}
	}

	@Reference
	private CheckCPInstancesHelper _checkCPInstancesHelper;

	@Reference
	private CounterLocalService _counterLocalService;

	@Reference
	private CPDefinitionIndexHelper _cpDefinitionIndexHelper;

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
	private PortalUUID _portalUUID;

	@Reference
	private UpdateCPInstanceStatusHelper _updateCPInstanceStatusHelper;

	@Reference
	private UserLocalService _userLocalService;

}