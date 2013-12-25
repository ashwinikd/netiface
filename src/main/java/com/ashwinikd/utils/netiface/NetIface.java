
package com.ashwinikd.utils.netiface;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NetIface {
    private static boolean                        connected   = false;
    private static boolean                        initialized = false;
    private static Map<NetIfaceListener, Boolean> listeners   = new ConcurrentHashMap<NetIfaceListener, Boolean>();

    static {
        final Thread workerThread = new Thread(new NetWorker());
        workerThread.setDaemon(true);
        workerThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            public void run() {
                workerThread.interrupt();
            }
        }));
    }

    /**
     * @return the connected
     */
    public static boolean isConnected() {
        return connected;
    }

    /**
     * @return the initialized
     */
    public static boolean isInitialized() {
        return initialized;
    }

    public static void addListener(NetIfaceListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null.");
        }
        listeners.put(listener, false);
    }

    public static void removeListener(NetIfaceListener listener) {
        listeners.remove(listener);
    }

    private static class NetWorker implements Runnable {
        private static boolean                 RUN           = true;
        private static final long              SLEEP_TIME_MS = 1000;
        private Map<NetworkInterface, Boolean> ifaceStatus   = new HashMap<NetworkInterface, Boolean>();

        public void run() {
            try {
                while (RUN) {
                    try {
                        checkIfaces();
                    } catch (SocketException e) {}
                    Thread.sleep(SLEEP_TIME_MS);
                }
            } catch (InterruptedException e) {
                if (Thread.interrupted()) {
                    RUN = false;
                }
            }
        }

        private void checkIfaces() throws SocketException {
            Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
            boolean isConnected = false;
            while (ifaces.hasMoreElements()) {
                NetworkInterface iface = ifaces.nextElement();
                if (iface.isLoopback()) {
                    continue;
                }
                Boolean currentState = ifaceStatus.get(iface);
                boolean isNew = false;
                if (currentState == null) {
                    isNew = true;
                }
                if (isNew) {
                    if (iface.isUp()) {
                        for (NetIfaceListener listener : listeners.keySet()) {
                            listener.onIfaceUp(iface);
                        }
                        currentState = true;
                    } else {
                        for (NetIfaceListener listener : listeners.keySet()) {
                            listener.onIfaceDown(iface);
                        }
                        currentState = false;
                    }
                } else {
                    if (iface.isUp() && !currentState.booleanValue()) {
                        for (NetIfaceListener listener : listeners.keySet()) {
                            listener.onIfaceUp(iface);
                        }
                        currentState = true;
                    } else if (!iface.isUp() && currentState.booleanValue()) {
                        for (NetIfaceListener listener : listeners.keySet()) {
                            listener.onIfaceDown(iface);
                        }
                        currentState = false;
                    }
                }
                ifaceStatus.put(iface, currentState);
                if (currentState) {
                    isConnected = true;
                }
            }
            connected = isConnected;
            if (!initialized) {
                initialized = true;
            }
        }
    }
}
