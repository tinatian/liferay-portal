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

package com.liferay.journal.internal.transformer;

import com.liferay.journal.configuration.JournalServiceConfiguration;
import com.liferay.portal.kernel.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.template.StringTemplateResource;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateException;
import com.liferay.portal.kernel.template.TemplateResource;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.template.TemplateResourceParser;

import org.osgi.service.component.annotations.Component;

/**
 * @author Tina Tian
 */
@Component(
	immediate = true,
	property = {
		"lang.type=" + TemplateConstants.LANG_TYPE_FTL,
		"lang.type=" + TemplateConstants.LANG_TYPE_VM,
		"lang.type=" + TemplateConstants.LANG_TYPE_XSL
	},
	service = TemplateResourceParser.class
)
public class JournalErrorTemplateResourceParser
	implements TemplateResourceParser {

	@Override
	public TemplateResource getTemplateResource(String templateId)
		throws TemplateException {

		int pos = templateId.indexOf(JOURNAL_SEPARATOR_ERROR);

		if (pos < 0) {
			return null;
		}

		String langType = templateId.substring(0, pos);

		try {
			JournalServiceConfiguration journalServiceConfiguration =
				ConfigurationProviderUtil.getCompanyConfiguration(
					JournalServiceConfiguration.class,
					GetterUtil.getLong(templateId.substring(pos + 1)));

			String template;

			if (langType.equals(TemplateConstants.LANG_TYPE_FTL)) {
				template = journalServiceConfiguration.errorTemplateFTL();
			}
			else if (langType.equals(TemplateConstants.LANG_TYPE_VM)) {
				template = journalServiceConfiguration.errorTemplateVM();
			}
			else if (langType.equals(TemplateConstants.LANG_TYPE_XSL)) {
				template = journalServiceConfiguration.errorTemplateXSL();
			}
			else {
				return null;
			}

			return new StringTemplateResource(templateId, template);
		}
		catch (Exception e) {
			throw new TemplateException(
				"Unable to find template resource with id " + templateId, e);
		}
	}

	@Override
	public boolean isTemplateResourceValid(String templateId, String langType) {
		if (templateId.contains(JOURNAL_SEPARATOR_ERROR)) {
			return true;
		}

		return false;
	}

	protected static final String JOURNAL_SEPARATOR_ERROR =
		"_JOURNAL_SEPARATOR_ERROR";

}