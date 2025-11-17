package com.vinhtt.jarlauncher.services.impl;

import com.vinhtt.jarlauncher.services.interfaces.IJarFinderService;
import java.io.File;
import java.util.Optional;

public class MavenJarFinderService implements IJarFinderService {
    @Override
    public Optional<String> findJarInProject(String projectPath) {
        File targetDir = new File(projectPath, "target");
        if (!targetDir.exists() || !targetDir.isDirectory()) {
            return Optional.empty();
        }

        File[] files = targetDir.listFiles();
        if (files == null) return Optional.empty();

        for (File file : files) {
            String name = file.getName();
            // Logic lọc file Jar: Có đuôi .jar và không phải sources/javadoc/original
            if (name.endsWith(".jar")
                    && !name.contains("-sources.jar")
                    && !name.contains("-javadoc.jar")
                    && !name.startsWith("original-")) {
                return Optional.of(file.getAbsolutePath());
            }
        }
        return Optional.empty();
    }
}