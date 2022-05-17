package util;

import com.teamdev.jxbrowser.browser.Browser;

/**
 * A utility for client applications.
 */
public final class Clients {

    private static final String defaultPort = "3000";

    /**
     * Connects the customer client application with the given customer id to the server.
     *
     * @param browser    the browser instance the customer client use
     * @param args       command line arguments that contain information about the port
     * @param customerId id of the customer to be connected
     */
    public static void connectCustomerToServer(Browser browser, String[] args, String customerId) {
        loadHost(browser, args);
        executeJS(String.format("initializeCustomer('%s')", customerId), browser);
    }

    /**
     * Connects the tech support client application to the server.
     *
     * @param browser the browser instance the tech support client use
     * @param args    command line arguments that contain information about the port
     */
    public static void connectTechSupportToServer(Browser browser, String[] args) {
        loadHost(browser, args);
        executeJS("initializeTechSupport()", browser);
    }

    /**
     * Executes the given script in the given browser instance if the mainframe is present.
     *
     * @param script  JavaScript code fragment to be executed
     * @param browser the browser that should execute this code fragment
     */
    public static void executeJS(String script, Browser browser) {
        browser.mainFrame().ifPresent(mainFrame -> mainFrame.executeJavaScript(script));
    }

    private static void loadHost(Browser browser, String[] args) {
        String port = getPort(args);
        String url = String.format("http://localhost:%s/", port);
        browser.navigation().loadUrlAndWait(url);
    }

    private static String getPort(String[] args) {
        return args.length > 0 ? args[0].equals("-p") ? args[1] : defaultPort : defaultPort;
    }

    /**
     * Prevent instantiation of this utility class.
     */
    private Clients() {
    }
}
