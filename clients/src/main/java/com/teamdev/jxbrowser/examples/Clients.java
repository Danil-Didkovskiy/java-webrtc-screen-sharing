package com.teamdev.jxbrowser.examples;

import com.teamdev.jxbrowser.browser.Browser;

/**
 * A utility for client applications.
 */
public final class Clients {

    /**
     * Loads a web resource to communicate with the server.
     *
     * @param browser a browser that should load the host
     */
    public static void loadHost(Browser browser) {
        String port = System.getProperty("server.port");
        String url = String.format("http://localhost:%s/", port);
        browser.navigation().loadUrlAndWait(url);
    }

    /**
     * Prevents instantiation of this utility class.
     */
    private Clients() {
    }
}
