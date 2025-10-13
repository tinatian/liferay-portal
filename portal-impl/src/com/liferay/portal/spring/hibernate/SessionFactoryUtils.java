/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.hibernate;

import com.liferay.portal.spring.hibernate.exception.HibernateJdbcException;
import com.liferay.portal.spring.hibernate.exception.HibernateObjectRetrievalFailureException;
import com.liferay.portal.spring.hibernate.exception.HibernateOptimisticLockingFailureException;
import com.liferay.portal.spring.hibernate.exception.HibernateQueryException;
import com.liferay.portal.spring.hibernate.exception.HibernateSystemException;

import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.NonUniqueResultException;
import org.hibernate.ObjectDeletedException;
import org.hibernate.PersistentObjectException;
import org.hibernate.PessimisticLockException;
import org.hibernate.PropertyValueException;
import org.hibernate.QueryException;
import org.hibernate.QueryTimeoutException;
import org.hibernate.StaleObjectStateException;
import org.hibernate.StaleStateException;
import org.hibernate.TransientObjectException;
import org.hibernate.UnresolvableObjectException;
import org.hibernate.WrongClassException;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.hibernate.dialect.lock.PessimisticEntityLockException;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;
import org.hibernate.exception.JDBCConnectionException;
import org.hibernate.exception.LockAcquisitionException;
import org.hibernate.exception.SQLGrammarException;

import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.dao.PessimisticLockingFailureException;

public class SessionFactoryUtils {

	public static DataAccessException convertHibernateAccessException(
		HibernateException ex) {

		if (ex instanceof JDBCConnectionException) {
			return new DataAccessResourceFailureException(ex.getMessage(), ex);
		}
		else if (ex instanceof SQLGrammarException) {
			SQLGrammarException hibJdbcEx = (SQLGrammarException)ex;

			return new InvalidDataAccessResourceUsageException(
				ex.getMessage() + "; SQL [" + hibJdbcEx.getSQL() + "]", ex);
		}
		else if (ex instanceof QueryTimeoutException) {
			QueryTimeoutException hibJdbcEx = (QueryTimeoutException)ex;

			return new org.springframework.dao.QueryTimeoutException(
				ex.getMessage() + "; SQL [" + hibJdbcEx.getSQL() + "]", ex);
		}
		else if (ex instanceof LockAcquisitionException) {
			LockAcquisitionException hibJdbcEx = (LockAcquisitionException)ex;

			return new CannotAcquireLockException(
				ex.getMessage() + "; SQL [" + hibJdbcEx.getSQL() + "]", ex);
		}
		else if (ex instanceof PessimisticLockException) {
			PessimisticLockException hibJdbcEx = (PessimisticLockException)ex;

			return new PessimisticLockingFailureException(
				ex.getMessage() + "; SQL [" + hibJdbcEx.getSQL() + "]", ex);
		}
		else if (ex instanceof ConstraintViolationException) {
			ConstraintViolationException hibJdbcEx =
				(ConstraintViolationException)ex;
			String var10002 = ex.getMessage();

			return new DataIntegrityViolationException(
				var10002 + "; SQL [" + hibJdbcEx.getSQL() + "]; constraint [" +
					hibJdbcEx.getConstraintName() + "]",
				ex);
		}
		else if (ex instanceof DataException) {
			DataException hibJdbcEx = (DataException)ex;

			return new DataIntegrityViolationException(
				ex.getMessage() + "; SQL [" + hibJdbcEx.getSQL() + "]", ex);
		}
		else if (ex instanceof JDBCException) {
			JDBCException hibJdbcEx = (JDBCException)ex;

			return new HibernateJdbcException(hibJdbcEx);
		}
		else if (ex instanceof QueryException) {
			QueryException queryException = (QueryException)ex;

			return new HibernateQueryException(queryException);
		}
		else if (ex instanceof NonUniqueResultException) {
			return new IncorrectResultSizeDataAccessException(
				ex.getMessage(), 1, ex);
		}
		else if (ex instanceof NonUniqueObjectException) {
			return new DuplicateKeyException(ex.getMessage(), ex);
		}
		else if (ex instanceof PropertyValueException) {
			return new DataIntegrityViolationException(ex.getMessage(), ex);
		}
		else if (ex instanceof PersistentObjectException) {
			return new InvalidDataAccessApiUsageException(ex.getMessage(), ex);
		}
		else if (ex instanceof TransientObjectException) {
			return new InvalidDataAccessApiUsageException(ex.getMessage(), ex);
		}
		else if (ex instanceof ObjectDeletedException) {
			return new InvalidDataAccessApiUsageException(ex.getMessage(), ex);
		}
		else if (ex instanceof UnresolvableObjectException) {
			UnresolvableObjectException unresolvableObjectException =
				(UnresolvableObjectException)ex;

			return new HibernateObjectRetrievalFailureException(
				unresolvableObjectException);
		}
		else if (ex instanceof WrongClassException) {
			WrongClassException wrongClassException = (WrongClassException)ex;

			return new HibernateObjectRetrievalFailureException(
				wrongClassException);
		}
		else if (ex instanceof StaleObjectStateException) {
			StaleObjectStateException staleObjectStateException =
				(StaleObjectStateException)ex;

			return new HibernateOptimisticLockingFailureException(
				staleObjectStateException);
		}
		else if (ex instanceof StaleStateException) {
			StaleStateException staleStateException = (StaleStateException)ex;

			return new HibernateOptimisticLockingFailureException(
				staleStateException);
		}
		else if (ex instanceof OptimisticEntityLockException) {
			OptimisticEntityLockException optimisticEntityLockException =
				(OptimisticEntityLockException)ex;

			return new HibernateOptimisticLockingFailureException(
				optimisticEntityLockException);
		}
		else if (ex instanceof PessimisticEntityLockException) {
			return (DataAccessException)
				(ex.getCause() instanceof LockAcquisitionException ?
					new CannotAcquireLockException(
						ex.getMessage(), ex.getCause()) :
							new PessimisticLockingFailureException(
								ex.getMessage(), ex));
		}

		return new HibernateSystemException(ex);
	}

}