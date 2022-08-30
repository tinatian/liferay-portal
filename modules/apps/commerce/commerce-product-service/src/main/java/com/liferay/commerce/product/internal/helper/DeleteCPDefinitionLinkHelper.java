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

import com.liferay.commerce.product.model.CPDefinitionLink;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.service.persistence.CPDefinitionLinkPersistence;
import com.liferay.commerce.product.service.persistence.CProductPersistence;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.portal.kernel.exception.PortalException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lily Chi
 */
@Component(enabled = false, service = DeleteCPDefinitionLinkHelper.class)
public class DeleteCPDefinitionLinkHelper {

	public CPDefinitionLink deleteCPDefinitionLink(
			CPDefinitionLink cpDefinitionLink)
		throws PortalException {

		// Commerce product definition link

		_cpDefinitionLinkPersistence.remove(cpDefinitionLink);

		// Expando

		_expandoRowLocalService.deleteRows(
			cpDefinitionLink.getCPDefinitionLinkId());

		CProduct cProduct = _cProductPersistence.findByPrimaryKey(
			cpDefinitionLink.getCProductId());

		_cpDefinitionIndexHelper.reindexCPDefinition(
			cProduct.getPublishedCPDefinitionId());

		_cpDefinitionIndexHelper.reindexCPDefinition(
			cpDefinitionLink.getCPDefinitionId());

		return cpDefinitionLink;
	}

	@Reference
	private CPDefinitionIndexHelper _cpDefinitionIndexHelper;

	@Reference
	private CPDefinitionLinkPersistence _cpDefinitionLinkPersistence;

	@Reference
	private CProductPersistence _cProductPersistence;

	@Reference
	private ExpandoRowLocalService _expandoRowLocalService;

}