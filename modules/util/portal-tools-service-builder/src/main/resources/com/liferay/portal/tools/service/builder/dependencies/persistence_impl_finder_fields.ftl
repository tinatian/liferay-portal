<#assign entityColumns = entityFinder.entityColumns />

<#list entityColumns as entityColumn>
	<#assign entityColumnName = entityColumn.DBName />

	<#include "persistence_impl_finder_field.ftl">
</#list>