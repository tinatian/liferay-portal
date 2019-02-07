<#if entityFinder.where?? && entityFinder.DBWhere?? && (entityFinder.where != entityFinder.DBWhere)>
	<#assign isDBWhere = true />
</#if>

<#list entityColumns as entityColumn>
	<#if sqlQuery?? && sqlQuery && ((entityColumn.name != entityColumn.DBName) || (isDBWhere?? && isDBWhere))>
		<#assign finderFieldSuffix = finderFieldSQLSuffix />
	<#else>
		<#assign finderFieldSuffix = "" />
	</#if>

	<#include "persistence_impl_finder_col.ftl">
</#list>