import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.browser.callback.StartCaptureSessionCallback;
import com.teamdev.jxbrowser.browser.event.CaptureSessionStarted;
import com.teamdev.jxbrowser.capture.AudioCaptureMode;
import com.teamdev.jxbrowser.capture.CaptureSession;
import com.teamdev.jxbrowser.capture.CaptureSource;
import com.teamdev.jxbrowser.capture.CaptureSources;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Paths;
import java.util.List;

import static com.teamdev.jxbrowser.engine.RenderingMode.HARDWARE_ACCELERATED;
import static javax.swing.SwingUtilities.invokeLater;
import static util.Clients.connectCustomerToServer;
import static util.Clients.executeJS;

/**
 * A customer client application that opens a window with a button to request technical support.
 */
public final class CustomerClient {

    private static final String CUSTOMER_ID = "Walter White";
    private static Browser browser;
    private static JPanel mainPanel;

    public static void main(String[] args) {
        Engine engine = Engine.newInstance(HARDWARE_ACCELERATED);
        browser = engine.newBrowser();

        browser.set(StartCaptureSessionCallback.class, (params, tell) -> {
            CaptureSources sources = params.sources();
            CaptureSource screen = sources.screens().get(0);
            tell.selectSource(screen, AudioCaptureMode.CAPTURE);
        });

        initUI();
        connectCustomerToServer(browser, args, CUSTOMER_ID);
    }

    private static void initUI() {
        invokeLater(() -> {
            JFrame frame = new JFrame("Customer Browser");
            mainPanel = initMainPanel();

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
        });
    }

    private static JPanel initMainPanel() {
        mainPanel = new JPanel();
        JButton callSupportButton = new JButton("Call Support");
        JButton stopSessionButton = new JButton("Stop session");
        ImageIcon loaderIcon = new ImageIcon("browsers/src/main/resources/spinner.gif");
        JLabel waitingForResponseLabel = new JLabel("Waiting for a response from support... ", loaderIcon, JLabel.CENTER);
        JLabel sharingScreenLabel = new JLabel("You are sharing the primary screen", JLabel.CENTER);

        mainPanel.setBackground(Color.WHITE);
        mainPanel.add(callSupportButton);

        callSupportButton.addActionListener(e -> {
            executeJS("notifySupportRequested()", browser);
            updateMainPanel(
                    List.of(waitingForResponseLabel),
                    List.of(callSupportButton));
        });

        browser.on(CaptureSessionStarted.class, (event) -> {
            updateMainPanel(
                    List.of(sharingScreenLabel, stopSessionButton),
                    List.of(waitingForResponseLabel));

            stopSessionButton.addActionListener(e -> {
                CaptureSession captureSession = event.capture();
                captureSession.stop();
                updateMainPanel(
                        List.of(callSupportButton),
                        List.of(sharingScreenLabel, stopSessionButton));
            });
        });

        return mainPanel;
    }

    private static void updateMainPanel(java.util.List<JComponent> componentsToAdd,
                                        java.util.List<JComponent> componentsToRemove) {
        componentsToAdd.forEach(component -> mainPanel.add(component));
        componentsToRemove.forEach(component -> mainPanel.remove(component));
        mainPanel.revalidate();
        mainPanel.repaint();
    }
}
