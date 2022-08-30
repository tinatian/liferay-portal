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

import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.service.persistence.CPDefinitionOptionRelPersistence;
import com.liferay.commerce.product.service.persistence.CPDefinitionOptionValueRelPersistence;
import com.liferay.commerce.product.util.JsonHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lily Chi
 */
@Component(
	enabled = false,
	service = CPDefinitionOptionRelCPDefinitionOptionValueRelHelper.class
)
public class CPDefinitionOptionRelCPDefinitionOptionValueRelHelper {

	public Map<Long, List<Long>>
			getCPDefinitionOptionRelCPDefinitionOptionValueRelIds(
				long cpDefinitionId, boolean skuContributorsOnly, String json)
		throws PortalException {

		if (_jsonHelper.isEmpty(json)) {
			return Collections.emptyMap();
		}

		Map<Long, List<Long>>
			cpDefinitionOptionRelIdCPDefinitionOptionValueRelIds =
				new HashMap<>();

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		if (_jsonHelper.isArray(json)) {
			jsonArray = _jsonFactory.createJSONArray(json);
		}
		else {
			jsonArray.put(_jsonFactory.createJSONObject(json));
		}

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			CPDefinitionOptionRel cpDefinitionOptionRel =
				_cpDefinitionOptionRelPersistence.fetchByC_K(
					cpDefinitionId, jsonObject.getString("key"));

			if ((cpDefinitionOptionRel == null) ||
				(skuContributorsOnly &&
				 !cpDefinitionOptionRel.isSkuContributor())) {

				continue;
			}

			JSONArray valueJSONArray = _jsonHelper.getValueAsJSONArray(
				"value", jsonObject);

			for (int j = 0; j < valueJSONArray.length(); j++) {
				CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
					_cpDefinitionOptionValueRelPersistence.fetchByC_K(
						cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
						valueJSONArray.getString(j));

				if (cpDefinitionOptionValueRel == null) {
					continue;
				}

				List<Long> cpDefinitionOptionValueRelIds =
					cpDefinitionOptionRelIdCPDefinitionOptionValueRelIds.get(
						cpDefinitionOptionRel.getCPDefinitionOptionRelId());

				if (cpDefinitionOptionValueRelIds == null) {
					cpDefinitionOptionValueRelIds = new ArrayList<>();

					cpDefinitionOptionRelIdCPDefinitionOptionValueRelIds.put(
						cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
						cpDefinitionOptionValueRelIds);
				}

				cpDefinitionOptionValueRelIds.add(
					cpDefinitionOptionValueRel.
						getCPDefinitionOptionValueRelId());
			}
		}

		return cpDefinitionOptionRelIdCPDefinitionOptionValueRelIds;
	}

	@Reference
	private CPDefinitionOptionRelPersistence _cpDefinitionOptionRelPersistence;

	@Reference
	private CPDefinitionOptionValueRelPersistence
		_cpDefinitionOptionValueRelPersistence;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private JsonHelper _jsonHelper;

}