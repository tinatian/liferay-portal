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

import com.liferay.portal.kernel.model.ClassNameModel;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.model.impl.ClassNameModelImpl;
import com.liferay.util.SimpleCounter;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lily Chi
 */
public class InitRuntimeContext {

	public InitRuntimeContext(InitPropertiesContext initPropertiesContext)
		throws Exception {

		_counter = new SimpleCounter(
			initPropertiesContext.getMaxGroupsCount() + 1);

		_resourcePermissionCounter = new SimpleCounter();
		_socialActivityCounter = new SimpleCounter();
		_timeCounter = new SimpleCounter();
		_futureDateCounter = new SimpleCounter();
		_userScreenNameCounter = new SimpleCounter();

		_classNameModels = _initClassNameModels();

		_accountId = _counter.get();
		_companyId = _counter.get();
		_defaultUserId = _counter.get();
		_sampleUserId = _counter.get();
	}

	public long getAccountId() {
		return _accountId;
	}

	public Map<String, ClassNameModel> getClassNameModels() {
		return _classNameModels;
	}

	public Collection<ClassNameModel> getClassNameModelValues() {
		return _classNameModels.values();
	}

	public long getCompanyId() {
		return _companyId;
	}

	public SimpleCounter getCounter() {
		return _counter;
	}

	public long getDefaultUserId() {
		return _defaultUserId;
	}

	public SimpleCounter getFutureDateCounter() {
		return _futureDateCounter;
	}

	public SimpleCounter getResourcePermissionCounter() {
		return _resourcePermissionCounter;
	}

	public long getSampleUserId() {
		return _sampleUserId;
	}

	public SimpleCounter getSocialActivityCounter() {
		return _socialActivityCounter;
	}

	public SimpleCounter getTimeCounter() {
		return _timeCounter;
	}

	public SimpleCounter getUserScreenNameCounter() {
		return _userScreenNameCounter;
	}

	private Map<String, ClassNameModel> _initClassNameModels() {
		Map<String, ClassNameModel> classNameModels = new HashMap<>();
		List<String> models = ModelHintsUtil.getModels();

		for (String model : models) {
			ClassNameModel classNameModel = new ClassNameModelImpl();

			long classNameId = _counter.get();

			classNameModel.setClassNameId(classNameId);

			classNameModel.setValue(model);

			classNameModels.put(model, classNameModel);
		}

		return classNameModels;
	}

	private final long _accountId;
	private final Map<String, ClassNameModel> _classNameModels;
	private final long _companyId;
	private final SimpleCounter _counter;
	private final long _defaultUserId;
	private final SimpleCounter _futureDateCounter;
	private final SimpleCounter _resourcePermissionCounter;
	private final long _sampleUserId;
	private final SimpleCounter _socialActivityCounter;
	private final SimpleCounter _timeCounter;
	private final SimpleCounter _userScreenNameCounter;

}