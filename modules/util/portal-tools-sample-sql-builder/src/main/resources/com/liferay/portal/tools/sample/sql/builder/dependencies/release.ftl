<#assign releaseModels = releaseDataFactory.newReleaseModels() />

<#list releaseModels as releaseModel>
	insert into Release_ values (${releaseModel.mvccVersion}, ${releaseModel.releaseId}, '${initContext.getDateString(releaseModel.createDate)}', '${initContext.getDateString(releaseModel.modifiedDate)}', '${releaseModel.servletContextName}', '${releaseModel.schemaVersion}', ${releaseModel.buildNumber}, '${initContext.getDateString(releaseModel.buildDate)}', ${releaseModel.verified?string}, ${releaseModel.state}, '${releaseModel.testString}');
</#list>