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

package com.liferay.portal.search.web.display.context;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcherManager;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tina Tian
 */
@Component(immediate = true, service = SearchDisplayContextFactory.class)
public class SearchDisplayContextFactory {
	
	public static SearchDisplayContext create(
			RenderRequest renderRequest, RenderResponse renderResponse,
			PortletPreferences portletPreferences)
		throws Exception {
		
		return _instance.doCreate(renderRequest, renderResponse, portletPreferences);
	}
	
	@Activate
	@Modified
	protected void activate() {
		_instance = this;
	}

	protected SearchDisplayContext doCreate(
			RenderRequest renderRequest, RenderResponse renderResponse,
			PortletPreferences portletPreferences)
		throws Exception {
		
		return new SearchDisplayContext(
			renderRequest, renderResponse, portletPreferences,
			PortalUtil.getPortal(), HtmlUtil.getHtml(), LanguageUtil.getLanguage(),
			facetedSearcherManager, new IndexSearchPropsValuesImpl(),
			new PortletURLFactoryImpl());
	}
	
	private static SearchDisplayContextFactory _instance;
	
	@Reference
	protected FacetedSearcherManager facetedSearcherManager;

}
