/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.hibernate.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.dao.orm.hibernate.DB2Dialect;
import com.liferay.portal.dao.orm.hibernate.SQLServerDialect;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.portal.spring.hibernate.DialectDetector;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.sql.Connection;

import java.util.function.Function;

import javax.sql.DataSource;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.dialect.MariaDBDialect;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.OracleDialect;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.engine.jdbc.dialect.spi.DatabaseMetaDataDialectResolutionInfoAdapter;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tina Tian
 */
@RunWith(Arquillian.class)
public class DialectDetectorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetDialect() throws Exception {
		DataSource dataSource = InfrastructureUtil.getDataSource();

		Dialect dialect = DialectDetector.getDialect(dataSource);

		DB db = DBManagerUtil.getDB();

		DBType dbType = db.getDBType();

		Assert.assertEquals(dbType, DBManagerUtil.getDBType(dialect));

		if (dbType == DBType.DB2) {
			Assert.assertSame(DB2Dialect.class, dialect.getClass());

			_assertVersion(
				dataSource, dialect, org.hibernate.dialect.DB2Dialect::new);
		}
		else if (dbType == DBType.HYPERSONIC) {
			Assert.assertSame(HSQLDialect.class, dialect.getClass());
		}
		else if (dbType == DBType.MARIADB) {
			Assert.assertSame(MariaDBDialect.class, dialect.getClass());
		}
		else if (dbType == DBType.MYSQL) {
			Assert.assertSame(MySQLDialect.class, dialect.getClass());
		}
		else if (dbType == DBType.ORACLE) {
			Assert.assertSame(OracleDialect.class, dialect.getClass());
		}
		else if (dbType == DBType.POSTGRESQL) {
			Assert.assertSame(PostgreSQLDialect.class, dialect.getClass());
		}
		else if (dbType == DBType.SQLSERVER) {
			Assert.assertSame(SQLServerDialect.class, dialect.getClass());

			_assertVersion(
				dataSource, dialect,
				org.hibernate.dialect.SQLServerDialect::new);
		}

		Assert.assertSame(dialect, DialectDetector.getDialect(dataSource));
	}

	private void _assertVersion(
			DataSource dataSource, Dialect dialect,
			Function<DialectResolutionInfo, Dialect> function)
		throws Exception {

		try (Connection connection = dataSource.getConnection()) {
			Dialect expectedDialect = function.apply(
				new DatabaseMetaDataDialectResolutionInfoAdapter(
					connection.getMetaData()));

			Assert.assertEquals(
				expectedDialect.getVersion(), dialect.getVersion());
		}
	}

}