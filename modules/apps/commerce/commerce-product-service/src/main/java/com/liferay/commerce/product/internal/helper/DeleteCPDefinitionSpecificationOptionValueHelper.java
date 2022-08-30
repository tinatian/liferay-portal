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

import com.liferay.commerce.product.model.CPDefinitionSpecificationOptionValue;
import com.liferay.commerce.product.service.persistence.CPDefinitionSpecificationOptionValuePersistence;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.portal.kernel.exception.PortalException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lily Chi
 */
@Component(
	enabled = false,
	service = DeleteCPDefinitionSpecificationOptionValueHelper.class
)
public class DeleteCPDefinitionSpecificationOptionValueHelper {

	public CPDefinitionSpecificationOptionValue
			deleteCPDefinitionSpecificationOptionValue(
				CPDefinitionSpecificationOptionValue
					cpDefinitionSpecificationOptionValue)
		throws PortalException {

		// Commerce product definition specification option value

		_cpDefinitionSpecificationOptionValuePersistence.remove(
			cpDefinitionSpecificationOptionValue);

		// Expando

		_expandoRowLocalService.deleteRows(
			cpDefinitionSpecificationOptionValue.
				getCPDefinitionSpecificationOptionValueId());

		_cpDefinitionIndexHelper.reindexCPDefinition(
			cpDefinitionSpecificationOptionValue.getCPDefinitionId());

		return cpDefinitionSpecificationOptionValue;
	}

	@Reference
	private CPDefinitionIndexHelper _cpDefinitionIndexHelper;

	@Reference
	private CPDefinitionSpecificationOptionValuePersistence
		_cpDefinitionSpecificationOptionValuePersistence;

	@Reference
	private ExpandoRowLocalService _expandoRowLocalService;

}