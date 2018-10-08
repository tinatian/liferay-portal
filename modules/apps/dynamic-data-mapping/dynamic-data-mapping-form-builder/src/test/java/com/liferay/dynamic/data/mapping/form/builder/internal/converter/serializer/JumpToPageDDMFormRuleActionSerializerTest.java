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

package com.liferay.dynamic.data.mapping.form.builder.internal.converter.serializer;

import com.liferay.dynamic.data.mapping.form.builder.internal.converter.model.action.JumpToPageDDMFormRuleAction;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Leonardo Barros
 */
public class JumpToPageDDMFormRuleActionSerializerTest {

	@Test
	public void testSerialize() {
		JumpToPageDDMFormRuleAction jumpToPageDDMFormRuleAction =
			new JumpToPageDDMFormRuleAction();

		jumpToPageDDMFormRuleAction.setSource("1");
		jumpToPageDDMFormRuleAction.setTarget("3");

		JumpToPageDDMFormRuleActionSerializer
			jumpToPageDDMFormRuleActionSerializer =
				new JumpToPageDDMFormRuleActionSerializer(
					jumpToPageDDMFormRuleAction);

		String result = jumpToPageDDMFormRuleActionSerializer.serialize(null);

		Assert.assertEquals("jumpPage(1, 3)", result);
	}

}