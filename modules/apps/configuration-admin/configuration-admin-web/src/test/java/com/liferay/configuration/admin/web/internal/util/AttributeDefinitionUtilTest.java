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

package com.liferay.configuration.admin.web.internal.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.util.MockHelperUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Vector;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.osgi.service.cm.Configuration;
import org.osgi.service.metatype.AttributeDefinition;

/**
 * @author André de Oliveira
 */
public class AttributeDefinitionUtilTest {

	@Before
	public void setUp() throws Exception {
		MockHelperUtil.setMethodAlwaysReturnExpected(
			_attributeDefinition, "getID", _ID);

		MockHelperUtil.setMethodAlwaysReturnExpected(
			_configuration, "getProperties", _properties);
	}

	@Test
	public void testDefaultValueArray() throws Exception {
		mockCardinality(Integer.MAX_VALUE);

		mockDefaultValue("A", "B", "C");

		assertDefaultValue("A", "B", "C");
	}

	@Test
	public void testDefaultValueBlankString() throws Exception {
		mockDefaultValue(StringPool.BLANK);

		assertDefaultValue(StringPool.BLANK);
	}

	@Test
	public void testDefaultValueEmpty() throws Exception {
		MockHelperUtil.setMethodAlwaysReturnExpected(
			_attributeDefinition, "getDefaultValue", new String[0]);

		assertDefaultValue(StringPool.BLANK);
	}

	@Test
	public void testDefaultValueWithPipesArray() throws Exception {
		mockCardinality(42);

		mockDefaultValue("A|B|C");

		assertDefaultValue("A", "B", "C");
	}

	@Test
	public void testDefaultValueWithPipesString() throws Exception {
		mockDefaultValue("A|B|C");

		assertDefaultValue("A|B|C");
	}

	@Test
	public void testPropertyArray() throws Exception {
		mockCardinality(2);

		mockProperty(new Object[] {false, true});

		assertProperty("false", "true");
	}

	@Test
	public void testPropertyEmpty() {
		assertProperty();
	}

	@Test
	public void testPropertyObject() {
		mockProperty(42);

		assertProperty("42");
	}

	@Test
	public void testPropertyVector() throws Exception {
		mockCardinality(-3);

		mockProperty(new Vector<Integer>(Arrays.asList(1, 2, 3)));

		assertProperty("1", "2", "3");
	}

	protected void assertDefaultValue(String... expecteds) {
		Assert.assertArrayEquals(
			expecteds,
			AttributeDefinitionUtil.getDefaultValue(_attributeDefinition));
	}

	protected void assertProperty(String... expecteds) {
		Assert.assertArrayEquals(
			expecteds,
			AttributeDefinitionUtil.getProperty(
				_attributeDefinition, _configuration));
	}

	protected void mockCardinality(int value) throws Exception {
		MockHelperUtil.setMethodAlwaysReturnExpected(
			_attributeDefinition, "getCardinality", value);
	}

	protected void mockDefaultValue(String... value) throws Exception {
		MockHelperUtil.setMethodAlwaysReturnExpected(
			_attributeDefinition, "getDefaultValue", value);
	}

	protected void mockProperty(Object value) {
		_properties.put(_ID, value);
	}

	private static final String _ID = RandomTestUtil.randomString();

	private final AttributeDefinition _attributeDefinition =
		MockHelperUtil.initMock(AttributeDefinition.class);
	private final Configuration _configuration = MockHelperUtil.initMock(
		Configuration.class);
	private final Dictionary<String, Object> _properties = new Hashtable<>();

}