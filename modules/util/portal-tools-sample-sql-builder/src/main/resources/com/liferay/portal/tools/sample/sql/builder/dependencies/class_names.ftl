<#list initRuntimeContext.classNameModelValues as classNameModelValue>
	${dataFactory.toInsertSQL(classNameModelValue)}
</#list>