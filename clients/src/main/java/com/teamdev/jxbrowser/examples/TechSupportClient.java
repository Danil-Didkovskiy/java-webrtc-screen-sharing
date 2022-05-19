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
import java.util.Objects;

import static com.teamdev.jxbrowser.engine.RenderingMode.HARDWARE_ACCELERATED;
import static com.teamdev.jxbrowser.examples.Clients.loadHost;

/**
 * A tech support client application that waits for a support request from a customer client application
 * and loads a browser widget to remotely observe the customer's screen.
 */
public final class TechSupportClient {

    private Browser browser;
    private JPanel mainPanel;
    private static final TechSupportClient techSupportClient = new TechSupportClient();

    public void start() {
        Engine engine = Engine.newInstance(HARDWARE_ACCELERATED);
        browser = initBrowser(engine);
        initUI();

        loadHost(browser);
        browser.mainFrame().ifPresent(mainFrame -> mainFrame.executeJavaScript("initializeTechSupport()"));
    }

    private Browser initBrowser(Engine engine) {
        Browser browser = engine.newBrowser();

        // Inject an instance of the Java object into JavaScript
        // so that we can communicate with that object from JS.
        browser.set(InjectJsCallback.class, params -> {
            JsObject window = params.frame().executeJavaScript("window");
            Objects.requireNonNull(window).putProperty("java", techSupportClient);
            return InjectJsCallback.Response.proceed();
        });

        return browser;
    }

    private void initUI() {
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

            mainPanel.remove(label);
            mainPanel.remove(acceptSupportButton);
            mainPanel.revalidate();
            mainPanel.repaint();
        });

        mainPanel.add(label);
        mainPanel.add(acceptSupportButton);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public static void main(String[] args) {
        techSupportClient.start();
    }
}
