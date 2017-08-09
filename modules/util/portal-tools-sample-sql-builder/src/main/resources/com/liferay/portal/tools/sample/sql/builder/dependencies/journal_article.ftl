<#assign ddmStructureModel = journalDataFactory.defaultJournalDDMStructureModel />

<@insertDDMStructure
	_ddmStructureLayoutModel=journalDataFactory.defaultJournalDDMStructureLayoutModel
	_ddmStructureModel=ddmStructureModel
	_ddmStructureVersionModel=journalDataFactory.defaultJournalDDMStructureVersionModel
/>

<#assign ddmTemplateModel = journalDataFactory.defaultJournalDDMTemplateModel />

${journalDataFactory.toInsertSQL(ddmTemplateModel)}

${resourcePermissionDataFactory.generateResourcePermissionSQL(ddmTemplateModel)}

<#assign
	journalArticlePageCounts = counterDataFactory.getSequence(initContext.maxJournalArticlePageCount)

	resourcePermissionModels = resourcePermissionDataFactory.newResourcePermissionModels("com.liferay.journal", groupId)
/>

<#list resourcePermissionModels as resourcePermissionModel>
	${journalDataFactory.toInsertSQL(resourcePermissionModel)}

	${resourcePermissionDataFactory.generateResourcePermissionSQL(resourcePermissionModel)}
</#list>

<#list journalArticlePageCounts as journalArticlePageCount>
	<#assign
		portletIdPrefix = "com_liferay_journal_content_web_portlet_JournalContentPortlet_INSTANCE_TEST_" + journalArticlePageCount + "_"

		layoutModel = layoutDataFactory.newLayoutModel(groupId, groupId + "_journal_article_" + journalArticlePageCount, "", journalDataFactory.getJournalArticleLayoutColumn(portletIdPrefix))
	/>

	${initContext.getCSVWriter("layout").write(layoutModel.friendlyURL + "\n")}

	<@insertLayout _layoutModel=layoutModel />

	<#assign portletPreferencesModels = portletPreferenceDataFactory.newJournalPortletPreferencesModels(layoutModel.plid) />

	<#list portletPreferencesModels as portletPreferencesModel>
		${journalDataFactory.toInsertSQL(portletPreferencesModel)}

		${resourcePermissionDataFactory.generateResourcePermissionSQL(portletPreferencesModel)}
	</#list>

	<#assign journalArticleCounts = counterDataFactory.getSequence(initContext.maxJournalArticleCount) />

	<#list journalArticleCounts as journalArticleCount>
		<#assign journalArticleResourceModel = journalDataFactory.newJournalArticleResourceModel(groupId) />

		${journalDataFactory.toInsertSQL(journalArticleResourceModel)}

		${resourcePermissionDataFactory.generateResourcePermissionSQL(journalArticleResourceModel)}

		<#assign versionCounts = counterDataFactory.getSequence(initContext.maxJournalArticleVersionCount) />

		<#list versionCounts as versionCount>
			<#assign journalArticleModel = journalDataFactory.newJournalArticleModel(journalArticleResourceModel, journalArticleCount, versionCount) />

			${journalDataFactory.toInsertSQL(journalArticleModel)}

			${resourcePermissionDataFactory.generateResourcePermissionSQL(journalArticleModel)}

			<#assign journalArticleLocalizationModel = journalDataFactory.newJournalArticleLocalizationModel(journalArticleModel, journalArticleCount, versionCount) />

			${journalDataFactory.toInsertSQL(journalArticleLocalizationModel)}

			${resourcePermissionDataFactory.generateResourcePermissionSQL(journalArticleLocalizationModel)}

			${journalDataFactory.toInsertSQL(journalDataFactory.newDDMTemplateLinkModel(journalArticleModel, ddmTemplateModel.templateId))}

			${resourcePermissionDataFactory.generateResourcePermissionSQL(journalDataFactory.newDDMTemplateLinkModel(journalArticleModel, ddmTemplateModel.templateId))}

			${journalDataFactory.toInsertSQL(journalDataFactory.newDDMStorageLinkModel(journalArticleModel, ddmStructureModel.structureId))}

			${resourcePermissionDataFactory.generateResourcePermissionSQL(journalDataFactory.newDDMTemplateLinkModel(journalArticleModel, ddmTemplateModel.templateId))}

			${journalDataFactory.toInsertSQL(socialActivityDataFactory.newSocialActivityModel(journalArticleModel))}

			${resourcePermissionDataFactory.generateResourcePermissionSQL(socialActivityDataFactory.newSocialActivityModel(journalArticleModel))}

			<#if versionCount = initContext.maxJournalArticleVersionCount>
				<@insertAssetEntry
					_categoryAndTag=true
					_entry=journalDataFactory.newObjectValuePair(journalArticleModel, journalArticleLocalizationModel)
				/>
			</#if>
		</#list>

		<@insertMBDiscussion
			_classNameId=journalDataFactory.journalArticleClassNameId
			_classPK=journalArticleResourceModel.resourcePrimKey
			_groupId=groupId
			_maxCommentCount=0
			_mbRootMessageId=counterDataFactory.getCounterNext()
			_mbThreadId=counterDataFactory.getCounterNext()
		/>

		<#assign portletPreferencesModel = portletPreferenceDataFactory.newPortletPreferencesModel(layoutModel.plid, portletIdPrefix + journalArticleCount, journalArticleResourceModel) />

		${journalDataFactory.toInsertSQL(portletPreferencesModel)}

		${resourcePermissionDataFactory.generateResourcePermissionSQL(portletPreferencesModel)}

		${journalDataFactory.toInsertSQL(journalDataFactory.newJournalContentSearchModel(journalArticleModel, layoutModel.plid))}

		${resourcePermissionDataFactory.generateResourcePermissionSQL(journalDataFactory.newJournalContentSearchModel(journalArticleModel, layoutModel.plid))}
	</#list>
</#list>