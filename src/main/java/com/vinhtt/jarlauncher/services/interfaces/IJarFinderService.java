package com.vinhtt.jarlauncher.services.interfaces;

import java.util.Optional;

public interface IJarFinderService {
    Optional<String> findJarInProject(String projectPath);
}