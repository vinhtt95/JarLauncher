package com.vinhtt.jarlauncher.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinhtt.jarlauncher.model.JavaProject;
import com.vinhtt.jarlauncher.services.interfaces.IProjectRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JsonProjectRepository implements IProjectRepository {
    private final ObjectMapper mapper = new ObjectMapper();
    private final File dataFile;

    public JsonProjectRepository() {
        String userHome = System.getProperty("user.home");
        File configDir = Paths.get(userHome, ".jar-launcher").toFile();
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
        this.dataFile = new File(configDir, "data.json");
    }

    @Override
    public List<JavaProject> loadProjects() {
        if (!dataFile.exists()) return new ArrayList<>();
        try {
            return mapper.readValue(dataFile, new TypeReference<List<JavaProject>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public void saveProjects(List<JavaProject> projects) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(dataFile, projects);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}