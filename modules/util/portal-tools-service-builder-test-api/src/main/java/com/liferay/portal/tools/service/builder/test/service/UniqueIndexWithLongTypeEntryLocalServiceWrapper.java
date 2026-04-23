/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link UniqueIndexWithLongTypeEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see UniqueIndexWithLongTypeEntryLocalService
 * @generated
 */
public class UniqueIndexWithLongTypeEntryLocalServiceWrapper
	implements ServiceWrapper<UniqueIndexWithLongTypeEntryLocalService>,
			   UniqueIndexWithLongTypeEntryLocalService {

	public UniqueIndexWithLongTypeEntryLocalServiceWrapper() {
		this(null);
	}

	public UniqueIndexWithLongTypeEntryLocalServiceWrapper(
		UniqueIndexWithLongTypeEntryLocalService
			uniqueIndexWithLongTypeEntryLocalService) {

		_uniqueIndexWithLongTypeEntryLocalService =
			uniqueIndexWithLongTypeEntryLocalService;
	}

	/**
	 * Adds the unique index with long type entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UniqueIndexWithLongTypeEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param uniqueIndexWithLongTypeEntry the unique index with long type entry
	 * @return the unique index with long type entry that was added
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.
		UniqueIndexWithLongTypeEntry addUniqueIndexWithLongTypeEntry(
			com.liferay.portal.tools.service.builder.test.model.
				UniqueIndexWithLongTypeEntry uniqueIndexWithLongTypeEntry) {

		return _uniqueIndexWithLongTypeEntryLocalService.
			addUniqueIndexWithLongTypeEntry(uniqueIndexWithLongTypeEntry);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _uniqueIndexWithLongTypeEntryLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Creates a new unique index with long type entry with the primary key. Does not add the unique index with long type entry to the database.
	 *
	 * @param uniqueIndexWithLongTypeEntryId the primary key for the new unique index with long type entry
	 * @return the new unique index with long type entry
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.
		UniqueIndexWithLongTypeEntry createUniqueIndexWithLongTypeEntry(
			long uniqueIndexWithLongTypeEntryId) {

		return _uniqueIndexWithLongTypeEntryLocalService.
			createUniqueIndexWithLongTypeEntry(uniqueIndexWithLongTypeEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _uniqueIndexWithLongTypeEntryLocalService.deletePersistedModel(
			persistedModel);
	}

	/**
	 * Deletes the unique index with long type entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UniqueIndexWithLongTypeEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param uniqueIndexWithLongTypeEntryId the primary key of the unique index with long type entry
	 * @return the unique index with long type entry that was removed
	 * @throws PortalException if a unique index with long type entry with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.
		UniqueIndexWithLongTypeEntry deleteUniqueIndexWithLongTypeEntry(
				long uniqueIndexWithLongTypeEntryId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _uniqueIndexWithLongTypeEntryLocalService.
			deleteUniqueIndexWithLongTypeEntry(uniqueIndexWithLongTypeEntryId);
	}

	/**
	 * Deletes the unique index with long type entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UniqueIndexWithLongTypeEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param uniqueIndexWithLongTypeEntry the unique index with long type entry
	 * @return the unique index with long type entry that was removed
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.
		UniqueIndexWithLongTypeEntry deleteUniqueIndexWithLongTypeEntry(
			com.liferay.portal.tools.service.builder.test.model.
				UniqueIndexWithLongTypeEntry uniqueIndexWithLongTypeEntry) {

		return _uniqueIndexWithLongTypeEntryLocalService.
			deleteUniqueIndexWithLongTypeEntry(uniqueIndexWithLongTypeEntry);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _uniqueIndexWithLongTypeEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _uniqueIndexWithLongTypeEntryLocalService.dslQueryCount(
			dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _uniqueIndexWithLongTypeEntryLocalService.dynamicQuery();
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

		return _uniqueIndexWithLongTypeEntryLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.UniqueIndexWithLongTypeEntryModelImpl</code>.
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

		return _uniqueIndexWithLongTypeEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.UniqueIndexWithLongTypeEntryModelImpl</code>.
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

		return _uniqueIndexWithLongTypeEntryLocalService.dynamicQuery(
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

		return _uniqueIndexWithLongTypeEntryLocalService.dynamicQueryCount(
			dynamicQuery);
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

		return _uniqueIndexWithLongTypeEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.
		UniqueIndexWithLongTypeEntry fetchUniqueIndexWithLongTypeEntry(
			long uniqueIndexWithLongTypeEntryId) {

		return _uniqueIndexWithLongTypeEntryLocalService.
			fetchUniqueIndexWithLongTypeEntry(uniqueIndexWithLongTypeEntryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _uniqueIndexWithLongTypeEntryLocalService.
			getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _uniqueIndexWithLongTypeEntryLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _uniqueIndexWithLongTypeEntryLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _uniqueIndexWithLongTypeEntryLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Returns a range of all the unique index with long type entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.UniqueIndexWithLongTypeEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of unique index with long type entries
	 * @param end the upper bound of the range of unique index with long type entries (not inclusive)
	 * @return the range of unique index with long type entries
	 */
	@Override
	public java.util.List
		<com.liferay.portal.tools.service.builder.test.model.
			UniqueIndexWithLongTypeEntry> getUniqueIndexWithLongTypeEntries(
				int start, int end) {

		return _uniqueIndexWithLongTypeEntryLocalService.
			getUniqueIndexWithLongTypeEntries(start, end);
	}

	/**
	 * Returns the number of unique index with long type entries.
	 *
	 * @return the number of unique index with long type entries
	 */
	@Override
	public int getUniqueIndexWithLongTypeEntriesCount() {
		return _uniqueIndexWithLongTypeEntryLocalService.
			getUniqueIndexWithLongTypeEntriesCount();
	}

	/**
	 * Returns the unique index with long type entry with the primary key.
	 *
	 * @param uniqueIndexWithLongTypeEntryId the primary key of the unique index with long type entry
	 * @return the unique index with long type entry
	 * @throws PortalException if a unique index with long type entry with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.
		UniqueIndexWithLongTypeEntry getUniqueIndexWithLongTypeEntry(
				long uniqueIndexWithLongTypeEntryId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _uniqueIndexWithLongTypeEntryLocalService.
			getUniqueIndexWithLongTypeEntry(uniqueIndexWithLongTypeEntryId);
	}

	/**
	 * Updates the unique index with long type entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UniqueIndexWithLongTypeEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param uniqueIndexWithLongTypeEntry the unique index with long type entry
	 * @return the unique index with long type entry that was updated
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.
		UniqueIndexWithLongTypeEntry updateUniqueIndexWithLongTypeEntry(
			com.liferay.portal.tools.service.builder.test.model.
				UniqueIndexWithLongTypeEntry uniqueIndexWithLongTypeEntry) {

		return _uniqueIndexWithLongTypeEntryLocalService.
			updateUniqueIndexWithLongTypeEntry(uniqueIndexWithLongTypeEntry);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _uniqueIndexWithLongTypeEntryLocalService.getBasePersistence();
	}

	@Override
	public UniqueIndexWithLongTypeEntryLocalService getWrappedService() {
		return _uniqueIndexWithLongTypeEntryLocalService;
	}

	@Override
	public void setWrappedService(
		UniqueIndexWithLongTypeEntryLocalService
			uniqueIndexWithLongTypeEntryLocalService) {

		_uniqueIndexWithLongTypeEntryLocalService =
			uniqueIndexWithLongTypeEntryLocalService;
	}

	private UniqueIndexWithLongTypeEntryLocalService
		_uniqueIndexWithLongTypeEntryLocalService;

}