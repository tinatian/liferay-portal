/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.security.permission.SimplePermissionChecker;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchPermissionCheckFinderEntryException;
import com.liferay.portal.tools.service.builder.test.model.PermissionCheckFinderEntry;
import com.liferay.portal.tools.service.builder.test.service.PermissionCheckFinderEntryLocalServiceUtil;
import com.liferay.portal.tools.service.builder.test.service.persistence.PermissionCheckFinderEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.PermissionCheckFinderEntryUtil;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @generated
 */
@RunWith(Arquillian.class)
public class PermissionCheckFinderEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.tools.service.builder.test.service"));

	@Before
	public void setUp() {
		_persistence = PermissionCheckFinderEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<PermissionCheckFinderEntry> iterator =
			_permissionCheckFinderEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}

		for (UserGroupRole userGroupRole : _userGroupRoles) {
			UserGroupRoleLocalServiceUtil.deleteUserGroupRole(userGroupRole);
		}

		for (Role role : _roles) {
			RoleLocalServiceUtil.deleteRole(role);
		}

		for (Group group : _groups) {
			GroupLocalServiceUtil.deleteGroup(group);
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PermissionCheckFinderEntry permissionCheckFinderEntry =
			_persistence.create(pk);

		Assert.assertNotNull(permissionCheckFinderEntry);

		Assert.assertEquals(permissionCheckFinderEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		PermissionCheckFinderEntry newPermissionCheckFinderEntry =
			addPermissionCheckFinderEntry();

		_persistence.remove(newPermissionCheckFinderEntry);

		PermissionCheckFinderEntry existingPermissionCheckFinderEntry =
			_persistence.fetchByPrimaryKey(
				newPermissionCheckFinderEntry.getPrimaryKey());

		Assert.assertNull(existingPermissionCheckFinderEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addPermissionCheckFinderEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PermissionCheckFinderEntry newPermissionCheckFinderEntry =
			_persistence.create(pk);

		newPermissionCheckFinderEntry.setGroupId(RandomTestUtil.nextLong());

		newPermissionCheckFinderEntry.setInteger(RandomTestUtil.nextInt());

		newPermissionCheckFinderEntry.setName(RandomTestUtil.randomString());

		newPermissionCheckFinderEntry.setType(RandomTestUtil.randomString());

		_permissionCheckFinderEntries.add(
			_persistence.update(newPermissionCheckFinderEntry));

		PermissionCheckFinderEntry existingPermissionCheckFinderEntry =
			_persistence.findByPrimaryKey(
				newPermissionCheckFinderEntry.getPrimaryKey());

		Assert.assertEquals(
			existingPermissionCheckFinderEntry.
				getPermissionCheckFinderEntryId(),
			newPermissionCheckFinderEntry.getPermissionCheckFinderEntryId());
		Assert.assertEquals(
			existingPermissionCheckFinderEntry.getGroupId(),
			newPermissionCheckFinderEntry.getGroupId());
		Assert.assertEquals(
			existingPermissionCheckFinderEntry.getInteger(),
			newPermissionCheckFinderEntry.getInteger());
		Assert.assertEquals(
			existingPermissionCheckFinderEntry.getName(),
			newPermissionCheckFinderEntry.getName());
		Assert.assertEquals(
			existingPermissionCheckFinderEntry.getType(),
			newPermissionCheckFinderEntry.getType());
	}

	@Test
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testCountByGroupIdArrayable() throws Exception {
		_persistence.countByGroupId(new long[] {RandomTestUtil.nextLong(), 0L});
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		PermissionCheckFinderEntry newPermissionCheckFinderEntry =
			addPermissionCheckFinderEntry();

		PermissionCheckFinderEntry existingPermissionCheckFinderEntry =
			_persistence.findByPrimaryKey(
				newPermissionCheckFinderEntry.getPrimaryKey());

		Assert.assertEquals(
			existingPermissionCheckFinderEntry, newPermissionCheckFinderEntry);
	}

	@Test(expected = NoSuchPermissionCheckFinderEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	@Test
	public void testFilterFindByGroupId() throws Exception {
		long[] roleIds = new long[1];

		PermissionThreadLocal.setPermissionChecker(
			new SimplePermissionChecker() {
				{
					init(TestPropsValues.getUser());
				}

				@Override
				public boolean isCompanyAdmin(long companyId) {
					return false;
				}

				@Override
				public boolean isGroupAdmin(long groupId) {
					return false;
				}

				@Override
				public boolean isGroupOwner(long groupId) {
					return false;
				}

				@Override
				public long[] getRoleIds(long userId, long groupId) {
					return roleIds;
				}
			});

		Assert.assertTrue(InlineSQLHelperUtil.isEnabled(0));

		_persistence.filterFindByGroupId(
			0, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		_persistence.filterFindByGroupId(
			0, QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());

		// Test scope: GROUP

		Group group1 = GroupTestUtil.addGroup();

		_groups.add(group1);

		PermissionCheckFinderEntry newPermissionCheckFinderEntry1 =
			_addPermissionCheckFinderEntry(group1.getGroupId(), 1);

		Group group2 = GroupTestUtil.addGroup();

		_groups.add(group2);

		PermissionCheckFinderEntry newPermissionCheckFinderEntry2 =
			_addPermissionCheckFinderEntry(group2.getGroupId(), 2);

		Group group3 = GroupTestUtil.addGroup();

		_groups.add(group3);

		_addPermissionCheckFinderEntry(group3.getGroupId(), 3);

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_SITE);

		roleIds[0] = role.getRoleId();

		_roles.add(role);

		_userGroupRoles.addAll(
			UserGroupRoleLocalServiceUtil.addUserGroupRoles(
				new long[] {TestPropsValues.getUserId()}, group1.getGroupId(),
				role.getRoleId()));
		_userGroupRoles.addAll(
			UserGroupRoleLocalServiceUtil.addUserGroupRoles(
				new long[] {TestPropsValues.getUserId()}, group2.getGroupId(),
				role.getRoleId()));

		ResourcePermissionLocalServiceUtil.addResourcePermission(
			TestPropsValues.getCompanyId(),
			PermissionCheckFinderEntry.class.getName(),
			ResourceConstants.SCOPE_GROUP,
			String.valueOf(group1.getGroupId()), role.getRoleId(),
			ActionKeys.VIEW);
		ResourcePermissionLocalServiceUtil.addResourcePermission(
			TestPropsValues.getCompanyId(),
			PermissionCheckFinderEntry.class.getName(),
			ResourceConstants.SCOPE_GROUP,
			String.valueOf(group2.getGroupId()), role.getRoleId(),
			ActionKeys.VIEW);

		Assert.assertTrue(InlineSQLHelperUtil.isEnabled(group1.getGroupId()));
		Assert.assertTrue(InlineSQLHelperUtil.isEnabled(group2.getGroupId()));
		Assert.assertTrue(InlineSQLHelperUtil.isEnabled(group3.getGroupId()));

		Assert.assertEquals(
			Arrays.asList(newPermissionCheckFinderEntry1),
			_persistence.filterFindByGroupId(
				group1.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				null));
		Assert.assertEquals(
			Arrays.asList(newPermissionCheckFinderEntry2),
			_persistence.filterFindByGroupId(
				group2.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				null));
		Assert.assertEquals(
			Collections.emptyList(),
			_persistence.filterFindByGroupId(
				group3.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				null));

		Assert.assertEquals(
			Arrays.asList(newPermissionCheckFinderEntry1),
			_persistence.filterFindByGroupId(
				new long[]{group1.getGroupId()}, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null));
		Assert.assertEquals(
			Arrays.asList(newPermissionCheckFinderEntry2),
			_persistence.filterFindByGroupId(
				new long[]{group2.getGroupId()}, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null));
		Assert.assertEquals(
			Collections.emptyList(),
			_persistence.filterFindByGroupId(
				new long[]{group3.getGroupId()}, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null));

		Assert.assertEquals(
			Arrays.asList(
				newPermissionCheckFinderEntry1, newPermissionCheckFinderEntry2),
			_persistence.filterFindByGroupId(
				new long[]{
					group1.getGroupId(), group2.getGroupId()},
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null));

		Assert.assertEquals(
			Arrays.asList(newPermissionCheckFinderEntry1),
			_persistence.filterFindByGroupId(
				new long[]{
					group1.getGroupId(), group3.getGroupId()},
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null));

		// Test Scope: GROUP_TEMPLATE

		Group group4 = GroupTestUtil.addGroup();

		_groups.add(group4);

		PermissionCheckFinderEntry newPermissionCheckFinderEntry4 =
			_addPermissionCheckFinderEntry(group4.getGroupId(), 1);

		Group group5 = GroupTestUtil.addGroup();

		_groups.add(group5);

		PermissionCheckFinderEntry newPermissionCheckFinderEntry5 =
			_addPermissionCheckFinderEntry(group5.getGroupId(), 2);

		ResourcePermissionLocalServiceUtil.addResourcePermission(
			CompanyThreadLocal.getCompanyId(),
			PermissionCheckFinderEntry.class.getName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE,
			String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
			role.getRoleId(), ActionKeys.VIEW);

		Assert.assertTrue(InlineSQLHelperUtil.isEnabled(group4.getGroupId()));
		Assert.assertTrue(InlineSQLHelperUtil.isEnabled(group5.getGroupId()));

		Assert.assertEquals(
			Arrays.asList(newPermissionCheckFinderEntry4),
			_persistence.filterFindByGroupId(
				group4.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				null));
		Assert.assertEquals(
			Arrays.asList(newPermissionCheckFinderEntry5),
			_persistence.filterFindByGroupId(
				group5.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				null));
		Assert.assertEquals(
			Collections.emptyList(),
			_persistence.filterFindByGroupId(
				0L, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null));

		Assert.assertEquals(
			Arrays.asList(newPermissionCheckFinderEntry4),
			_persistence.filterFindByGroupId(
				new long[]{group4.getGroupId()}, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null));
		Assert.assertEquals(
			Arrays.asList(newPermissionCheckFinderEntry5),
			_persistence.filterFindByGroupId(
				new long[]{group5.getGroupId()}, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null));
		Assert.assertEquals(
			Collections.emptyList(),
			_persistence.filterFindByGroupId(
				new long[]{0L}, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null));

		Assert.assertEquals(
			Arrays.asList(
				newPermissionCheckFinderEntry4, newPermissionCheckFinderEntry5),
			_persistence.filterFindByGroupId(
				new long[]{
					group4.getGroupId(), group5.getGroupId()},
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null));
		Assert.assertEquals(
			Arrays.asList(newPermissionCheckFinderEntry4),
			_persistence.filterFindByGroupId(
				new long[]{group4.getGroupId(), 0L},
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null));
	}

	protected OrderByComparator<PermissionCheckFinderEntry>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"PermissionCheckFinderEntry", "permissionCheckFinderEntryId", true,
			"groupId", true, "integer", true, "name", true, "type", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		PermissionCheckFinderEntry newPermissionCheckFinderEntry =
			addPermissionCheckFinderEntry();

		PermissionCheckFinderEntry existingPermissionCheckFinderEntry =
			_persistence.fetchByPrimaryKey(
				newPermissionCheckFinderEntry.getPrimaryKey());

		Assert.assertEquals(
			existingPermissionCheckFinderEntry, newPermissionCheckFinderEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PermissionCheckFinderEntry missingPermissionCheckFinderEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingPermissionCheckFinderEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		PermissionCheckFinderEntry newPermissionCheckFinderEntry1 =
			addPermissionCheckFinderEntry();
		PermissionCheckFinderEntry newPermissionCheckFinderEntry2 =
			addPermissionCheckFinderEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPermissionCheckFinderEntry1.getPrimaryKey());
		primaryKeys.add(newPermissionCheckFinderEntry2.getPrimaryKey());

		Map<Serializable, PermissionCheckFinderEntry>
			permissionCheckFinderEntries = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, permissionCheckFinderEntries.size());
		Assert.assertEquals(
			newPermissionCheckFinderEntry1,
			permissionCheckFinderEntries.get(
				newPermissionCheckFinderEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newPermissionCheckFinderEntry2,
			permissionCheckFinderEntries.get(
				newPermissionCheckFinderEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, PermissionCheckFinderEntry>
			permissionCheckFinderEntries = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(permissionCheckFinderEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		PermissionCheckFinderEntry newPermissionCheckFinderEntry =
			addPermissionCheckFinderEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPermissionCheckFinderEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, PermissionCheckFinderEntry>
			permissionCheckFinderEntries = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, permissionCheckFinderEntries.size());
		Assert.assertEquals(
			newPermissionCheckFinderEntry,
			permissionCheckFinderEntries.get(
				newPermissionCheckFinderEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, PermissionCheckFinderEntry>
			permissionCheckFinderEntries = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(permissionCheckFinderEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		PermissionCheckFinderEntry newPermissionCheckFinderEntry =
			addPermissionCheckFinderEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPermissionCheckFinderEntry.getPrimaryKey());

		Map<Serializable, PermissionCheckFinderEntry>
			permissionCheckFinderEntries = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, permissionCheckFinderEntries.size());
		Assert.assertEquals(
			newPermissionCheckFinderEntry,
			permissionCheckFinderEntries.get(
				newPermissionCheckFinderEntry.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			PermissionCheckFinderEntryLocalServiceUtil.
				getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<PermissionCheckFinderEntry>() {

				@Override
				public void performAction(
					PermissionCheckFinderEntry permissionCheckFinderEntry) {

					Assert.assertNotNull(permissionCheckFinderEntry);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		PermissionCheckFinderEntry newPermissionCheckFinderEntry =
			addPermissionCheckFinderEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PermissionCheckFinderEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"permissionCheckFinderEntryId",
				newPermissionCheckFinderEntry.
					getPermissionCheckFinderEntryId()));

		List<PermissionCheckFinderEntry> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		PermissionCheckFinderEntry existingPermissionCheckFinderEntry =
			result.get(0);

		Assert.assertEquals(
			existingPermissionCheckFinderEntry, newPermissionCheckFinderEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PermissionCheckFinderEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"permissionCheckFinderEntryId", RandomTestUtil.nextLong()));

		List<PermissionCheckFinderEntry> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		PermissionCheckFinderEntry newPermissionCheckFinderEntry =
			addPermissionCheckFinderEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PermissionCheckFinderEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("permissionCheckFinderEntryId"));

		Object newPermissionCheckFinderEntryId =
			newPermissionCheckFinderEntry.getPermissionCheckFinderEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"permissionCheckFinderEntryId",
				new Object[] {newPermissionCheckFinderEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingPermissionCheckFinderEntryId = result.get(0);

		Assert.assertEquals(
			existingPermissionCheckFinderEntryId,
			newPermissionCheckFinderEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PermissionCheckFinderEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("permissionCheckFinderEntryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"permissionCheckFinderEntryId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected PermissionCheckFinderEntry addPermissionCheckFinderEntry()
		throws Exception {

		return _addPermissionCheckFinderEntry(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());
	}

	private PermissionCheckFinderEntry _addPermissionCheckFinderEntry(
		long groupId, int integer) throws Exception {

		long pk = RandomTestUtil.nextLong();

		PermissionCheckFinderEntry permissionCheckFinderEntry =
			_persistence.create(pk);

		permissionCheckFinderEntry.setGroupId(groupId);

		permissionCheckFinderEntry.setInteger(integer);

		permissionCheckFinderEntry.setName(RandomTestUtil.randomString());

		permissionCheckFinderEntry.setType(RandomTestUtil.randomString());

		permissionCheckFinderEntry = _persistence.update(
			permissionCheckFinderEntry);

		_permissionCheckFinderEntries.add(permissionCheckFinderEntry);

		return permissionCheckFinderEntry;
	}

	private List<Group> _groups = new ArrayList<>();
	private List<PermissionCheckFinderEntry> _permissionCheckFinderEntries =
		new ArrayList<PermissionCheckFinderEntry>();
	private List<Role> _roles = new ArrayList<>();
	private List<UserGroupRole> _userGroupRoles = new ArrayList<>();
	private PermissionCheckFinderEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}