/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link UniqueIndexWithLongTypeEntry}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see UniqueIndexWithLongTypeEntry
 * @generated
 */
public class UniqueIndexWithLongTypeEntryWrapper
	extends BaseModelWrapper<UniqueIndexWithLongTypeEntry>
	implements ModelWrapper<UniqueIndexWithLongTypeEntry>,
			   UniqueIndexWithLongTypeEntry {

	public UniqueIndexWithLongTypeEntryWrapper(
		UniqueIndexWithLongTypeEntry uniqueIndexWithLongTypeEntry) {

		super(uniqueIndexWithLongTypeEntry);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put(
			"uniqueIndexWithLongTypeEntryId",
			getUniqueIndexWithLongTypeEntryId());
		attributes.put("name", getName());
		attributes.put("longTypeId", getLongTypeId());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long uniqueIndexWithLongTypeEntryId = (Long)attributes.get(
			"uniqueIndexWithLongTypeEntryId");

		if (uniqueIndexWithLongTypeEntryId != null) {
			setUniqueIndexWithLongTypeEntryId(uniqueIndexWithLongTypeEntryId);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		Long longTypeId = (Long)attributes.get("longTypeId");

		if (longTypeId != null) {
			setLongTypeId(longTypeId);
		}
	}

	@Override
	public UniqueIndexWithLongTypeEntry cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the long type ID of this unique index with long type entry.
	 *
	 * @return the long type ID of this unique index with long type entry
	 */
	@Override
	public Long getLongTypeId() {
		return model.getLongTypeId();
	}

	/**
	 * Returns the name of this unique index with long type entry.
	 *
	 * @return the name of this unique index with long type entry
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the primary key of this unique index with long type entry.
	 *
	 * @return the primary key of this unique index with long type entry
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the unique index with long type entry ID of this unique index with long type entry.
	 *
	 * @return the unique index with long type entry ID of this unique index with long type entry
	 */
	@Override
	public long getUniqueIndexWithLongTypeEntryId() {
		return model.getUniqueIndexWithLongTypeEntryId();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the long type ID of this unique index with long type entry.
	 *
	 * @param longTypeId the long type ID of this unique index with long type entry
	 */
	@Override
	public void setLongTypeId(Long longTypeId) {
		model.setLongTypeId(longTypeId);
	}

	/**
	 * Sets the name of this unique index with long type entry.
	 *
	 * @param name the name of this unique index with long type entry
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the primary key of this unique index with long type entry.
	 *
	 * @param primaryKey the primary key of this unique index with long type entry
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the unique index with long type entry ID of this unique index with long type entry.
	 *
	 * @param uniqueIndexWithLongTypeEntryId the unique index with long type entry ID of this unique index with long type entry
	 */
	@Override
	public void setUniqueIndexWithLongTypeEntryId(
		long uniqueIndexWithLongTypeEntryId) {

		model.setUniqueIndexWithLongTypeEntryId(uniqueIndexWithLongTypeEntryId);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	protected UniqueIndexWithLongTypeEntryWrapper wrap(
		UniqueIndexWithLongTypeEntry uniqueIndexWithLongTypeEntry) {

		return new UniqueIndexWithLongTypeEntryWrapper(
			uniqueIndexWithLongTypeEntry);
	}

}