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

package com.liferay.portal.dao.orm.custom.sql.internal;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.dao.orm.custom.sql.CustomSQLPool;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.UnsecureSAXReaderUtil;

import java.io.IOException;
import java.io.InputStream;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lance Ji
 */
@Component
public class CustomSQLPoolImpl implements CustomSQLPool {

	@Override
	public void addSQLSource(Bundle bundle) {
		BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

		ClassLoader classLoader = bundleWiring.getClassLoader();

		_sqlSourcePool.put(bundle, classLoader);
	}

	@Override
	public String get(Bundle bundle, String id) {
		Map<String, String> bundlSQL = _sqlPool.get(bundle);

		if (bundlSQL == null) {
			bundlSQL = _loadCustomSQL(bundle);
		}

		return bundlSQL.get(id);
	}

	@Override
	public Map<String, String> removeBundleSQL(Bundle bundle) {
		return _sqlPool.remove(bundle);
	}

	protected String transform(String sql) {
		sql = _portal.transformCustomSQL(sql);

		StringBundler sb = new StringBundler();

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(sql))) {

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				line = line.trim();

				if (line.startsWith(StringPool.CLOSE_PARENTHESIS)) {
					sb.setIndex(sb.index() - 1);
				}

				sb.append(line);

				if (!line.endsWith(StringPool.OPEN_PARENTHESIS)) {
					sb.append(StringPool.SPACE);
				}
			}
		}
		catch (IOException ioe) {
			return sql;
		}

		return sb.toString();
	}

	private Map<String, String> _loadCustomSQL(Bundle bundle) {
		Map<String, String> sqls = new HashMap<>();

		try {
			ClassLoader classLoader = _sqlSourcePool.get(bundle);

			_read(classLoader, "custom-sql/default.xml", sqls);
			_read(classLoader, "META-INF/custom-sql/default.xml", sqls);

			_sqlPool.put(bundle, sqls);
		}
		catch (Exception e) {
			_log.error(e, e);
		}

		return sqls;
	}

	private void _read(
			ClassLoader classLoader, String source, Map<String, String> sqls)
		throws Exception {

		try (InputStream is = classLoader.getResourceAsStream(source)) {
			if (is == null) {
				return;
			}

			if (_log.isDebugEnabled()) {
				_log.debug("Loading " + source);
			}

			Document document = UnsecureSAXReaderUtil.read(is);

			Element rootElement = document.getRootElement();

			for (Element sqlElement : rootElement.elements("sql")) {
				String file = sqlElement.attributeValue("file");

				if (Validator.isNotNull(file)) {
					_read(classLoader, file, sqls);
				}
				else {
					String id = sqlElement.attributeValue("id");
					String content = transform(sqlElement.getText());

					content = _replaceIsNull(content);

					sqls.put(id, content);
				}
			}
		}
	}

	private String _replaceIsNull(String sql) {
		String functionIsNull = _portal.getCustomSQLFunctionIsNull();
		String functionIsNotNull = _portal.getCustomSQLFunctionIsNotNull();

		if (Validator.isNotNull(functionIsNull)) {
			sql = StringUtil.replace(
				sql, new String[] {"? IS NULL", "? IS NOT NULL"},
				new String[] {functionIsNull, functionIsNotNull});
		}

		return sql;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CustomSQLPoolImpl.class);

	@Reference
	private Portal _portal;

	private final Map<Bundle, Map<String, String>> _sqlPool =
		new ConcurrentHashMap<>();
	private final Map<Bundle, ClassLoader> _sqlSourcePool =
		new ConcurrentHashMap<>();

}