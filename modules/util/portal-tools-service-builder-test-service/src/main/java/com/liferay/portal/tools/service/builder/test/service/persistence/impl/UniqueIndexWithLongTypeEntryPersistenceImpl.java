/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchUniqueIndexWithLongTypeEntryException;
import com.liferay.portal.tools.service.builder.test.model.UniqueIndexWithLongTypeEntry;
import com.liferay.portal.tools.service.builder.test.model.UniqueIndexWithLongTypeEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.UniqueIndexWithLongTypeEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.UniqueIndexWithLongTypeEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.UniqueIndexWithLongTypeEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.UniqueIndexWithLongTypeEntryUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * The persistence implementation for the unique index with long type entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class UniqueIndexWithLongTypeEntryPersistenceImpl
	extends BasePersistenceImpl<UniqueIndexWithLongTypeEntry>
	implements UniqueIndexWithLongTypeEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>UniqueIndexWithLongTypeEntryUtil</code> to access the unique index with long type entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		UniqueIndexWithLongTypeEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathFetchByName_LongTypeId;

	/**
	 * Returns the unique index with long type entry where name = &#63; and longTypeId = &#63; or throws a <code>NoSuchUniqueIndexWithLongTypeEntryException</code> if it could not be found.
	 *
	 * @param name the name
	 * @param longTypeId the long type ID
	 * @return the matching unique index with long type entry
	 * @throws NoSuchUniqueIndexWithLongTypeEntryException if a matching unique index with long type entry could not be found
	 */
	@Override
	public UniqueIndexWithLongTypeEntry findByName_LongTypeId(
			String name, Long longTypeId)
		throws NoSuchUniqueIndexWithLongTypeEntryException {

		UniqueIndexWithLongTypeEntry uniqueIndexWithLongTypeEntry =
			fetchByName_LongTypeId(name, longTypeId);

		if (uniqueIndexWithLongTypeEntry == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("name=");
			sb.append(name);

			sb.append(", longTypeId=");
			sb.append(longTypeId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchUniqueIndexWithLongTypeEntryException(
				sb.toString());
		}

		return uniqueIndexWithLongTypeEntry;
	}

	/**
	 * Returns the unique index with long type entry where name = &#63; and longTypeId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param name the name
	 * @param longTypeId the long type ID
	 * @return the matching unique index with long type entry, or <code>null</code> if a matching unique index with long type entry could not be found
	 */
	@Override
	public UniqueIndexWithLongTypeEntry fetchByName_LongTypeId(
		String name, Long longTypeId) {

		return fetchByName_LongTypeId(name, longTypeId, true);
	}

	/**
	 * Returns the unique index with long type entry where name = &#63; and longTypeId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param name the name
	 * @param longTypeId the long type ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching unique index with long type entry, or <code>null</code> if a matching unique index with long type entry could not be found
	 */
	@Override
	public UniqueIndexWithLongTypeEntry fetchByName_LongTypeId(
		String name, Long longTypeId, boolean useFinderCache) {

		name = Objects.toString(name, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {name, longTypeId};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByName_LongTypeId, finderArgs, this);
		}

		if (result instanceof UniqueIndexWithLongTypeEntry) {
			UniqueIndexWithLongTypeEntry uniqueIndexWithLongTypeEntry =
				(UniqueIndexWithLongTypeEntry)result;

			if (!Objects.equals(name, uniqueIndexWithLongTypeEntry.getName()) ||
				!Objects.equals(
					longTypeId, uniqueIndexWithLongTypeEntry.getLongTypeId())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_UNIQUEINDEXWITHLONGTYPEENTRY_WHERE);

			boolean bindName = false;

			if (name.isEmpty()) {
				sb.append(_FINDER_COLUMN_NAME_LONGTYPEID_NAME_3);
			}
			else {
				bindName = true;

				sb.append(_FINDER_COLUMN_NAME_LONGTYPEID_NAME_2);
			}

			sb.append(_FINDER_COLUMN_NAME_LONGTYPEID_LONGTYPEID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindName) {
					queryPos.add(name);
				}

				queryPos.add(longTypeId.longValue());

				List<UniqueIndexWithLongTypeEntry> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByName_LongTypeId, finderArgs,
							list);
					}
				}
				else {
					UniqueIndexWithLongTypeEntry uniqueIndexWithLongTypeEntry =
						list.get(0);

					result = uniqueIndexWithLongTypeEntry;

					cacheResult(uniqueIndexWithLongTypeEntry);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		if (result instanceof List<?>) {
			return null;
		}
		else {
			return (UniqueIndexWithLongTypeEntry)result;
		}
	}

	/**
	 * Removes the unique index with long type entry where name = &#63; and longTypeId = &#63; from the database.
	 *
	 * @param name the name
	 * @param longTypeId the long type ID
	 * @return the unique index with long type entry that was removed
	 */
	@Override
	public UniqueIndexWithLongTypeEntry removeByName_LongTypeId(
			String name, Long longTypeId)
		throws NoSuchUniqueIndexWithLongTypeEntryException {

		UniqueIndexWithLongTypeEntry uniqueIndexWithLongTypeEntry =
			findByName_LongTypeId(name, longTypeId);

		return remove(uniqueIndexWithLongTypeEntry);
	}

	/**
	 * Returns the number of unique index with long type entries where name = &#63; and longTypeId = &#63;.
	 *
	 * @param name the name
	 * @param longTypeId the long type ID
	 * @return the number of matching unique index with long type entries
	 */
	@Override
	public int countByName_LongTypeId(String name, Long longTypeId) {
		UniqueIndexWithLongTypeEntry uniqueIndexWithLongTypeEntry =
			fetchByName_LongTypeId(name, longTypeId);

		if (uniqueIndexWithLongTypeEntry == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_NAME_LONGTYPEID_NAME_2 =
		"uniqueIndexWithLongTypeEntry.name = ? AND ";

	private static final String _FINDER_COLUMN_NAME_LONGTYPEID_NAME_3 =
		"(uniqueIndexWithLongTypeEntry.name IS NULL OR uniqueIndexWithLongTypeEntry.name = '') AND ";

	private static final String _FINDER_COLUMN_NAME_LONGTYPEID_LONGTYPEID_2 =
		"uniqueIndexWithLongTypeEntry.longTypeId = ?";

	public UniqueIndexWithLongTypeEntryPersistenceImpl() {
		setModelClass(UniqueIndexWithLongTypeEntry.class);

		setModelImplClass(UniqueIndexWithLongTypeEntryImpl.class);
		setModelPKClass(long.class);

		setTable(UniqueIndexWithLongTypeEntryTable.INSTANCE);
	}

	/**
	 * Caches the unique index with long type entry in the entity cache if it is enabled.
	 *
	 * @param uniqueIndexWithLongTypeEntry the unique index with long type entry
	 */
	@Override
	public void cacheResult(
		UniqueIndexWithLongTypeEntry uniqueIndexWithLongTypeEntry) {

		entityCache.putResult(
			UniqueIndexWithLongTypeEntryImpl.class,
			uniqueIndexWithLongTypeEntry.getPrimaryKey(),
			uniqueIndexWithLongTypeEntry);

		finderCache.putResult(
			_finderPathFetchByName_LongTypeId,
			new Object[] {
				uniqueIndexWithLongTypeEntry.getName(),
				uniqueIndexWithLongTypeEntry.getLongTypeId()
			},
			uniqueIndexWithLongTypeEntry);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the unique index with long type entries in the entity cache if it is enabled.
	 *
	 * @param uniqueIndexWithLongTypeEntries the unique index with long type entries
	 */
	@Override
	public void cacheResult(
		List<UniqueIndexWithLongTypeEntry> uniqueIndexWithLongTypeEntries) {

		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (uniqueIndexWithLongTypeEntries.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (UniqueIndexWithLongTypeEntry uniqueIndexWithLongTypeEntry :
				uniqueIndexWithLongTypeEntries) {

			if (entityCache.getResult(
					UniqueIndexWithLongTypeEntryImpl.class,
					uniqueIndexWithLongTypeEntry.getPrimaryKey()) == null) {

				cacheResult(uniqueIndexWithLongTypeEntry);
			}
		}
	}

	/**
	 * Clears the cache for all unique index with long type entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(UniqueIndexWithLongTypeEntryImpl.class);

		finderCache.clearCache(UniqueIndexWithLongTypeEntryImpl.class);
	}

	/**
	 * Clears the cache for the unique index with long type entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(
		UniqueIndexWithLongTypeEntry uniqueIndexWithLongTypeEntry) {

		entityCache.removeResult(
			UniqueIndexWithLongTypeEntryImpl.class,
			uniqueIndexWithLongTypeEntry);
	}

	@Override
	public void clearCache(
		List<UniqueIndexWithLongTypeEntry> uniqueIndexWithLongTypeEntries) {

		for (UniqueIndexWithLongTypeEntry uniqueIndexWithLongTypeEntry :
				uniqueIndexWithLongTypeEntries) {

			entityCache.removeResult(
				UniqueIndexWithLongTypeEntryImpl.class,
				uniqueIndexWithLongTypeEntry);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(UniqueIndexWithLongTypeEntryImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(
				UniqueIndexWithLongTypeEntryImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		UniqueIndexWithLongTypeEntryModelImpl
			uniqueIndexWithLongTypeEntryModelImpl) {

		Object[] args = new Object[] {
			uniqueIndexWithLongTypeEntryModelImpl.getName(),
			uniqueIndexWithLongTypeEntryModelImpl.getLongTypeId()
		};

		finderCache.putResult(
			_finderPathFetchByName_LongTypeId, args,
			uniqueIndexWithLongTypeEntryModelImpl);
	}

	/**
	 * Creates a new unique index with long type entry with the primary key. Does not add the unique index with long type entry to the database.
	 *
	 * @param uniqueIndexWithLongTypeEntryId the primary key for the new unique index with long type entry
	 * @return the new unique index with long type entry
	 */
	@Override
	public UniqueIndexWithLongTypeEntry create(
		long uniqueIndexWithLongTypeEntryId) {

		UniqueIndexWithLongTypeEntry uniqueIndexWithLongTypeEntry =
			new UniqueIndexWithLongTypeEntryImpl();

		uniqueIndexWithLongTypeEntry.setNew(true);
		uniqueIndexWithLongTypeEntry.setPrimaryKey(
			uniqueIndexWithLongTypeEntryId);

		return uniqueIndexWithLongTypeEntry;
	}

	/**
	 * Removes the unique index with long type entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param uniqueIndexWithLongTypeEntryId the primary key of the unique index with long type entry
	 * @return the unique index with long type entry that was removed
	 * @throws NoSuchUniqueIndexWithLongTypeEntryException if a unique index with long type entry with the primary key could not be found
	 */
	@Override
	public UniqueIndexWithLongTypeEntry remove(
			long uniqueIndexWithLongTypeEntryId)
		throws NoSuchUniqueIndexWithLongTypeEntryException {

		return remove((Serializable)uniqueIndexWithLongTypeEntryId);
	}

	/**
	 * Removes the unique index with long type entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the unique index with long type entry
	 * @return the unique index with long type entry that was removed
	 * @throws NoSuchUniqueIndexWithLongTypeEntryException if a unique index with long type entry with the primary key could not be found
	 */
	@Override
	public UniqueIndexWithLongTypeEntry remove(Serializable primaryKey)
		throws NoSuchUniqueIndexWithLongTypeEntryException {

		Session session = null;

		try {
			session = openSession();

			UniqueIndexWithLongTypeEntry uniqueIndexWithLongTypeEntry =
				(UniqueIndexWithLongTypeEntry)session.get(
					UniqueIndexWithLongTypeEntryImpl.class, primaryKey);

			if (uniqueIndexWithLongTypeEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchUniqueIndexWithLongTypeEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(uniqueIndexWithLongTypeEntry);
		}
		catch (NoSuchUniqueIndexWithLongTypeEntryException
					noSuchEntityException) {

			throw noSuchEntityException;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	protected UniqueIndexWithLongTypeEntry removeImpl(
		UniqueIndexWithLongTypeEntry uniqueIndexWithLongTypeEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(uniqueIndexWithLongTypeEntry)) {
				uniqueIndexWithLongTypeEntry =
					(UniqueIndexWithLongTypeEntry)session.get(
						UniqueIndexWithLongTypeEntryImpl.class,
						uniqueIndexWithLongTypeEntry.getPrimaryKeyObj());
			}

			if (uniqueIndexWithLongTypeEntry != null) {
				session.delete(uniqueIndexWithLongTypeEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (uniqueIndexWithLongTypeEntry != null) {
			clearCache(uniqueIndexWithLongTypeEntry);
		}

		return uniqueIndexWithLongTypeEntry;
	}

	@Override
	public UniqueIndexWithLongTypeEntry updateImpl(
		UniqueIndexWithLongTypeEntry uniqueIndexWithLongTypeEntry) {

		boolean isNew = uniqueIndexWithLongTypeEntry.isNew();

		if (!(uniqueIndexWithLongTypeEntry instanceof
				UniqueIndexWithLongTypeEntryModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					uniqueIndexWithLongTypeEntry.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					uniqueIndexWithLongTypeEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in uniqueIndexWithLongTypeEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom UniqueIndexWithLongTypeEntry implementation " +
					uniqueIndexWithLongTypeEntry.getClass());
		}

		UniqueIndexWithLongTypeEntryModelImpl
			uniqueIndexWithLongTypeEntryModelImpl =
				(UniqueIndexWithLongTypeEntryModelImpl)
					uniqueIndexWithLongTypeEntry;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(uniqueIndexWithLongTypeEntry);
			}
			else {
				uniqueIndexWithLongTypeEntry =
					(UniqueIndexWithLongTypeEntry)session.merge(
						uniqueIndexWithLongTypeEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			UniqueIndexWithLongTypeEntryImpl.class,
			uniqueIndexWithLongTypeEntryModelImpl, false, true);

		cacheUniqueFindersCache(uniqueIndexWithLongTypeEntryModelImpl);

		if (isNew) {
			uniqueIndexWithLongTypeEntry.setNew(false);
		}

		uniqueIndexWithLongTypeEntry.resetOriginalValues();

		return uniqueIndexWithLongTypeEntry;
	}

	/**
	 * Returns the unique index with long type entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the unique index with long type entry
	 * @return the unique index with long type entry
	 * @throws NoSuchUniqueIndexWithLongTypeEntryException if a unique index with long type entry with the primary key could not be found
	 */
	@Override
	public UniqueIndexWithLongTypeEntry findByPrimaryKey(
			Serializable primaryKey)
		throws NoSuchUniqueIndexWithLongTypeEntryException {

		UniqueIndexWithLongTypeEntry uniqueIndexWithLongTypeEntry =
			fetchByPrimaryKey(primaryKey);

		if (uniqueIndexWithLongTypeEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchUniqueIndexWithLongTypeEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return uniqueIndexWithLongTypeEntry;
	}

	/**
	 * Returns the unique index with long type entry with the primary key or throws a <code>NoSuchUniqueIndexWithLongTypeEntryException</code> if it could not be found.
	 *
	 * @param uniqueIndexWithLongTypeEntryId the primary key of the unique index with long type entry
	 * @return the unique index with long type entry
	 * @throws NoSuchUniqueIndexWithLongTypeEntryException if a unique index with long type entry with the primary key could not be found
	 */
	@Override
	public UniqueIndexWithLongTypeEntry findByPrimaryKey(
			long uniqueIndexWithLongTypeEntryId)
		throws NoSuchUniqueIndexWithLongTypeEntryException {

		return findByPrimaryKey((Serializable)uniqueIndexWithLongTypeEntryId);
	}

	/**
	 * Returns the unique index with long type entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param uniqueIndexWithLongTypeEntryId the primary key of the unique index with long type entry
	 * @return the unique index with long type entry, or <code>null</code> if a unique index with long type entry with the primary key could not be found
	 */
	@Override
	public UniqueIndexWithLongTypeEntry fetchByPrimaryKey(
		long uniqueIndexWithLongTypeEntryId) {

		return fetchByPrimaryKey((Serializable)uniqueIndexWithLongTypeEntryId);
	}

	/**
	 * Returns all the unique index with long type entries.
	 *
	 * @return the unique index with long type entries
	 */
	@Override
	public List<UniqueIndexWithLongTypeEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
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
	@Override
	public List<UniqueIndexWithLongTypeEntry> findAll(int start, int end) {
		return findAll(start, end, null);
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
	@Override
	public List<UniqueIndexWithLongTypeEntry> findAll(
		int start, int end,
		OrderByComparator<UniqueIndexWithLongTypeEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
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
	@Override
	public List<UniqueIndexWithLongTypeEntry> findAll(
		int start, int end,
		OrderByComparator<UniqueIndexWithLongTypeEntry> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindAll;
				finderArgs = FINDER_ARGS_EMPTY;
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindAll;
			finderArgs = new Object[] {start, end, orderByComparator};
		}

		List<UniqueIndexWithLongTypeEntry> list = null;

		if (useFinderCache) {
			list = (List<UniqueIndexWithLongTypeEntry>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_UNIQUEINDEXWITHLONGTYPEENTRY);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_UNIQUEINDEXWITHLONGTYPEENTRY;

				sql = sql.concat(
					UniqueIndexWithLongTypeEntryModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<UniqueIndexWithLongTypeEntry>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(finderPath, finderArgs, list);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Removes all the unique index with long type entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (UniqueIndexWithLongTypeEntry uniqueIndexWithLongTypeEntry :
				findAll()) {

			remove(uniqueIndexWithLongTypeEntry);
		}
	}

	/**
	 * Returns the number of unique index with long type entries.
	 *
	 * @return the number of unique index with long type entries
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(
					_SQL_COUNT_UNIQUEINDEXWITHLONGTYPEENTRY);

				count = (Long)query.uniqueResult();

				finderCache.putResult(
					_finderPathCountAll, FINDER_ARGS_EMPTY, count);
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "uniqueIndexWithLongTypeEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_UNIQUEINDEXWITHLONGTYPEENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return UniqueIndexWithLongTypeEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the unique index with long type entry persistence.
	 */
	public void afterPropertiesSet() {
		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		_finderPathWithPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathWithoutPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathCountAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0], new String[0], false);

		_finderPathFetchByName_LongTypeId = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByName_LongTypeId",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"name", "longTypeId"}, true);

		UniqueIndexWithLongTypeEntryUtil.setPersistence(this);
	}

	public void destroy() {
		UniqueIndexWithLongTypeEntryUtil.setPersistence(null);

		entityCache.removeCache(
			UniqueIndexWithLongTypeEntryImpl.class.getName());
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_UNIQUEINDEXWITHLONGTYPEENTRY =
		"SELECT uniqueIndexWithLongTypeEntry FROM UniqueIndexWithLongTypeEntry uniqueIndexWithLongTypeEntry";

	private static final String _SQL_SELECT_UNIQUEINDEXWITHLONGTYPEENTRY_WHERE =
		"SELECT uniqueIndexWithLongTypeEntry FROM UniqueIndexWithLongTypeEntry uniqueIndexWithLongTypeEntry WHERE ";

	private static final String _SQL_COUNT_UNIQUEINDEXWITHLONGTYPEENTRY =
		"SELECT COUNT(uniqueIndexWithLongTypeEntry) FROM UniqueIndexWithLongTypeEntry uniqueIndexWithLongTypeEntry";

	private static final String _SQL_COUNT_UNIQUEINDEXWITHLONGTYPEENTRY_WHERE =
		"SELECT COUNT(uniqueIndexWithLongTypeEntry) FROM UniqueIndexWithLongTypeEntry uniqueIndexWithLongTypeEntry WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS =
		"uniqueIndexWithLongTypeEntry.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No UniqueIndexWithLongTypeEntry exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No UniqueIndexWithLongTypeEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		UniqueIndexWithLongTypeEntryPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}