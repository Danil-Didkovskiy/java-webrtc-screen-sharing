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

import static com.teamdev.jxbrowser.engine.RenderingMode.HARDWARE_ACCELERATED;

/**
 * An application that shares the primary screen.
 */
public final class Streamer {

    private static final String START_SHARING_BUTTON_TEXT = "Share your screen";
    private static final String STOP_SHARING_BUTTON_TEXT = "Stop sharing";

    private Browser browser;
    private CaptureSession captureSession;

    public void start() {
        Engine engine = Engine.newInstance(HARDWARE_ACCELERATED);
        browser = engine.newBrowser();

        configureCapturing();
        initUI();
        navigateToLocalhost(browser);
        browser.mainFrame().ifPresent(frame -> frame.executeJavaScript("connectStreamerToWebRtcServer()"));
    }

    private static void navigateToLocalhost(Browser browser) {
        String port = System.getProperty("server.port");
        String url = String.format("http://localhost:%s/", port);
        browser.navigation().loadUrlAndWait(url);
    }

    private void configureCapturing() {
        // When the browser is about to start a capturing session, select the capturing source:
        browser.set(StartCaptureSessionCallback.class, (params, tell) -> {
            CaptureSources sources = params.sources();
            // Share the entire screen.
            CaptureSource screen = sources.screens().get(0);
            tell.selectSource(screen, AudioCaptureMode.CAPTURE);
        });

        // When the capture session starts, save it to programmatically stop it later.
        browser.on(CaptureSessionStarted.class, event -> captureSession = event.capture());
    }

    private void initUI() {
        JFrame frame = new JFrame("Streamer");
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
        frame.add(mainPanel, new GridBagConstraints());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JPanel initMainPanel() {
        JPanel panel = new JPanel();
        JButton startSharingButton = new JButton(START_SHARING_BUTTON_TEXT);
        JButton stopSharingButton = new JButton(STOP_SHARING_BUTTON_TEXT);

        panel.add(startSharingButton);

        startSharingButton.addActionListener(e -> {
            browser.mainFrame().ifPresent(mainFrame -> mainFrame.executeJavaScript("startScreenSharing()"));
            panel.remove(startSharingButton);
            panel.add(stopSharingButton);
            panel.revalidate();
            panel.repaint();
        });

        stopSharingButton.addActionListener(e -> {
            captureSession.stop();
            panel.remove(stopSharingButton);
            panel.add(startSharingButton);
            panel.revalidate();
            panel.repaint();
        });

        return panel;
    }

    public static void main(String[] args) {
        new Streamer().start();
    }
}
