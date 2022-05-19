package com.teamdev.jxbrowser.examples;

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.browser.callback.InjectJsCallback;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.js.JsAccessible;
import com.teamdev.jxbrowser.js.JsObject;
import com.teamdev.jxbrowser.view.swing.BrowserView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Objects;

import static com.teamdev.jxbrowser.engine.RenderingMode.HARDWARE_ACCELERATED;
import static com.teamdev.jxbrowser.examples.Clients.*;
import static javax.swing.SwingUtilities.invokeLater;

/**
 * A tech support client application that waits for a support request from a customer client application
 * and loads a browser widget to remotely observe the customer's screen.
 */
public final class TechSupportClient {

    private static Browser browser;
    private static JPanel mainPanel;

    public static void main(String[] args) {

        // Create an Engine and Browser instances.
        Engine engine = Engine.newInstance(HARDWARE_ACCELERATED);
        browser = engine.newBrowser();

        // Inject an instance of the Java object into JavaScript
        // so that we can communicate with that object from JS.
        browser.set(InjectJsCallback.class, params -> {
            JsObject window = params.frame().executeJavaScript("window");
            Objects.requireNonNull(window).putProperty("java", new TechSupportClient());
            return InjectJsCallback.Response.proceed();
        });

        initUI();
        loadHost(browser, args);
        browser.mainFrame().ifPresent(mainFrame -> mainFrame.executeJavaScript("initializeTechSupport()"));
    }

    private static void initUI() {
        invokeLater(() -> {
            JFrame frame = new JFrame("Tech Support Browser");
            mainPanel = new JPanel();
            mainPanel.setBackground(Color.WHITE);

            BrowserView view = BrowserView.newInstance(browser);

            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    browser.engine().close();
                }
            });
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(700, 500);
            frame.getContentPane().setBackground(Color.WHITE);
            frame.add(view, BorderLayout.CENTER);
            frame.add(mainPanel, BorderLayout.NORTH);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    /**
     * Displays a message with a button to accept a support request from a customer.
     */
    @JsAccessible
    public void onSupportRequested() {
        JLabel label = new JLabel("Received a request from customer");
        JButton acceptSupportButton = new JButton("Accept");

        acceptSupportButton.addActionListener((event) -> {
            browser.mainFrame().ifPresent(mainFrame -> mainFrame.executeJavaScript("acceptSupportRequest()"));
            updatePanel(mainPanel, List.of(), List.of(label, acceptSupportButton));
        });

        updatePanel(mainPanel, List.of(label, acceptSupportButton), List.of());
    }
}
