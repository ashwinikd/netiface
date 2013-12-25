
package com.ashwinikd.utils.netiface;

import java.net.NetworkInterface;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
    /**
     * Create the test case
     * 
     * @param testName
     *            name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        NetIfaceListener listener = new NetIfaceListener() {

            public void onInit() {
                System.out.println("NetIface is initialized.");
            }

            public void onIfaceUp(NetworkInterface iface) {
                System.out.println("iface " + iface.getDisplayName() + " is up");
            }

            public void onIfaceDown(NetworkInterface iface) {
                System.out.println("iface " + iface.getDisplayName() + " is down");
            }
        };
        NetIface.addListener(listener);
        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
