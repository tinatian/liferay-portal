<#assign hasConjunction = false />

<#if entityColumn_has_next || ((validator.isNull(finderFieldSuffix) && entityFinder.where?? && validator.isNotNull(entityFinder.getWhere())) || (validator.isNotNull(finderFieldSuffix) && entityFinder.DBWhere?? && validator.isNotNull(entityFinder.getDBWhere())))>
	<#assign hasConjunction = true />
</#if>

<#if !entityColumn.isConvertNull()>
	if (${entityColumn.name} == null) {
		<#if hasConjunction>
			query.append(_FINDER_COLUMN_${entityFinder.name?upper_case}_${entityColumn.name?upper_case}_4${finderFieldSuffix});
		<#else>
			query.append(_FINDER_COLUMN_${entityFinder.name?upper_case}_${entityColumn.name?upper_case}_1${finderFieldSuffix});
		</#if>
	}
	else
</#if>
if (${entityColumn.name}.isEmpty()) {
	<#if hasConjunction>
		query.append(_FINDER_COLUMN_${entityFinder.name?upper_case}_${entityColumn.name?upper_case}_6${finderFieldSuffix});
	<#else>
		query.append(_FINDER_COLUMN_${entityFinder.name?upper_case}_${entityColumn.name?upper_case}_3${finderFieldSuffix});
	</#if>
}
else {
	<#if hasConjunction>
		query.append(_FINDER_COLUMN_${entityFinder.name?upper_case}_${entityColumn.name?upper_case}_5${finderFieldSuffix});
	<#else>
		query.append(_FINDER_COLUMN_${entityFinder.name?upper_case}_${entityColumn.name?upper_case}_2${finderFieldSuffix});
	</#if>
}