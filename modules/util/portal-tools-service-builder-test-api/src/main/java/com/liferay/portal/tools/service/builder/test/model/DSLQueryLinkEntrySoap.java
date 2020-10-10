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

package com.liferay.portal.tools.service.builder.test.model;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used by SOAP remote services.
 *
 * @author Brian Wing Shun Chan
 * @deprecated As of Athanasius (7.3.x), with no direct replacement
 * @generated
 */
@Deprecated
public class DSLQueryLinkEntrySoap implements Serializable {

	public static DSLQueryLinkEntrySoap toSoapModel(DSLQueryLinkEntry model) {
		DSLQueryLinkEntrySoap soapModel = new DSLQueryLinkEntrySoap();

		soapModel.setDslQueryLinkEntryId(model.getDslQueryLinkEntryId());
		soapModel.setParentDSLQueryEntryId(model.getParentDSLQueryEntryId());
		soapModel.setChildDSLQueryEntryId(model.getChildDSLQueryEntryId());

		return soapModel;
	}

	public static DSLQueryLinkEntrySoap[] toSoapModels(
		DSLQueryLinkEntry[] models) {

		DSLQueryLinkEntrySoap[] soapModels =
			new DSLQueryLinkEntrySoap[models.length];

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModel(models[i]);
		}

		return soapModels;
	}

	public static DSLQueryLinkEntrySoap[][] toSoapModels(
		DSLQueryLinkEntry[][] models) {

		DSLQueryLinkEntrySoap[][] soapModels = null;

		if (models.length > 0) {
			soapModels =
				new DSLQueryLinkEntrySoap[models.length][models[0].length];
		}
		else {
			soapModels = new DSLQueryLinkEntrySoap[0][0];
		}

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModels(models[i]);
		}

		return soapModels;
	}

	public static DSLQueryLinkEntrySoap[] toSoapModels(
		List<DSLQueryLinkEntry> models) {

		List<DSLQueryLinkEntrySoap> soapModels =
			new ArrayList<DSLQueryLinkEntrySoap>(models.size());

		for (DSLQueryLinkEntry model : models) {
			soapModels.add(toSoapModel(model));
		}

		return soapModels.toArray(new DSLQueryLinkEntrySoap[soapModels.size()]);
	}

	public DSLQueryLinkEntrySoap() {
	}

	public long getPrimaryKey() {
		return _dslQueryLinkEntryId;
	}

	public void setPrimaryKey(long pk) {
		setDslQueryLinkEntryId(pk);
	}

	public long getDslQueryLinkEntryId() {
		return _dslQueryLinkEntryId;
	}

	public void setDslQueryLinkEntryId(long dslQueryLinkEntryId) {
		_dslQueryLinkEntryId = dslQueryLinkEntryId;
	}

	public long getParentDSLQueryEntryId() {
		return _parentDSLQueryEntryId;
	}

	public void setParentDSLQueryEntryId(long parentDSLQueryEntryId) {
		_parentDSLQueryEntryId = parentDSLQueryEntryId;
	}

	public long getChildDSLQueryEntryId() {
		return _childDSLQueryEntryId;
	}

	public void setChildDSLQueryEntryId(long childDSLQueryEntryId) {
		_childDSLQueryEntryId = childDSLQueryEntryId;
	}

	private long _dslQueryLinkEntryId;
	private long _parentDSLQueryEntryId;
	private long _childDSLQueryEntryId;

}