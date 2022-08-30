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

import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.service.persistence.CPDefinitionOptionValueRelPersistence;

import java.math.BigDecimal;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lily Chi
 */
@Component(
	enabled = false, service = CPInstanceCPDefinitionOptionValueRelHelper.class
)
public class CPInstanceCPDefinitionOptionValueRelHelper {

	public void resetCPInstanceCPDefinitionOptionValueRels(
		String cpInstanceUuid) {

		List<CPDefinitionOptionValueRel> cpDefinitionOptionValueRels =
			_cpDefinitionOptionValueRelPersistence.findByCPInstanceUuid(
				cpInstanceUuid);

		for (CPDefinitionOptionValueRel cpDefinitionOptionValueRel :
				cpDefinitionOptionValueRels) {

			cpDefinitionOptionValueRel.setCPInstanceUuid(null);
			cpDefinitionOptionValueRel.setCProductId(0);
			cpDefinitionOptionValueRel.setQuantity(0);
			cpDefinitionOptionValueRel.setPrice(BigDecimal.ZERO);

			_cpDefinitionOptionValueRelPersistence.update(
				cpDefinitionOptionValueRel);
		}
	}

	@Reference
	private CPDefinitionOptionValueRelPersistence
		_cpDefinitionOptionValueRelPersistence;

}