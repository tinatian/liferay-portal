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

package com.liferay.portal.cluster.multiple.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Lance Ji
 */
@ExtendedObjectClassDefinition(category = "foundation")
@Meta.OCD(
	id = "com.liferay.portal.cluster.configuration.ClusterLinkConfiguration",
	localization = "content/Language", name = "cluster-link-configuration-name"
)
public interface ClusterLinkConfiguration {

	@Meta.AD(required = false)
	public String channelLogicName0();

	@Meta.AD(required = false)
	public String channelName0();

	@Meta.AD(required = false)
	public String channelProperties0();

	@Meta.AD(required = false)
	public String channelLogicName1();

	@Meta.AD(required = false)
	public String channelName1();

	@Meta.AD(required = false)
	public String channelProperties1();

	@Meta.AD(required = false)
	public String channelLogicName2();

	@Meta.AD(required = false)
	public String channelName2();

	@Meta.AD(required = false)
	public String channelProperties2();

	@Meta.AD(required = false)
	public String channelLogicName3();

	@Meta.AD(required = false)
	public String channelName3();

	@Meta.AD(required = false)
	public String channelProperties3();

	@Meta.AD(required = false)
	public String channelLogicName4();

	@Meta.AD(required = false)
	public String channelName4();

	@Meta.AD(required = false)
	public String channelProperties4();

	@Meta.AD(required = false)
	public String channelLogicName5();

	@Meta.AD(required = false)
	public String channelName5();

	@Meta.AD(required = false)
	public String channelProperties5();

	@Meta.AD(required = false)
	public String channelLogicName6();

	@Meta.AD(required = false)
	public String channelName6();

	@Meta.AD(required = false)
	public String channelProperties6();

	@Meta.AD(required = false)
	public String channelLogicName7();

	@Meta.AD(required = false)
	public String channelName7();

	@Meta.AD(required = false)
	public String channelProperties7();

	@Meta.AD(required = false)
	public String channelLogicName8();

	@Meta.AD(required = false)
	public String channelName8();

	@Meta.AD(required = false)
	public String channelProperties8();

	@Meta.AD(required = false)
	public String channelLogicName9();

	@Meta.AD(required = false)
	public String channelName9();

	@Meta.AD(required = false)
	public String channelProperties9();

}