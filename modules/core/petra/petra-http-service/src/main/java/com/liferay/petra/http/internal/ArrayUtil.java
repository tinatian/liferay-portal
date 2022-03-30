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

import java.lang.reflect.Array;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Janis Zhang
 */
public class ArrayUtil {

	public static <T> T[] append(T[] array, T value) {
		Class<?> arrayClass = array.getClass();

		T[] newArray = (T[])Array.newInstance(
			arrayClass.getComponentType(), array.length + 1);

		System.arraycopy(array, 0, newArray, 0, array.length);

		newArray[array.length] = value;

		return newArray;
	}

	public static String[] unique(String[] array) {
		Set<String> set = new LinkedHashSet<>();

		for (String s : array) {
			set.add(s);
		}

		if (array.length == set.size()) {
			return array;
		}

		return set.toArray(new String[0]);
	}

}