<#setting number_format = "computer">

<#macro insertAssetEntry
	_entry
	_categoryAndTag = false
>
	<#local assetEntryModel = assetDataFactory.newAssetEntryModel(_entry)>

	${assetDataFactory.toInsertSQL(assetEntryModel)}

	<#if _categoryAndTag>
		<#local assetCategoryIds = assetDataFactory.getAssetCategoryIds(assetEntryModel)>

		<#list assetCategoryIds as assetCategoryId>
			insert into AssetEntries_AssetCategories values (${assetEntryModel.companyId}, ${assetCategoryId}, ${assetEntryModel.entryId});
		</#list>

		<#local assetTagIds = assetDataFactory.getAssetTagIds(assetEntryModel)>

		<#list assetTagIds as assetTagId>
			insert into AssetEntries_AssetTags values (${assetEntryModel.companyId}, ${assetEntryModel.entryId}, ${assetTagId});
		</#list>
	</#if>
</#macro>

<#macro insertDDMContent
	_ddmStorageLinkId
	_ddmStructureId
	_entry
	_currentIndex = -1
>
	<#if _currentIndex = -1>
		<#local ddmContentModel = dDLDataFactory.newDDMContentModel(_entry)>
	<#else>
		<#local ddmContentModel = dDLDataFactory.newDDMContentModel(_entry, _currentIndex)>
	</#if>

	${dDLDataFactory.toInsertSQL(ddmContentModel)}

	${dDLDataFactory.toInsertSQL(dDLDataFactory.newDDMStorageLinkModel(_ddmStorageLinkId, ddmContentModel, _ddmStructureId))}
</#macro>

<#macro insertDDMStructure
	_ddmStructureModel
	_ddmStructureLayoutModel
	_ddmStructureVersionModel
>
	${dDLDataFactory.toInsertSQL(_ddmStructureModel)}

	${resourcePermissionDataFactory.generateResourcePermissionSQL(_ddmStructureModel)}

	${dDLDataFactory.toInsertSQL(_ddmStructureLayoutModel)}

	${dDLDataFactory.toInsertSQL(_ddmStructureVersionModel)}
</#macro>

<#macro insertDLFolder
	_ddmStructureId
	_dlFolderDepth
	_groupId
	_parentDLFolderId
>
	<#if _dlFolderDepth <= initContext.maxDLFolderDepth>
		<#local dlFolderModels = dLDataFactory.newDLFolderModels(_groupId, _parentDLFolderId)>

		<#list dlFolderModels as dlFolderModel>
			${dLDataFactory.toInsertSQL(dlFolderModel)}

			${resourcePermissionDataFactory.generateResourcePermissionSQL(dlFolderModel)}

			<@insertAssetEntry _entry=dlFolderModel />

			<#local dlFileEntryModels = dLDataFactory.newDlFileEntryModels(dlFolderModel)>

			<#list dlFileEntryModels as dlFileEntryModel>
				${dLDataFactory.toInsertSQL(dlFileEntryModel)}

				<#local dlFileVersionModel = dLDataFactory.newDLFileVersionModel(dlFileEntryModel)>

				${dLDataFactory.toInsertSQL(dlFileVersionModel)}

				${resourcePermissionDataFactory.generateResourcePermissionSQL(dlFileEntryModel)}

				<@insertAssetEntry _entry=dlFileEntryModel />

				<#local ddmStorageLinkId = counterDataFactory.getCounterNext()>

				<@insertDDMContent
					_ddmStorageLinkId=ddmStorageLinkId
					_ddmStructureId=_ddmStructureId
					_entry=dlFileEntryModel
				/>

				<@insertMBDiscussion
					_classNameId=dLDataFactory.DLFileEntryClassNameId
					_classPK=dlFileEntryModel.fileEntryId
					_groupId=dlFileEntryModel.groupId
					_maxCommentCount=0
					_mbRootMessageId=counterDataFactory.getCounterNext()
					_mbThreadId=counterDataFactory.getCounterNext()
				/>

				${dLDataFactory.toInsertSQL(socialActivityDataFactory.newSocialActivityModel(dlFileEntryModel))}

				<#local dlFileEntryMetadataModel = dLDataFactory.newDLFileEntryMetadataModel(ddmStorageLinkId, _ddmStructureId, dlFileVersionModel)>

				${dLDataFactory.toInsertSQL(dlFileEntryMetadataModel)}

				${dLDataFactory.toInsertSQL(dDLDataFactory.newDDMStructureLinkModel(dlFileEntryMetadataModel))}

				${initContext.getCSVWriter("documentLibrary").write(dlFileEntryModel.uuid + "," + dlFolderModel.folderId + "," + dlFileEntryModel.name + "," + dlFileEntryModel.fileEntryId + "," + dLDataFactory.getDateLong(dlFileEntryModel.createDate) + "," + dLDataFactory.getDateLong(dlFolderModel.createDate) + "\n")}
			</#list>

			<@insertDLFolder
				_ddmStructureId=_ddmStructureId
				_dlFolderDepth=_dlFolderDepth + 1
				_groupId=groupId
				_parentDLFolderId=dlFolderModel.folderId
			/>
		</#list>
	</#if>
