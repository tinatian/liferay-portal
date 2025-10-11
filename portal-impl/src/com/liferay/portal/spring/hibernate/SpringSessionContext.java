package com.liferay.portal.spring.hibernate;

import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.context.spi.CurrentSessionContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class SpringSessionContext implements CurrentSessionContext {
	private final SessionFactoryImplementor sessionFactory;

	public SpringSessionContext(SessionFactoryImplementor sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public Session currentSession() throws HibernateException {
		Object value = TransactionSynchronizationManager.getResource(this.sessionFactory);
		if (value instanceof Session session) {
			return session;
		} else if (value instanceof SessionHolder sessionHolder) {
			Session session = sessionHolder.getSession();
			if (!sessionHolder.isSynchronizedWithTransaction() && TransactionSynchronizationManager.isSynchronizationActive()) {
				TransactionSynchronizationManager.registerSynchronization(new SpringSessionSynchronization(sessionHolder, this.sessionFactory, false));
				sessionHolder.setSynchronizedWithTransaction(true);
				FlushMode flushMode = session.getHibernateFlushMode();
				if (flushMode.equals(FlushMode.MANUAL) && !TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
					session.setHibernateFlushMode(FlushMode.AUTO);
					sessionHolder.setPreviousFlushMode(flushMode);
				}
			}

			return session;
		}
		else {
			if (TransactionSynchronizationManager.isSynchronizationActive()) {
				Session session = this.sessionFactory.openSession();
				if (TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
					session.setHibernateFlushMode(FlushMode.MANUAL);
				}

				SessionHolder sessionHolder = new SessionHolder(session);
				TransactionSynchronizationManager.registerSynchronization(new SpringSessionSynchronization(sessionHolder, this.sessionFactory, true));
				TransactionSynchronizationManager.bindResource(this.sessionFactory, sessionHolder);
				sessionHolder.setSynchronizedWithTransaction(true);
				return session;
			} else {
				throw new HibernateException("Could not obtain transaction-synchronized Session for current thread");
			}
		}
	}
}

