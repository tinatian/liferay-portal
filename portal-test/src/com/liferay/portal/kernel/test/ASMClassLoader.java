/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author Tina Tian
 */
public class ASMClassLoader extends ClassLoader {

	public ASMClassLoader(String className, String newValue, String oldValue) {
		super(ASMClassLoader.class.getClassLoader());

		_className = className;
		_newValue = newValue;
		_oldValue = oldValue;
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve)
		throws ClassNotFoundException {

		if (!name.equals(_className)) {
			return super.loadClass(name, resolve);
		}

		ClassLoader classLoader = getParent();

		try (InputStream inputStream = classLoader.getResourceAsStream(
				StringUtil.replace(name, CharPool.PERIOD, CharPool.SLASH) +
					".class")) {

			if (inputStream == null) {
				throw new ClassNotFoundException(name);
			}

			ClassReader classReader = new ClassReader(inputStream);

			ClassWriter classWriter = new ClassWriter(classReader, 0);

			classReader.accept(
				new ClassVisitor(Opcodes.ASM9, classWriter) {

					@Override
					public MethodVisitor visitMethod(
						int access, String name, String descriptor,
						String signature, String[] exceptions) {

						return new MethodVisitor(
							api,
							super.visitMethod(
								access, name, descriptor, signature,
								exceptions)) {

							@Override
							public void visitLdcInsn(Object value) {
								if (_oldValue.equals(value)) {
									value = _newValue;
								}

								super.visitLdcInsn(value);
							}

						};
					}

				},
				0);

			byte[] bytes = classWriter.toByteArray();

			Class<?> clazz = defineClass(name, bytes, 0, bytes.length);

			if (resolve) {
				resolveClass(clazz);
			}

			return clazz;
		}
		catch (IOException ioException) {
			throw new ClassNotFoundException(name, ioException);
		}
	}

	private final String _className;
	private final String _newValue;
	private final String _oldValue;

}