</#macro>

<#macro insertGroup
	_groupModel
	_publicPageCount
>
	${userDataFactory.toInsertSQL(_groupModel)}

	${resourcePermissionDataFactory.generateResourcePermissionSQL(_groupModel)}

	<#local layoutSetModels = layoutDataFactory.newLayoutSetModels(_groupModel.groupId, _publicPageCount)>

	<#list layoutSetModels as layoutSetModel>
		${userDataFactory.toInsertSQL(layoutSetModel)}
	</#list>
</#macro>

<#macro insertLayout
	_layoutModel
>
	${layoutDataFactory.toInsertSQL(_layoutModel)}

	${resourcePermissionDataFactory.generateResourcePermissionSQL(_layoutModel)}

	${layoutDataFactory.toInsertSQL(layoutDataFactory.newLayoutFriendlyURLModel(_layoutModel))}
</#macro>

<#macro insertMBDiscussion
	_classNameId
	_classPK
	_groupId
	_maxCommentCount
	_mbRootMessageId
	_mbThreadId
>
	<#local mbThreadModel = messageBoardDataFactory.newMBThreadModel(_mbThreadId, _groupId, _mbRootMessageId, _maxCommentCount)>

	${messageBoardDataFactory.toInsertSQL(mbThreadModel)}

	<#local mbRootMessageModel = messageBoardDataFactory.newMBMessageModel(mbThreadModel, _classNameId, _classPK, 0)>

	<@insertMBMessage _mbMessageModel=mbRootMessageModel />

	<#local mbMessageModels = messageBoardDataFactory.newMBMessageModels(mbThreadModel, _classNameId, _classPK, _maxCommentCount)>

	<#list mbMessageModels as mbMessageModel>
		<@insertMBMessage _mbMessageModel=mbMessageModel />

		${messageBoardDataFactory.toInsertSQL(socialActivityDataFactory.newSocialActivityModel(mbMessageModel))}
	</#list>

	${messageBoardDataFactory.toInsertSQL(messageBoardDataFactory.newMBDiscussionModel(_groupId, _classNameId, _classPK, _mbThreadId))}
</#macro>

<#macro insertMBMessage
	_mbMessageModel
>
	${messageBoardDataFactory.toInsertSQL(_mbMessageModel)}

	${resourcePermissionDataFactory.generateResourcePermissionSQL(_mbMessageModel)}

	<@insertAssetEntry _entry=_mbMessageModel />
</#macro>

<#macro insertUser
	_userModel
	_groupIds = []
	_roleIds = []
>
	${userDataFactory.toInsertSQL(_userModel)}

	${userDataFactory.toInsertSQL(userDataFactory.newContactModel(_userModel))}

	<#list _roleIds as roleId>
		insert into Users_Roles values (0, ${roleId}, ${_userModel.userId});
	</#list>

	<#list _groupIds as groupId>
		insert into Users_Groups values (0, ${groupId}, ${_userModel.userId});
	</#list>
</#macro>