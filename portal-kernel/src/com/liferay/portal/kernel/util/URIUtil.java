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

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Tina Tian
 */
public class URIUtil {

	public static String getHost(String uriString) throws URISyntaxException {
		if (Validator.isNull(uriString)) {
			return null;
		}

		int start = uriString.indexOf(StringPool.DOUBLE_SLASH);

		if (start >= 0) {
			start += 2;

			int end = -1;

			for (int i = start; i < uriString.length(); i++) {
				if (uriString.charAt(i) != CharPool.SLASH) {
					end = i;

					break;
				}
			}

			if (end != (start + 1)) {
				String prefix = uriString.substring(0, start);

				if (end == -1) {
					uriString = prefix;
				}
				else {
					String postfix = uriString.substring(end);

					uriString = prefix.concat(postfix);
				}
			}
		}

		URI uri = new URI(uriString);

		return uri.getHost();
	}

}