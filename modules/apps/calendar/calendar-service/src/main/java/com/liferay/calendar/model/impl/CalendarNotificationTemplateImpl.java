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

package com.liferay.calendar.model.impl;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PropertiesEncoderUtil;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Adam Brandizzi
 */
public class CalendarNotificationTemplateImpl
	extends CalendarNotificationTemplateBaseImpl {

	@Override
	public String getNotificationTypeSettings() {
		if (_notificationTypeSettingsProperties == null) {
			return super.getNotificationTypeSettings();
		}
		else {
			return _notificationTypeSettingsProperties.toString();
		}
	}

	@Override
	public Map<String, String> getNotificationTypeSettingsProperties() {
		if (_notificationTypeSettingsProperties == null) {
			_notificationTypeSettingsProperties = new HashMap<>();

			_notificationTypeSettingsProperties.put(
				PropertiesEncoderUtil.SAFE_ENCODER_HOLDER, StringPool.TRUE);

			try {
				PropertiesEncoderUtil.load(
					_notificationTypeSettingsProperties,
					super.getNotificationTypeSettings());
			}
			catch (IOException ioe) {
				_log.error(ioe, ioe);
			}
		}

		return _notificationTypeSettingsProperties;
	}

	@Override
	public void setNotificationTypeSettings(String notificationTypeSettings) {
		_notificationTypeSettingsProperties = null;

		super.setNotificationTypeSettings(notificationTypeSettings);
	}

	@Override
	public void setTypeSettingsProperties(
		Map<String, String> notificationTypeSettingsProperties) {

		_notificationTypeSettingsProperties =
			notificationTypeSettingsProperties;

		super.setNotificationTypeSettings(
			PropertiesEncoderUtil.getPropertiesString(
				_notificationTypeSettingsProperties));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CalendarNotificationTemplateImpl.class);

	private Map<String, String> _notificationTypeSettingsProperties;

}