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

package com.liferay.petra.http;

import com.liferay.petra.http.internal.ArrayUtil;
import com.liferay.petra.http.internal.PortalUtil;
import com.liferay.petra.http.internal.URLCodec;
import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Janis Zhang
 */
public class HttpImpl {

	public static final int URL_MAXIMUM_LENGTH = 2083;

	public String addParameter(String url, String name, String value) {
		if (url == null) {
			return null;
		}

		String[] urlArray = PortalUtil.stripURLAnchor(url, StringPool.POUND);

		url = urlArray[0];

		String anchor = urlArray[1];

		StringBundler sb = new StringBundler(6);

		sb.append(url);

		if (url.indexOf(CharPool.QUESTION) == -1) {
			sb.append(StringPool.QUESTION);
		}
		else if (!url.endsWith(StringPool.QUESTION) &&
				 !url.endsWith(StringPool.AMPERSAND)) {

			sb.append(StringPool.AMPERSAND);
		}

		sb.append(name);
		sb.append(StringPool.EQUAL);
		sb.append(URLCodec.encodeURL(value));
		sb.append(anchor);

		return shortenURL(sb.toString());
	}

	public String decodePath(String path) {
		return decodeURL(path);
	}

	public String decodeURL(String url) {
		if ((url == null) || url.isEmpty()) {
			return url;
		}

		try {
			return URLCodec.decodeURL(url, StringPool.UTF8);
		}
		catch (IllegalArgumentException illegalArgumentException) {
			_logger.log(
				Level.WARNING, illegalArgumentException.getMessage(),
				illegalArgumentException);
		}

		return StringPool.BLANK;
	}

	public String getParameter(String url, String name) {
		return getParameter(url, name, true);
	}

	public String getParameter(String url, String name, boolean escaped) {
		if ((url == null) || url.isEmpty() || (name == null) ||
			name.isEmpty()) {

			return StringPool.BLANK;
		}

		List<String> partsList = StringUtil.split(url, CharPool.QUESTION);

		String[] parts = partsList.toArray(new String[0]);

		if (parts.length == 2) {
			List<String> params = null;

			if (escaped) {
				params = StringUtil.split(parts[1], "&amp;");
			}
			else {
				params = StringUtil.split(parts[1], CharPool.AMPERSAND);
			}

			for (String param : params.toArray(new String[0])) {
				List<String> kvps = StringUtil.split(param, CharPool.EQUAL);

				String[] kvp = kvps.toArray(new String[0]);

				if ((kvp.length == 2) && kvp[0].equals(name)) {
					return kvp[1];
				}
			}
		}

		return StringPool.BLANK;
	}

	public Map<String, String[]> getParameterMap(String queryString) {
		return parameterMapFromString(queryString);
	}

	public String getQueryString(String url) {
		if ((url == null) || url.isEmpty()) {
			return url;
		}

		URI uri = getURI(url);

		if (uri == null) {
			return StringPool.BLANK;
		}

		String queryString = uri.getRawQuery();

		if (queryString == null) {
			return StringPool.BLANK;
		}

		return queryString;
	}

	public URI getURI(String uriString) {
		try {
			return getURIImpl(uriString);
		}
		catch (URISyntaxException uriSyntaxException) {
			_logger.log(
				Level.CONFIG, uriSyntaxException.getMessage(),
				uriSyntaxException);

			return null;
		}
	}

	public URI getURIImpl(String uriString) throws URISyntaxException {
		Map<String, URI> uris = _uris.get();

		uriString = uriString.trim();

		URI uri = uris.get(uriString);

		if (uri == null) {
			uri = new URI(uriString);

			uris.put(uriString, uri);
		}

		return uri;
	}

	public Map<String, String[]> parameterMapFromString(String queryString) {
		Map<String, String[]> parameterMap = new LinkedHashMap<>();

		if ((queryString == null) || queryString.isEmpty()) {
			return parameterMap;
		}

		List<String> parameters = StringUtil.split(
			queryString, CharPool.AMPERSAND);

		for (String parameter : parameters.toArray(new String[0])) {
			if (parameter.length() == 0) {
				continue;
			}

			List<String> kvps = StringUtil.split(parameter, CharPool.EQUAL);

			String[] kvp = kvps.toArray(new String[0]);

			if (kvp.length == 0) {
				continue;
			}

			String key = kvp[0];

			String value = StringPool.BLANK;

			if (kvp.length > 1) {
				try {
					value = decodeURL(kvp[1]);
				}
				catch (IllegalArgumentException illegalArgumentException) {
					_logger.log(
						Level.INFO,
						StringBundler.concat(
							"Skipping parameter with key ", key,
							" because of invalid value ", kvp[1]),
						illegalArgumentException);

					continue;
				}
			}

			String[] values = parameterMap.get(key);

			if (values == null) {
				parameterMap.put(key, new String[] {value});
			}
			else {
				parameterMap.put(key, ArrayUtil.append(values, value));
			}
		}

		return parameterMap;
	}

