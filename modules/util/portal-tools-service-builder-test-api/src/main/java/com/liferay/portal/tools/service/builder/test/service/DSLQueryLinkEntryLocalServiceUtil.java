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

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Provides the local service utility for DSLQueryLinkEntry. This utility wraps
 * <code>com.liferay.portal.tools.service.builder.test.service.impl.DSLQueryLinkEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see DSLQueryLinkEntryLocalService
 * @generated
 */
public class DSLQueryLinkEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.tools.service.builder.test.service.impl.DSLQueryLinkEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

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
	public static
		com.liferay.portal.tools.service.builder.test.model.DSLQueryLinkEntry
			addDSLQueryLinkEntry(
				com.liferay.portal.tools.service.builder.test.model.
					DSLQueryLinkEntry dslQueryLinkEntry) {

		return getService().addDSLQueryLinkEntry(dslQueryLinkEntry);
	}

	/**
	 * Creates a new dsl query link entry with the primary key. Does not add the dsl query link entry to the database.
	 *
	 * @param dslQueryLinkEntryId the primary key for the new dsl query link entry
	 * @return the new dsl query link entry
	 */
	public static
		com.liferay.portal.tools.service.builder.test.model.DSLQueryLinkEntry
			createDSLQueryLinkEntry(long dslQueryLinkEntryId) {

		return getService().createDSLQueryLinkEntry(dslQueryLinkEntryId);
	}

	/**
	 * @throws PortalException
	 */
	public static com.liferay.portal.kernel.model.PersistedModel
			createPersistedModel(java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().createPersistedModel(primaryKeyObj);
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
	public static
		com.liferay.portal.tools.service.builder.test.model.DSLQueryLinkEntry
			deleteDSLQueryLinkEntry(
				com.liferay.portal.tools.service.builder.test.model.
					DSLQueryLinkEntry dslQueryLinkEntry) {

		return getService().deleteDSLQueryLinkEntry(dslQueryLinkEntry);
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
	public static
		com.liferay.portal.tools.service.builder.test.model.DSLQueryLinkEntry
				deleteDSLQueryLinkEntry(long dslQueryLinkEntryId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return getService().deleteDSLQueryLinkEntry(dslQueryLinkEntryId);
	}

	/**
	 * @throws PortalException
	 */
	public static com.liferay.portal.kernel.model.PersistedModel
			deletePersistedModel(
				com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static <T> T dslQuery(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return getService().dslQuery(dslQuery);
	}

	public static com.liferay.portal.kernel.dao.orm.DynamicQuery
		dynamicQuery() {

		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return getService().dynamicQuery(dynamicQuery);
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
	public static <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
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
	public static <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static
		com.liferay.portal.tools.service.builder.test.model.DSLQueryLinkEntry
			fetchDSLQueryLinkEntry(long dslQueryLinkEntryId) {

		return getService().fetchDSLQueryLinkEntry(dslQueryLinkEntryId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
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
	public static java.util.List
		<com.liferay.portal.tools.service.builder.test.model.DSLQueryLinkEntry>
			getDSLQueryLinkEntries(int start, int end) {

		return getService().getDSLQueryLinkEntries(start, end);
	}

	/**
	 * Returns the number of dsl query link entries.
	 *
	 * @return the number of dsl query link entries
	 */
	public static int getDSLQueryLinkEntriesCount() {
		return getService().getDSLQueryLinkEntriesCount();
	}

	/**
	 * Returns the dsl query link entry with the primary key.
	 *
	 * @param dslQueryLinkEntryId the primary key of the dsl query link entry
	 * @return the dsl query link entry
	 * @throws PortalException if a dsl query link entry with the primary key could not be found
	 */
	public static
		com.liferay.portal.tools.service.builder.test.model.DSLQueryLinkEntry
				getDSLQueryLinkEntry(long dslQueryLinkEntryId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getDSLQueryLinkEntry(dslQueryLinkEntryId);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	public static com.liferay.portal.kernel.model.PersistedModel
			getPersistedModel(java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getPersistedModel(primaryKeyObj);
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
	public static
		com.liferay.portal.tools.service.builder.test.model.DSLQueryLinkEntry
			updateDSLQueryLinkEntry(
				com.liferay.portal.tools.service.builder.test.model.
					DSLQueryLinkEntry dslQueryLinkEntry) {

		return getService().updateDSLQueryLinkEntry(dslQueryLinkEntry);
	}

	public static DSLQueryLinkEntryLocalService getService() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker
		<DSLQueryLinkEntryLocalService, DSLQueryLinkEntryLocalService>
			_serviceTracker;

	static {
		Bundle bundle = FrameworkUtil.getBundle(
			DSLQueryLinkEntryLocalService.class);

		ServiceTracker
			<DSLQueryLinkEntryLocalService, DSLQueryLinkEntryLocalService>
				serviceTracker =
					new ServiceTracker
						<DSLQueryLinkEntryLocalService,
						 DSLQueryLinkEntryLocalService>(
							 bundle.getBundleContext(),
							 DSLQueryLinkEntryLocalService.class, null);

		serviceTracker.open();

		_serviceTracker = serviceTracker;
	}

}