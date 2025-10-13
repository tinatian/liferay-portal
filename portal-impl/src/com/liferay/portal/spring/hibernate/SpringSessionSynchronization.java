/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.hibernate;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.persistence.PersistenceException;

import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionImplementor;

import org.springframework.core.Ordered;
import org.springframework.dao.DataAccessException;
import org.springframework.lang.Nullable;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class SpringSessionSynchronization
	implements Ordered, TransactionSynchronization {

	public SpringSessionSynchronization(
		SessionHolder sessionHolder, SessionFactory sessionFactory,
		boolean newSession) {

		this.sessionHolder = sessionHolder;
		this.sessionFactory = sessionFactory;
		this.newSession = newSession;

		this.holderActive = true;
	}

	public void afterCommit() {
	}

	public void afterCompletion(int status) {
		try {
			if (status != 0) {
				this.sessionHolder.getSession(
				).clear();
			}
		}
		finally {
			this.sessionHolder.setSynchronizedWithTransaction(false);

			if (this.newSession) {
				_closeSession(this.sessionHolder.getSession());
			}
		}
	}

	public void beforeCommit(boolean readOnly) throws DataAccessException {
		if (!readOnly) {
			Session session = this.getCurrentSession();

			if (!FlushMode.MANUAL.equals(session.getHibernateFlushMode())) {
				_flush(this.getCurrentSession(), true);
			}
		}
	}

	public void beforeCompletion() {
		try {
			Session session = this.sessionHolder.getSession();

			if (this.sessionHolder.getPreviousFlushMode() != null) {
				session.setHibernateFlushMode(
					this.sessionHolder.getPreviousFlushMode());
			}

			if (session instanceof SessionImplementor sessionImpl) {
				sessionImpl.getJdbcCoordinator(
				).getLogicalConnection(
				).manualDisconnect();
			}
		}
		finally {
			if (this.newSession) {
				TransactionSynchronizationManager.unbindResource(
					this.sessionFactory);
				this.holderActive = false;
			}
		}
	}

	public void flush() {
		_flush(this.getCurrentSession(), false);
	}

	public int getOrder() {
		return 900;
	}

	public void resume() {
		if (this.holderActive) {
			TransactionSynchronizationManager.bindResource(
				this.sessionFactory, this.sessionHolder);
		}
	}

	public void suspend() {
		if (this.holderActive) {
			TransactionSynchronizationManager.unbindResource(
				this.sessionFactory);
		}
	}

	private void _closeSession(@Nullable Session session) {
		if (session != null) {
			try {
				if (session.isOpen()) {
					session.close();
				}
			}
			catch (Throwable var2) {
				_log.error("Failed to release Hibernate Session", var2);
			}
		}
	}

	private void _flush(Session session, boolean synch)
		throws DataAccessException {

		if (synch) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Flushing Hibernate Session on transaction synchronization");
			}
		}
		else {
			if (_log.isDebugEnabled()) {
				_log.debug("Flushing Hibernate Session on explicit request");
			}
		}

		try {
			session.flush();
		}
		catch (HibernateException hibernateException) {
			throw SessionFactoryUtils.convertHibernateAccessException(
				hibernateException);
		}
		catch (PersistenceException persistenceException) {
			Throwable throwable = persistenceException.getCause();

			if (throwable instanceof HibernateException hibernateException) {
				throw SessionFactoryUtils.convertHibernateAccessException(
					hibernateException);
			}

			throw persistenceException;
		}
	}

	private Session getCurrentSession() {
		return this.sessionHolder.getSession();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SpringSessionSynchronization.class);

	private boolean holderActive;
	private final boolean newSession;
	private final SessionFactory sessionFactory;
	private final SessionHolder sessionHolder;

}