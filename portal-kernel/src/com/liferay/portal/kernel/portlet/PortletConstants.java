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

package com.liferay.portal.kernel.portlet;

import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.security.InvalidParameterException;

/**
 * @author Tina Tian
 */
public class PortletConstants {

	/**
	 * Default preferences.
	 */
	public static final String DEFAULT_PREFERENCES = "<portlet-preferences />";

	/**
	 * Facebook integration method for FBML.
	 *
	 * @deprecated As of 7.0.0, with no direct replacement
	 */
	@Deprecated
	public static final String FACEBOOK_INTEGRATION_FBML = "fbml";

	/**
	 * Facebook integration method for IFrame.
	 *
	 * @deprecated As of 7.0.0, with no direct replacement
	 */
	@Deprecated
	public static final String FACEBOOK_INTEGRATION_IFRAME = "iframe";

	/**
	 * Instance separator.
	 *
	 * @deprecated As of 7.0.0, with no direct replacement
	 */
	@Deprecated
	public static final String INSTANCE_SEPARATOR = "_INSTANCE_";

	/**
	 * Layout separator.
	 */
	public static final String LAYOUT_SEPARATOR = "_LAYOUT_";

	/**
	 * User principal strategy for screen name.
	 */
	public static final String USER_PRINCIPAL_STRATEGY_SCREEN_NAME =
		"screenName";

	/**
	 * User principal strategy for screen name.
	 */
	public static final String USER_PRINCIPAL_STRATEGY_USER_ID = "userId";

	/**
	 * User separator.
	 *
	 * @deprecated As of 7.0.0, with no direct replacement
	 */
	@Deprecated
	public static final String USER_SEPARATOR = "_USER_";

	/**
	 * War file separator.
	 */
	public static final String WAR_SEPARATOR = "_WAR_";

	/**
	 * Returns a properly assembled portlet ID from the parameters passed. If
	 * the portlet ID contains an instance ID it will be properly retained. If
	 * the portlet ID contains a user ID it will be replaced by the user ID
	 * parameter.
	 *
	 * @param  portletId the portlet ID
	 * @param  userId a user ID
	 * @return the properly assembled portlet ID
	 */
	public static String assemblePortletId(String portletId, long userId) {
		return assemblePortletId(portletId, userId, null);
	}

	/**
	 * Returns a properly assembled portlet ID from the parameters passed. If
	 * the portlet ID contains a user ID it will be replaced by the user ID
	 * parameter. If the portlet ID contains an instance ID it will be replaced
	 * by the instance ID parameter.
	 *
	 * @param  portletId the portlet ID
	 * @param  userId the user ID
	 * @param  instanceId an instance ID. If <code>null</code>, an instance ID
	 *         is derived from the portlet ID.
	 * @return the properly assembled portlet ID
	 */
	public static String assemblePortletId(
		String portletId, long userId, String instanceId) {

		int x = portletId.indexOf(_USER_SEPARATOR);
		int y = portletId.indexOf(_INSTANCE_SEPARATOR);

		String portletName = portletId;

		if (x != -1) {
			portletName = portletId.substring(0, x);

			if (userId == 0) {
				if (y != -1) {
					userId = GetterUtil.getLong(
						portletId.substring(x + _USER_SEPARATOR.length(), y));
				}
				else {
					userId = GetterUtil.getLong(
						portletId.substring(x + _USER_SEPARATOR.length()));
				}
			}
		}
		else if (y != -1) {
			portletName = portletId.substring(0, y);

			if (Validator.isNull(instanceId)) {
				instanceId = portletId.substring(
					y + _INSTANCE_SEPARATOR.length());
			}
		}

		StringBundler sb = new StringBundler(5);

		sb.append(portletName);

		if (userId > 0) {
			sb.append(_USER_SEPARATOR);
			sb.append(userId);
		}

		if (Validator.isNotNull(instanceId)) {
			sb.append(_INSTANCE_SEPARATOR);
			sb.append(instanceId);
		}

		return sb.toString();
	}

	/**
	 * Returns a properly assembled portlet ID from the parameters passed. If
	 * the portlet ID contains a user ID it will be properly retained. If the
	 * portlet ID contains an instance ID it will be replaced by the instance ID
	 * parameter.
	 *
	 * @param  portletId the portlet ID
	 * @param  instanceId an instance ID
	 * @return the properly assembled portlet ID
	 */
	public static String assemblePortletId(
		String portletId, String instanceId) {

		return assemblePortletId(portletId, 0, instanceId);
	}

	public static String assembleUserAndInstanceId(
		long userId, String instanceId) {

		if (userId > 0) {
			StringBundler sb = new StringBundler(3);

			sb.append(userId);
			sb.append(StringPool.UNDERLINE);

			if (Validator.isNotNull(instanceId)) {
				sb.append(instanceId);
			}

			return sb.toString();
		}

		if (Validator.isBlank(instanceId)) {
			return null;
		}

		return instanceId;
	}

