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

package com.liferay.portal.store.db.test.activator;

import com.liferay.portal.store.test.util.BaseBundleActivator;

import java.util.Collections;
import java.util.Set;

/**
 * @author Manuel de la Peña
 */
public class DBStoreTestBundleActivator extends BaseBundleActivator {

	@Override
	protected Set<String> getComponentNames() {
		return Collections.singleton("com.liferay.portal.store.db.DBStore");
	}

	@Override
	protected String getTargetBundleName() {
		return "com.liferay.portal.store.db";
	}

}