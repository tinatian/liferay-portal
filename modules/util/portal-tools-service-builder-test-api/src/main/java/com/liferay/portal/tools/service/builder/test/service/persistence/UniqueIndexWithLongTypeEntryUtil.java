/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.tools.service.builder.test.model.UniqueIndexWithLongTypeEntry;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the unique index with long type entry service. This utility wraps <code>com.liferay.portal.tools.service.builder.test.service.persistence.impl.UniqueIndexWithLongTypeEntryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see UniqueIndexWithLongTypeEntryPersistence
 * @generated
 */
public class UniqueIndexWithLongTypeEntryUtil {

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
	public static void clearCache(
		UniqueIndexWithLongTypeEntry uniqueIndexWithLongTypeEntry) {

		getPersistence().clearCache(uniqueIndexWithLongTypeEntry);
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
	public static Map<Serializable, UniqueIndexWithLongTypeEntry>
		fetchByPrimaryKeys(Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<UniqueIndexWithLongTypeEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<UniqueIndexWithLongTypeEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<UniqueIndexWithLongTypeEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<UniqueIndexWithLongTypeEntry> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static UniqueIndexWithLongTypeEntry update(
		UniqueIndexWithLongTypeEntry uniqueIndexWithLongTypeEntry) {

		return getPersistence().update(uniqueIndexWithLongTypeEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static UniqueIndexWithLongTypeEntry update(
		UniqueIndexWithLongTypeEntry uniqueIndexWithLongTypeEntry,
		ServiceContext serviceContext) {

		return getPersistence().update(
			uniqueIndexWithLongTypeEntry, serviceContext);
	}

	/**
	 * Returns the unique index with long type entry where name = &#63; and longTypeId = &#63; or throws a <code>NoSuchUniqueIndexWithLongTypeEntryException</code> if it could not be found.
	 *
	 * @param name the name
	 * @param longTypeId the long type ID
	 * @return the matching unique index with long type entry
	 * @throws NoSuchUniqueIndexWithLongTypeEntryException if a matching unique index with long type entry could not be found
	 */
	public static UniqueIndexWithLongTypeEntry findByName_LongTypeId(
			String name, Long longTypeId)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchUniqueIndexWithLongTypeEntryException {

		return getPersistence().findByName_LongTypeId(name, longTypeId);
	}

	/**
	 * Returns the unique index with long type entry where name = &#63; and longTypeId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param name the name
	 * @param longTypeId the long type ID
	 * @return the matching unique index with long type entry, or <code>null</code> if a matching unique index with long type entry could not be found
	 */
	public static UniqueIndexWithLongTypeEntry fetchByName_LongTypeId(
		String name, Long longTypeId) {

		return getPersistence().fetchByName_LongTypeId(name, longTypeId);
	}

	/**
	 * Returns the unique index with long type entry where name = &#63; and longTypeId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param name the name
	 * @param longTypeId the long type ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching unique index with long type entry, or <code>null</code> if a matching unique index with long type entry could not be found
	 */
	public static UniqueIndexWithLongTypeEntry fetchByName_LongTypeId(
		String name, Long longTypeId, boolean useFinderCache) {

		return getPersistence().fetchByName_LongTypeId(
			name, longTypeId, useFinderCache);
	}

	/**
	 * Removes the unique index with long type entry where name = &#63; and longTypeId = &#63; from the database.
	 *
	 * @param name the name
	 * @param longTypeId the long type ID
	 * @return the unique index with long type entry that was removed
	 */
	public static UniqueIndexWithLongTypeEntry removeByName_LongTypeId(
			String name, Long longTypeId)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchUniqueIndexWithLongTypeEntryException {

		return getPersistence().removeByName_LongTypeId(name, longTypeId);
	}

	/**
	 * Returns the number of unique index with long type entries where name = &#63; and longTypeId = &#63;.
	 *
	 * @param name the name
	 * @param longTypeId the long type ID
	 * @return the number of matching unique index with long type entries
	 */
	public static int countByName_LongTypeId(String name, Long longTypeId) {
		return getPersistence().countByName_LongTypeId(name, longTypeId);
	}

	/**
	 * Caches the unique index with long type entry in the entity cache if it is enabled.
	 *
	 * @param uniqueIndexWithLongTypeEntry the unique index with long type entry
	 */
	public static void cacheResult(
		UniqueIndexWithLongTypeEntry uniqueIndexWithLongTypeEntry) {

		getPersistence().cacheResult(uniqueIndexWithLongTypeEntry);
	}

	/**
	 * Caches the unique index with long type entries in the entity cache if it is enabled.
	 *
	 * @param uniqueIndexWithLongTypeEntries the unique index with long type entries
	 */
	public static void cacheResult(
		List<UniqueIndexWithLongTypeEntry> uniqueIndexWithLongTypeEntries) {

		getPersistence().cacheResult(uniqueIndexWithLongTypeEntries);
	}

	/**
	 * Creates a new unique index with long type entry with the primary key. Does not add the unique index with long type entry to the database.
	 *
	 * @param uniqueIndexWithLongTypeEntryId the primary key for the new unique index with long type entry
	 * @return the new unique index with long type entry
	 */
	public static UniqueIndexWithLongTypeEntry create(
		long uniqueIndexWithLongTypeEntryId) {

		return getPersistence().create(uniqueIndexWithLongTypeEntryId);
	}

	/**
	 * Removes the unique index with long type entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param uniqueIndexWithLongTypeEntryId the primary key of the unique index with long type entry
	 * @return the unique index with long type entry that was removed
	 * @throws NoSuchUniqueIndexWithLongTypeEntryException if a unique index with long type entry with the primary key could not be found
	 */
	public static UniqueIndexWithLongTypeEntry remove(
			long uniqueIndexWithLongTypeEntryId)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchUniqueIndexWithLongTypeEntryException {

		return getPersistence().remove(uniqueIndexWithLongTypeEntryId);
	}

	public static UniqueIndexWithLongTypeEntry updateImpl(
		UniqueIndexWithLongTypeEntry uniqueIndexWithLongTypeEntry) {

		return getPersistence().updateImpl(uniqueIndexWithLongTypeEntry);
	}

	/**
	 * Returns the unique index with long type entry with the primary key or throws a <code>NoSuchUniqueIndexWithLongTypeEntryException</code> if it could not be found.
	 *
	 * @param uniqueIndexWithLongTypeEntryId the primary key of the unique index with long type entry
	 * @return the unique index with long type entry
	 * @throws NoSuchUniqueIndexWithLongTypeEntryException if a unique index with long type entry with the primary key could not be found
	 */
	public static UniqueIndexWithLongTypeEntry findByPrimaryKey(
			long uniqueIndexWithLongTypeEntryId)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchUniqueIndexWithLongTypeEntryException {

		return getPersistence().findByPrimaryKey(
			uniqueIndexWithLongTypeEntryId);
	}

	/**
	 * Returns the unique index with long type entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param uniqueIndexWithLongTypeEntryId the primary key of the unique index with long type entry
	 * @return the unique index with long type entry, or <code>null</code> if a unique index with long type entry with the primary key could not be found
	 */
	public static UniqueIndexWithLongTypeEntry fetchByPrimaryKey(
		long uniqueIndexWithLongTypeEntryId) {

		return getPersistence().fetchByPrimaryKey(
			uniqueIndexWithLongTypeEntryId);
	}

	/**
	 * Returns all the unique index with long type entries.
	 *
	 * @return the unique index with long type entries
	 */
	public static List<UniqueIndexWithLongTypeEntry> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the unique index with long type entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UniqueIndexWithLongTypeEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of unique index with long type entries
	 * @param end the upper bound of the range of unique index with long type entries (not inclusive)
	 * @return the range of unique index with long type entries
	 */
	public static List<UniqueIndexWithLongTypeEntry> findAll(
		int start, int end) {

		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the unique index with long type entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UniqueIndexWithLongTypeEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of unique index with long type entries
	 * @param end the upper bound of the range of unique index with long type entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of unique index with long type entries
	 */
	public static List<UniqueIndexWithLongTypeEntry> findAll(
		int start, int end,
		OrderByComparator<UniqueIndexWithLongTypeEntry> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the unique index with long type entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UniqueIndexWithLongTypeEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of unique index with long type entries
	 * @param end the upper bound of the range of unique index with long type entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of unique index with long type entries
	 */
	public static List<UniqueIndexWithLongTypeEntry> findAll(
		int start, int end,
		OrderByComparator<UniqueIndexWithLongTypeEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the unique index with long type entries from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of unique index with long type entries.
	 *
	 * @return the number of unique index with long type entries
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static UniqueIndexWithLongTypeEntryPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		UniqueIndexWithLongTypeEntryPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile UniqueIndexWithLongTypeEntryPersistence
		_persistence;

}