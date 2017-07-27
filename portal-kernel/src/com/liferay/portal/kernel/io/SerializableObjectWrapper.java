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
package com.liferay.portal.kernel.io;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

import java.nio.ByteBuffer;

import java.util.Objects;

/**
 * @author Tina Tian
 */
public class SerializableObjectWrapper implements Externalizable {

	public static <T> T unwrap(Object object) {
		if (!(object instanceof SerializableObjectWrapper)) {
			return (T)object;
		}

		SerializableObjectWrapper serializableWrapper =
			(SerializableObjectWrapper)object;

		return (T)serializableWrapper.getSerializable();
	}

	/**
	 * The empty constructor is required by {@link Externalizable}. Do not use
	 * this for any other purpose.
	 */
	public SerializableObjectWrapper() {
	}

	public SerializableObjectWrapper(Serializable serializable) {
		_serializable = serializable;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SerializableObjectWrapper)) {
			return false;
		}

		SerializableObjectWrapper serializableWrapper =
			(SerializableObjectWrapper)object;

		return Objects.equals(
			getSerializable(), serializableWrapper.getSerializable());
	}

	public Serializable getSerializable() {
		if (_serializable != null) {
			return _serializable;
		}

		if (_data == null) {
			return null;
		}

		Deserializer deserializer = new Deserializer(ByteBuffer.wrap(_data));

		try {
			_serializable = deserializer.readObject();

			_data = null;
		}
		catch (ClassNotFoundException cnfe) {
			_log.error("Unable to deserialize object", cnfe);
		}

		return _serializable;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getSerializable());
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		byte[] data = new byte[objectInput.readInt()];

		objectInput.readFully(data);

		_data = data;
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		Serializer serializer = new Serializer();

		serializer.writeObject(getSerializable());

		ByteBuffer byteBuffer = serializer.toByteBuffer();

		objectOutput.writeInt(byteBuffer.remaining());

		objectOutput.write(
			byteBuffer.array(), byteBuffer.position(), byteBuffer.remaining());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SerializableObjectWrapper.class);

	private byte[] _data;
	private Serializable _serializable;

}