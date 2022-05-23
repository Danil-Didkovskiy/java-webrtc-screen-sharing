package com.teamdev.jxbrowser.examples;

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.view.swing.BrowserView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static com.teamdev.jxbrowser.engine.RenderingMode.HARDWARE_ACCELERATED;
import static com.teamdev.jxbrowser.examples.Clients.loadHost;

/**
 * A tech support client application that waits for a support request from a customer client application
 * and loads a browser widget to remotely observe the customer's screen.
 */
public final class Receiver {

    public void start() {
        Engine engine = Engine.newInstance(HARDWARE_ACCELERATED);
        Browser browser = engine.newBrowser();
        initUI(browser);

        loadHost(browser);
        browser.mainFrame().ifPresent(mainFrame -> mainFrame.executeJavaScript("initializeReceiver()"));
    }

    private void initUI(Browser browser) {
        JFrame frame = new JFrame("Receiver Browser");
        BrowserView view = BrowserView.newInstance(browser);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                browser.engine().close();
            }
        });
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.add(view, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new Receiver().start();
    }
}
