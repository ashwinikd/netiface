
package com.ashwinikd.utils.netiface;

import java.net.NetworkInterface;

public interface NetIfaceListener {
    public void onIfaceDown(NetworkInterface iface);

    public void onIfaceUp(NetworkInterface iface);

    public void onInit();
}
