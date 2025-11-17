package com.vinhtt.jarlauncher.model;

import java.util.UUID;

public class JavaProject {
    private String id;
    private String name;
    private String projectPath;
    private String jarPath;
    private String iconPath;

    // Constructor mặc định cho Jackson
    public JavaProject() {
        this.id = UUID.randomUUID().toString();
    }

    public JavaProject(String name, String projectPath, String jarPath) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.projectPath = projectPath;
        this.jarPath = jarPath;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getProjectPath() { return projectPath; }
    public void setProjectPath(String projectPath) { this.projectPath = projectPath; }

    public String getJarPath() { return jarPath; }
    public void setJarPath(String jarPath) { this.jarPath = jarPath; }

    public String getIconPath() { return iconPath; }
    public void setIconPath(String iconPath) { this.iconPath = iconPath; }
}