<#assign wikiNodeModels = wikiDataFactory.newWikiNodeModels(groupId) />

<#list wikiNodeModels as wikiNodeModel>
	${wikiDataFactory.toInsertSQL(wikiNodeModel)}

	${resourcePermissionDataFactory.generateResourcePermissionSQL(wikiNodeModel)}

	<#assign wikiPageModels = wikiDataFactory.newWikiPageModels(wikiNodeModel) />

	<#list wikiPageModels as wikiPageModel>
		${wikiDataFactory.toInsertSQL(wikiPageModel)}

		${resourcePermissionDataFactory.generateResourcePermissionSQL(wikiPageModel)}

		${wikiDataFactory.toInsertSQL(subscriptionDataFactory.newSubscriptionModel(wikiPageModel))}

		${resourcePermissionDataFactory.generateResourcePermissionSQL(subscriptionDataFactory.newSubscriptionModel(wikiPageModel))}

		${wikiDataFactory.toInsertSQL(wikiDataFactory.newWikiPageResourceModel(wikiPageModel))}

		${resourcePermissionDataFactory.generateResourcePermissionSQL(wikiDataFactory.newWikiPageResourceModel(wikiPageModel))}

		<@insertAssetEntry
			_categoryAndTag=true
			_entry=wikiPageModel
		/>

		<#assign
			mbRootMessageId = counterDataFactory.getCounterNext()
			mbThreadId = counterDataFactory.getCounterNext()
		/>

		<@insertMBDiscussion
			_classNameId=wikiDataFactory.wikiPageClassNameId
			_classPK=wikiPageModel.resourcePrimKey
			_groupId=groupId
			_maxCommentCount=initContext.maxWikiPageCommentCount
			_mbRootMessageId=mbRootMessageId
			_mbThreadId=mbThreadId
		/>

		${initContext.getCSVWriter("wiki").write(wikiNodeModel.nodeId + "," + wikiNodeModel.name + "," + wikiPageModel.resourcePrimKey + "," + wikiPageModel.title + "," + mbThreadId + "," + mbRootMessageId + "\n")}
	</#list>
</#list>