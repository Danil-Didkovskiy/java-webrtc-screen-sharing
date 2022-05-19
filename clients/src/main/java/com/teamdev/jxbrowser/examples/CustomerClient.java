package com.teamdev.jxbrowser.examples;

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.browser.callback.StartCaptureSessionCallback;
import com.teamdev.jxbrowser.browser.event.CaptureSessionStarted;
import com.teamdev.jxbrowser.capture.AudioCaptureMode;
import com.teamdev.jxbrowser.capture.CaptureSession;
import com.teamdev.jxbrowser.capture.CaptureSource;
import com.teamdev.jxbrowser.capture.CaptureSources;
import com.teamdev.jxbrowser.engine.Engine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import static com.teamdev.jxbrowser.engine.RenderingMode.HARDWARE_ACCELERATED;
import static com.teamdev.jxbrowser.examples.Clients.loadHost;
import static java.util.Arrays.asList;

/**
 * A client application for a customer that opens a window with a button to request technical support.
 */
public final class CustomerClient {

    private Browser browser;
    private CaptureSession captureSession;
    private Runnable confirmCaptureSessionSuccess;

    public void start() {
        Engine engine = Engine.newInstance(HARDWARE_ACCELERATED);
        browser = initBrowser(engine);
        initUI();

        loadHost(browser);
        browser.mainFrame().ifPresent(mainFrame -> mainFrame.executeJavaScript("initializeCustomer()"));
    }

    private Browser initBrowser(Engine engine) {
        Browser browser = engine.newBrowser();

        // Register a callback that will be executed when the capture session starts.
        browser.set(StartCaptureSessionCallback.class, (params, tell) -> {
            CaptureSources sources = params.sources();

            // Share the entire screen.
            CaptureSource screen = sources.screens().get(0);

            // Tell the browser instance to start a new capture session.
            // This is a programmatic version of selecting a sharing source in Chromium's dialog.
            tell.selectSource(screen, AudioCaptureMode.CAPTURE);
        });

        // Register an event observer to be notified when the capture session starts.
        browser.on(CaptureSessionStarted.class, event -> {
            captureSession = event.capture();

            // Invoke a callback to confirm the capture session success.
            confirmCaptureSessionSuccess.run();
        });

        return browser;
    }

    private void initUI() {
        JFrame frame = new JFrame("Customer Browser");
        JPanel mainPanel = initMainPanel();

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                browser.engine().close();
            }
        });
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setLayout(new GridBagLayout());
        frame.getContentPane().setBackground(Color.WHITE);
        frame.add(mainPanel, new GridBagConstraints());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JPanel initMainPanel() {
        JPanel panel = new JPanel();
        JButton callSupportButton = new JButton("Call Support");
        JButton stopSessionButton = new JButton("Stop session");
        ImageIcon loaderIcon = new ImageIcon("clients/src/main/resources/spinner.gif");
        JLabel waitingForResponseLabel = new JLabel("Waiting for a response from support... ", loaderIcon, JLabel.CENTER);
        JLabel sharingScreenLabel = new JLabel("You are sharing the primary screen", JLabel.CENTER);

        panel.setBackground(Color.WHITE);
        panel.add(callSupportButton);

        callSupportButton.addActionListener(e -> {
            browser.mainFrame().ifPresent(mainFrame -> mainFrame.executeJavaScript("requestSupport()"));
            updatePanel(panel,
                    asList(waitingForResponseLabel),
                    asList(callSupportButton));
        });

        stopSessionButton.addActionListener(e -> {
            captureSession.stop();
            updatePanel(panel,
                    asList(callSupportButton),
                    asList(sharingScreenLabel, stopSessionButton));
        });

        confirmCaptureSessionSuccess = () ->
                updatePanel(panel,
                        asList(sharingScreenLabel, stopSessionButton),
                        asList(waitingForResponseLabel));

        return panel;
    }

    private void updatePanel(JPanel panel,
                             List<JComponent> componentsToAdd,
                             List<JComponent> componentsToRemove) {
        componentsToAdd.forEach(panel::add);
        componentsToRemove.forEach(panel::remove);
        panel.revalidate();
        panel.repaint();
    }

    public static void main(String[] args) {
        new CustomerClient().start();
    }
}