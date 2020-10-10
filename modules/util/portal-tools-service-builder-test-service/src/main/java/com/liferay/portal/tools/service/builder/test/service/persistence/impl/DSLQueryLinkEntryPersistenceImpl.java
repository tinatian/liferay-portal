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

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchDSLQueryLinkEntryException;
import com.liferay.portal.tools.service.builder.test.model.DSLQueryLinkEntry;
import com.liferay.portal.tools.service.builder.test.model.DSLQueryLinkEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.DSLQueryLinkEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.DSLQueryLinkEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.DSLQueryLinkEntryPersistence;

import java.io.Serializable;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * The persistence implementation for the dsl query link entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class DSLQueryLinkEntryPersistenceImpl
	extends BasePersistenceImpl<DSLQueryLinkEntry>
	implements DSLQueryLinkEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DSLQueryLinkEntryUtil</code> to access the dsl query link entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DSLQueryLinkEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;

	public DSLQueryLinkEntryPersistenceImpl() {
		setModelClass(DSLQueryLinkEntry.class);

		setModelImplClass(DSLQueryLinkEntryImpl.class);
		setModelPKClass(long.class);

		setTable(DSLQueryLinkEntryTable.INSTANCE);
	}

	/**
	 * Caches the dsl query link entry in the entity cache if it is enabled.
	 *
	 * @param dslQueryLinkEntry the dsl query link entry
	 */
	@Override
	public void cacheResult(DSLQueryLinkEntry dslQueryLinkEntry) {
		entityCache.putResult(
			DSLQueryLinkEntryImpl.class, dslQueryLinkEntry.getPrimaryKey(),
			dslQueryLinkEntry);
	}

	/**
	 * Caches the dsl query link entries in the entity cache if it is enabled.
	 *
	 * @param dslQueryLinkEntries the dsl query link entries
	 */
	@Override
	public void cacheResult(List<DSLQueryLinkEntry> dslQueryLinkEntries) {
		for (DSLQueryLinkEntry dslQueryLinkEntry : dslQueryLinkEntries) {
			if (entityCache.getResult(
					DSLQueryLinkEntryImpl.class,
					dslQueryLinkEntry.getPrimaryKey()) == null) {

				cacheResult(dslQueryLinkEntry);
			}
		}
	}

	/**
	 * Clears the cache for all dsl query link entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(DSLQueryLinkEntryImpl.class);

		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the dsl query link entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(DSLQueryLinkEntry dslQueryLinkEntry) {
		entityCache.removeResult(
			DSLQueryLinkEntryImpl.class, dslQueryLinkEntry);
	}

	@Override
	public void clearCache(List<DSLQueryLinkEntry> dslQueryLinkEntries) {
		for (DSLQueryLinkEntry dslQueryLinkEntry : dslQueryLinkEntries) {
			entityCache.removeResult(
				DSLQueryLinkEntryImpl.class, dslQueryLinkEntry);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(DSLQueryLinkEntryImpl.class, primaryKey);
		}
	}

	/**
	 * Creates a new dsl query link entry with the primary key. Does not add the dsl query link entry to the database.
	 *
	 * @param dslQueryLinkEntryId the primary key for the new dsl query link entry
	 * @return the new dsl query link entry
	 */
	@Override
	public DSLQueryLinkEntry create(long dslQueryLinkEntryId) {
		DSLQueryLinkEntry dslQueryLinkEntry = new DSLQueryLinkEntryImpl();

		dslQueryLinkEntry.setNew(true);
		dslQueryLinkEntry.setPrimaryKey(dslQueryLinkEntryId);

		return dslQueryLinkEntry;
	}

	/**
	 * Removes the dsl query link entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param dslQueryLinkEntryId the primary key of the dsl query link entry
	 * @return the dsl query link entry that was removed
	 * @throws NoSuchDSLQueryLinkEntryException if a dsl query link entry with the primary key could not be found
	 */
	@Override
	public DSLQueryLinkEntry remove(long dslQueryLinkEntryId)
		throws NoSuchDSLQueryLinkEntryException {

		return remove((Serializable)dslQueryLinkEntryId);
	}

	/**
	 * Removes the dsl query link entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the dsl query link entry
	 * @return the dsl query link entry that was removed
	 * @throws NoSuchDSLQueryLinkEntryException if a dsl query link entry with the primary key could not be found
	 */
	@Override
	public DSLQueryLinkEntry remove(Serializable primaryKey)
		throws NoSuchDSLQueryLinkEntryException {

		Session session = null;

		try {
			session = openSession();

			DSLQueryLinkEntry dslQueryLinkEntry =
				(DSLQueryLinkEntry)session.get(
					DSLQueryLinkEntryImpl.class, primaryKey);

			if (dslQueryLinkEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchDSLQueryLinkEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(dslQueryLinkEntry);
		}
		catch (NoSuchDSLQueryLinkEntryException noSuchEntityException) {
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
	protected DSLQueryLinkEntry removeImpl(
		DSLQueryLinkEntry dslQueryLinkEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(dslQueryLinkEntry)) {
				dslQueryLinkEntry = (DSLQueryLinkEntry)session.get(
					DSLQueryLinkEntryImpl.class,
					dslQueryLinkEntry.getPrimaryKeyObj());
			}

			if (dslQueryLinkEntry != null) {
				session.delete(dslQueryLinkEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (dslQueryLinkEntry != null) {
			clearCache(dslQueryLinkEntry);
		}

		return dslQueryLinkEntry;
	}

	@Override
	public DSLQueryLinkEntry updateImpl(DSLQueryLinkEntry dslQueryLinkEntry) {
		boolean isNew = dslQueryLinkEntry.isNew();

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(dslQueryLinkEntry);
			}
			else {
				dslQueryLinkEntry = (DSLQueryLinkEntry)session.merge(
					dslQueryLinkEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			DSLQueryLinkEntryImpl.class, dslQueryLinkEntry, false, true);

		if (isNew) {
			dslQueryLinkEntry.setNew(false);
		}

		dslQueryLinkEntry.resetOriginalValues();

		return dslQueryLinkEntry;
	}

	/**
	 * Returns the dsl query link entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the dsl query link entry
	 * @return the dsl query link entry
	 * @throws NoSuchDSLQueryLinkEntryException if a dsl query link entry with the primary key could not be found
	 */
	@Override
	public DSLQueryLinkEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchDSLQueryLinkEntryException {

		DSLQueryLinkEntry dslQueryLinkEntry = fetchByPrimaryKey(primaryKey);

		if (dslQueryLinkEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchDSLQueryLinkEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return dslQueryLinkEntry;
	}

	/**
	 * Returns the dsl query link entry with the primary key or throws a <code>NoSuchDSLQueryLinkEntryException</code> if it could not be found.
	 *
	 * @param dslQueryLinkEntryId the primary key of the dsl query link entry
	 * @return the dsl query link entry
	 * @throws NoSuchDSLQueryLinkEntryException if a dsl query link entry with the primary key could not be found
	 */
	@Override
	public DSLQueryLinkEntry findByPrimaryKey(long dslQueryLinkEntryId)
		throws NoSuchDSLQueryLinkEntryException {

		return findByPrimaryKey((Serializable)dslQueryLinkEntryId);
	}

	/**
	 * Returns the dsl query link entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param dslQueryLinkEntryId the primary key of the dsl query link entry
	 * @return the dsl query link entry, or <code>null</code> if a dsl query link entry with the primary key could not be found
	 */
	@Override
	public DSLQueryLinkEntry fetchByPrimaryKey(long dslQueryLinkEntryId) {
		return fetchByPrimaryKey((Serializable)dslQueryLinkEntryId);
	}

	/**
	 * Returns all the dsl query link entries.
	 *
	 * @return the dsl query link entries
	 */
	@Override
	public List<DSLQueryLinkEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
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
	@Override
	public List<DSLQueryLinkEntry> findAll(int start, int end) {
		return findAll(start, end, null);
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
	@Override
	public List<DSLQueryLinkEntry> findAll(
		int start, int end,
		OrderByComparator<DSLQueryLinkEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
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
	@Override
	public List<DSLQueryLinkEntry> findAll(
		int start, int end,
		OrderByComparator<DSLQueryLinkEntry> orderByComparator,
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

		List<DSLQueryLinkEntry> list = null;

		if (useFinderCache) {
			list = (List<DSLQueryLinkEntry>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_DSLQUERYLINKENTRY);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_DSLQUERYLINKENTRY;

				sql = sql.concat(DSLQueryLinkEntryModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<DSLQueryLinkEntry>)QueryUtil.list(
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
	 * Removes all the dsl query link entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (DSLQueryLinkEntry dslQueryLinkEntry : findAll()) {
			remove(dslQueryLinkEntry);
		}
	}

	/**
	 * Returns the number of dsl query link entries.
	 *
	 * @return the number of dsl query link entries
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_DSLQUERYLINKENTRY);

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
		return "dslQueryLinkEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DSLQUERYLINKENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return DSLQueryLinkEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the dsl query link entry persistence.
	 */
	public void afterPropertiesSet() {
		Bundle bundle = FrameworkUtil.getBundle(
			DSLQueryLinkEntryPersistenceImpl.class);

		_bundleContext = bundle.getBundleContext();

		_argumentsResolverServiceRegistration = _bundleContext.registerService(
			ArgumentsResolver.class,
			new DSLQueryLinkEntryModelArgumentsResolver(),
			MapUtil.singletonDictionary(
				"model.class.name", DSLQueryLinkEntry.class.getName()));

		_finderPathWithPaginationFindAll = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathWithoutPaginationFindAll = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathCountAll = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0], new String[0], false);
	}

	public void destroy() {
		entityCache.removeCache(DSLQueryLinkEntryImpl.class.getName());

		_argumentsResolverServiceRegistration.unregister();

		for (ServiceRegistration<FinderPath> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}
	}

	private BundleContext _bundleContext;

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_DSLQUERYLINKENTRY =
		"SELECT dslQueryLinkEntry FROM DSLQueryLinkEntry dslQueryLinkEntry";

	private static final String _SQL_COUNT_DSLQUERYLINKENTRY =
		"SELECT COUNT(dslQueryLinkEntry) FROM DSLQueryLinkEntry dslQueryLinkEntry";

	private static final String _ORDER_BY_ENTITY_ALIAS = "dslQueryLinkEntry.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No DSLQueryLinkEntry exists with the primary key ";

	private static final Log _log = LogFactoryUtil.getLog(
		DSLQueryLinkEntryPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

	@Override
	protected FinderPath getDSLQueryFinderPath(
		String sql, String[] tableNames, Object[] arguments,
		boolean baseModelResult) {

		return _dslQueryFinderPathMap.computeIfAbsent(
			sql,
			key -> _createFinderPath(
				getDSLQueryCacheName(tableNames), "dslQuery",
				getArgumentTypes(arguments), new String[0], tableNames,
				baseModelResult));
	}

	private FinderPath _createFinderPath(
		String cacheName, String methodName, String[] params,
		String[] columnNames, boolean baseModelResult) {

		return _createFinderPath(
			cacheName, methodName, params, columnNames, new String[0],
			baseModelResult);
	}

	private FinderPath _createFinderPath(
		String cacheName, String methodName, String[] params,
		String[] columnNames, String[] tableNames, boolean baseModelResult) {

		FinderPath finderPath = new FinderPath(
			cacheName, methodName, params, columnNames, baseModelResult);

		if (!cacheName.equals(FINDER_CLASS_NAME_LIST_WITH_PAGINATION)) {
			_serviceRegistrations.add(
				_bundleContext.registerService(
					FinderPath.class, finderPath,
					new HashMapDictionary() {
						{
							put("cache.name", cacheName);
							put("table.names", tableNames);
						}
					}));
		}

		return finderPath;
	}

	private ServiceRegistration<ArgumentsResolver>
		_argumentsResolverServiceRegistration;
	private Set<ServiceRegistration<FinderPath>> _serviceRegistrations =
		new HashSet<>();
	private Map<String, FinderPath> _dslQueryFinderPathMap =
		new ConcurrentHashMap();

	private static class DSLQueryLinkEntryModelArgumentsResolver
		implements ArgumentsResolver {

		@Override
		public Object[] getArguments(
			FinderPath finderPath, BaseModel<?> baseModel, boolean checkColumn,
			boolean original) {

			String[] columnNames = finderPath.getColumnNames();

			if ((columnNames == null) || (columnNames.length == 0)) {
				if (baseModel.isNew()) {
					return FINDER_ARGS_EMPTY;
				}

				return null;
			}

			DSLQueryLinkEntryModelImpl dslQueryLinkEntryModelImpl =
				(DSLQueryLinkEntryModelImpl)baseModel;

			long columnBitmask = dslQueryLinkEntryModelImpl.getColumnBitmask();

			if (!checkColumn || (columnBitmask == 0)) {
				return _getValue(
					dslQueryLinkEntryModelImpl, columnNames, original);
			}

			Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
				finderPath);

			if (finderPathColumnBitmask == null) {
				finderPathColumnBitmask = 0L;

				for (String columnName : columnNames) {
					finderPathColumnBitmask |=
						dslQueryLinkEntryModelImpl.getColumnBitmask(columnName);
				}

				_finderPathColumnBitmasksCache.put(
					finderPath, finderPathColumnBitmask);
			}

			if ((columnBitmask & finderPathColumnBitmask) != 0) {
				return _getValue(
					dslQueryLinkEntryModelImpl, columnNames, original);
			}

			return null;
		}

		private Object[] _getValue(
			DSLQueryLinkEntryModelImpl dslQueryLinkEntryModelImpl,
			String[] columnNames, boolean original) {

			Object[] arguments = new Object[columnNames.length];

			for (int i = 0; i < arguments.length; i++) {
				String columnName = columnNames[i];

				if (original) {
					arguments[i] =
						dslQueryLinkEntryModelImpl.getColumnOriginalValue(
							columnName);
				}
				else {
					arguments[i] = dslQueryLinkEntryModelImpl.getColumnValue(
						columnName);
				}
			}

			return arguments;
		}

		private static Map<FinderPath, Long> _finderPathColumnBitmasksCache =
			new ConcurrentHashMap<>();

	}

}