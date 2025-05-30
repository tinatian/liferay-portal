/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.tools.service.builder.test.model.UniqueIndexWithLongTypeEntry;

import java.io.Serializable;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for UniqueIndexWithLongTypeEntry. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see UniqueIndexWithLongTypeEntryLocalServiceUtil
 * @generated
 */
@OSGiBeanProperties(
	property = {
		"model.class.name=com.liferay.portal.tools.service.builder.test.model.UniqueIndexWithLongTypeEntry"
	}
)
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface UniqueIndexWithLongTypeEntryLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portal.tools.service.builder.test.service.impl.UniqueIndexWithLongTypeEntryLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the unique index with long type entry local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link UniqueIndexWithLongTypeEntryLocalServiceUtil} if injection and service tracking are not available.
	 */

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
	@Indexable(type = IndexableType.REINDEX)
	public UniqueIndexWithLongTypeEntry addUniqueIndexWithLongTypeEntry(
		UniqueIndexWithLongTypeEntry uniqueIndexWithLongTypeEntry);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Creates a new unique index with long type entry with the primary key. Does not add the unique index with long type entry to the database.
	 *
	 * @param uniqueIndexWithLongTypeEntryId the primary key for the new unique index with long type entry
	 * @return the new unique index with long type entry
	 */
	@Transactional(enabled = false)
	public UniqueIndexWithLongTypeEntry createUniqueIndexWithLongTypeEntry(
		long uniqueIndexWithLongTypeEntryId);

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

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
	@Indexable(type = IndexableType.DELETE)
	public UniqueIndexWithLongTypeEntry deleteUniqueIndexWithLongTypeEntry(
			long uniqueIndexWithLongTypeEntryId)
		throws PortalException;

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
	@Indexable(type = IndexableType.DELETE)
	public UniqueIndexWithLongTypeEntry deleteUniqueIndexWithLongTypeEntry(
		UniqueIndexWithLongTypeEntry uniqueIndexWithLongTypeEntry);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> T dslQuery(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int dslQueryCount(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DynamicQuery dynamicQuery();

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(DynamicQuery dynamicQuery);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(
		DynamicQuery dynamicQuery, Projection projection);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public UniqueIndexWithLongTypeEntry fetchUniqueIndexWithLongTypeEntry(
		long uniqueIndexWithLongTypeEntryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<UniqueIndexWithLongTypeEntry> getUniqueIndexWithLongTypeEntries(
		int start, int end);

	/**
	 * Returns the number of unique index with long type entries.
	 *
	 * @return the number of unique index with long type entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getUniqueIndexWithLongTypeEntriesCount();

	/**
	 * Returns the unique index with long type entry with the primary key.
	 *
	 * @param uniqueIndexWithLongTypeEntryId the primary key of the unique index with long type entry
	 * @return the unique index with long type entry
	 * @throws PortalException if a unique index with long type entry with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public UniqueIndexWithLongTypeEntry getUniqueIndexWithLongTypeEntry(
			long uniqueIndexWithLongTypeEntryId)
		throws PortalException;

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
	@Indexable(type = IndexableType.REINDEX)
	public UniqueIndexWithLongTypeEntry updateUniqueIndexWithLongTypeEntry(
		UniqueIndexWithLongTypeEntry uniqueIndexWithLongTypeEntry);

}