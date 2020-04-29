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

package com.liferay.portal.db.partition.internal.messaging;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.db.partition.DBPartitionUtil;
import com.liferay.portal.db.partition.internal.configuration.DBPartitionConfiguration;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationInterceptor;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alberto Chaparro
 */
@Component(
	configurationPid = "com.liferay.portal.db.partition.internal.configuration.DBPartitionConfiguration",
	immediate = true, service = DestinationInterceptor.class
)
public class DBPartitionDestinationInterceptor
	implements DestinationInterceptor {

	@Override
	public void send(Destination destination, Message message) {
		long companyId = message.getLong("companyId");

		String destinationName = destination.getName();

		if (!DBPartitionUtil.isDBPartitionEnabled() ||
			_excludedMessageBusDestinationNames.contains(destinationName) ||
			(companyId != CompanyConstants.SYSTEM)) {

			destination.send(message);

			return;
		}

		List<Company> companies = _companyLocalService.getCompanies(false);

		for (Company company : companies) {
			if (!company.isActive()) {
				continue;
			}

			if (_log.isInfoEnabled()) {
				_log.info(
					StringBundler.concat(
						"Executing ", destinationName, " for company ",
						company.getCompanyId()));
			}

			message.put("companyId", company.getCompanyId());

			destination.send(message);
		}
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		Set<String> excludedMessageBusDestinationNames = new HashSet<>();

		DBPartitionConfiguration dbPartitionConfiguration =
			ConfigurableUtil.createConfigurable(
				DBPartitionConfiguration.class, properties);

		Collections.addAll(
			excludedMessageBusDestinationNames,
			dbPartitionConfiguration.excludedMessageBusDestinationNames());

		_excludedMessageBusDestinationNames =
			excludedMessageBusDestinationNames;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DBPartitionDestinationInterceptor.class);

	@Reference
	private CompanyLocalService _companyLocalService;

	private volatile Set<String> _excludedMessageBusDestinationNames;

}