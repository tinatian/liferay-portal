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

package com.liferay.portal.template.xsl.internal;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateException;
import com.liferay.portal.kernel.template.TemplateResource;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.template.TemplateContextHelper;
import com.liferay.portal.template.xsl.configuration.XSLEngineConfiguration;
import com.liferay.portal.xsl.XSLTemplateResource;
import com.liferay.portal.xsl.XSLURIResolver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import javax.xml.XMLConstants;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * @author Tina Tian
 * @author Peter Fellwock
 */
public class XSLTemplate implements Template {

	public XSLTemplate(
		XSLTemplateResource xslTemplateResource,
		TemplateResource errorTemplateResource,
		TemplateContextHelper templateContextHelper,
		XSLEngineConfiguration xslEngineConfiguration) {

		if (xslTemplateResource == null) {
			throw new IllegalArgumentException("XSL template resource is null");
		}

		if (templateContextHelper == null) {
			throw new IllegalArgumentException(
				"Template context helper is null");
		}

		_xslTemplateResource = xslTemplateResource;
		_errorTemplateResource = errorTemplateResource;
		_templateContextHelper = templateContextHelper;

		_displayXSLErrors = xslEngineConfiguration.displayXSLErrors();
		_preventLocalConnections =
			xslEngineConfiguration.preventLocalConnections();

		Thread thread = Thread.currentThread();

		ClassLoader contextClassLoader = thread.getContextClassLoader();

		thread.setContextClassLoader(PortalClassLoaderUtil.getClassLoader());

		try {
			_errorTransformerFactory = TransformerFactory.newInstance();
			_transformerFactory = TransformerFactory.newInstance();
		}
		finally {
			thread.setContextClassLoader(contextClassLoader);
		}

		try {
			_transformerFactory.setFeature(
				XMLConstants.FEATURE_SECURE_PROCESSING,
				xslEngineConfiguration.secureProcessingEnabled());
		}
		catch (TransformerConfigurationException tce) {
			_log.error(
				"Unable to configure secure processing: " + tce.getMessage(),
				tce);
		}

		_context = new HashMap<>();
	}

	@Override
	public void clear() {
		_context.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return _context.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return _context.containsValue(value);
	}

	@Override
	public void doProcessTemplate(Writer writer) throws Exception {
		String languageId = null;

		XSLURIResolver xslURIResolver =
			_xslTemplateResource.getXSLURIResolver();

		if (xslURIResolver != null) {
			languageId = xslURIResolver.getLanguageId();
		}

		Locale locale = LocaleUtil.fromLanguageId(languageId);

		XSLErrorListener xslErrorListener = new XSLErrorListener(locale);

		_errorTransformerFactory.setErrorListener(xslErrorListener);
		_transformerFactory.setErrorListener(xslErrorListener);

		if (_preventLocalConnections) {
			xslURIResolver = new XSLSecureURIResolver(xslURIResolver);
		}

		_transformerFactory.setURIResolver(xslURIResolver);

		StreamSource xmlStreamSource = new StreamSource(
			_xslTemplateResource.getXMLReader());

		Transformer transformer = null;

		if (_errorTemplateResource == null) {
			try {
				transformer = _getTransformer(
					_xslTemplateResource, _transformerFactory);

				transformer.transform(
					xmlStreamSource, new StreamResult(writer));

				return;
			}
			catch (Exception e) {
				throw new TemplateException(
					"Unable to process XSL template " +
						_xslTemplateResource.getTemplateId(),
					e);
			}
		}

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		transformer = _getTransformer(
			_xslTemplateResource, _transformerFactory);

		transformer.setParameter(TemplateConstants.WRITER, unsyncStringWriter);

		transformer.transform(
			xmlStreamSource, new StreamResult(unsyncStringWriter));

		StringBundler sb = unsyncStringWriter.getStringBundler();

		sb.writeTo(writer);
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		return _context.entrySet();
	}

	@Override
	public Object get(Object key) {
		return _context.get(key);
	}

	@Override
	public Object get(String key) {
		return _context.get(key);
	}

