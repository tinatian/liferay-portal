/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.tools.sample.sql.builder;

import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.model.AccountModel;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyModel;
import com.liferay.portal.kernel.model.ContactConstants;
import com.liferay.portal.kernel.model.ContactModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.GroupModel;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.RoleModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserModel;
import com.liferay.portal.kernel.model.VirtualHostModel;
import com.liferay.portal.kernel.security.auth.FullNameGenerator;
import com.liferay.portal.kernel.security.auth.FullNameGeneratorFactory;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.impl.AccountModelImpl;
import com.liferay.portal.model.impl.CompanyModelImpl;
import com.liferay.portal.model.impl.ContactModelImpl;
import com.liferay.portal.model.impl.GroupModelImpl;
import com.liferay.portal.model.impl.RoleModelImpl;
import com.liferay.portal.model.impl.UserModelImpl;
import com.liferay.portal.model.impl.VirtualHostModelImpl;
import com.liferay.util.SimpleCounter;

import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Lily Chi
 */
public class UserDataFactory extends BaseDataFactory {

	public UserDataFactory(
			InitRuntimeContext initRuntimeContext,
			InitPropertiesContext initPropertiesContext)
		throws Exception {

		super(initRuntimeContext, initPropertiesContext);

		SimpleCounter counter = initRuntimeContext.getCounter();

		globalGroupId = counter.get();
		guestGroupId = counter.get();

		_initAccountModel();
		_initCompanyModel();
		_initGroupModels();
		_initUserFirstNames();
		_initUserLastNames();
		_initUserModels();
		_initRoleModels();
		_initVirtualHostModel();
	}

	public AccountModel getAccountModel() {
		return accountModel;
	}

	public RoleModel getAdministratorRoleModel() {
		return administratorRoleModel;
	}

	public CompanyModel getCompanyModel() {
		return companyModel;
	}

	public UserModel getDefaultUserModel() {
		return defaultUserModel;
	}

	public List<String> getFirstNames() {
		return firstNames;
	}

	public long getGlobalGroupId() {
		return globalGroupId;
	}

	public GroupModel getGlobalGroupModel() {
		return globalGroupModel;
	}

	public long getGroupClassNameId() {
		return getClassNameId(Group.class);
	}

	public List<GroupModel> getGroupModels() {
		return groupModels;
	}

	public GroupModel getGuestGroupModel() {
		return guestGroupModel;
	}

	public RoleModel getGuestRoleModel() {
		return guestRoleModel;
	}

	public UserModel getGuestUserModel() {
		return guestUserModel;
	}

	public List<String> getLastNames() {
		return lastNames;
	}

	public List<Long> getNewUserGroupIds(long groupId) {
		int maxUserToGroupCount =
			initPropertiesContext.getMaxUserToGroupCount();
		int maxGroupsCount = initPropertiesContext.getMaxGroupsCount();

		List<Long> groupIds = new ArrayList<>(maxUserToGroupCount + 1);

		groupIds.add(guestGroupModel.getGroupId());

		if ((groupId + maxUserToGroupCount) > maxGroupsCount) {
			groupId = groupId - maxUserToGroupCount + 1;
		}

		for (int i = 0; i < maxUserToGroupCount; i++) {
			groupIds.add(groupId + i);
		}

		return groupIds;
	}

	public RoleModel getOwnerRoleModel() {
		return ownerRoleModel;
	}

	public RoleModel getPowerUserRoleModel() {
		return powerUserRoleModel;
	}

	public List<RoleModel> getRoleModels() {
		return roleModels;
	}

	public UserModel getSampleUserModel() {
		return sampleUserModel;
	}

	public RoleModel getSiteMemberRoleModel() {
		return siteMemberRoleModel;
	}

	public RoleModel getUserRoleModel() {
		return userRoleModel;
	}

	public VirtualHostModel getVirtualHostModel() {
		return virtualHostModel;
	}

