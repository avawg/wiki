package org.wg.wiki.utils;

import java.io.Serializable;

public class RequestContext implements Serializable {

    private static ThreadLocal<String> remoteAddress = new ThreadLocal<>();

    public static String getRemoteAddress() {
        return remoteAddress.get();
    }

    public static void setRemoteAddress(String remoteAddress) {
        RequestContext.remoteAddress.set(remoteAddress);
    }
}
