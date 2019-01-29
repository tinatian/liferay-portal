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

package com.liferay.portal.workflow.kaleo.internal.upgrade.v2_0_1;

import com.liferay.portal.kernel.upgrade.BaseUpgradeBadColumnNames;
import com.liferay.portal.workflow.kaleo.internal.upgrade.v2_0_1.util.KaleoActionTable;
import com.liferay.portal.workflow.kaleo.internal.upgrade.v2_0_1.util.KaleoDefinitionTable;
import com.liferay.portal.workflow.kaleo.internal.upgrade.v2_0_1.util.KaleoDefinitionVersionTable;
import com.liferay.portal.workflow.kaleo.internal.upgrade.v2_0_1.util.KaleoNodeTable;
import com.liferay.portal.workflow.kaleo.internal.upgrade.v2_0_1.util.KaleoNotificationTable;
import com.liferay.portal.workflow.kaleo.internal.upgrade.v2_0_1.util.KaleoTaskFormTable;
import com.liferay.portal.workflow.kaleo.internal.upgrade.v2_0_1.util.KaleoTaskTable;
import com.liferay.portal.workflow.kaleo.internal.upgrade.v2_0_1.util.KaleoTimerTable;
import com.liferay.portal.workflow.kaleo.internal.upgrade.v2_0_1.util.KaleoTransitionTable;

/**
 * @author Tina Tian
 */
public class UpgradeBadColumnNames extends BaseUpgradeBadColumnNames {

	@Override
	protected void doUpgrade() throws Exception {
		upgradeBadColumnNames(KaleoActionTable.class, "description");
		upgradeBadColumnNames(KaleoDefinitionTable.class, "description");
		upgradeBadColumnNames(KaleoDefinitionVersionTable.class, "description");
		upgradeBadColumnNames(KaleoNodeTable.class, "description");
		upgradeBadColumnNames(KaleoNotificationTable.class, "description");
		upgradeBadColumnNames(KaleoTaskTable.class, "description");
		upgradeBadColumnNames(KaleoTaskFormTable.class, "description");
		upgradeBadColumnNames(KaleoTimerTable.class, "description");
		upgradeBadColumnNames(KaleoTransitionTable.class, "description");
	}

}