<#assign counterModels = counterDataFactory.newCounterModels() />

<#list counterModels as counterModel>
	<#if '${counterModel.name}' == 'com.liferay.counter.kernel.model.Counter'>
		update Counter set currentId = ${counterModel.currentId} where name = '${counterModel.name}';
	<#else>
		${counterDataFactory.toInsertSQL(counterModel)}

		${resourcePermissionDataFactory.generateResourcePermissionSQL(counterModel)}
	</#if>
</#list>