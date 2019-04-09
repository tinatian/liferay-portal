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

package com.liferay.portal.template;

import com.liferay.portal.kernel.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateException;
import com.liferay.portal.kernel.template.TemplateResource;
import com.liferay.portal.kernel.template.TemplateResourceCache;
import com.liferay.portal.kernel.util.StringBundler;

import java.io.Writer;

import java.util.Map;
import java.util.function.Supplier;

/**
 * @author Miroslav Ligas
 */
public abstract class BaseSingleResourceTemplate extends BaseTemplate {

	public BaseSingleResourceTemplate(
		TemplateResource templateResource, Map<String, Object> context,
		TemplateContextHelper templateContextHelper,
		TemplateResourceCache templateResourceCache) {

		super(context, templateContextHelper);

		if (templateResource == null) {
			throw new IllegalArgumentException("Template resource is null");
		}

		this.templateResource = templateResource;

		if (templateResourceCache.isEnabled()) {
			cacheTemplateResource(templateResourceCache, templateResource);
		}
	}

	@Override
	public void processTemplate(Writer writer) throws TemplateException {
		try {
			processTemplate(templateResource, writer);
		}
		catch (Exception e) {
			throw new TemplateException(
				"Unable to process template " +
					templateResource.getTemplateId(),
				e);
		}
	}

	public void processTemplate(
			Writer writer,
			Supplier<TemplateResource> errorTemplateResourceSupplier)
		throws TemplateException {

		if (errorTemplateResourceSupplier == null) {
			processTemplate(writer);

			return;
		}

		Writer oldWriter = (Writer)get(TemplateConstants.WRITER);

		try {
			UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

			put(TemplateConstants.WRITER, unsyncStringWriter);

			processTemplate(templateResource, unsyncStringWriter);

			StringBundler sb = unsyncStringWriter.getStringBundler();

			sb.writeTo(writer);
		}
		catch (Exception e) {
			TemplateResource errorTemplateResource =
				errorTemplateResourceSupplier.get();

			if (errorTemplateResource == null) {
				throw new TemplateException(
					"Unable to process template " +
						templateResource.getTemplateId(),
					e);
			}

			put(TemplateConstants.WRITER, writer);

			handleException(errorTemplateResource, e, writer);
		}
		finally {
			put(TemplateConstants.WRITER, oldWriter);
		}
	}

	protected void cacheTemplateResource(
		TemplateResourceCache templateResourceCache,
		TemplateResource templateResource) {

		TemplateResource cachedTemplateResource =
			templateResourceCache.getTemplateResource(
				templateResource.getTemplateId());

		if ((cachedTemplateResource == null) ||
			!templateResource.equals(cachedTemplateResource)) {

			templateResourceCache.put(
				templateResource.getTemplateId(), templateResource);
		}
	}

	protected abstract void processTemplate(
			TemplateResource templateResource, Writer writer)
		throws Exception;

	protected TemplateResource templateResource;

}