	public ContactModel newContactModel(UserModel userModel) {
		ContactModel contactModel = new ContactModelImpl();

		contactModel.setContactId(userModel.getContactId());
		contactModel.setCompanyId(userModel.getCompanyId());
		contactModel.setUserId(userModel.getUserId());

		FullNameGenerator fullNameGenerator =
			FullNameGeneratorFactory.getInstance();

		String fullName = fullNameGenerator.getFullName(
			userModel.getFirstName(), userModel.getMiddleName(),
			userModel.getLastName());

		contactModel.setUserName(fullName);

		contactModel.setCreateDate(new Date());
		contactModel.setModifiedDate(new Date());
		contactModel.setClassNameId(getClassNameId(User.class));
		contactModel.setClassPK(userModel.getUserId());
		contactModel.setAccountId(initRuntimeContext.getAccountId());
		contactModel.setParentContactId(
			ContactConstants.DEFAULT_PARENT_CONTACT_ID);
		contactModel.setEmailAddress(userModel.getEmailAddress());
		contactModel.setFirstName(userModel.getFirstName());
		contactModel.setLastName(userModel.getLastName());
		contactModel.setMale(true);
		contactModel.setBirthday(new Date());

		return contactModel;
	}

	public GroupModel newGroupModel(UserModel userModel) throws Exception {
		SimpleCounter counter = initRuntimeContext.getCounter();

		return _newGroupModel(
			counter.get(), getClassNameId(User.class), userModel.getUserId(),
			userModel.getScreenName(), false);
	}

	public List<UserModel> newUserModels() {
		int maxUserCount = initPropertiesContext.getMaxUserCount();

		SimpleCounter userScreenNameCounter =
			initRuntimeContext.getUserScreenNameCounter();

		List<UserModel> userModels = new ArrayList<>(maxUserCount);

		SimpleCounter counter = initRuntimeContext.getCounter();

		for (int i = 0; i < initPropertiesContext.getMaxUserCount(); i++) {
			String[] userName = nextUserName(i);
			String lastName = "test" + userScreenNameCounter.get();

			userModels.add(
				newUserModel(
					counter.get(), userName[0], userName[1], lastName, false));
		}

		return userModels;
	}

	protected UserModel newUserModel(
		long userId, String firstName, String lastName, String screenName,
		boolean defaultUser) {

		String greeting =
			DataFactoryConstants.GREETING_PREFIX + screenName +
				StringPool.EXCLAMATION;

		if (Validator.isNull(screenName)) {
			screenName = String.valueOf(userId);
		}

		SimpleCounter counter = initRuntimeContext.getCounter();

		UserModel userModel = new UserModelImpl();

		userModel.setUuid(SequentialUUID.generate());
		userModel.setUserId(userId);
		userModel.setCompanyId(initRuntimeContext.getCompanyId());
		userModel.setCreateDate(new Date());
		userModel.setModifiedDate(new Date());
		userModel.setDefaultUser(defaultUser);
		userModel.setContactId(counter.get());
		userModel.setPassword(DataFactoryConstants.USER_INFO);
		userModel.setPasswordModifiedDate(new Date());
		userModel.setReminderQueryQuestion(
			DataFactoryConstants.REMINDER_QUERY_QUESTION);
		userModel.setReminderQueryAnswer(screenName);
		userModel.setEmailAddress(
			screenName + DataFactoryConstants.EMAIL_POSTFIX);
		userModel.setScreenName(screenName);
		userModel.setLanguageId(DataFactoryConstants.LANGUAGE_ID);
		userModel.setGreeting(greeting);
		userModel.setFirstName(firstName);
		userModel.setLastName(lastName);
		userModel.setLoginDate(new Date());
		userModel.setLastLoginDate(new Date());
		userModel.setLastFailedLoginDate(new Date());
		userModel.setLockoutDate(new Date());
		userModel.setAgreedToTermsOfUse(true);
		userModel.setEmailAddressVerified(true);

		return userModel;
	}

	protected String[] nextUserName(long index) {
		String[] userName = new String[2];

		int firstNameSize = firstNames.size();
		int lastNameSize = lastNames.size();

		int firstNameIndex = (int)(index / lastNameSize) % firstNameSize;
		int lastNameIndex = (int)(index % lastNameSize);

		userName[0] = firstNames.get(firstNameIndex);
		userName[1] = lastNames.get(lastNameIndex);

		return userName;
	}

	protected AccountModel accountModel;
	protected RoleModel administratorRoleModel;
	protected CompanyModel companyModel;
	protected UserModel defaultUserModel;
	protected List<String> firstNames;
	protected final long globalGroupId;
	protected GroupModel globalGroupModel;
	protected List<GroupModel> groupModels;
	protected final long guestGroupId;
	protected GroupModel guestGroupModel;
	protected RoleModel guestRoleModel;
	protected UserModel guestUserModel;
	protected List<String> lastNames;
	protected RoleModel ownerRoleModel;
	protected RoleModel powerUserRoleModel;
	protected List<RoleModel> roleModels;
	protected UserModel sampleUserModel;
	protected RoleModel siteMemberRoleModel;
	protected RoleModel userRoleModel;
	protected VirtualHostModel virtualHostModel;

