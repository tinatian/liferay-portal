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

package com.liferay.portal.kernel.dao.orm;

import com.liferay.petra.executor.PortalExecutorManager;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.test.ProxyTestUtil;
import com.liferay.registry.BasicRegistryImpl;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

/**
 * @author André de Oliveira
 */
public class IndexableActionableDynamicQueryTest {

	@Before
	public void setUp() {
		document1 = ProxyTestUtil.getProxy(Document.class);

		document2 = ProxyTestUtil.getProxy(Document.class);

		document3 = ProxyTestUtil.getProxy(Document.class);

		Registry registry = new BasicRegistryImpl();

		registry.registerService(
			PortalExecutorManager.class,
			ProxyTestUtil.getDummyProxy(PortalExecutorManager.class));

		RegistryUtil.setRegistry(registry);

		indexableActionableDynamicQuery = new IndexableActionableDynamicQuery();

		indexWriterHelper = ProxyTestUtil.getProxy(IndexWriterHelper.class);

		indexableActionableDynamicQuery.setIndexWriterHelper(indexWriterHelper);
	}

	@Test
	public void testAddDocuments() throws Exception {
		indexableActionableDynamicQuery.setInterval(1);

		indexableActionableDynamicQuery.addDocuments(document1, document2);

		verifyDocumentsUpdated(document1, document2);
	}

	@Test
	public void testAddDocumentsWithinInterval() throws Exception {
		indexableActionableDynamicQuery.setInterval(3);

		indexableActionableDynamicQuery.addDocuments(document1, document2);

		verifyNoDocumentsUpdated();

		indexableActionableDynamicQuery.addDocuments(document3);

		verifyDocumentsUpdated(document1, document2, document3);
	}

	protected void verifyDocumentsUpdated(Document... documents) {
		ProxyTestUtil.assertAction(
			indexWriterHelper,
			ProxyTestUtil.getProxyAction(
				"updateDocuments",
				new Object[] {null, 0L, Arrays.asList(documents), false}),
			times -> times == 1);
	}

	protected void verifyNoDocumentsUpdated() {
		ProxyTestUtil.assertAction(indexWriterHelper, times -> times == 0);
	}

	protected Document document1;
	protected Document document2;
	protected Document document3;
	protected IndexableActionableDynamicQuery indexableActionableDynamicQuery;
	protected IndexWriterHelper indexWriterHelper;

}