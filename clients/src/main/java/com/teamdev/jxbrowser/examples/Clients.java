package com.teamdev.jxbrowser.examples;

import com.teamdev.jxbrowser.browser.Browser;

import javax.swing.*;
import java.util.List;

/**
 * A utility for client applications.
 */
public final class Clients {

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
     * Loads a web resource to communicate with the server.
     *
     * @param browser a browser that should load the host
     */
    public static void loadHost(Browser browser) {
        String port = System.getProperty("example.port");
        String url = String.format("http://localhost:%s/", port);
        browser.navigation().loadUrlAndWait(url);
    }

    /**
     * Prevents instantiation of this utility class.
     */
    private Clients() {
    }
}
