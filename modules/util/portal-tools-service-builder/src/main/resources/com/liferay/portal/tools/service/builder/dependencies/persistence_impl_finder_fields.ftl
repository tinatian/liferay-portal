<#if entityFinder.where?? && entityFinder.DBWhere?? && (entityFinder.where != entityFinder.DBWhere)>
	<#assign isDBWhere = true />
</#if>

<#assign entityColumns = entityFinder.entityColumns />

<#list entityColumns as entityColumn>
	<#assign entityColumnName = entityColumn.name finderFieldSuffix = "" />

	<#include "persistence_impl_finder_field.ftl">

	<#if entity.isPermissionCheckEnabled(entityFinder) && !entity.isPermissionedModel() && ((entityColumn.name != entityColumn.DBName) || (isDBWhere?? && isDBWhere))>
		<#assign entityColumnName = entityColumn.DBName finderFieldSuffix = finderFieldSQLSuffix />

		<#include "persistence_impl_finder_field.ftl">
	</#if>
</#list>