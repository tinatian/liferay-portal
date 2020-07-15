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

package com.liferay.portal.enterprise.app.gate.keeper.internal;

import com.liferay.petra.lang.HashUtil;

import java.util.Objects;

/**
 * @author Tina Tian
 */
public class BlockedBundleData {

	public BlockedBundleData(String location, int startLevel) {
		_location = location;
		_startLevel = startLevel;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof BlockedBundleData)) {
			return false;
		}

		BlockedBundleData blockedBundleData = (BlockedBundleData)object;

		if (Objects.equals(_location, blockedBundleData._location) &&
			(_startLevel == blockedBundleData._startLevel)) {

			return true;
		}

		return false;
	}

	public String getLocation() {
		return _location;
	}

	public int getStartLevel() {
		return _startLevel;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, _location);

		return HashUtil.hash(hashCode, _startLevel);
	}

	private final String _location;
	private final int _startLevel;

}