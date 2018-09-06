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

package com.liferay.structured.content.apio.internal.architect.filter;

import java.util.List;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.apache.olingo.commons.api.ex.ODataException;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author David Arques
 */
public class StructuredContentSingleEntitySchemaBasedEdmProviderTest {

	@Test
	public void testGetName() {
		StructuredContentSingleEntitySchemaBasedEdmProvider
			structuredContentSingleEntitySchemaBasedEdmProvider =
				new StructuredContentSingleEntitySchemaBasedEdmProvider();

		Assert.assertEquals(
			"StructuredContent",
			structuredContentSingleEntitySchemaBasedEdmProvider.getName());
	}

	@Test
	public void testGetSchemas() throws ODataException {
		StructuredContentSingleEntitySchemaBasedEdmProvider
			structuredContentSingleEntitySchemaBasedEdmProvider =
				new StructuredContentSingleEntitySchemaBasedEdmProvider();

		List<CsdlSchema> csdlSchemas =
			structuredContentSingleEntitySchemaBasedEdmProvider.getSchemas();

		Assert.assertEquals(csdlSchemas.toString(), 1, csdlSchemas.size());

		CsdlSchema csdlSchema = csdlSchemas.get(0);

		Assert.assertEquals("HypermediaRestApis", csdlSchema.getNamespace());

		List<CsdlEntityType> csdlEntityTypes = csdlSchema.getEntityTypes();

		Assert.assertEquals(
			csdlEntityTypes.toString(), 1, csdlEntityTypes.size());

		CsdlEntityType csdlEntityType = csdlEntityTypes.get(0);

		Assert.assertEquals("StructuredContent", csdlEntityType.getName());

		CsdlProperty csdlProperty = csdlEntityType.getProperty("title");

		Assert.assertNotNull(csdlProperty);
		Assert.assertEquals(
			EdmPrimitiveTypeKind.String.getFullQualifiedName(),
			csdlProperty.getTypeAsFQNObject());
	}

}