	public static String generateInstanceId() {
		return StringUtil.randomString(12);
	}

	/**
	 * Returns the instance ID of the portlet.
	 *
	 * @param  portletId the portlet ID
	 * @return the instance ID of the portlet
	 */
	public static String getInstanceId(String portletId) {
		int index = portletId.indexOf(_INSTANCE_SEPARATOR);

		if (index == -1) {
			return null;
		}

		return portletId.substring(index + _INSTANCE_SEPARATOR.length());
	}

	public static String getInstanceIdFromUserIdAndInstanceId(
		String userIdAndInstanceId) {

		if (userIdAndInstanceId == null) {
			throw new InvalidParameterException("Instance ID are null");
		}

		if (userIdAndInstanceId.isEmpty()) {
			return null;
		}

		int underlineCount = StringUtil.count(
			userIdAndInstanceId, CharPool.UNDERLINE);

		if (underlineCount > 1) {
			throw new InvalidParameterException(
				"User ID and instance ID has more than one underscore");
		}

		if (underlineCount == 1) {
			int index = userIdAndInstanceId.indexOf(CharPool.UNDERLINE);

			String instanceId = null;

			if (index < (userIdAndInstanceId.length() - 1)) {
				instanceId = userIdAndInstanceId.substring(index + 1);

				int slashCount = StringUtil.count(instanceId, CharPool.SLASH);

				if (slashCount > 0) {
					throw new InvalidParameterException(
						"Instance ID contain slashes");
				}
			}

			return instanceId;
		}

		return userIdAndInstanceId;
	}

	/**
	 * Returns the root portlet ID of the portlet.
	 *
	 * @param  portletId the portlet ID
	 * @return the root portlet ID of the portlet
	 */
	public static String getRootPortletId(String portletId) {
		int x = portletId.indexOf(_USER_SEPARATOR);
		int y = portletId.indexOf(_INSTANCE_SEPARATOR);

		if ((x == -1) && (y == -1)) {
			return portletId;
		}
		else if (x != -1) {
			return portletId.substring(0, x);
		}

		return portletId.substring(0, y);
	}

	/**
	 * Returns the user ID of the portlet. This only applies when the portlet is
	 * added by a user to a page in customizable mode.
	 *
	 * @param  portletId the portlet ID
	 * @return the user ID of the portlet
	 */
	public static long getUserId(String portletId) {
		int x = portletId.indexOf(_USER_SEPARATOR);
		int y = portletId.indexOf(_INSTANCE_SEPARATOR);

		if (x == -1) {
			return 0;
		}

		if (y != -1) {
			return GetterUtil.getLong(
				portletId.substring(x + _USER_SEPARATOR.length(), y));
		}

		return GetterUtil.getLong(
			portletId.substring(x + _USER_SEPARATOR.length()));
	}

	public static long getUserIdFromUserIdAndInstanceId(
		String userIdAndInstanceId) {

		if (userIdAndInstanceId == null) {
			throw new InvalidParameterException("User ID is null");
		}

		if (userIdAndInstanceId.isEmpty()) {
			return 0;
		}

		int underlineCount = StringUtil.count(
			userIdAndInstanceId, CharPool.UNDERLINE);

		if (underlineCount > 1) {
			throw new InvalidParameterException(
				"User ID and instance ID has more than one underscore");
		}

		if (underlineCount == 1) {
			int index = userIdAndInstanceId.indexOf(CharPool.UNDERLINE);

			long userId = GetterUtil.getLong(
				userIdAndInstanceId.substring(0, index), -1);

			if (userId == -1) {
				throw new InvalidParameterException("User ID is not a number");
			}

			return userId;
		}

		return 0;
	}

	public static boolean hasIdenticalRootPortletId(
		String portletId1, String portletId2) {

		portletId1 = getRootPortletId(portletId1);
		portletId2 = getRootPortletId(portletId2);

		return portletId1.equals(portletId2);
	}

	/**
	 * Returns <code>true</code> if the portlet ID contains an instance ID.
	 *
	 * @param  portletId the portlet ID
	 * @return <code>true</code> if the portlet ID contains an instance ID;
	 *         <code>false</code> otherwise
	 */
	public static boolean hasInstanceId(String portletId) {
		if (portletId.indexOf(_INSTANCE_SEPARATOR) == -1) {
			return false;
		}

		return true;
	}

	/**
	 * Returns <code>true</code> if the portlet ID contains a user ID.
	 *
	 * @param  portletId the portlet ID
	 * @return <code>true</code> if the portlet ID contains a user ID;
	 *         <code>false</code> otherwise
	 */
	public static boolean hasUserId(String portletId) {
		if (portletId.indexOf(_USER_SEPARATOR) == -1) {
			return false;
		}

		return true;
	}

	private static final String _INSTANCE_SEPARATOR = "_INSTANCE_";

	private static final String _USER_SEPARATOR = "_USER_";

}