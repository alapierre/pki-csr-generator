package io.alapierre.pki.gui;

import java.awt.*;
import java.io.File;

/**
 * @author Adrian Lapierre {@literal <alapierre@soft-project.pl>}
 */
public class FileUtil {

    public static String toFileSystemSafeName(String inputName) {
        return inputName.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
    }

    public static void runDefaultApp(File file) throws Exception {

        Desktop desktop = null;
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
            desktop.open(file);
        } else {
            System.out.println("Desktop not supported");
        }
    }

    public static String getFileExtension(String fileName) {
        if (fileName.isEmpty() || !fileName.contains(".") || fileName.endsWith(".")) return null;
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public static String getBaseFileName(String fileName) {
        if (fileName.isEmpty() || !fileName.contains(".") || fileName.endsWith(".")) return null;
        return fileName.substring(0,fileName.lastIndexOf("."));
    }
}
