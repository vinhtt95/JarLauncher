package com.vinhtt.jarlauncher.services.impl;

import com.vinhtt.jarlauncher.model.JavaProject;
import com.vinhtt.jarlauncher.services.interfaces.IProcessLaunchService;

public class MacOSProcessLaunchService implements IProcessLaunchService {
    @Override
    public void launchProject(JavaProject project) throws Exception {
        String safeProjectPath = project.getProjectPath().replace("\"", "\\\"");

        // Lệnh chạy script: cd vào thư mục -> chạy sh run.sh
        // Dùng 'sh' để đảm bảo chạy được kể cả khi chưa chmod +x
        String command = "cd \\\"" + safeProjectPath + "\\\" && sh run.sh";

        String appleScript = String.format(
                "tell application \"Terminal\"\n" +
                        "    activate\n" +
                        "    do script \"%s\"\n" +
                        "end tell",
                command
        );

        new ProcessBuilder("osascript", "-e", appleScript).start();
    }
}