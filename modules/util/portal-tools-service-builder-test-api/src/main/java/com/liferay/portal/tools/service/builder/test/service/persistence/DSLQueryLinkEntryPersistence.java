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

import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchDSLQueryLinkEntryException;
import com.liferay.portal.tools.service.builder.test.model.DSLQueryLinkEntry;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the dsl query link entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DSLQueryLinkEntryUtil
 * @generated
 */
@ProviderType
public interface DSLQueryLinkEntryPersistence
	extends BasePersistence<DSLQueryLinkEntry> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link DSLQueryLinkEntryUtil} to access the dsl query link entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Caches the dsl query link entry in the entity cache if it is enabled.
	 *
	 * @param dslQueryLinkEntry the dsl query link entry
	 */
	public void cacheResult(DSLQueryLinkEntry dslQueryLinkEntry);

	/**
	 * Caches the dsl query link entries in the entity cache if it is enabled.
	 *
	 * @param dslQueryLinkEntries the dsl query link entries
	 */
	public void cacheResult(
		java.util.List<DSLQueryLinkEntry> dslQueryLinkEntries);

	/**
	 * Creates a new dsl query link entry with the primary key. Does not add the dsl query link entry to the database.
	 *
	 * @param dslQueryLinkEntryId the primary key for the new dsl query link entry
	 * @return the new dsl query link entry
	 */
	public DSLQueryLinkEntry create(long dslQueryLinkEntryId);

	/**
	 * Removes the dsl query link entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param dslQueryLinkEntryId the primary key of the dsl query link entry
	 * @return the dsl query link entry that was removed
	 * @throws NoSuchDSLQueryLinkEntryException if a dsl query link entry with the primary key could not be found
	 */
	public DSLQueryLinkEntry remove(long dslQueryLinkEntryId)
		throws NoSuchDSLQueryLinkEntryException;

	public DSLQueryLinkEntry updateImpl(DSLQueryLinkEntry dslQueryLinkEntry);

	/**
	 * Returns the dsl query link entry with the primary key or throws a <code>NoSuchDSLQueryLinkEntryException</code> if it could not be found.
	 *
	 * @param dslQueryLinkEntryId the primary key of the dsl query link entry
	 * @return the dsl query link entry
	 * @throws NoSuchDSLQueryLinkEntryException if a dsl query link entry with the primary key could not be found
	 */
	public DSLQueryLinkEntry findByPrimaryKey(long dslQueryLinkEntryId)
		throws NoSuchDSLQueryLinkEntryException;

	/**
	 * Returns the dsl query link entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param dslQueryLinkEntryId the primary key of the dsl query link entry
	 * @return the dsl query link entry, or <code>null</code> if a dsl query link entry with the primary key could not be found
	 */
	public DSLQueryLinkEntry fetchByPrimaryKey(long dslQueryLinkEntryId);

	/**
	 * Returns all the dsl query link entries.
	 *
	 * @return the dsl query link entries
	 */
	public java.util.List<DSLQueryLinkEntry> findAll();

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
	public java.util.List<DSLQueryLinkEntry> findAll(int start, int end);

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
	public java.util.List<DSLQueryLinkEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DSLQueryLinkEntry>
			orderByComparator);

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
	public java.util.List<DSLQueryLinkEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DSLQueryLinkEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the dsl query link entries from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of dsl query link entries.
	 *
	 * @return the number of dsl query link entries
	 */
	public int countAll();

}