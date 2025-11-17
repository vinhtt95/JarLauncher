package com.vinhtt.jarlauncher.services.interfaces;

import java.util.Optional;

public interface IScriptFinderService {
    // Tìm file script (run.sh) thay vì tìm jar
    Optional<String> findRunScript(String projectPath);
}