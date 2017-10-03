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

package com.liferay.portal.kernel.servlet;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * @author Tina Tian
 */
public class ReloadHeadersRequestDispatcherWrapper
	implements RequestDispatcher {

	public ReloadHeadersRequestDispatcherWrapper(
		RequestDispatcher requestDispatcher) {

		_requestDispatcher = requestDispatcher;
	}

	@Override
	public void forward(
			ServletRequest servletRequest, ServletResponse servletResponse)
		throws IOException, ServletException {

		_requestDispatcher.forward(servletRequest, servletResponse);
	}

	@Override
	public void include(
			ServletRequest servletRequest, ServletResponse servletResponse)
		throws IOException, ServletException {

		ReloadHeadersServletResponseWrapper
			reloadHeadersServletResponseWrapper =
				new ReloadHeadersServletResponseWrapper(
					(HttpServletResponse)servletResponse);

		_requestDispatcher.include(
			servletRequest, reloadHeadersServletResponseWrapper);

		reloadHeadersServletResponseWrapper.reload();
	}

	private final RequestDispatcher _requestDispatcher;

	private class HeaderAction<T> {

		public String getName() {
			return _name;
		}

		public T getValue() {
			return _value;
		}

		public boolean isOverride() {
			return _override;
		}

		private HeaderAction(String name, T value, boolean override) {
			_name = name;
			_value = value;
			_override = override;
		}

		private final String _name;
		private final boolean _override;
		private final T _value;

	}

	private class ReloadHeadersServletResponseWrapper
		extends HttpServletResponseWrapper {

		public ReloadHeadersServletResponseWrapper(
			HttpServletResponse httpServletResponse) {

			super(httpServletResponse);

			_httpServletResponse = httpServletResponse;
		}

		@Override
		public void addCookie(Cookie cookie) {
			_headerActions.add(
				new HeaderAction<>(cookie.getName(), cookie, false));
		}

		@Override
		public void addDateHeader(String name, long value) {
			_headerActions.add(new HeaderAction<>(name, value, false));
		}

		@Override
		public void addHeader(String name, String value) {
			_headerActions.add(new HeaderAction<>(name, value, false));
		}

		@Override
		public void addIntHeader(String name, int value) {
			_headerActions.add(new HeaderAction<>(name, value, false));
		}

		public void reload() {
			for (int i = 0; i < _headerActions.size(); i++) {
				HeaderAction<?> headerAction = _headerActions.get(i);

				Object value = headerAction.getValue();

				if (value instanceof String) {
					if (headerAction.isOverride()) {
						_httpServletResponse.setHeader(
							headerAction.getName(), (String)value);
					}
					else {
						_httpServletResponse.addHeader(
							headerAction.getName(), (String)value);
					}
				}
				else if (value instanceof Long) {
					if (headerAction.isOverride()) {
						_httpServletResponse.setDateHeader(
							headerAction.getName(), (Long)value);
					}
					else {
						_httpServletResponse.addDateHeader(
							headerAction.getName(), (Long)value);
					}
				}
				else if (value instanceof Cookie) {
					_httpServletResponse.addCookie((Cookie)value);
				}
				else if (value instanceof Integer) {
					if (headerAction.isOverride()) {
						_httpServletResponse.setIntHeader(
							headerAction.getName(), (Integer)value);
					}
					else {
						_httpServletResponse.addIntHeader(
							headerAction.getName(), (Integer)value);
					}
				}
				else {
					throw new IllegalStateException(
						"Unable to handle value type " +
							value.getClass().getName());
				}
			}
		}

		@Override
		public void setDateHeader(String name, long value) {
			_headerActions.add(new HeaderAction<>(name, value, true));
		}

		@Override
		public void setHeader(String name, String value) {
			_headerActions.add(new HeaderAction<>(name, value, true));
		}

		@Override
		public void setIntHeader(String name, int value) {
			_headerActions.add(new HeaderAction<>(name, value, true));
		}

		private final List<HeaderAction<?>> _headerActions = new ArrayList<>();
		private final HttpServletResponse _httpServletResponse;

	}

}