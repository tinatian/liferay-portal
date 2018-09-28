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

package com.liferay.wiki.editor.configuration.internal;

import com.liferay.item.selector.ItemSelector;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.ProxyTestUtil;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletURL;

import org.junit.Before;
import org.junit.Test;

import org.skyscreamer.jsonassert.JSONAssert;

/**
 * @author Roberto Díaz
 */
public class WikiLinksCKEditorEditorConfigContributorTest {

	@Before
	public void setUp() throws Exception {
		_itemSelector = ProxyTestUtil.getProxy(ItemSelector.class);

		_inputEditorTaglibAttributes.put(
			"liferay-ui:input-editor:name", "testEditor");

		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());

		JSONObject originalJSONObject =
			getJSONObjectWithDefaultItemSelectorURL();

		_jsonObject = JSONFactoryUtil.createJSONObject(
			originalJSONObject.toJSONString());

		_wikiLinksCKEditorEditorConfigContributor =
			new WikiLinksCKEditorConfigContributor();

		ReflectionTestUtil.setFieldValue(
			_wikiLinksCKEditorEditorConfigContributor, "itemSelector",
			_itemSelector);
	}

	@Test
	public void testItemSelectorURLWhenNullWikiPageAndValidNode()
		throws Exception {

		populateInputEditorWikiPageAttributes(0, 1);

		String portletURLString = "oneTabItemSelectorPortletURL";

		ProxyTestUtil.updateProxy(
			_itemSelector, "getItemSelectorURL",
			ProxyTestUtil.getProxy(
				PortletURL.class, "toString", portletURLString));

		_wikiLinksCKEditorEditorConfigContributor.populateConfigJSONObject(
			_jsonObject, _inputEditorTaglibAttributes, null, null);

		JSONObject expectedJSONObject = JSONFactoryUtil.createJSONObject();

		expectedJSONObject.put("filebrowserBrowseUrl", portletURLString);
		expectedJSONObject.put("removePlugins", "plugin1");

		JSONAssert.assertEquals(
			expectedJSONObject.toJSONString(), _jsonObject.toJSONString(),
			true);
	}

	@Test
	public void testItemSelectorURLWhenValidWikiPageAndNode() throws Exception {
		populateInputEditorWikiPageAttributes(1, 1);

		String portletURLString = "twoTabsItemSelectorPortletURL";

		ProxyTestUtil.updateProxy(
			_itemSelector, "getItemSelectorURL",
			ProxyTestUtil.getProxy(
				PortletURL.class, "toString", portletURLString));

		_wikiLinksCKEditorEditorConfigContributor.populateConfigJSONObject(
			_jsonObject, _inputEditorTaglibAttributes, null, null);

		JSONObject expectedJSONObject = JSONFactoryUtil.createJSONObject();

		expectedJSONObject.put("filebrowserBrowseUrl", portletURLString);
		expectedJSONObject.put("removePlugins", "plugin1");

		JSONAssert.assertEquals(
			expectedJSONObject.toJSONString(), _jsonObject.toJSONString(),
			true);
	}

	@Test
	public void testItemSelectorURLWhenValidWikiPageAndNullNode()
		throws Exception {

		populateInputEditorWikiPageAttributes(1, 0);

		String portletURLString = "oneTabItemSelectorPortletURL";

		ProxyTestUtil.updateProxy(
			_itemSelector, "getItemSelectorURL",
			ProxyTestUtil.getProxy(
				PortletURL.class, "toString", portletURLString));

		_wikiLinksCKEditorEditorConfigContributor.populateConfigJSONObject(
			_jsonObject, _inputEditorTaglibAttributes, null, null);

		JSONObject expectedJSONObject = JSONFactoryUtil.createJSONObject();

		expectedJSONObject.put("filebrowserBrowseUrl", portletURLString);
		expectedJSONObject.put("removePlugins", "plugin1");

		JSONAssert.assertEquals(
			expectedJSONObject.toJSONString(), _jsonObject.toJSONString(),
			true);
	}

	protected JSONObject getJSONObjectWithDefaultItemSelectorURL()
		throws Exception {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		jsonObject.put("filebrowserBrowseUrl", "defaultItemSelectorPortletURL");

		jsonObject.put("removePlugins", "plugin1");

		return jsonObject;
	}

	protected void populateInputEditorWikiPageAttributes(
		long wikiPageResourcePrimKey, long nodeId) {

		Map<String, String> fileBrowserParamsMap = new HashMap<>();

		fileBrowserParamsMap.put("nodeId", String.valueOf(nodeId));
		fileBrowserParamsMap.put(
			"wikiPageResourcePrimKey", String.valueOf(wikiPageResourcePrimKey));

		_inputEditorTaglibAttributes.put(
			"liferay-ui:input-editor:fileBrowserParams", fileBrowserParamsMap);
	}

	private final Map<String, Object> _inputEditorTaglibAttributes =
		new HashMap<>();
	private ItemSelector _itemSelector;
	private JSONObject _jsonObject;
	private WikiLinksCKEditorConfigContributor
		_wikiLinksCKEditorEditorConfigContributor;

}