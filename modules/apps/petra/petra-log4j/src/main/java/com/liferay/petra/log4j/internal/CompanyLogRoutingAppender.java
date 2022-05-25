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

package com.liferay.petra.log4j.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.Serializable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.RolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.util.Constants;

/**
 * @author Hai Yu
 */
@Plugin(
	category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE,
	name = CompanyLogRoutingAppender.PLUGIN_NAME, printObject = true
)
public final class CompanyLogRoutingAppender extends AbstractAppender {

	public static final String PLUGIN_NAME = "CompanyLogRouting";

	@PluginBuilderFactory
	public static <B extends Builder<B>> B newBuilder() {
		return (B)new Builder<>().asBuilder();
	}

	@Override
	public void append(LogEvent logEvent) {
		Appender textFileAppender = _fileAppenders.computeIfAbsent(
			CompanyThreadLocal.getCompanyId(), key -> _createFileAppender(key));

		textFileAppender.append(logEvent);
	}

	public static class Builder<B extends Builder<B>>
		extends AbstractAppender.Builder<B>
		implements org.apache.logging.log4j.core.util.Builder
			<CompanyLogRoutingAppender> {

		@Override
		public CompanyLogRoutingAppender build() {
			return new CompanyLogRoutingAppender(
				advertise, advertiseUri, append, bufferedIo, bufferSize,
				createOnDemand, fileGroup, fileName, fileOwner, filePattern,
				filePermissions, immediateFlush, locking, policy, strategy,
				getName(), getLayout(), getFilter());
		}

		@PluginBuilderAttribute
		private boolean advertise;

		@PluginBuilderAttribute
		private String advertiseUri;

		@PluginBuilderAttribute
		private boolean append = true;

		@PluginBuilderAttribute
		private boolean bufferedIo = true;

		@PluginBuilderAttribute
		private int bufferSize = Constants.ENCODER_BYTE_BUFFER_SIZE;

		@PluginBuilderAttribute
		private boolean createOnDemand;

		@PluginBuilderAttribute
		private String fileGroup;

		@PluginBuilderAttribute
		private String fileName;

		@PluginBuilderAttribute
		private String fileOwner;

		@PluginBuilderAttribute
		@Required
		private String filePattern;

		@PluginBuilderAttribute
		private String filePermissions;

		@PluginBuilderAttribute
		private boolean immediateFlush = true;

		@PluginBuilderAttribute
		private boolean locking;

		@PluginElement("Policy")
		@Required
		private TriggeringPolicy policy;

		@PluginElement("Strategy")
		private RolloverStrategy strategy;

	}

	private CompanyLogRoutingAppender(
		boolean advertise, String advertiseUri, boolean append,
		boolean bufferedIo, int bufferSize, boolean createOnDemand,
		String fileGroup, String fileName, String fileOwner, String filePattern,
		String filePermissions, boolean immediateFlush, boolean locking,
		TriggeringPolicy triggeringPolicy, RolloverStrategy rolloverStrategy,
		String name, Layout<? extends Serializable> layout, Filter filter) {

		super(name, filter, layout, true, null);

		_advertise = advertise;
		_advertiseUri = advertiseUri;
		_append = append;
		_bufferedIo = bufferedIo;
		_bufferSize = bufferSize;
		_createOnDemand = createOnDemand;
		_fileGroup = fileGroup;
		_fileName = fileName;
		_fileOwner = fileOwner;
		_filePattern = filePattern;
		_filePermissions = filePermissions;
		_immediateFlush = immediateFlush;
		_locking = locking;
		_triggeringPolicy = triggeringPolicy;
		_rolloverStrategy = rolloverStrategy;
	}

	private Appender _createFileAppender(long companyId) {
		RollingFileAppender.Builder builder = RollingFileAppender.newBuilder();

		LoggerContext loggerContext = (LoggerContext)LogManager.getContext();

		builder.setConfiguration(loggerContext.getConfiguration());

		builder.setName(companyId + StringPool.DASH + getName());
		builder.setIgnoreExceptions(ignoreExceptions());
		builder.setLayout(getLayout());
		builder.withAdvertise(_advertise);
		builder.withAdvertiseUri(_advertiseUri);
		builder.withAppend(_append);
		builder.withBufferedIo(_bufferedIo);
		builder.withBufferSize(_bufferSize);
		builder.withCreateOnDemand(_createOnDemand);
		builder.withFileGroup(_fileGroup);
		builder.withFileName(_fileName);
		builder.withFileOwner(_fileOwner);
		builder.withFilePattern(
			StringBundler.concat(
				StringUtil.replace(
					PropsUtil.get(PropsKeys.LIFERAY_HOME), '\\', '/'),
				"/logs/companies/", companyId, StringPool.SLASH,
				StringUtil.extractLast(_filePattern, StringPool.SLASH)));
		builder.withFilePermissions(_filePermissions);
		builder.withImmediateFlush(_immediateFlush);
		builder.withLocking(_locking);
		builder.withStrategy(_rolloverStrategy);
		builder.withPolicy(_triggeringPolicy);

		Appender fileAppender = builder.build();

		fileAppender.start();

		return fileAppender;
	}

	private final boolean _advertise;
	private final String _advertiseUri;
	private final boolean _append;
	private final boolean _bufferedIo;
	private final int _bufferSize;
	private final boolean _createOnDemand;
	private final Map<Long, Appender> _fileAppenders =
		new ConcurrentHashMap<>();
	private final String _fileGroup;
	private final String _fileName;
	private final String _fileOwner;
	private final String _filePattern;
	private final String _filePermissions;
	private final boolean _immediateFlush;
	private final boolean _locking;
	private final RolloverStrategy _rolloverStrategy;
	private final TriggeringPolicy _triggeringPolicy;

}