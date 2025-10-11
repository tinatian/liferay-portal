/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.hibernate;

import java.io.IOException;
import java.util.Properties;
import javax.sql.DataSource;
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
import org.springframework.lang.Nullable;

public class LocalSessionFactoryBean extends HibernateExceptionTranslator
	implements FactoryBean<SessionFactory>, ResourceLoaderAware, BeanFactoryAware, InitializingBean, SmartInitializingSingleton, DisposableBean {
	@Nullable
	private DataSource dataSource;
	@Nullable
	private Interceptor entityInterceptor;
	@Nullable
	private Properties hibernateProperties;
	private boolean metadataSourcesAccessed = false;
	@Nullable
	private MetadataSources metadataSources;
	@Nullable
	private ResourcePatternResolver resourcePatternResolver;
	@Nullable
	private ConfigurableListableBeanFactory beanFactory;
	@Nullable
	private Configuration configuration;
	@Nullable
	private SessionFactory sessionFactory;

	public LocalSessionFactoryBean() {
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

	public Properties getHibernateProperties() {
		if (this.hibernateProperties == null) {
			this.hibernateProperties = new Properties();
		}

		return this.hibernateProperties;
	}

	public void setMetadataSources(MetadataSources metadataSources) {
		this.metadataSourcesAccessed = true;
		this.metadataSources = metadataSources;
	}

	public MetadataSources getMetadataSources() {
		this.metadataSourcesAccessed = true;
		if (this.metadataSources == null) {
			BootstrapServiceRegistryBuilder builder = new BootstrapServiceRegistryBuilder();
			if (this.resourcePatternResolver != null) {
				builder = builder.applyClassLoader(this.resourcePatternResolver.getClassLoader());
			}

			this.metadataSources = new MetadataSources(builder.build());
		}

		return this.metadataSources;
	}

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
	}

	public ResourceLoader getResourceLoader() {
		if (this.resourcePatternResolver == null) {
			this.resourcePatternResolver = new PathMatchingResourcePatternResolver();
		}

		return this.resourcePatternResolver;
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		if (beanFactory instanceof ConfigurableListableBeanFactory clbf) {
			this.beanFactory = clbf;
		}

	}

	public void afterPropertiesSet() throws IOException {
		if (this.metadataSources != null && !this.metadataSourcesAccessed) {
			this.metadataSources = null;
		}

		LocalSessionFactoryBuilder
			sfb = new LocalSessionFactoryBuilder(this.dataSource, this.getResourceLoader(), this.getMetadataSources());

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

	protected SessionFactory buildSessionFactory(LocalSessionFactoryBuilder sfb) {
		return sfb.buildSessionFactory();
	}

	public final Configuration getConfiguration() {
		if (this.configuration == null) {
			throw new IllegalStateException("Configuration not initialized yet");
		} else {
			return this.configuration;
		}
	}

	@Nullable
	public SessionFactory getObject() {
		return this.sessionFactory;
	}

	public Class<?> getObjectType() {
		return this.sessionFactory != null ? this.sessionFactory.getClass() : SessionFactory.class;
	}

	public boolean isSingleton() {
		return true;
	}

	public void destroy() {
		if (this.sessionFactory != null) {
			this.sessionFactory.close();
		}

	}
}

