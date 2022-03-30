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

package com.liferay.petra.string;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.RandomAccess;
import java.util.function.Function;

/**
 * The String utility class.
 *
 * @author Brian Wing Shun Chan
 * @author Sandeep Soni
 * @author Ganesh Ram
 * @author Shuyang Zhou
 * @author Hugo Huijser
 */
public class StringUtil {

	public static String merge(boolean[] array, String delimiter) {
		if (array == null) {
			return null;
		}

		if (array.length == 0) {
			return StringPool.BLANK;
		}

		if (array.length == 1) {
			return String.valueOf(array[0]);
		}

		StringBundler sb = new StringBundler(2 * array.length);

		for (int i = 0; i < array.length; i++) {
			sb.append(array[i]);
			sb.append(delimiter);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	public static String merge(byte[] array, String delimiter) {
		if (array == null) {
			return null;
		}

		if (array.length == 0) {
			return StringPool.BLANK;
		}

		if (array.length == 1) {
			return String.valueOf(array[0]);
		}

		StringBundler sb = new StringBundler(2 * array.length);

		for (int i = 0; i < array.length; i++) {
			sb.append(array[i]);
			sb.append(delimiter);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	public static String merge(char[] array, String delimiter) {
		if (array == null) {
			return null;
		}

		if (array.length == 0) {
			return StringPool.BLANK;
		}

		if (array.length == 1) {
			return String.valueOf(array[0]);
		}

		StringBundler sb = new StringBundler(2 * array.length);

		for (int i = 0; i < array.length; i++) {
			sb.append(array[i]);
			sb.append(delimiter);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	public static <T> String merge(
		Collection<T> col, Function<T, String> toStringFunction,
		String delimiter) {

		if (col instanceof List && col instanceof RandomAccess) {
			return merge(
				(List<T> & RandomAccess)col, toStringFunction, delimiter);
		}

		if (col == null) {
			return null;
		}

		if (col.isEmpty()) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(2 * col.size());

		for (T t : col) {
			sb.append(toStringFunction.apply(t));
			sb.append(delimiter);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	public static <T> String merge(Collection<T> col, String delimiter) {
		return merge(col, String::valueOf, delimiter);
	}

	public static String merge(double[] array, String delimiter) {
		if (array == null) {
			return null;
		}

		if (array.length == 0) {
			return StringPool.BLANK;
		}

		if (array.length == 1) {
			return String.valueOf(array[0]);
		}

		StringBundler sb = new StringBundler(2 * array.length);

		for (int i = 0; i < array.length; i++) {
			sb.append(array[i]);
			sb.append(delimiter);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	public static String merge(float[] array, String delimiter) {
		if (array == null) {
			return null;
		}

		if (array.length == 0) {
			return StringPool.BLANK;
		}

		if (array.length == 1) {
			return String.valueOf(array[0]);
		}

		StringBundler sb = new StringBundler(2 * array.length);

		for (int i = 0; i < array.length; i++) {
			sb.append(array[i]);
			sb.append(delimiter);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	public static String merge(int[] array, String delimiter) {
		if (array == null) {
			return null;
		}

		if (array.length == 0) {
			return StringPool.BLANK;
		}

		if (array.length == 1) {
			return String.valueOf(array[0]);
		}

		StringBundler sb = new StringBundler(2 * array.length);

		for (int i = 0; i < array.length; i++) {
			sb.append(array[i]);
			sb.append(delimiter);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	public static <T, L extends List<T> & RandomAccess> String merge(
		L list, Function<T, String> toStringFunction, String delimiter) {

		if (list == null) {
			return null;
		}

		if (list.isEmpty()) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(2 * list.size());

		for (int i = 0; i < list.size(); i++) {
			sb.append(toStringFunction.apply(list.get(i)));
			sb.append(delimiter);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	public static <T, L extends List<T> & RandomAccess> String merge(
		L list, String delimiter) {

		return merge(list, String::valueOf, delimiter);
	}

	public static String merge(long[] array, String delimiter) {
		if (array == null) {
			return null;
		}

		if (array.length == 0) {
			return StringPool.BLANK;
		}

		if (array.length == 1) {
			return String.valueOf(array[0]);
		}

		StringBundler sb = new StringBundler(2 * array.length);

		for (int i = 0; i < array.length; i++) {
			sb.append(array[i]);
			sb.append(delimiter);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	public static String merge(short[] array, String delimiter) {
		if (array == null) {
			return null;
		}

		if (array.length == 0) {
			return StringPool.BLANK;
		}

		if (array.length == 1) {
			return String.valueOf(array[0]);
		}

		StringBundler sb = new StringBundler(2 * array.length);

		for (int i = 0; i < array.length; i++) {
			sb.append(array[i]);
			sb.append(delimiter);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	public static String merge(String[] array, String delimiter) {
		if (array == null) {
			return null;
		}

		if (array.length == 0) {
			return StringPool.BLANK;
		}

		if (array.length == 1) {
			return array[0];
		}

		StringBundler sb = new StringBundler(2 * array.length);

		for (int i = 0; i < array.length; i++) {
			sb.append(array[i]);
			sb.append(delimiter);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	public static <T> String merge(
		T[] array, Function<T, String> toStringFunction, String delimiter) {

		if (array == null) {
			return null;
		}

		if (array.length == 0) {
			return StringPool.BLANK;
		}

		if (array.length == 1) {
			return toStringFunction.apply(array[0]);
		}

		StringBundler sb = new StringBundler(2 * array.length);

		for (int i = 0; i < array.length; i++) {
			sb.append(toStringFunction.apply(array[i]));
			sb.append(delimiter);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	public static <T> String merge(T[] array, String delimiter) {
		return merge(array, String::valueOf, delimiter);
	}

	public static String read(ClassLoader classLoader, String name)
		throws IOException {

		return read(classLoader, name, false);
	}

	public static String read(ClassLoader classLoader, String name, boolean all)
		throws IOException {

		if (all) {
			StringBundler sb = new StringBundler();

			Enumeration<URL> enumeration = classLoader.getResources(name);

			while (enumeration.hasMoreElements()) {
				URL url = enumeration.nextElement();

				try (InputStream inputStream = url.openStream()) {
					String s = read(inputStream);

					if (s != null) {
						sb.append(s);
						sb.append(StringPool.NEW_LINE);
					}
				}
			}

			String s = sb.toString();

			return s.trim();
		}

		try (InputStream inputStream = classLoader.getResourceAsStream(name)) {
			if (inputStream == null) {
				throw new IOException(
					StringBundler.concat(
						"Unable to open resource ", name, " in class loader ",
						classLoader));
			}

			return read(inputStream);
		}
	}

	public static String read(InputStream inputStream) throws IOException {
		String s = _read(inputStream);

		s = replace(s, "\r\n", StringPool.NEW_LINE);

		s = replace(s, CharPool.RETURN, CharPool.NEW_LINE);

		return s.trim();
	}

	/**
	 * Replaces all occurrences of the character with the new character.
	 *
	 * @param s      the original string
	 * @param oldSub the character to be searched for and replaced in the
	 *               original string
	 * @param newSub the character with which to replace the
	 *               <code>oldSub</code> character
	 * @return a string representing the original string with all occurrences of
	 * the <code>oldSub</code> character replaced with the
	 * <code>newSub</code> character, or <code>null</code> if the
	 * original string is <code>null</code>
	 */
	public static String replace(String s, char oldSub, char newSub) {
		if (s == null) {
			return null;
		}

		return s.replace(oldSub, newSub);
	}

	public static String replace(String s, char[] oldSubs, String[] newSubs) {
		if ((s == null) || (oldSubs == null) || (newSubs == null)) {
			return null;
		}

		if (oldSubs.length != newSubs.length) {
			return s;
		}

		StringBundler sb = null;

		int lastReplacementIndex = 0;

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			for (int j = 0; j < oldSubs.length; j++) {
				if (c == oldSubs[j]) {
					if (sb == null) {
						sb = new StringBundler();
					}

					if (i > lastReplacementIndex) {
						sb.append(s.substring(lastReplacementIndex, i));
					}

					sb.append(newSubs[j]);

					lastReplacementIndex = i + 1;

					break;
				}
			}
		}

		if (sb == null) {
			return s;
		}

		if (lastReplacementIndex < s.length()) {
			sb.append(s.substring(lastReplacementIndex));
		}

		return sb.toString();
	}

	/**
	 * Replaces all occurrences of the string with the new string.
	 *
	 * @param s      the original string
	 * @param oldSub the string to be searched for and replaced in the original
	 *               string
	 * @param newSub the string with which to replace the <code>oldSub</code>
	 *               string
	 * @return a string representing the original string with all occurrences of
	 * the <code>oldSub</code> string replaced with the string
	 * <code>newSub</code>, or <code>null</code> if the original string
	 * is <code>null</code>
	 */
	public static String replace(String s, String oldSub, String newSub) {
		return replace(s, oldSub, newSub, 0);
	}

	/**
	 * Replaces all occurrences of the string with the new string, starting from
	 * the specified index.
	 *
	 * @param s         the original string
	 * @param oldSub    the string to be searched for and replaced in the original
	 *                  string
	 * @param newSub    the string with which to replace the <code>oldSub</code>
	 *                  string
	 * @param fromIndex the index of the original string from which to begin
	 *                  searching
	 * @return a string representing the original string with all occurrences of
	 * the <code>oldSub</code> string occurring after the specified
	 * index replaced with the string <code>newSub</code>, or
	 * <code>null</code> if the original string is <code>null</code>
	 */
	public static String replace(
		String s, String oldSub, String newSub, int fromIndex) {

		if (s == null) {
			return null;
		}

		if ((oldSub == null) || oldSub.equals(StringPool.BLANK)) {
			return s;
		}

		if (newSub == null) {
			newSub = StringPool.BLANK;
		}

		int y = s.indexOf(oldSub, fromIndex);

		if (y >= 0) {
			StringBundler sb = new StringBundler();

			int length = oldSub.length();
			int x = 0;

			while (x <= y) {
				sb.append(s.substring(x, y));
				sb.append(newSub);

				x = y + length;

				y = s.indexOf(oldSub, x);
			}

			sb.append(s.substring(x));

			return sb.toString();
		}

		return s;
	}

	public static String replace(String s, String[] oldSubs, String[] newSubs) {
		if ((s == null) || (oldSubs == null) || (newSubs == null)) {
			return null;
		}

		if (oldSubs.length != newSubs.length) {
			return s;
		}

		for (int i = 0; i < oldSubs.length; i++) {
			s = replace(s, oldSubs[i], newSubs[i]);
		}

		return s;
	}

	public static List<String> split(String s) {
		return split(s, CharPool.COMMA);
	}

	public static List<String> split(String s, char delimiter) {
		if ((s == null) || s.isEmpty()) {
			return Collections.emptyList();
		}

		s = s.trim();

		if (s.isEmpty()) {
			return Collections.emptyList();
		}

		List<String> elements = new ArrayList<>();

		_split(elements, s, delimiter);

		return elements;
	}

	/**
	 * Splits the string <code>s</code> around the specified delimiter string.
	 *
	 * <p>
	 * Example:
	 * </p>
	 *
	 * <p>
	 * <pre>
	 * <code>
	 * splitLines("oneandtwoandthreeandfour", "and") returns {"one","two","three","four"}
	 * </code>
	 * </pre></p>
	 *
	 * @param s         the string to split
	 * @param delimiter the delimiter
	 * @return the array of strings resulting from splitting string
	 * <code>s</code> around the specified delimiter string, or an empty
	 * string array if <code>s</code> is <code>null</code> or equals the
	 * delimiter
	 */
	public static List<String> split(String s, String delimiter) {
		if ((s == null) || s.isEmpty() || (delimiter == null) ||
			delimiter.equals(StringPool.BLANK)) {

			return Collections.emptyList();
		}

		s = s.trim();

		if (s.equals(delimiter)) {
			return Collections.emptyList();
		}

		if (delimiter.length() == 1) {
			return split(s, delimiter.charAt(0));
		}

		List<String> nodeValues = new ArrayList<>();

		int offset = 0;

		int pos = s.indexOf(delimiter, offset);

		while (pos != -1) {
			nodeValues.add(s.substring(offset, pos));

			offset = pos + delimiter.length();

			pos = s.indexOf(delimiter, offset);
		}

		if (offset < s.length()) {
			nodeValues.add(s.substring(offset));
		}

		return nodeValues;
	}

	private static String _read(InputStream inputStream) throws IOException {
		byte[] buffer = new byte[8192];
		int offset = 0;

		while (true) {
			int count = inputStream.read(
				buffer, offset, buffer.length - offset);

			if (count == -1) {
				break;
			}

			offset += count;

			if (offset == buffer.length) {
				byte[] newBuffer = new byte[buffer.length << 1];

				System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);

				buffer = newBuffer;
			}
		}

		if (offset == 0) {
			return StringPool.BLANK;
		}

		return new String(buffer, 0, offset, StringPool.UTF8);
	}

	private static void _split(List<String> values, String s, char delimiter) {
		int offset = 0;
		int pos;

		while ((pos = s.indexOf(delimiter, offset)) != -1) {
			if (offset < pos) {
				values.add(s.substring(offset, pos));
			}

			offset = pos + 1;
		}

		if (offset < s.length()) {
			values.add(s.substring(offset));
		}
	}

}