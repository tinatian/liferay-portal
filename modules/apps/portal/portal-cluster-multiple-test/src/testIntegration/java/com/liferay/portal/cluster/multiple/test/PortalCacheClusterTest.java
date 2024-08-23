/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */


package com.liferay.portal.cluster.multiple.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tina Tian
 */
@RunWith(Arquillian.class)
public class PortalCacheClusterTest extends BaseClusterTestCase {

	@Test
	public void testPortalCacheWithReplicateByRemove() throws Exception {
		setupTomcat("/home/me/tomcat1");
		//startTomcat("/home/me/tomcat1");

		//Thread.sleep(1000000);
	}

}
