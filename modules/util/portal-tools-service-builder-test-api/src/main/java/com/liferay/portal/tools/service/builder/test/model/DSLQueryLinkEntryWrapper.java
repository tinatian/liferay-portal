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

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link DSLQueryLinkEntry}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DSLQueryLinkEntry
 * @generated
 */
public class DSLQueryLinkEntryWrapper
	extends BaseModelWrapper<DSLQueryLinkEntry>
	implements DSLQueryLinkEntry, ModelWrapper<DSLQueryLinkEntry> {

	public DSLQueryLinkEntryWrapper(DSLQueryLinkEntry dslQueryLinkEntry) {
		super(dslQueryLinkEntry);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("dslQueryLinkEntryId", getDslQueryLinkEntryId());
		attributes.put("parentDSLQueryEntryId", getParentDSLQueryEntryId());
		attributes.put("childDSLQueryEntryId", getChildDSLQueryEntryId());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long dslQueryLinkEntryId = (Long)attributes.get("dslQueryLinkEntryId");

		if (dslQueryLinkEntryId != null) {
			setDslQueryLinkEntryId(dslQueryLinkEntryId);
		}

		Long parentDSLQueryEntryId = (Long)attributes.get(
			"parentDSLQueryEntryId");

		if (parentDSLQueryEntryId != null) {
			setParentDSLQueryEntryId(parentDSLQueryEntryId);
		}

		Long childDSLQueryEntryId = (Long)attributes.get(
			"childDSLQueryEntryId");

		if (childDSLQueryEntryId != null) {
			setChildDSLQueryEntryId(childDSLQueryEntryId);
		}
	}

	/**
	 * Returns the child dsl query entry ID of this dsl query link entry.
	 *
	 * @return the child dsl query entry ID of this dsl query link entry
	 */
	@Override
	public long getChildDSLQueryEntryId() {
		return model.getChildDSLQueryEntryId();
	}

	/**
	 * Returns the dsl query link entry ID of this dsl query link entry.
	 *
	 * @return the dsl query link entry ID of this dsl query link entry
	 */
	@Override
	public long getDslQueryLinkEntryId() {
		return model.getDslQueryLinkEntryId();
	}

	/**
	 * Returns the parent dsl query entry ID of this dsl query link entry.
	 *
	 * @return the parent dsl query entry ID of this dsl query link entry
	 */
	@Override
	public long getParentDSLQueryEntryId() {
		return model.getParentDSLQueryEntryId();
	}

	/**
	 * Returns the primary key of this dsl query link entry.
	 *
	 * @return the primary key of this dsl query link entry
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the child dsl query entry ID of this dsl query link entry.
	 *
	 * @param childDSLQueryEntryId the child dsl query entry ID of this dsl query link entry
	 */
	@Override
	public void setChildDSLQueryEntryId(long childDSLQueryEntryId) {
		model.setChildDSLQueryEntryId(childDSLQueryEntryId);
	}

	/**
	 * Sets the dsl query link entry ID of this dsl query link entry.
	 *
	 * @param dslQueryLinkEntryId the dsl query link entry ID of this dsl query link entry
	 */
	@Override
	public void setDslQueryLinkEntryId(long dslQueryLinkEntryId) {
		model.setDslQueryLinkEntryId(dslQueryLinkEntryId);
	}

	/**
	 * Sets the parent dsl query entry ID of this dsl query link entry.
	 *
	 * @param parentDSLQueryEntryId the parent dsl query entry ID of this dsl query link entry
	 */
	@Override
	public void setParentDSLQueryEntryId(long parentDSLQueryEntryId) {
		model.setParentDSLQueryEntryId(parentDSLQueryEntryId);
	}

	/**
	 * Sets the primary key of this dsl query link entry.
	 *
	 * @param primaryKey the primary key of this dsl query link entry
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	@Override
	protected DSLQueryLinkEntryWrapper wrap(
		DSLQueryLinkEntry dslQueryLinkEntry) {

		return new DSLQueryLinkEntryWrapper(dslQueryLinkEntry);
	}

}