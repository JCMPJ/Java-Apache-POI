/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jcmpj.trt15.resources;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

/**
 *
 * @author jcmpj
 */
public class WindowManager {

    private static JDesktopPane jDesktopPane;

    public WindowManager(JDesktopPane jDesktopPane) {

        WindowManager.jDesktopPane = jDesktopPane;
    }

    public void openWindow(JInternalFrame jInternalFrame) {
        if (jInternalFrame.isVisible()) {
            jInternalFrame.toFront();
            jInternalFrame.requestFocusInWindow();
        } else {
            jDesktopPane.add(jInternalFrame);
            jInternalFrame.setVisible(true);            
        }
    }
}