	public String removeParameter(String url, String name) {
		if ((url == null) || url.isEmpty() || (name == null) ||
			name.isEmpty()) {

			return url;
		}

		int pos = url.indexOf(CharPool.QUESTION);

		if (pos == -1) {
			return url;
		}

		String[] array = PortalUtil.stripURLAnchor(url, StringPool.POUND);

		url = array[0];

		String anchor = array[1];

		StringBundler sb = new StringBundler();

		sb.append(url.substring(0, pos + 1));

		List<String> parameters = StringUtil.split(
			url.substring(pos + 1), CharPool.AMPERSAND);

		for (String parameter : parameters.toArray(new String[0])) {
			if (parameter.length() > 0) {
				List<String> kvps = StringUtil.split(parameter, CharPool.EQUAL);

				String[] kvp = kvps.toArray(new String[0]);

				String key = kvp[0];

				String value = StringPool.BLANK;

				if (kvp.length > 1) {
					value = kvp[1];
				}

				if (!key.equals(name)) {
					sb.append(key);
					sb.append(StringPool.EQUAL);
					sb.append(value);
					sb.append(StringPool.AMPERSAND);
				}
			}
		}

		url = StringUtil.replace(
			sb.toString(), StringPool.AMPERSAND + StringPool.AMPERSAND,
			StringPool.AMPERSAND);

		if (url.endsWith(StringPool.AMPERSAND)) {
			url = url.substring(0, url.length() - 1);
		}

		if (url.endsWith(StringPool.QUESTION)) {
			url = url.substring(0, url.length() - 1);
		}

		return url + anchor;
	}

	public String setParameter(String url, String name, String value) {
		if ((url == null) || url.isEmpty() || (name == null) ||
			name.isEmpty()) {

			return url;
		}

		url = removeParameter(url, name);

		return addParameter(url, name, value);
	}

	public String shortenURL(String url) {
		if (url.length() <= URL_MAXIMUM_LENGTH) {
			return url;
		}

		return shortenURL(
			url, 0, StringPool.QUESTION, StringPool.AMPERSAND,
			StringPool.EQUAL);
	}

	public String shortenURL(
		String encodedURL, int currentLength, String encodedQuestion,
		String encodedAmpersand, String encodedEqual) {

		if ((currentLength + encodedURL.length()) <= URL_MAXIMUM_LENGTH) {
			return encodedURL;
		}

		int index = encodedURL.indexOf(encodedQuestion);

		if (index == -1) {
			return encodedURL;
		}

		StringBundler sb = new StringBundler();

		sb.append(encodedURL.substring(0, index));
		sb.append(encodedQuestion);

		String queryString = encodedURL.substring(
			index + encodedQuestion.length());

		List<String> paramsList = StringUtil.split(
			queryString, encodedAmpersand);

		String[] params = paramsList.toArray(new String[0]);

		params = ArrayUtil.unique(params);

		List<String> encodedRedirectParams = new ArrayList<>();

		for (String param : params) {
			if (param.contains("_backURL" + encodedEqual) ||
				param.contains("_redirect" + encodedEqual) ||
				param.contains("_returnToFullPageURL" + encodedEqual) ||
				(param.startsWith("redirect") &&
				 (param.indexOf(encodedEqual) != -1))) {

				encodedRedirectParams.add(param);
			}
			else {
				sb.append(param);
				sb.append(encodedAmpersand);
			}
		}

		if ((currentLength + sb.length()) > URL_MAXIMUM_LENGTH) {
			sb.setIndex(sb.index() - 1);

			return sb.toString();
		}

		for (String encodedRedirectParam : encodedRedirectParams) {
			int pos = encodedRedirectParam.indexOf(encodedEqual);

			String key = encodedRedirectParam.substring(0, pos);

			String redirect = encodedRedirectParam.substring(
				pos + encodedEqual.length());

			sb.append(key);
			sb.append(encodedEqual);

			int newLength = sb.length();

			redirect = shortenURL(
				redirect, currentLength + newLength,
				URLCodec.encodeURL(encodedQuestion),
				URLCodec.encodeURL(encodedAmpersand),
				URLCodec.encodeURL(encodedEqual));

			newLength += redirect.length();

			if ((currentLength + newLength) > URL_MAXIMUM_LENGTH) {
				sb.setIndex(sb.index() - 2);
			}
			else {
				sb.append(redirect);
				sb.append(encodedAmpersand);
			}
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	private static final Logger _logger = Logger.getLogger(
		HttpImpl.class.getName());

	private static final ThreadLocal<Map<String, URI>> _uris =
		new CentralizedThreadLocal<>(HttpImpl.class + "._uris", HashMap::new);

}