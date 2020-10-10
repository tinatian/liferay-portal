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

package com.liferay.portal.tools.service.builder.test.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.tools.service.builder.test.model.DSLQueryLinkEntry;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The persistence utility for the dsl query link entry service. This utility wraps <code>com.liferay.portal.tools.service.builder.test.service.persistence.impl.DSLQueryLinkEntryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DSLQueryLinkEntryPersistence
 * @generated
 */
public class DSLQueryLinkEntryUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(DSLQueryLinkEntry dslQueryLinkEntry) {
		getPersistence().clearCache(dslQueryLinkEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#countWithDynamicQuery(DynamicQuery)
	 */
	public static long countWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#fetchByPrimaryKeys(Set)
	 */
	public static Map<Serializable, DSLQueryLinkEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<DSLQueryLinkEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<DSLQueryLinkEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<DSLQueryLinkEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<DSLQueryLinkEntry> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static DSLQueryLinkEntry update(
		DSLQueryLinkEntry dslQueryLinkEntry) {

		return getPersistence().update(dslQueryLinkEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static DSLQueryLinkEntry update(
		DSLQueryLinkEntry dslQueryLinkEntry, ServiceContext serviceContext) {

		return getPersistence().update(dslQueryLinkEntry, serviceContext);
	}

	/**
	 * Caches the dsl query link entry in the entity cache if it is enabled.
	 *
	 * @param dslQueryLinkEntry the dsl query link entry
	 */
	public static void cacheResult(DSLQueryLinkEntry dslQueryLinkEntry) {
		getPersistence().cacheResult(dslQueryLinkEntry);
	}

	/**
	 * Caches the dsl query link entries in the entity cache if it is enabled.
	 *
	 * @param dslQueryLinkEntries the dsl query link entries
	 */
	public static void cacheResult(
		List<DSLQueryLinkEntry> dslQueryLinkEntries) {

		getPersistence().cacheResult(dslQueryLinkEntries);
	}

	/**
	 * Creates a new dsl query link entry with the primary key. Does not add the dsl query link entry to the database.
	 *
	 * @param dslQueryLinkEntryId the primary key for the new dsl query link entry
	 * @return the new dsl query link entry
	 */
	public static DSLQueryLinkEntry create(long dslQueryLinkEntryId) {
		return getPersistence().create(dslQueryLinkEntryId);
	}

	/**
	 * Removes the dsl query link entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param dslQueryLinkEntryId the primary key of the dsl query link entry
	 * @return the dsl query link entry that was removed
	 * @throws NoSuchDSLQueryLinkEntryException if a dsl query link entry with the primary key could not be found
	 */
	public static DSLQueryLinkEntry remove(long dslQueryLinkEntryId)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchDSLQueryLinkEntryException {

		return getPersistence().remove(dslQueryLinkEntryId);
	}

	public static DSLQueryLinkEntry updateImpl(
		DSLQueryLinkEntry dslQueryLinkEntry) {

		return getPersistence().updateImpl(dslQueryLinkEntry);
	}

	/**
	 * Returns the dsl query link entry with the primary key or throws a <code>NoSuchDSLQueryLinkEntryException</code> if it could not be found.
	 *
	 * @param dslQueryLinkEntryId the primary key of the dsl query link entry
	 * @return the dsl query link entry
	 * @throws NoSuchDSLQueryLinkEntryException if a dsl query link entry with the primary key could not be found
	 */
	public static DSLQueryLinkEntry findByPrimaryKey(long dslQueryLinkEntryId)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchDSLQueryLinkEntryException {

		return getPersistence().findByPrimaryKey(dslQueryLinkEntryId);
	}

	/**
	 * Returns the dsl query link entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param dslQueryLinkEntryId the primary key of the dsl query link entry
	 * @return the dsl query link entry, or <code>null</code> if a dsl query link entry with the primary key could not be found
	 */
	public static DSLQueryLinkEntry fetchByPrimaryKey(
		long dslQueryLinkEntryId) {

		return getPersistence().fetchByPrimaryKey(dslQueryLinkEntryId);
	}

	/**
	 * Returns all the dsl query link entries.
	 *
	 * @return the dsl query link entries
	 */
	public static List<DSLQueryLinkEntry> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the dsl query link entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DSLQueryLinkEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of dsl query link entries
	 * @param end the upper bound of the range of dsl query link entries (not inclusive)
	 * @return the range of dsl query link entries
	 */
	public static List<DSLQueryLinkEntry> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the dsl query link entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DSLQueryLinkEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of dsl query link entries
	 * @param end the upper bound of the range of dsl query link entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of dsl query link entries
	 */
	public static List<DSLQueryLinkEntry> findAll(
		int start, int end,
		OrderByComparator<DSLQueryLinkEntry> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the dsl query link entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DSLQueryLinkEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of dsl query link entries
	 * @param end the upper bound of the range of dsl query link entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of dsl query link entries
	 */
	public static List<DSLQueryLinkEntry> findAll(
		int start, int end,
		OrderByComparator<DSLQueryLinkEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the dsl query link entries from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of dsl query link entries.
	 *
	 * @return the number of dsl query link entries
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static DSLQueryLinkEntryPersistence getPersistence() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker
		<DSLQueryLinkEntryPersistence, DSLQueryLinkEntryPersistence>
			_serviceTracker;

	static {
		Bundle bundle = FrameworkUtil.getBundle(
			DSLQueryLinkEntryPersistence.class);

		ServiceTracker
			<DSLQueryLinkEntryPersistence, DSLQueryLinkEntryPersistence>
				serviceTracker =
					new ServiceTracker
						<DSLQueryLinkEntryPersistence,
						 DSLQueryLinkEntryPersistence>(
							 bundle.getBundleContext(),
							 DSLQueryLinkEntryPersistence.class, null);

		serviceTracker.open();

		_serviceTracker = serviceTracker;
	}

}