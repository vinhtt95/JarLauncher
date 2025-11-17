package com.vinhtt.jarlauncher.services.impl;

import com.vinhtt.jarlauncher.services.interfaces.IScriptFinderService;
import java.io.File;
import java.util.Optional;

public class RunScriptFinderService implements IScriptFinderService {
    private static final String SCRIPT_NAME = "run.sh";

    @Override
    public Optional<String> findRunScript(String projectPath) {
        File scriptFile = new File(projectPath, SCRIPT_NAME);

        // Kiểm tra file tồn tại và có thể đọc được
        if (scriptFile.exists() && scriptFile.isFile()) {
            return Optional.of(scriptFile.getAbsolutePath());
        }

        return Optional.empty();
    }
}