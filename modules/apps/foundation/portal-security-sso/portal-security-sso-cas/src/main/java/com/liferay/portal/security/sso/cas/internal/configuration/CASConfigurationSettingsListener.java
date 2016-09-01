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

package com.liferay.portal.security.sso.cas.internal.configuration;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.settings.CompanyServiceSettingsLocator;
import com.liferay.portal.kernel.settings.SettingsListener;
import com.liferay.portal.security.sso.cas.configuration.CASConfiguration;
import com.liferay.portal.security.sso.cas.constants.CASConstants;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tina Tian
 */
@Component(
	immediate = true,
	service = {SettingsListener.class, CASConfigurationSettingsListener.class}
)
public class CASConfigurationSettingsListener implements SettingsListener {

	public CASConfiguration getCASConfiguration(long companyId) {
		CASConfiguration casConfiguration = _casConfigurations.get(companyId);

		if (casConfiguration != null) {
			return casConfiguration;
		}

		casConfiguration = _createCASConfiguration(companyId);

		_casConfigurations.put(companyId, casConfiguration);

		return casConfiguration;
	}

	@Override
	public void notifyUpdate(long companyId) {
		if (companyId > 0) {
			CASConfiguration casConfiguration = _createCASConfiguration(
				companyId);

			_casConfigurations.put(companyId, casConfiguration);

			return;
		}

		_casConfigurations.clear();

		for (Company company : _companyLocalService.getCompanies()) {
			companyId = company.getCompanyId();

			CASConfiguration casConfiguration = _createCASConfiguration(
				companyId);

			_casConfigurations.put(companyId, casConfiguration);
		}
	}

	@Reference(unbind = "-")
	protected void setCompanyLocalService(
		CompanyLocalService companyLocalService) {

		_companyLocalService = companyLocalService;
	}

	@Reference(unbind = "-")
	protected void setConfigurationProvider(
		ConfigurationProvider configurationProvider) {

		_configurationProvider = configurationProvider;
	}

	private CASConfiguration _createCASConfiguration(long companyId) {
		try {
			CASConfiguration casCompanyServiceSettings =
				_configurationProvider.getConfiguration(
					CASConfiguration.class,
					new CompanyServiceSettingsLocator(
						companyId, CASConstants.SERVICE_NAME));

			CASConfigurationImpl casConfigurationImpl =
				new CASConfigurationImpl(
					casCompanyServiceSettings.enabled(),
					casCompanyServiceSettings.importFromLDAP(),
					casCompanyServiceSettings.loginURL(),
					casCompanyServiceSettings.logoutOnSessionExpiration(),
					casCompanyServiceSettings.logoutURL(),
					casCompanyServiceSettings.serverName(),
					casCompanyServiceSettings.serverURL(),
					casCompanyServiceSettings.serviceURL(),
					casCompanyServiceSettings.noSuchUserRedirectURL());

			return casConfigurationImpl;
		}
		catch (ConfigurationException ce) {
			_log.error("Unable to get CAS configuration", ce);
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CASConfigurationSettingsListener.class);

	private final Map<Long, CASConfiguration> _casConfigurations =
		new HashMap<>();
	private CompanyLocalService _companyLocalService;
	private ConfigurationProvider _configurationProvider;

	private class CASConfigurationImpl implements CASConfiguration {

		@Override
		public boolean enabled() {
			return _enabled;
		}

		@Override
		public boolean importFromLDAP() {
			return _importFromLDAP;
		}

		@Override
		public String loginURL() {
			return _loginURL;
		}

		@Override
		public boolean logoutOnSessionExpiration() {
			return _logoutOnSessionExpiration;
		}

		@Override
		public String logoutURL() {
			return _logoutURL;
		}

		@Override
		public String noSuchUserRedirectURL() {
			return _noSuchUserRedirectURL;
		}

		@Override
		public String serverName() {
			return _serverName;
		}

		@Override
		public String serverURL() {
			return _serverURL;
		}

		@Override
		public String serviceURL() {
			return _serviceURL;
		}

		private CASConfigurationImpl(
			boolean enabled, boolean importFromLDAP, String loginURL,
			boolean logoutOnSessionExpiration, String logoutURL,
			String serverName, String serverURL, String serviceURL,
			String noSuchUserRedirectURL) {

			_enabled = enabled;
			_importFromLDAP = importFromLDAP;
			_loginURL = loginURL;
			_logoutOnSessionExpiration = logoutOnSessionExpiration;
			_logoutURL = logoutURL;
			_serverName = serverName;
			_serverURL = serverURL;
			_serviceURL = serviceURL;
			_noSuchUserRedirectURL = noSuchUserRedirectURL;
		}

		private final boolean _enabled;
		private final boolean _importFromLDAP;
		private final String _loginURL;
		private final boolean _logoutOnSessionExpiration;
		private final String _logoutURL;
		private final String _noSuchUserRedirectURL;
		private final String _serverName;
		private final String _serverURL;
		private final String _serviceURL;

	}

}