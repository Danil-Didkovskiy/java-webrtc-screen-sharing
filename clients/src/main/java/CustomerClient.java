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
import static javax.swing.SwingUtilities.invokeLater;
import static util.Clients.*;

/**
 * A customer client application that opens a window with a button to request technical support.
 */
public final class CustomerClient {

    private static final String CUSTOMER_ID = "Walter White";
    private static CaptureSession captureSession;
    private static Runnable confirmCaptureSessionSuccess;
    private static Runnable requestTechSupport;

    public static void main(String[] args) {

        // Create an Engine and Browser instances.
        Engine engine = Engine.newInstance(HARDWARE_ACCELERATED);
        Browser browser = engine.newBrowser();

        // Handle a request to start a capture session.
        browser.set(StartCaptureSessionCallback.class, (params, tell) -> {
            CaptureSources sources = params.sources();

            // Get the capture source (the first entire screen).
            CaptureSource screen = sources.screens().get(0);

            // Tell the browser instance to start a new capture session with capturing the audio content.
            tell.selectSource(screen, AudioCaptureMode.CAPTURE);
        });

        // Subscribe on capture session started event.
        browser.on(CaptureSessionStarted.class, (event) -> {

            // Get the capture session.
            captureSession = event.capture();

            // Invoke callback to confirm the capture session success.
            confirmCaptureSessionSuccess.run();
        });

        requestTechSupport = () -> executeJS("notifySupportRequested()", browser);

        initUI(browser);
        connectCustomerClient(browser, args, CUSTOMER_ID);
    }

    private static void initUI(Browser browser) {
        invokeLater(() -> {
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
        });
    }

    private static JPanel initMainPanel() {
        JPanel panel = new JPanel();
        JButton callSupportButton = new JButton("Call Support");
        JButton stopSessionButton = new JButton("Stop session");
        ImageIcon loaderIcon = new ImageIcon("browsers/src/main/resources/spinner.gif");
        JLabel waitingForResponseLabel = new JLabel("Waiting for a response from support... ", loaderIcon, JLabel.CENTER);
        JLabel sharingScreenLabel = new JLabel("You are sharing the primary screen", JLabel.CENTER);

        panel.setBackground(Color.WHITE);
        panel.add(callSupportButton);

        callSupportButton.addActionListener(e -> {
            requestTechSupport.run();
            updatePanel(panel,
                    List.of(waitingForResponseLabel),
                    List.of(callSupportButton));
        });

        stopSessionButton.addActionListener(e -> {
            captureSession.stop();
            updatePanel(panel,
                    List.of(callSupportButton),
                    List.of(sharingScreenLabel, stopSessionButton));
        });

        confirmCaptureSessionSuccess = () ->
                updatePanel(panel,
                        List.of(sharingScreenLabel, stopSessionButton),
                        List.of(waitingForResponseLabel));

        return panel;
    }
}
