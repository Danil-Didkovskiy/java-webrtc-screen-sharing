package com.teamdev.jxbrowser.examples;

import com.teamdev.jxbrowser.browser.Browser;

import javax.swing.*;
import java.util.List;

/**
 * A utility for client applications.
 */
public final class Clients {

    private static final String defaultPort = "3000";

    /**
     * Updates the given instance of {@link JPanel} by adding and removing given components.
     *
     * @param panel              panel to be updated
     * @param componentsToAdd    components to be added to a panel
     * @param componentsToRemove components to be removed from a panel
     */
    public static void updatePanel(JPanel panel,
                                   List<JComponent> componentsToAdd,
                                   List<JComponent> componentsToRemove) {
        componentsToAdd.forEach(panel::add);
        componentsToRemove.forEach(panel::remove);
        panel.revalidate();
        panel.repaint();
    }

    /**
     *
     * @param browser
     * @param args
     */
    public static void loadHost(Browser browser, String[] args) {
        String port = getPort(args);
        String url = String.format("http://localhost:%s/", port);
        browser.navigation().loadUrlAndWait(url);
    }

    private static String getPort(String[] args) {
        return args.length > 0 ? args[0].equals("-p") ? args[1] : defaultPort : defaultPort;
    }

    /**
     * Prevents instantiation of this utility class.
     */
    private Clients() {
    }
}
