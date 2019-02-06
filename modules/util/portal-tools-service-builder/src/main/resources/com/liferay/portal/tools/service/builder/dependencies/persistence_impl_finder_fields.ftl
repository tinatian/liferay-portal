<#assign entityColumns = entityFinder.entityColumns />

<#list entityColumns as entityColumn>
	<#assign entityColumnName = entityColumn.DBName finderFieldSuffix = finderFieldSQLSuffix />

	<#include "persistence_impl_finder_field.ftl">
</#list>