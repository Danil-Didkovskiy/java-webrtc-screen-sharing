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
import static com.teamdev.jxbrowser.examples.Clients.loadHost;

/**
 * A client application for a customer that opens a window with a button to request technical support.
 */
public final class Streamer {

    private Browser browser;
    private CaptureSession captureSession;

    public void start() {
        Engine engine = Engine.newInstance(HARDWARE_ACCELERATED);
        browser = engine.newBrowser();

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
        browser.on(CaptureSessionStarted.class, event -> captureSession = event.capture());

        initUI();

        loadHost(browser);
        browser.mainFrame().ifPresent(mainFrame -> mainFrame.executeJavaScript("initializeStreamer()"));
    }

    private void initUI() {
        JFrame frame = new JFrame("Streamer Browser");
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
        JButton startSharingButton = new JButton("Share your screen...");
        JButton stopSharingButton = new JButton("Stop sharing");
        JLabel sharingScreenLabel = new JLabel("You are sharing the primary screen", JLabel.CENTER);

        panel.add(startSharingButton);

        startSharingButton.addActionListener(e -> {
            browser.mainFrame().ifPresent(mainFrame -> mainFrame.executeJavaScript("startScreenSharing()"));
            panel.remove(startSharingButton);
            panel.add(sharingScreenLabel);
            panel.add(stopSharingButton);
            panel.revalidate();
            panel.repaint();
        });

        stopSharingButton.addActionListener(e -> {
            captureSession.stop();
            panel.remove(sharingScreenLabel);
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