	private void _initAccountModel() {
		accountModel = new AccountModelImpl();

		accountModel.setAccountId(initRuntimeContext.getAccountId());
		accountModel.setCompanyId(initRuntimeContext.getCompanyId());
		accountModel.setCreateDate(new Date());
		accountModel.setModifiedDate(new Date());
		accountModel.setName(DataFactoryConstants.ACCOUNT_NAME);
		accountModel.setLegalName(DataFactoryConstants.ACCOUNT_LEGAL_NAME);
	}

	private void _initCompanyModel() {
		companyModel = new CompanyModelImpl();

		companyModel.setCompanyId(initRuntimeContext.getCompanyId());
		companyModel.setAccountId(initRuntimeContext.getAccountId());
		companyModel.setWebId(DataFactoryConstants.COMPANY_WEBID);
		companyModel.setMx(DataFactoryConstants.COMPANY_WEBID);
		companyModel.setActive(true);
	}

	private GroupModel _initGroupModel(
			long groupId, long classNameId, long classPK, String name,
			boolean site)
		throws Exception {

		GroupModel globalGroupModel = _newGroupModel(
			groupId, classNameId, classPK, name, site);

		return globalGroupModel;
	}

	private void _initGroupModels() throws Exception {
		int maxGroupsCount = initPropertiesContext.getMaxGroupsCount();

		globalGroupModel = _initGroupModel(
			globalGroupId, getClassNameId(Company.class),
			initRuntimeContext.getCompanyId(), GroupConstants.GLOBAL, false);

		guestGroupModel = _initGroupModel(
			guestGroupId, getGroupClassNameId(), guestGroupId,
			GroupConstants.GUEST, true);

		groupModels = new ArrayList<>(maxGroupsCount);

		for (int i = 1; i <= maxGroupsCount; i++) {
			GroupModel groupModel = _initGroupModel(
				i, getGroupClassNameId(), i,
				DataFactoryConstants.GROUP_NAME_PREFIX + i, true);

			groupModels.add(groupModel);
		}
	}

	private void _initRoleModels() {
		roleModels = new ArrayList<>();

		// Administrator

		administratorRoleModel = _newRoleModel(
			RoleConstants.ADMINISTRATOR, RoleConstants.TYPE_REGULAR);

		roleModels.add(administratorRoleModel);

		// Guest

		guestRoleModel = _newRoleModel(
			RoleConstants.GUEST, RoleConstants.TYPE_REGULAR);

		roleModels.add(guestRoleModel);

		// Organization Administrator

		RoleModel organizationAdministratorRoleModel = _newRoleModel(
			RoleConstants.ORGANIZATION_ADMINISTRATOR,
			RoleConstants.TYPE_ORGANIZATION);

		roleModels.add(organizationAdministratorRoleModel);

		// Organization Owner

		RoleModel organizationOwnerRoleModel = _newRoleModel(
			RoleConstants.ORGANIZATION_OWNER, RoleConstants.TYPE_ORGANIZATION);

		roleModels.add(organizationOwnerRoleModel);

		// Organization User

		RoleModel organizationUserRoleModel = _newRoleModel(
			RoleConstants.ORGANIZATION_USER, RoleConstants.TYPE_ORGANIZATION);

		roleModels.add(organizationUserRoleModel);

		// Owner

		ownerRoleModel = _newRoleModel(
			RoleConstants.OWNER, RoleConstants.TYPE_REGULAR);

		roleModels.add(ownerRoleModel);

		// Power User

		powerUserRoleModel = _newRoleModel(
			RoleConstants.POWER_USER, RoleConstants.TYPE_REGULAR);

		roleModels.add(powerUserRoleModel);

		// Site Administrator

		RoleModel siteAdministratorRoleModel = _newRoleModel(
			RoleConstants.SITE_ADMINISTRATOR, RoleConstants.TYPE_SITE);

		roleModels.add(siteAdministratorRoleModel);

		// Site Member

		siteMemberRoleModel = _newRoleModel(
			RoleConstants.SITE_MEMBER, RoleConstants.TYPE_SITE);

		roleModels.add(siteMemberRoleModel);

		// Site Owner

		RoleModel siteOwnerRoleModel = _newRoleModel(
			RoleConstants.SITE_OWNER, RoleConstants.TYPE_SITE);

		roleModels.add(siteOwnerRoleModel);

		// User

		userRoleModel = _newRoleModel(
			RoleConstants.USER, RoleConstants.TYPE_REGULAR);

		roleModels.add(userRoleModel);
	}

