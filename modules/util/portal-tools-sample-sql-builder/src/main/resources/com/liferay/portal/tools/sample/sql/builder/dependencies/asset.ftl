<#list assetDataFactory.assetVocabularyModels as assetVocabularyModel>
	${assetDataFactory.toInsertSQL(assetVocabularyModel)}
	${resourcePermissionDataFactory.generateResourcePermissionSQL(assetVocabularyModel)}
</#list>

<#list assetDataFactory.assetCategoryModels as assetCategoryModel>
	${assetDataFactory.toInsertSQL(assetCategoryModel)}
	${resourcePermissionDataFactory.generateResourcePermissionSQL(assetCategoryModel)}
</#list>

<#list assetDataFactory.assetTagModels as assetTagModel>
	${assetDataFactory.toInsertSQL(assetTagModel)}
	${resourcePermissionDataFactory.generateResourcePermissionSQL(assetTagModel)}
</#list>

<#list assetDataFactory.assetTagStatsModels as assetTagStatsModel>
	${assetDataFactory.toInsertSQL(assetTagStatsModel)}
</#list>