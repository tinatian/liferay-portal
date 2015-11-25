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

package com.liferay.portal.kernel.cluster;

import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

/**
 * @author Tina Tian
 */
public class ClusterNodeResponse implements Serializable {

	public static ClusterNodeResponse createExceptionClusterNodeResponse(
		ClusterNode clusterNode, String uuid, Exception exception) {

		return new ClusterNodeResponse(clusterNode, uuid, exception, true);
	}

	public static ClusterNodeResponse createResultClusterNodeResponse(
		ClusterNode clusterNode, String uuid, Object result) {

		if ((result != null) && !(result instanceof Serializable)) {
			return new ClusterNodeResponse(
				clusterNode, uuid,
				new ClusterException("Return value is not serializable"), true);
		}

		return new ClusterNodeResponse(
			clusterNode, uuid, (Serializable)result, false);
	}

	public ClusterNode getClusterNode() {
		return _clusterNode;
	}

	public Serializable getPayload() {
		return _payload;
	}

	public Object getResult() throws Exception {
		if (_hasException) {
			throw (Exception)_payload;
		}

		return _payload;
	}

	public String getUuid() {
		return _uuid;
	}

	public boolean hasException() {
		return _hasException;
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(9);

		sb.append("{clusterNode=");
		sb.append(_clusterNode);
		sb.append(", hasException=");
		sb.append(_hasException);
		sb.append(", payload=");
		sb.append(_payload);
		sb.append(", uuid=");
		sb.append(_uuid);
		sb.append("}");

		return sb.toString();
	}

	private ClusterNodeResponse(
		ClusterNode clusterNode, String uuid, Serializable payload,
		boolean hasException) {

		if (clusterNode == null) {
			throw new NullPointerException("ClusterNode is null");
		}

		if (Validator.isNull(uuid)) {
			throw new NullPointerException("Uuid is null");
		}

		if (payload == null) {
			throw new NullPointerException("Payload is null");
		}

		_clusterNode = clusterNode;
		_uuid = uuid;
		_payload = payload;
		_hasException = hasException;
	}

	private final ClusterNode _clusterNode;
	private final boolean _hasException;
	private final Serializable _payload;
	private final String _uuid;

}