	private void _initUserFirstNames() throws IOException {
		firstNames = new ArrayList<>();

		UnsyncBufferedReader unsyncBufferedReader = new UnsyncBufferedReader(
			new InputStreamReader(
				getResourceInputStream(DataFactoryConstants.FIRST_NAME_LIST)));

		String line = null;

		while ((line = unsyncBufferedReader.readLine()) != null) {
			firstNames.add(line);
		}

		unsyncBufferedReader.close();
	}

	private void _initUserLastNames() throws IOException {
		lastNames = new ArrayList<>();

		UnsyncBufferedReader unsyncBufferedReader = new UnsyncBufferedReader(
			new InputStreamReader(
				getResourceInputStream(DataFactoryConstants.LAST_NAME_LIST)));

		String line = null;

		while ((line = unsyncBufferedReader.readLine()) != null) {
			lastNames.add(line);
		}

		unsyncBufferedReader.close();
	}

	private void _initUserModels() {
		SimpleCounter counter = initRuntimeContext.getCounter();

		long defaultUserId = initRuntimeContext.getDefaultUserId();

		defaultUserModel = newUserModel(
			defaultUserId, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			true);

		guestUserModel = newUserModel(
			counter.get(), "Test", "Test", "Test", false);

		sampleUserModel = newUserModel(
			initRuntimeContext.getSampleUserId(),
			DataFactoryConstants.SAMPLE_USER_NAME,
			DataFactoryConstants.SAMPLE_USER_NAME,
			DataFactoryConstants.SAMPLE_USER_NAME, false);
	}

	private void _initVirtualHostModel() {
		virtualHostModel = new VirtualHostModelImpl();

		SimpleCounter counter = initRuntimeContext.getCounter();

		virtualHostModel.setVirtualHostId(counter.get());

		virtualHostModel.setCompanyId(initRuntimeContext.getCompanyId());
		virtualHostModel.setHostname(
			initPropertiesContext.getVirtualHostname());
	}

	private GroupModel _newGroupModel(
			long groupId, long classNameId, long classPK, String name,
			boolean site)
		throws Exception {

		GroupModel groupModel = new GroupModelImpl();

		groupModel.setUuid(SequentialUUID.generate());
		groupModel.setGroupId(groupId);
		groupModel.setCompanyId(initRuntimeContext.getCompanyId());
		groupModel.setCreatorUserId(initRuntimeContext.getSampleUserId());
		groupModel.setClassNameId(classNameId);
		groupModel.setClassPK(classPK);
		groupModel.setTreePath(
			StringPool.SLASH + groupModel.getGroupId() + StringPool.SLASH);
		groupModel.setGroupKey(name);
		groupModel.setName(name);
		groupModel.setManualMembership(true);
		groupModel.setMembershipRestriction(
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION);
		groupModel.setFriendlyURL(
			StringPool.FORWARD_SLASH +
				FriendlyURLNormalizerUtil.normalize(name));
		groupModel.setSite(site);
		groupModel.setActive(true);

		return groupModel;
	}

	private RoleModel _newRoleModel(String name, int type) {
		RoleModel roleModel = new RoleModelImpl();

		SimpleCounter counter = initRuntimeContext.getCounter();

		long classNameId = getClassNameId(Role.class);

		roleModel.setUuid(SequentialUUID.generate());
		roleModel.setRoleId(counter.get());
		roleModel.setCompanyId(initRuntimeContext.getCompanyId());
		roleModel.setUserId(initRuntimeContext.getSampleUserId());
		roleModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		roleModel.setCreateDate(new Date());
		roleModel.setModifiedDate(new Date());
		roleModel.setClassNameId(classNameId);
		roleModel.setClassPK(roleModel.getRoleId());
		roleModel.setName(name);
		roleModel.setType(type);

		return roleModel;
	}

}