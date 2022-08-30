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
import com.liferay.commerce.product.service.persistence.CPInstanceOptionValueRelPersistence;
import com.liferay.commerce.product.service.persistence.CPInstancePersistence;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lily Chi
 */
@Component(enabled = false, service = DeleteCPInstanceHelper.class)
public class DeleteCPInstanceHelper {

	public CPInstance deleteCPInstance(CPInstance cpInstance)
		throws PortalException {

		// Commerce product instance

		_cpInstancePersistence.remove(cpInstance);

		_cpInstanceOptionValueRelPersistence.removeByCPInstanceId(
			cpInstance.getCPInstanceId());

		_cpInstanceCPDefinitionOptionValueRelHelper.
			resetCPInstanceCPDefinitionOptionValueRels(
				cpInstance.getCPInstanceUuid());

		// Expando

		_expandoRowLocalService.deleteRows(cpInstance.getCPInstanceId());

		// Workflow

		_workflowInstanceLinkLocalService.deleteWorkflowInstanceLinks(
			cpInstance.getCompanyId(), cpInstance.getGroupId(),
			CPInstance.class.getName(), cpInstance.getCPInstanceId());

		_cpDefinitionIndexHelper.reindexCPDefinition(
			cpInstance.getCPDefinitionId());

		return cpInstance;
	}

	@Reference
	private CPDefinitionIndexHelper _cpDefinitionIndexHelper;

	@Reference
	private CPInstanceCPDefinitionOptionValueRelHelper
		_cpInstanceCPDefinitionOptionValueRelHelper;

	@Reference
	private CPInstanceOptionValueRelPersistence
		_cpInstanceOptionValueRelPersistence;

	@Reference
	private CPInstancePersistence _cpInstancePersistence;

	@Reference
	private ExpandoRowLocalService _expandoRowLocalService;

	@Reference
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

}