	@Override
	public String[] getKeys() {
		Set<String> keys = _context.keySet();

		return keys.toArray(new String[keys.size()]);
	}

	@Override
	public boolean isEmpty() {
		return _context.isEmpty();
	}

	@Override
	public Set<String> keySet() {
		return _context.keySet();
	}

	@Override
	public void prepare(HttpServletRequest request) {
		_templateContextHelper.prepare(this, request);
	}

	@Override
	public void processTemplate(Writer writer) throws TemplateException {
		Thread thread = Thread.currentThread();

		ClassLoader contextClassLoader = thread.getContextClassLoader();

		thread.setContextClassLoader(PortalClassLoaderUtil.getClassLoader());

		try {
			doProcessTemplate(writer);
		}
		catch (Exception e1) {
			if (!_displayXSLErrors) {
				String message = StringBundler.concat(
					"Unable to process XSL template ",
					_xslTemplateResource.getTemplateId(), ": ",
					e1.getMessage());

				if (_log.isDebugEnabled()) {
					_log.debug(message, e1);
				}
				else {
					_log.error(message);
				}

				return;
			}

			Transformer errorTransformer = _getTransformer(
				_errorTemplateResource, _errorTransformerFactory);

			errorTransformer.setParameter(TemplateConstants.WRITER, writer);

			XSLErrorListener xslErrorListener =
				(XSLErrorListener)_transformerFactory.getErrorListener();

			errorTransformer.setParameter(
				"exception", xslErrorListener.getMessageAndLocation());

			try (BufferedReader br = new BufferedReader(
					_xslTemplateResource.getReader())) {

				Stream<String> stream = br.lines();

				String script = stream.collect(
					Collectors.joining(StringPool.NEW_LINE));

				errorTransformer.setParameter("script", script);
			}
			catch (IOException ioe) {
			}

			if (xslErrorListener.getLocation() != null) {
				errorTransformer.setParameter(
					"column",
					Integer.valueOf(xslErrorListener.getColumnNumber()));
				errorTransformer.setParameter(
					"line", Integer.valueOf(xslErrorListener.getLineNumber()));
			}

			try {
				errorTransformer.transform(
					new StreamSource(_xslTemplateResource.getXMLReader()),
					new StreamResult(writer));
			}
			catch (Exception e2) {
				throw new TemplateException(
					"Unable to process XSL template " +
						_errorTemplateResource.getTemplateId(),
					e2);
			}
		}
		finally {
			thread.setContextClassLoader(contextClassLoader);
		}
	}

	@Override
	public Object put(String key, Object value) {
		if (value == null) {
			return null;
		}

		return _context.put(key, value);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		_context.putAll(m);
	}

	@Override
	public Object remove(Object key) {
		return _context.remove(key);
	}

	@Override
	public int size() {
		return _context.size();
	}

	@Override
	public Collection<Object> values() {
		return _context.values();
	}

	private Transformer _getTransformer(
			TemplateResource templateResource,
			TransformerFactory transformerFactory)
		throws TemplateException {

		try {
			StreamSource scriptSource = new StreamSource(
				templateResource.getReader());

			Transformer transformer = transformerFactory.newTransformer(
				scriptSource);

			transformer.setErrorListener(transformerFactory.getErrorListener());

			for (Map.Entry<String, Object> entry : _context.entrySet()) {
				transformer.setParameter(entry.getKey(), entry.getValue());
			}

			return transformer;
		}
		catch (Exception e) {
			throw new TemplateException(
				"Unable to get Transformer for template " +
					templateResource.getTemplateId(),
				e);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(XSLTemplate.class);

	private final Map<String, Object> _context;
	private final boolean _displayXSLErrors;
	private TemplateResource _errorTemplateResource;
	private final TransformerFactory _errorTransformerFactory;
	private final boolean _preventLocalConnections;
	private final TemplateContextHelper _templateContextHelper;
	private final TransformerFactory _transformerFactory;
	private final XSLTemplateResource _xslTemplateResource;

}