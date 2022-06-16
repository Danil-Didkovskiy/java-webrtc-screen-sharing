/*
 * Copyright 2000-2022 TeamDev.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.teamdev.jxbrowser.examples;

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.view.swing.BrowserView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static com.teamdev.jxbrowser.engine.RenderingMode.HARDWARE_ACCELERATED;

/**
 * An application that receives a screen sharing stream and shows it.
 */
public final class Receiver {

    public static void main(String[] args) {
        Engine engine = Engine.newInstance(HARDWARE_ACCELERATED);
        Browser browser = engine.newBrowser();

        initUI(browser);
        navigateToLocalhost(browser);
    }

    private static void navigateToLocalhost(Browser browser) {
        String port = System.getProperty("server.port");
        String url = String.format("http://localhost:%s/receiver", port);
        browser.navigation().loadUrlAndWait(url);
    }

    private static void initUI(Browser browser) {
        JFrame frame = new JFrame("Receiver");
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
}
