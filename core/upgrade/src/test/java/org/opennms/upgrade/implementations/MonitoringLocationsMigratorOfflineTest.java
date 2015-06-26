/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2014 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2014 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.upgrade.implementations;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennms.core.db.DataSourceFactory;
import org.opennms.core.test.OpenNMSJUnit4ClassRunner;
import org.opennms.core.test.db.annotations.JUnitTemporaryDatabase;
import org.opennms.core.utils.DBUtils;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OpenNMSJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "classpath:/META-INF/opennms/emptyContext.xml"
})
@JUnitTemporaryDatabase
public class MonitoringLocationsMigratorOfflineTest {

    /**
     * Sets up the test.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception {
        FileUtils.copyDirectory(new File("src/test/resources/etc"), new File("target/home/etc"));
        System.setProperty("opennms.home", "target/home");
    }

    /**
     * Tear down the test.
     *
     * @throws Exception the exception
     */
    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(new File("target/home"));
    }

    /**
     * Test fixing the configuration file.
     *
     * @throws Exception the exception
     */
    @Test
    public void testMigrateLargeConfigToDatabase() throws Exception {
        MonitoringLocationsMigratorOffline migrator = new MonitoringLocationsMigratorOffline();
        migrator.execute();

        Connection connection = null;
        final DBUtils dbUtils = new DBUtils(getClass());
        try {
            connection = DataSourceFactory.getInstance().getConnection();
            dbUtils.watch(connection);

            ResultSet rs = connection.prepareStatement("SELECT COUNT(*) FROM monitoringlocations").executeQuery();
            dbUtils.watch(rs);
            rs.next();
            assertEquals(2864, rs.getInt(1));

            rs = connection.prepareStatement("SELECT COUNT(*) FROM monitoringlocationstags WHERE tag = 'divisbileBy3'").executeQuery(); // sic
            dbUtils.watch(rs);
            rs.next();
            assertEquals(954, rs.getInt(1));

            rs = connection.prepareStatement("SELECT COUNT(*) FROM monitoringlocationstags WHERE tag = 'divisibleBy5'").executeQuery();
            dbUtils.watch(rs);
            rs.next();
            assertEquals(572, rs.getInt(1));

            rs = connection.prepareStatement("SELECT COUNT(*) FROM monitoringlocationstags WHERE tag = 'divisibleBy7'").executeQuery();
            dbUtils.watch(rs);
            rs.next();
            assertEquals(409, rs.getInt(1));

            rs = connection.prepareStatement("SELECT COUNT(*) FROM monitoringlocationstags WHERE tag = 'odd'").executeQuery();
            dbUtils.watch(rs);
            rs.next();
            assertEquals(1432, rs.getInt(1));

            rs = connection.prepareStatement("SELECT COUNT(*) FROM monitoringlocationstags WHERE tag = 'even'").executeQuery();
            dbUtils.watch(rs);
            rs.next();
            assertEquals(1432, rs.getInt(1));
        } finally {
            dbUtils.cleanUp();
        }
    }
}
