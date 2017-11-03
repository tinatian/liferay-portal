<%--
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
--%>

<%@ include file="/html/taglib/aui/panel/init.jsp" %>

<%
String bodyContentString = StringPool.BLANK;

Object bodyContent = request.getAttribute("aui:panel:bodyContent");

if (bodyContent != null) {
	bodyContentString = bodyContent.toString();
}
%>

<liferay-ui:panel-container extended="<%= !collapsed %>" id='<%= id + "Container" %>' markupView="lexicon" persistState="<%= true %>">
	<liferay-ui:panel collapsible="<%= collapsible %>" extended="<%= !collapsed %>" id="<%= id %>" markupView="lexicon" persistState="<%= true %>" title="<%= localizeLabel ? LanguageUtil.get(request, label) : label %>">
		<%= bodyContentString %>
	</liferay-ui:panel>
</liferay-ui:panel-container>