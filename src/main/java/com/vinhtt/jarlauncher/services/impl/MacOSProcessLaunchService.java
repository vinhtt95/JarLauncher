package com.vinhtt.jarlauncher.services.impl;

import com.vinhtt.jarlauncher.model.JavaProject;
import com.vinhtt.jarlauncher.services.interfaces.IProcessLaunchService;

public class MacOSProcessLaunchService implements IProcessLaunchService {
    @Override
    public void launchProject(JavaProject project) throws Exception {
        // Escape dấu ngoặc kép để tránh lỗi AppleScript
        String safeProjectPath = project.getProjectPath().replace("\"", "\\\"");
        String safeJarPath = project.getJarPath().replace("\"", "\\\"");

        String appleScript = String.format(
                "tell application \"Terminal\"\n" +
                        "    activate\n" +
                        "    do script \"cd \\\"%s\\\" && java -jar \\\"%s\\\"\"\n" +
                        "end tell",
                safeProjectPath, safeJarPath
        );

        new ProcessBuilder("osascript", "-e", appleScript).start();
    }
}