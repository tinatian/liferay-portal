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

package com.liferay.portal.cluster.multiple.internal;

import com.liferay.petra.lang.ClassResolverUtil;
import com.liferay.portal.kernel.io.Deserializer;
import com.liferay.portal.kernel.io.ProtectedObjectInputStream;
import com.liferay.portal.kernel.io.SerializationConstants;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;

import java.nio.ByteBuffer;

/**
 * @author Lance Ji
 */
public class ClusterDeserializer extends Deserializer {

	public ClusterDeserializer(ByteBuffer byteBuffer) {
		super(byteBuffer);
	}

	@Override
	public <T extends Serializable> T readObject()
		throws ClassNotFoundException {

		byte tcByte = buffer[index++];

		if (tcByte == SerializationConstants.TC_BOOLEAN) {
			return (T)Boolean.valueOf(readBoolean());
		}
		else if (tcByte == SerializationConstants.TC_BYTE) {
			return (T)Byte.valueOf(readByte());
		}
		else if (tcByte == SerializationConstants.TC_CHARACTER) {
			return (T)Character.valueOf(readChar());
		}
		else if (tcByte == SerializationConstants.TC_CLASS) {
			String contextName = readString();
			String className = readString();

			ClassLoader classLoader = ClusterClassLoaderPool.getClassLoader(
				contextName);

			return (T)ClassResolverUtil.resolve(className, classLoader);
		}
		else if (tcByte == SerializationConstants.TC_DOUBLE) {
			return (T)Double.valueOf(readDouble());
		}
		else if (tcByte == SerializationConstants.TC_FLOAT) {
			return (T)Float.valueOf(readFloat());
		}
		else if (tcByte == SerializationConstants.TC_INTEGER) {
			return (T)Integer.valueOf(readInt());
		}
		else if (tcByte == SerializationConstants.TC_LONG) {
			return (T)Long.valueOf(readLong());
		}
		else if (tcByte == SerializationConstants.TC_NULL) {
			return null;
		}
		else if (tcByte == SerializationConstants.TC_SHORT) {
			return (T)Short.valueOf(readShort());
		}
		else if (tcByte == SerializationConstants.TC_STRING) {
			return (T)readString();
		}
		else if (tcByte == SerializationConstants.TC_OBJECT) {
			try {
				ObjectInputStream objectInputStream =
					new ClusterProtectedAnnotatedObjectInputStream(
						new BufferInputStream());

				return (T)objectInputStream.readObject();
			}
			catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}
		}

		throw new IllegalStateException("Unkown TC code " + tcByte);
	}

	private class BufferInputStream extends InputStream {

		@Override
		public int read() {
			return buffer[index++];
		}

		@Override
		public int read(byte[] bytes) {
			return read(bytes, 0, bytes.length);
		}

		@Override
		public int read(byte[] bytes, int offset, int length) {
			int remain = limit - index;

			if (remain < length) {
				length = remain;
			}

			System.arraycopy(buffer, index, bytes, offset, length);

			index += length;

			return length;
		}

	}

	private class ClusterProtectedAnnotatedObjectInputStream
		extends ProtectedObjectInputStream {

		public ClusterProtectedAnnotatedObjectInputStream(
				InputStream inputStream)
			throws IOException {

			super(inputStream);
		}

		@Override
		protected Class<?> doResolveClass(ObjectStreamClass objectStreamClass)
			throws ClassNotFoundException, IOException {

			String contextName = readUTF();

			ClassLoader classLoader = ClusterClassLoaderPool.getClassLoader(
				contextName);

			String className = objectStreamClass.getName();

			return ClassResolverUtil.resolve(className, classLoader);
		}

	}

}