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

package com.liferay.journal.util;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalServiceUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.model.JournalFolderConstants;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.service.JournalFolderLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.templateparser.TransformerListener;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Tom Wang
 */
public class JournalUtil {

	public static Layout getArticleLayout(String layoutUuid, long groupId) {
		if (Validator.isNull(layoutUuid)) {
			return null;
		}

		// The target page and the article must belong to the same group

		Layout layout = LayoutLocalServiceUtil.fetchLayoutByUuidAndGroupId(
			layoutUuid, groupId, false);

		if (layout == null) {
			layout = LayoutLocalServiceUtil.fetchLayoutByUuidAndGroupId(
				layoutUuid, groupId, true);
		}

		return layout;
	}

	/**
	 * @deprecated As of 3.0.0, with no direct replacement
	 */
	@Deprecated
	public static List<JournalArticle> getArticles(Hits hits)
		throws PortalException {

		List<Document> documents = hits.toList();

		List<JournalArticle> articles = new ArrayList<>(documents.size());

		for (Document document : documents) {
			String articleId = document.get(Field.ARTICLE_ID);
			long groupId = GetterUtil.getLong(
				document.get(Field.SCOPE_GROUP_ID));

			JournalArticle article =
				JournalArticleLocalServiceUtil.fetchLatestArticle(
					groupId, articleId, WorkflowConstants.STATUS_APPROVED);

			if (article == null) {
				articles = null;

				Indexer<JournalArticle> indexer =
					IndexerRegistryUtil.getIndexer(JournalArticle.class);

				long companyId = GetterUtil.getLong(
					document.get(Field.COMPANY_ID));

				indexer.delete(companyId, document.getUID());
			}
			else if (articles != null) {
				articles.add(article);
			}
		}

		return articles;
	}

	public static int getRestrictionType(long folderId) {
		int restrictionType = JournalFolderConstants.RESTRICTION_TYPE_INHERIT;

		JournalFolder folder = JournalFolderLocalServiceUtil.fetchFolder(
			folderId);

		if (folder != null) {
			restrictionType = folder.getRestrictionType();
		}

		return restrictionType;
	}

	public static String getTemplateScript(
			long groupId, String ddmTemplateKey, Map<String, String> tokens,
			String languageId)
		throws PortalException {

		DDMTemplate ddmTemplate = DDMTemplateLocalServiceUtil.getTemplate(
			groupId, PortalUtil.getClassNameId(DDMStructure.class),
			ddmTemplateKey, true);

		String script = ddmTemplate.getScript();

		for (TransformerListener transformerListener :
				JournalTransformerListenerRegistryUtil.
					getTransformerListeners()) {

			script = transformerListener.onScript(
				script, null, languageId, tokens);
		}

		return script;
	}

}