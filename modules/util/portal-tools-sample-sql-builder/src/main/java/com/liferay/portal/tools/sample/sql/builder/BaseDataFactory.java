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

package com.liferay.portal.tools.sample.sql.builder;

import com.liferay.counter.kernel.model.Counter;
import com.liferay.counter.kernel.model.CounterModel;
import com.liferay.counter.model.impl.CounterModelImpl;
import com.liferay.portal.kernel.io.OutputStreamWriter;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedWriter;
import com.liferay.portal.kernel.model.ClassNameModel;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.social.kernel.model.SocialActivity;
import com.liferay.util.SimpleCounter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lily Chi
 */
public abstract class BaseDataFactory {

	public void closeCSVWriters() throws IOException {
		for (Writer writer : _csvWriters.values()) {
			writer.close();
		}
	}

	public String getClassName(long classNameId) {
		for (ClassNameModel classNameModel :
				initRuntimeContext.getClassNameModelValues()) {

			if (classNameModel.getClassNameId() == classNameId) {
				return classNameModel.getValue();
			}
		}

		throw new RuntimeException(
			"Unable to find class name for id " + classNameId);
	}

	public long getClassNameId(Class<?> clazz) {
		ClassNameModel classNameModel =
			initRuntimeContext.getClassNameModels().get(clazz.getName());

		return classNameModel.getClassNameId();
	}

	public long getCounterNext() {
		SimpleCounter counter = initRuntimeContext.getCounter();

		return counter.get();
	}

	public Writer getCSVWriter(String csvFileName) {
		Writer writer = _csvWriters.get(csvFileName);

		if (writer == null) {
			throw new IllegalArgumentException(
				"Unknown CSV file name: " + csvFileName);
		}

		return writer;
	}

	public String getDateLong(Date date) {
		return String.valueOf(date.getTime());
	}

	public String getDateString(Date date) {
		if (date == null) {
			return null;
		}

		return initPropertiesContext.getSimpleDateFormat().format(date);
	}

	public String getPortletId(String portletPrefix) {
		return portletPrefix.concat(PortletIdCodec.generateInstanceId());
	}

	public InputStream getResourceInputStream(String resourceName) {
		ClassLoader classLoader = _clazz.getClassLoader();

		return classLoader.getResourceAsStream(
			_DEPENDENCIES_DIR + resourceName);
	}

	public List<Integer> getSequence(int size) {
		List<Integer> sequence = new ArrayList<>(size);

		for (int i = 1; i <= size; i++) {
			sequence.add(i);
		}

		return sequence;
	}

	public SimpleCounter getSimpleCounter(
		Map<Long, SimpleCounter>[] simpleCountersArray, long groupId,
		long classNameId) {

		Map<Long, SimpleCounter> simpleCounters =
			simpleCountersArray[(int)groupId - 1];

		if (simpleCounters == null) {
			simpleCounters = new HashMap<>();

			simpleCountersArray[(int)groupId - 1] = simpleCounters;
		}

		SimpleCounter simpleCounter = simpleCounters.get(classNameId);

		if (simpleCounter == null) {
			simpleCounter = new SimpleCounter(0);

			simpleCounters.put(classNameId, simpleCounter);
		}

		return simpleCounter;
	}

	public List<CounterModel> newCounterModels() {
		SimpleCounter counter = initRuntimeContext.getCounter();
		SimpleCounter resourcePermissionCounter =
			initRuntimeContext.getResourcePermissionCounter();
		SimpleCounter socialActivityCounter =
			initRuntimeContext.getSocialActivityCounter();

		List<CounterModel> counterModels = new ArrayList<>();

		// Counter

		CounterModel counterModel = new CounterModelImpl();

		counterModel.setName(Counter.class.getName());
		counterModel.setCurrentId(counter.get());

		counterModels.add(counterModel);

		// ResourcePermission

		counterModel = new CounterModelImpl();

		counterModel.setName(ResourcePermission.class.getName());
		counterModel.setCurrentId(resourcePermissionCounter.get());

		counterModels.add(counterModel);

		// SocialActivity

		counterModel = new CounterModelImpl();

		counterModel.setName(SocialActivity.class.getName());
		counterModel.setCurrentId(socialActivityCounter.get());

		counterModels.add(counterModel);

		return counterModels;
	}

	public final InitPropertiesContext initPropertiesContext;
	public final InitRuntimeContext initRuntimeContext;

	protected BaseDataFactory(
			InitRuntimeContext initRuntimeContext,
			InitPropertiesContext initPropertiesContext)
		throws FileNotFoundException {

		this.initRuntimeContext = initRuntimeContext;
		this.initPropertiesContext = initPropertiesContext;

		_initCSVWriter(
			initPropertiesContext.getOutputDir(),
			initPropertiesContext.getCsvFileNames());
	}

	protected Date nextFutureDate() {
		SimpleCounter futureDateCounter =
			initRuntimeContext.getFutureDateCounter();

		return new Date(_FUTURE_TIME + (futureDateCounter.get() * Time.SECOND));
	}

	protected static final long CURRENT_TIME = System.currentTimeMillis();

	private void _initCSVWriter(String outputDirectory, String csvFileNames)
		throws FileNotFoundException {

		File outputDir = new File(outputDirectory);

		outputDir.mkdirs();

		for (String csvFileName : StringUtil.split(csvFileNames)) {
			_csvWriters.put(
				csvFileName,
				new UnsyncBufferedWriter(
					new OutputStreamWriter(
						new FileOutputStream(
							new File(outputDir, csvFileName.concat(".csv")))),
					_WRITER_BUFFER_SIZE) {

					@Override
					public void flush() {

						// Disable FreeMarker from flushing

					}

				});
		}
	}

	private static final String _DEPENDENCIES_DIR =
		"com/liferay/portal/tools/sample/sql/builder/dependencies/";

	private static final long _FUTURE_TIME =
		System.currentTimeMillis() + Time.YEAR;

	private static final int _WRITER_BUFFER_SIZE = 16 * 1024;

	private final Class<?> _clazz = getClass();
	private final Map<String, Writer> _csvWriters = new HashMap<>();

}