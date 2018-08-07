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

package com.liferay.portal.kernel.util;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.IOException;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author Lance Ji
 */
public class PropertiesEncoderUtil {

	public static final String SAFE_ENCODER_HOLDER = "_SAFE_ENCODER_UTIL";

	public static void fastLoad(Map<String, String> properties, String props) {
		if (Validator.isNull(props)) {
			return;
		}

		int x = props.indexOf(CharPool.NEW_LINE);
		int y = 0;

		while (x != -1) {
			_put(properties, props.substring(y, x));

			y = x;

			x = props.indexOf(CharPool.NEW_LINE, y + 1);
		}

		_put(properties, props.substring(y));
	}

	public static String getPropertiesString(Map<String, String> properties) {
		StringBundler sb = new StringBundler(4 * properties.size());

		Map<String, String> treeMap = new TreeMap<>(properties);

		for (Map.Entry<String, String> entry : treeMap.entrySet()) {
			String key = entry.getKey();

			if (SAFE_ENCODER_HOLDER.equals(key)) {
				continue;
			}

			String value = entry.getValue();

			if (value == null) {
				continue;
			}

			if (GetterUtil.getBoolean(properties.get(SAFE_ENCODER_HOLDER))) {
				value = _encode(value);
			}

			sb.append(key);
			sb.append(StringPool.EQUAL);
			sb.append(value);
			sb.append(StringPool.NEW_LINE);
		}

		return sb.toString();
	}

	public static void load(Map<String, String> properties, String props)
		throws IOException {

		if (Validator.isNull(props)) {
			return;
		}

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(props))) {

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				_put(properties, line);
			}
		}
	}

	private static String _decode(String value) {
		return StringUtil.replace(
			value, _SAFE_NEWLINE_CHARACTER, StringPool.NEW_LINE);
	}

	private static String _encode(String value) {
		String encodedValue = StringUtil.replace(
			value, StringPool.RETURN_NEW_LINE, _SAFE_NEWLINE_CHARACTER);

		return StringUtil.replace(
			encodedValue, new char[] {CharPool.NEW_LINE, CharPool.RETURN},
			new String[] {_SAFE_NEWLINE_CHARACTER, _SAFE_NEWLINE_CHARACTER});
	}

	private static boolean _isComment(String line) {
		if (line.isEmpty() || (line.charAt(0) == CharPool.POUND)) {
			return true;
		}

		return false;
	}

	private static void _put(Map<String, String> properties, String line) {
		line = line.trim();

		if (_isComment(line)) {
			return;
		}

		int pos = line.indexOf(CharPool.EQUAL);

		if (pos == -1) {
			_log.error("Invalid property on line " + line);
		}
		else {
			String value = StringUtil.trim(line.substring(pos + 1));

			if (GetterUtil.getBoolean(properties.get(SAFE_ENCODER_HOLDER))) {
				value = _decode(value);
			}

			properties.put(StringUtil.trim(line.substring(0, pos)), value);
		}
	}

	private static final String _SAFE_NEWLINE_CHARACTER =
		"_SAFE_NEWLINE_CHARACTER_";

	private static final Log _log = LogFactoryUtil.getLog(
		PropertiesEncoderUtil.class);

}