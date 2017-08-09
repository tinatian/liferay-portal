<#list userDataFactory.roleModels as roleModel>
	${userDataFactory.toInsertSQL(roleModel)}

	${resourcePermissionDataFactory.generateResourcePermissionSQL(roleModel)}
</#list>