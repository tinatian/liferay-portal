${userDataFactory.toInsertSQL(userDataFactory.companyModel)}

${resourcePermissionDataFactory.generateResourcePermissionSQL(userDataFactory.companyModel)}

${userDataFactory.toInsertSQL(userDataFactory.accountModel)}

${resourcePermissionDataFactory.generateResourcePermissionSQL(userDataFactory.accountModel)}

${userDataFactory.toInsertSQL(userDataFactory.virtualHostModel)}

${resourcePermissionDataFactory.generateResourcePermissionSQL(userDataFactory.virtualHostModel)}

${initContext.getCSVWriter("company").write(userDataFactory.companyModel.companyId + "\n")}