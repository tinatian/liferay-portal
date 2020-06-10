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

package com.liferay.portal.kernel.security.permission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author Dante Wang
 */
public class ResourceActionsBag {

	public ResourceActionsBag(
		Set<String> resourceActions, Set<String> groupDefaultActions,
		Set<String> guestDefaultActions, Set<String> guestUnsupportedActions,
		Set<String> layoutManagerActions, Set<String> ownerDefaultActions) {

		_supportsActions = Collections.unmodifiableList(
			new ArrayList<>(resourceActions));
		_groupDefaultActions = Collections.unmodifiableList(
			new ArrayList<>(groupDefaultActions));
		_guestDefaultActions = Collections.unmodifiableList(
			new ArrayList<>(guestDefaultActions));
		_guestUnsupportedActions = Collections.unmodifiableList(
			new ArrayList<>(guestUnsupportedActions));
		_layoutManagerActions = Collections.unmodifiableList(
			new ArrayList<>(layoutManagerActions));
		_ownerDefaultActions = Collections.unmodifiableList(
			new ArrayList<>(ownerDefaultActions));
	}

	public List<String> getGroupDefaultActions() {
		return _groupDefaultActions;
	}

	public List<String> getGuestDefaultActions() {
		return _guestDefaultActions;
	}

	public List<String> getGuestUnsupportedActions() {
		return _guestUnsupportedActions;
	}

	public List<String> getLayoutManagerActions() {
		return _layoutManagerActions;
	}

	public List<String> getOwnerDefaultActions() {
		return _ownerDefaultActions;
	}

	public List<String> getSupportsActions() {
		return _supportsActions;
	}

	private final List<String> _groupDefaultActions;
	private final List<String> _guestDefaultActions;
	private final List<String> _guestUnsupportedActions;
	private final List<String> _layoutManagerActions;
	private final List<String> _ownerDefaultActions;
	private final List<String> _supportsActions;

}