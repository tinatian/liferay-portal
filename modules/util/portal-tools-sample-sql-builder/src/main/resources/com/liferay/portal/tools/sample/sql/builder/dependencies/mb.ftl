<#assign mbCategoryModels = messageBoardDataFactory.newMBCategoryModels(groupId) />

<#list mbCategoryModels as mbCategoryModel>
	${messageBoardDataFactory.toInsertSQL(mbCategoryModel)}

	${resourcePermissionDataFactory.generateResourcePermissionSQL(mbCategoryModel)}

	${messageBoardDataFactory.toInsertSQL(messageBoardDataFactory.newMBMailingListModel(mbCategoryModel))}

	${resourcePermissionDataFactory.generateResourcePermissionSQL(messageBoardDataFactory.newMBMailingListModel(mbCategoryModel))}

	<#assign mbThreadModels = messageBoardDataFactory.newMBThreadModels(mbCategoryModel) />

	<#list mbThreadModels as mbThreadModel>
		${messageBoardDataFactory.toInsertSQL(mbThreadModel)}

		${resourcePermissionDataFactory.generateResourcePermissionSQL(mbThreadModel)}

		${messageBoardDataFactory.toInsertSQL(subscriptionDataFactory.newSubscriptionModel(mbThreadModel))}

		${resourcePermissionDataFactory.generateResourcePermissionSQL(subscriptionDataFactory.newSubscriptionModel(mbThreadModel))}

		<@insertAssetEntry _entry=mbThreadModel />

		${messageBoardDataFactory.toInsertSQL(messageBoardDataFactory.newMBThreadFlagModel(mbThreadModel))}

		${resourcePermissionDataFactory.generateResourcePermissionSQL(messageBoardDataFactory.newMBThreadFlagModel(mbThreadModel))}

		<#assign mbMessageModels = messageBoardDataFactory.newMBMessageModels(mbThreadModel) />

		<#list mbMessageModels as mbMessageModel>
			<@insertMBMessage _mbMessageModel=mbMessageModel />

			${messageBoardDataFactory.toInsertSQL(socialActivityDataFactory.newSocialActivityModel(mbMessageModel))}

			${resourcePermissionDataFactory.generateResourcePermissionSQL(socialActivityDataFactory.newSocialActivityModel(mbMessageModel))}
		</#list>

		${initContext.getCSVWriter("messageBoard").write(mbCategoryModel.categoryId + "," + mbThreadModel.threadId + "," + mbThreadModel.rootMessageId + "\n")}
	</#list>
</#list>