package com.teamdev.jxbrowser.examples;

import com.teamdev.jxbrowser.browser.Browser;

/**
 * A helper class that connects client applications to the local WebRTC server.
 */
public final class LocalWebRtcServer {

    private static final String port = System.getProperty("server.port");
    private static final String url = String.format("http://localhost:%s/", port);

    /**
     * Connects a client of the given type to the local WebRTC server.
     *
     * @param clientType a type of client to be connected
     * @param browser    a browser instance the client use
     */
    public static void connect(ClientType clientType, Browser browser) {
        browser.navigation().loadUrlAndWait(url);

        switch (clientType) {
            case STREAMER: {
                browser.mainFrame().ifPresent(mainFrame ->
                        mainFrame.executeJavaScript("connectStreamerToWebRtcServer()"));
                break;
            }
            case RECEIVER: {
                browser.mainFrame().ifPresent(mainFrame ->
                        mainFrame.executeJavaScript("connectReceiverToWebRtcServer()"));
                break;
            }
        }
    }

    /**
     * Enumeration of possible client types.
     */
    public enum ClientType {
        STREAMER,
        RECEIVER
    }
}
