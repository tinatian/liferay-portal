package com.liferay.portal.spring.hibernate;

import java.util.Collections;
import javax.sql.DataSource;
import org.hibernate.boot.MetadataSources;
import org.hibernate.cfg.Configuration;
import org.hibernate.resource.jdbc.spi.PhysicalConnectionHandlingMode;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;

public class LocalSessionFactoryBuilder extends Configuration {
	public LocalSessionFactoryBuilder(@Nullable DataSource dataSource, ResourceLoader resourceLoader, MetadataSources metadataSources) {
		super(metadataSources);
		this.getProperties().put("hibernate.current_session_context_class", SpringSessionContext.class.getName());
		if (dataSource != null) {
			this.getProperties().put("hibernate.connection.datasource", dataSource);
		}

		this.getProperties().put("hibernate.connection.handling_mode", PhysicalConnectionHandlingMode.DELAYED_ACQUISITION_AND_HOLD);
		this.getProperties().put("hibernate.classLoaders", Collections.singleton(resourceLoader.getClassLoader()));
	}

	public LocalSessionFactoryBuilder setBeanContainer(ConfigurableListableBeanFactory beanFactory) {
		this.getProperties().put("hibernate.resource.beans.container", new SpringBeanContainer(beanFactory));
		return this;
	}
}

