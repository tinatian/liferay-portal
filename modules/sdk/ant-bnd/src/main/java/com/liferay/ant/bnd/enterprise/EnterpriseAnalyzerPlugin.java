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

package com.liferay.ant.bnd.enterprise;

import aQute.bnd.component.DSAnnotationReader;
import aQute.bnd.component.HeaderReader;
import aQute.bnd.component.TagResource;
import aQute.bnd.header.Attrs;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Constants;
import aQute.bnd.osgi.EmbeddedResource;
import aQute.bnd.osgi.Jar;
import aQute.bnd.service.AnalyzerPlugin;
import aQute.bnd.version.Version;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * @author Tina Tian
 */
public class EnterpriseAnalyzerPlugin implements AnalyzerPlugin {

	@Override
	public boolean analyzeJar(Analyzer analyzer) throws Exception {
		String liferayEnterpriseApp = analyzer.getProperty(
			"Liferay-Enterprise-App");

		if (liferayEnterpriseApp == null) {
			return false;
		}

		String serviceComponent = analyzer.getProperty("Service-Component");

		if ((serviceComponent == null) || (serviceComponent.length() == 0)) {
			return false;
		}

		_processServiceComponent(analyzer, serviceComponent);

		_processProvideCapability(analyzer);

		return true;
	}

	private EmbeddedResource _generateClassResource(String classBinaryName) {
		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);

		classWriter.visit(
			Opcodes.V1_6, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER,
			classBinaryName, null, _BASE_CLASS_BINARY_NAME, new String[0]);

		// Constructor

		MethodVisitor constructorMethodVisitor = classWriter.visitMethod(
			Opcodes.ACC_PUBLIC, "<init>",
			Type.getMethodDescriptor(Type.VOID_TYPE), null, null);

		constructorMethodVisitor.visitCode();

		constructorMethodVisitor.visitVarInsn(Opcodes.ALOAD, 0);

		constructorMethodVisitor.visitMethodInsn(
			Opcodes.INVOKESPECIAL, _BASE_CLASS_BINARY_NAME, "<init>", "()V",
			false);

		constructorMethodVisitor.visitInsn(Opcodes.RETURN);

		constructorMethodVisitor.visitMaxs(0, 0);

		constructorMethodVisitor.visitEnd();

		// Method

		MethodVisitor methodVisitor = classWriter.visitMethod(
			Opcodes.ACC_PROTECTED, "activate", _METHOD_DESCRIPTOR, null, null);

		methodVisitor.visitCode();

		methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
		methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);

		methodVisitor.visitMethodInsn(
			Opcodes.INVOKEVIRTUAL, classBinaryName, "init", _METHOD_DESCRIPTOR,
			false);

		methodVisitor.visitInsn(Opcodes.RETURN);

		methodVisitor.visitMaxs(0, 0);

		methodVisitor.visitEnd();

		classWriter.visitEnd();

		return new EmbeddedResource(classWriter.toByteArray(), 0);
	}

	private TagResource _generateTagResource(
			Analyzer analyzer, String modulePortalProfileClassName)
		throws Exception {

		Map<String, String> properties = new HashMap<>();

		//properties.put("activate:", "activate");
		properties.put("enabled:", "true");
		properties.put("immediate:", "true");
		properties.put("provide:", "com.liferay.portal.profile.PortalProfile");

		Version version = DSAnnotationReader.V1_3;

		properties.put("version:", version.toString());

		try (HeaderReader headerReader = new HeaderReader(analyzer)) {
			return new TagResource(
				headerReader.createComponentTag(
					modulePortalProfileClassName, modulePortalProfileClassName,
					properties));
		}
	}

	private void _processProvideCapability(Analyzer analyzer) {
		Attrs attrs = new Attrs();

		attrs.put(
			"objectClass:List<String>",
			"com.liferay.portal.profile.PortalProfile");
		attrs.put(Constants.USES_DIRECTIVE, "com.liferay.portal.profile");

		Parameters provideCapabilityHeaders = new Parameters(
			analyzer.getProperty(Constants.PROVIDE_CAPABILITY));

		provideCapabilityHeaders.add("osgi.service", attrs);

		analyzer.setProperty(
			Constants.PROVIDE_CAPABILITY, provideCapabilityHeaders.toString());
	}

	private void _processServiceComponent(
			Analyzer analyzer, String serviceComponent)
		throws Exception {

		String bundleSymbolicName = analyzer.getBsn();

		String modulePortalProfileClassName = bundleSymbolicName.concat(
			".internal.portal.profile.ModulePortalProfile");

		String classBinaryName = modulePortalProfileClassName.replace('.', '/');

		Jar jar = analyzer.getJar();

		jar.putResource(
			classBinaryName.concat(".class"),
			_generateClassResource(classBinaryName));

		String componentDefinitionFile =
			"OSGI-INF/" + modulePortalProfileClassName + ".xml";

		jar.putResource(
			componentDefinitionFile,
			_generateTagResource(analyzer, modulePortalProfileClassName));

		analyzer.setProperty(
			Constants.SERVICE_COMPONENT,
			serviceComponent + "," + componentDefinitionFile);
	}

	private static final String _BASE_CLASS_BINARY_NAME =
		"com/liferay/portal/profile/BaseEnterpriseDSModulePortalProfile";

	private static final String _METHOD_DESCRIPTOR = Type.getMethodDescriptor(
		Type.VOID_TYPE,
		Type.getObjectType("org/osgi/service/component/ComponentContext"));

}