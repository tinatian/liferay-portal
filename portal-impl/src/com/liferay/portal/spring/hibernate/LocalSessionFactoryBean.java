/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.hibernate;

import com.liferay.portal.spring.hibernate.exception.JpaObjectRetrievalFailureException;
import com.liferay.portal.spring.hibernate.exception.JpaOptimisticLockingFailureException;
import com.liferay.portal.spring.hibernate.exception.JpaSystemException;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.PessimisticLockException;
import jakarta.persistence.QueryTimeoutException;
import jakarta.persistence.TransactionRequiredException;

import java.io.IOException;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.InfrastructureProxy;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.lang.Nullable;

public class LocalSessionFactoryBean
	implements BeanFactoryAware, DisposableBean, FactoryBean<SessionFactory>,
			   InitializingBean, PersistenceExceptionTranslator,
			   ResourceLoaderAware, SmartInitializingSingleton {

	public LocalSessionFactoryBean() {
	}

	public void afterPropertiesSet() throws IOException {
		if ((this.metadataSources != null) && !this.metadataSourcesAccessed) {
			this.metadataSources = null;
		}

		LocalSessionFactoryBuilder sfb = new LocalSessionFactoryBuilder(
			this.dataSource, this.getResourceLoader(),
			this.getMetadataSources());

		if (this.entityInterceptor != null) {
			sfb.setInterceptor(this.entityInterceptor);
		}

		if (this.beanFactory != null) {
			sfb.setBeanContainer(this.beanFactory);
		}

		if (this.hibernateProperties != null) {
			sfb.addProperties(this.hibernateProperties);
		}

		this.configuration = sfb;
		this.sessionFactory = this.buildSessionFactory(sfb);
	}

	public void afterSingletonsInstantiated() {
		SessionFactory var2 = this.sessionFactory;

		if (var2 instanceof InfrastructureProxy proxy) {
			proxy.getWrappedObject();
		}
	}

	public void destroy() {
		if (this.sessionFactory != null) {
			this.sessionFactory.close();
		}
	}

	public final Configuration getConfiguration() {
		if (this.configuration == null) {
			throw new IllegalStateException(
				"Configuration not initialized yet");
		}

		return this.configuration;
	}

	public Properties getHibernateProperties() {
		if (this.hibernateProperties == null) {
			this.hibernateProperties = new Properties();
		}

		return this.hibernateProperties;
	}

	public MetadataSources getMetadataSources() {
		this.metadataSourcesAccessed = true;

		if (this.metadataSources == null) {
			BootstrapServiceRegistryBuilder builder =
				new BootstrapServiceRegistryBuilder();

			if (this.resourcePatternResolver != null) {
				builder = builder.applyClassLoader(
					this.resourcePatternResolver.getClassLoader());
			}

			this.metadataSources = new MetadataSources(builder.build());
		}

		return this.metadataSources;
	}

	@Nullable
	public SessionFactory getObject() {
		return this.sessionFactory;
	}

	public Class<?> getObjectType() {
		if (this.sessionFactory != null) {
			return this.sessionFactory.getClass();
		}

		return SessionFactory.class;
	}

	public ResourceLoader getResourceLoader() {
		if (this.resourcePatternResolver == null) {
			this.resourcePatternResolver =
				new PathMatchingResourcePatternResolver();
		}

		return this.resourcePatternResolver;
	}

	public boolean isSingleton() {
		return true;
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		if (beanFactory instanceof ConfigurableListableBeanFactory clbf) {
			this.beanFactory = clbf;
		}
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setEntityInterceptor(Interceptor entityInterceptor) {
		this.entityInterceptor = entityInterceptor;
	}

	public void setHibernateProperties(Properties hibernateProperties) {
		this.hibernateProperties = hibernateProperties;
	}

	public void setMetadataSources(MetadataSources metadataSources) {
		this.metadataSourcesAccessed = true;
		this.metadataSources = metadataSources;
	}

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourcePatternResolver =
			ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
	}

	@Nullable
	@Override
	public DataAccessException translateExceptionIfPossible(
		RuntimeException ex) {

		if (ex instanceof HibernateException hibernateEx) {
			return SessionFactoryUtils.convertHibernateAccessException(
				hibernateEx);
		}
		else if (ex instanceof PersistenceException) {
			Throwable var3 = ex.getCause();

			if (var3 instanceof HibernateException) {
				HibernateException hibernateEx = (HibernateException)var3;

				return SessionFactoryUtils.convertHibernateAccessException(
					hibernateEx);
			}

			return _convertJpaAccessExceptionIfPossible(ex);
		}

		return null;
	}

	protected SessionFactory buildSessionFactory(
		LocalSessionFactoryBuilder sfb) {

		return sfb.buildSessionFactory();
	}

	@Nullable
	private DataAccessException _convertJpaAccessExceptionIfPossible(
		RuntimeException ex) {

		if (ex instanceof IllegalStateException) {
			return new InvalidDataAccessApiUsageException(ex.getMessage(), ex);
		}
		else if (ex instanceof IllegalArgumentException) {
			return new InvalidDataAccessApiUsageException(ex.getMessage(), ex);
		}
		else if (ex instanceof EntityNotFoundException) {
			EntityNotFoundException entityNotFoundException =
				(EntityNotFoundException)ex;

			return new JpaObjectRetrievalFailureException(
				entityNotFoundException);
		}
		else if (ex instanceof NoResultException) {
			return new EmptyResultDataAccessException(ex.getMessage(), 1, ex);
		}
		else if (ex instanceof NonUniqueResultException) {
			return new IncorrectResultSizeDataAccessException(
				ex.getMessage(), 1, ex);
		}
		else if (ex instanceof QueryTimeoutException) {
			return new org.springframework.dao.QueryTimeoutException(
				ex.getMessage(), ex);
		}
		else if (ex instanceof LockTimeoutException) {
			return new CannotAcquireLockException(ex.getMessage(), ex);
		}
		else if (ex instanceof PessimisticLockException) {
			return new PessimisticLockingFailureException(ex.getMessage(), ex);
		}
		else if (ex instanceof OptimisticLockException) {
			OptimisticLockException optimisticLockException =
				(OptimisticLockException)ex;

			return new JpaOptimisticLockingFailureException(
				optimisticLockException);
		}
		else if (ex instanceof EntityExistsException) {
			return new DataIntegrityViolationException(ex.getMessage(), ex);
		}
		else if (ex instanceof TransactionRequiredException) {
			return new InvalidDataAccessApiUsageException(ex.getMessage(), ex);
		}

		if (ex instanceof PersistenceException) {
			return new JpaSystemException(ex);
		}

		return null;
	}

	@Nullable
	private ConfigurableListableBeanFactory beanFactory;

	@Nullable
	private Configuration configuration;

	@Nullable
	private DataSource dataSource;

	@Nullable
	private Interceptor entityInterceptor;

	@Nullable
	private Properties hibernateProperties;

	@Nullable
	private MetadataSources metadataSources;

	private boolean metadataSourcesAccessed;

	@Nullable
	private ResourcePatternResolver resourcePatternResolver;

	@Nullable
	private SessionFactory sessionFactory;

}