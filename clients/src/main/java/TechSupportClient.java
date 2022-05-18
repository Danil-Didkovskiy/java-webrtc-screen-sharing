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
import static javax.swing.SwingUtilities.invokeLater;
import static util.Clients.*;

/**
 * A tech support client application that waits for a support request from a customer client application
 * and loads a browser widget to remotely observe the customer's screen.
 */
public final class TechSupportClient {

    private static JPanel mainPanel;
    private static Runnable acceptSupportRequest;

    public static void main(String[] args) {

        // Create an Engine and Browser instances.
        Engine engine = Engine.newInstance(HARDWARE_ACCELERATED);
        Browser browser = engine.newBrowser();

        // Inject an instance of the Java object into JavaScript
        // so that we can communicate with that object from JS.
        browser.set(InjectJsCallback.class, params -> {
            JsObject window = params.frame().executeJavaScript("window");
            Objects.requireNonNull(window).putProperty("techSupportClient", new TechSupportClient());
            return InjectJsCallback.Response.proceed();
        });

        acceptSupportRequest = () -> executeJS("notifySupportRequestAccepted()", browser);

        initUI(browser);
        connectTechSupportClient(browser, args);
    }

    private static void initUI(Browser browser) {
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
     * Displays a message with a button to accept a support request from a customer with the given id.
     *
     * @param customerId id of a customer requesting support
     */
    @JsAccessible
    public void displayAcceptMessage(String customerId) {
        String message = String.format("Received a request from %s", customerId);
        JLabel label = new JLabel(message);
        JButton acceptSupportButton = new JButton("Accept");

        acceptSupportButton.addActionListener((event) -> {
            acceptSupportRequest.run();
            updatePanel(mainPanel, List.of(), List.of(label, acceptSupportButton));
        });

        updatePanel(mainPanel, List.of(label, acceptSupportButton), List.of());
    }
}
