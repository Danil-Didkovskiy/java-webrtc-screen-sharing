import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.browser.callback.InjectJsCallback;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.js.JsAccessible;
import com.teamdev.jxbrowser.js.JsObject;
import com.teamdev.jxbrowser.view.swing.BrowserView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Paths;
import java.util.Objects;

import static com.teamdev.jxbrowser.engine.RenderingMode.HARDWARE_ACCELERATED;
import static javax.swing.SwingUtilities.invokeLater;
import static util.Clients.connectTechSupportToServer;
import static util.Clients.executeJS;

/**
 * A tech support client application that waits for a support request from a customer client application
 * and loads a browser widget to remotely observe the customer's screen.
 */
public final class TechSupportClient {

    private static JFrame frame;
    private static Browser browser;

    public static void main(String[] args) {
        Engine engine = Engine.newInstance(HARDWARE_ACCELERATED);
        browser = engine.newBrowser();

        browser.set(InjectJsCallback.class, params -> {
            JsObject window = params.frame().executeJavaScript("window");
            Objects.requireNonNull(window).putProperty("techSupportBrowser", new TechSupportClient());
            return InjectJsCallback.Response.proceed();
        });

        initUI();
        connectTechSupportToServer(browser, args);
    }

    private static void initUI() {
        invokeLater(() -> {
            frame = new JFrame("Tech Support Browser");
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
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    @JsAccessible
    public void displayAcceptMessage(String customerId) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);

        String message = String.format("Received a request from %s", customerId);
        JLabel label = new JLabel(message);
        JButton button = new JButton("Accept");
        button.addActionListener((event) -> {
            executeJS("notifySupportRequestAccepted()", browser);

            panel.remove(label);
            panel.remove(button);
            panel.revalidate();
            panel.repaint();
        });

        panel.add(label);
        panel.add(button);

        frame.add(panel, BorderLayout.NORTH);
        frame.revalidate();
        frame.repaint();
    }
}
