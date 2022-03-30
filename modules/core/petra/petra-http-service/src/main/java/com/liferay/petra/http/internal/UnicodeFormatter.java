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

package com.liferay.petra.http.internal;

/**
 * @author Janis Zhang
 */
public class UnicodeFormatter {

	public static char[] byteToHex(byte b, char[] hexes, boolean upperCase) {
		if (upperCase) {
			return _byteToHex(b, hexes, _HEX_DIGITS_UPPER_CASE);
		}

		return _byteToHex(b, hexes, _HEX_DIGITS);
	}

	private static char[] _byteToHex(byte b, char[] hexes, char[] table) {
		hexes[0] = table[(b >> 4) & 0x0f];
		hexes[1] = table[b & 0x0f];

		return hexes;
	}

	private static final char[] _HEX_DIGITS = {
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
		'e', 'f'
	};

	private static final char[] _HEX_DIGITS_UPPER_CASE = {
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
		'E', 'F'
	};

}