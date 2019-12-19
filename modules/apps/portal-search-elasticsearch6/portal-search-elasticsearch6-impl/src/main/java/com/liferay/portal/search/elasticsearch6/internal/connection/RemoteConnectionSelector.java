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

package com.liferay.portal.search.elasticsearch6.internal.connection;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.search.elasticsearch6.configuration.ElasticsearchConfiguration;

import java.util.Arrays;
import java.util.Objects;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Tina Tian
 */
@Component(
	configurationPid = "com.liferay.portal.search.elasticsearch6.configuration.ElasticsearchConfiguration",
	immediate = true, service = RemoteConnectionSelector.class
)
public class RemoteConnectionSelector {

	public ElasticsearchConfiguration getElasticsearchConfiguration() {
		return _elasticsearchConfiguration;
	}

	@Activate
	protected void activate(ComponentContext componentContext) {
		_elasticsearchConfiguration = ConfigurableUtil.createConfigurable(
			ElasticsearchConfiguration.class, componentContext.getProperties());

		if (Arrays.equals(
				_elasticsearchConfiguration.transportAddresses(),
				new String[] {"localhost:9300"}) &&
			Objects.equals(
				_elasticsearchConfiguration.clusterName(),
				"LiferayElasticsearchCluster")) {

			_componentName = SidecarElasticsearchConnection.class.getName();
		}
		else {
			_componentName = RemoteElasticsearchConnection.class.getName();
		}

		componentContext.enableComponent(_componentName);
	}

	@Deactivate
	protected void deactivate(ComponentContext componentContext) {
		componentContext.disableComponent(_componentName);
	}

	private String _componentName;
	private ElasticsearchConfiguration _elasticsearchConfiguration;

}