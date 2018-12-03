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

package com.liferay.portal.kernel.test;

import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Lance Ji
 */
public class PropsTestUtil {

	public static void set(String key, String value) {
		_props.set(key, value);
	}

	private static final PropsTestImpl _props;

	private static class PropsTestImpl implements Props {

		@Override
		public boolean contains(String key) {
			return false;
		}

		@Override
		public String get(String key) {
			return _results.get(key);
		}

		@Override
		public String get(String key, Filter filter) {
			return _results.get(key);
		}

		@Override
		public String[] getArray(String key) {
			return null;
		}

		@Override
		public String[] getArray(String key, Filter filter) {
			return null;
		}

		@Override
		public Properties getProperties() {
			return null;
		}

		@Override
		public Properties getProperties(String prefix, boolean removePrefix) {
			return null;
		}

		public void set(String key, String value) {
			_results.put(key, value);
		}

		private final Map<String, String> _results = new HashMap<>();

	}

	static {
		_props = new PropsTestImpl();

		PropsUtil.setProps(_props);
	}

}