/*
 * Created on 24-Oct-2004
 * Created by damon
 * 
 * Copyright AI, all rights reserved
 */
package org.amnesty.aidoc.transform;

import java.net.ConnectException;

import net.sf.jooreports.openoffice.connection.OpenOfficeConnection;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

/**
 * Tests OpenOffice connectivity
 * 
 * @author damon
 * 
 */
public class OpenOfficeConnectionPoolTest extends
        AbstractDependencyInjectionSpringContextTests {

    // private static final String TEST_CATALOG_NAME = "JUNIT";

    protected String[] getConfigLocations() {
        String[] paths = { "test-context.xml" };
        return paths;
    }

    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();


    }

    public void testConnectionPooling() throws ConnectException, InterruptedException, OpenOfficeConnectionPoolEmptyException {

        OpenOfficeConnectionPool cp = (OpenOfficeConnectionPool) this.applicationContext.getBean("openOfficeConnectionPool");

        assertEquals(1, cp.getTotalConnectionCount());
        assertEquals(1, cp.getAvailableConnectionCount());
        cp.releaseConnection(cp.getConnection());
        assertEquals(2, cp.getAvailableConnectionCount());
        
        OpenOfficeConnection conn1 = cp.getConnection();
        OpenOfficeConnection conn2 = cp.getConnection();
        assertNotNull(conn1);
        assertNotNull(conn2);
        assertNotSame(conn1, conn2);
        assertTrue(conn1.isConnected());
        assertEquals(0, cp.getAvailableConnectionCount());
        cp.releaseConnection(conn1);
        assertEquals(1, cp.getAvailableConnectionCount());
        
        cp.markAsHung(conn2);
        assertEquals(1, cp.getAvailableConnectionCount());
        

    }

}