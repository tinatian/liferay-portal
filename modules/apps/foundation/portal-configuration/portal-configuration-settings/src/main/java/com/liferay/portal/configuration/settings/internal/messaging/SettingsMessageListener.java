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

package com.liferay.portal.configuration.settings.internal.messaging;

import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.settings.SettingsListener;

import java.util.Collection;
import java.util.HashSet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Tina Tian
 */
@Component(
	immediate = true,
	property = {"destination.name=" + DestinationNames.SETTINGS},
	service = MessageListener.class
)
public class SettingsMessageListener extends BaseMessageListener {

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		unbind = "unsetSettingsListener"
	)
	protected void addSettingsListener(SettingsListener settingsListener) {
		_settingsListeners.add(settingsListener);
	}

	@Override
	protected void doReceive(Message message) throws Exception {
		long companyId = message.getLong(SettingsListener.COMPANY_ID);

		for (SettingsListener settingsListener : _settingsListeners) {
			settingsListener.notifyUpdate(companyId);
		}
	}

	protected void unsetSettingsListener(SettingsListener settingsListener) {
		_settingsListeners.remove(settingsListener);
	}

	private final Collection<SettingsListener> _settingsListeners =
		new HashSet<>();

}