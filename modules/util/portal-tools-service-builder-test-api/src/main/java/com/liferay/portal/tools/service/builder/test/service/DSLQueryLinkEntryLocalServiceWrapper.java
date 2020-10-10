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

package com.liferay.portal.tools.service.builder.test.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link DSLQueryLinkEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see DSLQueryLinkEntryLocalService
 * @generated
 */
public class DSLQueryLinkEntryLocalServiceWrapper
	implements DSLQueryLinkEntryLocalService,
			   ServiceWrapper<DSLQueryLinkEntryLocalService> {

	public DSLQueryLinkEntryLocalServiceWrapper(
		DSLQueryLinkEntryLocalService dslQueryLinkEntryLocalService) {

		_dslQueryLinkEntryLocalService = dslQueryLinkEntryLocalService;
	}

	/**
	 * Adds the dsl query link entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DSLQueryLinkEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dslQueryLinkEntry the dsl query link entry
	 * @return the dsl query link entry that was added
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.DSLQueryLinkEntry
		addDSLQueryLinkEntry(
			com.liferay.portal.tools.service.builder.test.model.
				DSLQueryLinkEntry dslQueryLinkEntry) {

		return _dslQueryLinkEntryLocalService.addDSLQueryLinkEntry(
			dslQueryLinkEntry);
	}

	/**
	 * Creates a new dsl query link entry with the primary key. Does not add the dsl query link entry to the database.
	 *
	 * @param dslQueryLinkEntryId the primary key for the new dsl query link entry
	 * @return the new dsl query link entry
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.DSLQueryLinkEntry
		createDSLQueryLinkEntry(long dslQueryLinkEntryId) {

		return _dslQueryLinkEntryLocalService.createDSLQueryLinkEntry(
			dslQueryLinkEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dslQueryLinkEntryLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the dsl query link entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DSLQueryLinkEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dslQueryLinkEntry the dsl query link entry
	 * @return the dsl query link entry that was removed
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.DSLQueryLinkEntry
		deleteDSLQueryLinkEntry(
			com.liferay.portal.tools.service.builder.test.model.
				DSLQueryLinkEntry dslQueryLinkEntry) {

		return _dslQueryLinkEntryLocalService.deleteDSLQueryLinkEntry(
			dslQueryLinkEntry);
	}

	/**
	 * Deletes the dsl query link entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DSLQueryLinkEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dslQueryLinkEntryId the primary key of the dsl query link entry
	 * @return the dsl query link entry that was removed
	 * @throws PortalException if a dsl query link entry with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.DSLQueryLinkEntry
			deleteDSLQueryLinkEntry(long dslQueryLinkEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dslQueryLinkEntryLocalService.deleteDSLQueryLinkEntry(
			dslQueryLinkEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dslQueryLinkEntryLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _dslQueryLinkEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _dslQueryLinkEntryLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _dslQueryLinkEntryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.DSLQueryLinkEntryModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _dslQueryLinkEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.DSLQueryLinkEntryModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _dslQueryLinkEntryLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _dslQueryLinkEntryLocalService.dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _dslQueryLinkEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.DSLQueryLinkEntry
		fetchDSLQueryLinkEntry(long dslQueryLinkEntryId) {

		return _dslQueryLinkEntryLocalService.fetchDSLQueryLinkEntry(
			dslQueryLinkEntryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _dslQueryLinkEntryLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the dsl query link entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.DSLQueryLinkEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of dsl query link entries
	 * @param end the upper bound of the range of dsl query link entries (not inclusive)
	 * @return the range of dsl query link entries
	 */
	@Override
	public java.util.List
		<com.liferay.portal.tools.service.builder.test.model.DSLQueryLinkEntry>
			getDSLQueryLinkEntries(int start, int end) {

		return _dslQueryLinkEntryLocalService.getDSLQueryLinkEntries(
			start, end);
	}

	/**
	 * Returns the number of dsl query link entries.
	 *
	 * @return the number of dsl query link entries
	 */
	@Override
	public int getDSLQueryLinkEntriesCount() {
		return _dslQueryLinkEntryLocalService.getDSLQueryLinkEntriesCount();
	}

	/**
	 * Returns the dsl query link entry with the primary key.
	 *
	 * @param dslQueryLinkEntryId the primary key of the dsl query link entry
	 * @return the dsl query link entry
	 * @throws PortalException if a dsl query link entry with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.DSLQueryLinkEntry
			getDSLQueryLinkEntry(long dslQueryLinkEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dslQueryLinkEntryLocalService.getDSLQueryLinkEntry(
			dslQueryLinkEntryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _dslQueryLinkEntryLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _dslQueryLinkEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dslQueryLinkEntryLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the dsl query link entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DSLQueryLinkEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dslQueryLinkEntry the dsl query link entry
	 * @return the dsl query link entry that was updated
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.DSLQueryLinkEntry
		updateDSLQueryLinkEntry(
			com.liferay.portal.tools.service.builder.test.model.
				DSLQueryLinkEntry dslQueryLinkEntry) {

		return _dslQueryLinkEntryLocalService.updateDSLQueryLinkEntry(
			dslQueryLinkEntry);
	}

	@Override
	public DSLQueryLinkEntryLocalService getWrappedService() {
		return _dslQueryLinkEntryLocalService;
	}

	@Override
	public void setWrappedService(
		DSLQueryLinkEntryLocalService dslQueryLinkEntryLocalService) {

		_dslQueryLinkEntryLocalService = dslQueryLinkEntryLocalService;
	}

	private DSLQueryLinkEntryLocalService _dslQueryLinkEntryLocalService;

}