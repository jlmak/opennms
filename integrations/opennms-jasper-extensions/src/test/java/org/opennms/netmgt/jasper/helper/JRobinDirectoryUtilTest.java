package org.opennms.netmgt.jasper.helper;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class JRobinDirectoryUtilTest {
    private static String JRB_DIRECTORY = "src/test/resources/share/rrd/snmp";
    private static String RRD_TOOL_DIRECTORY = "src/test/resources/share/rrd";
    private static String NODE_ID = "9";
    private static String INTERFACE = "me1-0002baaacffe";
    
    @Before
    public void setup() {
        System.setProperty("org.opennms.rrd.storeByGroup", "true");
        System.setProperty("org.opennms.rrd.strategyClass", "org.opennms.netmgt.rrd.jrobin.JRobinRrdStrategy");
    }
    
    @Test
    public void testJRobinDirectoryLookup() throws IOException {
        JRobinDirectoryUtil lookup = new JRobinDirectoryUtil();
        
        assertEquals("src/test/resources/share/rrd/snmp/9/me1-0002baaacffe/mib2-interfaces.jrb", lookup.getIfInOctetsJrb(JRB_DIRECTORY, NODE_ID, INTERFACE));
        assertEquals("src/test/resources/share/rrd/snmp/9/me1-0002baaacffe/mib2-interfaces.jrb", lookup.getIfOutOctetsJrb( JRB_DIRECTORY, NODE_ID, INTERFACE));
        
        System.setProperty("org.opennms.rrd.storeByGroup", "false");
        
        assertEquals("src/test/resources/share/rrd/snmp/9/me1-0002baaacffe/ifHCInOctets.jrb", lookup.getIfInOctetsJrb(JRB_DIRECTORY, NODE_ID, INTERFACE));
        assertEquals("src/test/resources/share/rrd/snmp/9/me1-0002baaacffe/ifHCOutOctets.jrb", lookup.getIfOutOctetsJrb(JRB_DIRECTORY, NODE_ID, INTERFACE));
    }
    
    @Test
    public void testJRobinDirectoryUtilRrdExtension() throws FileNotFoundException, IOException {
        System.setProperty("org.opennms.rrd.strategyClass", "org.opennms.netmgt.rrd.jrobin.JniRrdStrategy");
        JRobinDirectoryUtil lookupUtil = new JRobinDirectoryUtil();
        
        assertEquals("src/test/resources/share/rrd/9/me1-0002baaacffe/mib2-interfaces.rrd", lookupUtil.getIfInOctetsJrb(RRD_TOOL_DIRECTORY, NODE_ID, INTERFACE));
        assertEquals("src/test/resources/share/rrd/9/me1-0002baaacffe/mib2-interfaces.rrd", lookupUtil.getIfOutOctetsJrb( RRD_TOOL_DIRECTORY, NODE_ID, INTERFACE));
        
        System.setProperty("org.opennms.rrd.storeByGroup", "false");
        
        assertEquals("src/test/resources/share/rrd/9/me1-0002baaacffe/ifHCInOctets.rrd", lookupUtil.getIfInOctetsJrb(RRD_TOOL_DIRECTORY, NODE_ID, INTERFACE));
        assertEquals("src/test/resources/share/rrd/9/me1-0002baaacffe/ifHCOutOctets.rrd", lookupUtil.getIfOutOctetsJrb(RRD_TOOL_DIRECTORY, NODE_ID, INTERFACE));
    }
    
    @Test
    public void testJRobinDirectoryUtilCustomExtension() throws FileNotFoundException, IOException {
        System.setProperty("org.opennms.rrd.fileExtension", ".jrb");
        JRobinDirectoryUtil lookupUtil = new JRobinDirectoryUtil();
        
        assertEquals("src/test/resources/share/rrd/snmp/9/me1-0002baaacffe/mib2-interfaces.jrb", lookupUtil.getIfInOctetsJrb(JRB_DIRECTORY, NODE_ID, INTERFACE));
        assertEquals("src/test/resources/share/rrd/snmp/9/me1-0002baaacffe/mib2-interfaces.jrb", lookupUtil.getIfOutOctetsJrb( JRB_DIRECTORY, NODE_ID, INTERFACE));
        
        System.setProperty("org.opennms.rrd.fileExtension", ".bogus");
        assertEquals("src/test/resources/share/rrd/snmp/9/me1-0002baaacffe/mib2-interfaces.bogus", lookupUtil.getIfInOctetsJrb(JRB_DIRECTORY, NODE_ID, INTERFACE));
        assertEquals("src/test/resources/share/rrd/snmp/9/me1-0002baaacffe/mib2-interfaces.bogus", lookupUtil.getIfOutOctetsJrb( JRB_DIRECTORY, NODE_ID, INTERFACE));
        
        System.setProperty("org.opennms.rrd.fileExtension", ".rrd");
        assertEquals("src/test/resources/share/rrd/snmp/9/me1-0002baaacffe/mib2-interfaces.rrd", lookupUtil.getIfInOctetsJrb(JRB_DIRECTORY, NODE_ID, INTERFACE));
        assertEquals("src/test/resources/share/rrd/snmp/9/me1-0002baaacffe/mib2-interfaces.rrd", lookupUtil.getIfOutOctetsJrb( JRB_DIRECTORY, NODE_ID, INTERFACE));
        
    }
    
    @Test
    public void testGetInterfaceDirectory() {
        JRobinDirectoryUtil lookup = new JRobinDirectoryUtil();
        
        String snmpphysaddr = "0002baaacffe";
        String snmpifname = "me1";
        String snmpifdescr = "me1";
        assertEquals("me1-0002baaacffe", lookup.getInterfaceDirectory(snmpifname, snmpifdescr, snmpphysaddr));
    }
    
    @Test
    public void testGetInterfaceDirectoryNoSnmpPhysAddr() {
        JRobinDirectoryUtil lookup = new JRobinDirectoryUtil();
        
        String snmpphysaddr = null;
        String snmpifname = "me1";
        String snmpifdescr = "me1";
        assertEquals("me1", lookup.getInterfaceDirectory(snmpifname, snmpifdescr, snmpphysaddr));
        
        snmpifdescr = null;
        assertEquals("me1", lookup.getInterfaceDirectory(snmpifname, snmpifdescr, snmpphysaddr));
    }
    
    @Test
    public void testGetInterfaceDirectoryNoSnmpIfName() {
        JRobinDirectoryUtil lookup = new JRobinDirectoryUtil();
        
        String snmpphysaddr = "0002baaacffe";
        String snmpifname = null;
        String snmpifdescr = "me1";
        assertEquals("me1-0002baaacffe", lookup.getInterfaceDirectory(snmpifname, snmpifdescr, snmpphysaddr));
    }
    
    @Test
    public void testGetInterfaceDirectoryATM() {
        JRobinDirectoryUtil lookup = new JRobinDirectoryUtil();
        
        String snmpphysaddr = "00e0817xxxxx";
        String snmpifname = "";
        String snmpifdescr = "eth0";
        assertEquals("eth0-00e0817xxxxx", lookup.getInterfaceDirectory(snmpifname, snmpifdescr, snmpphysaddr));
    }
    
}
