package com.vinhtt.jarlauncher.model;

import java.util.UUID;

public class JavaProject {
    private String id;
    private String name;
    private String projectPath;
    private String scriptPath;
    private String iconPath;

    public JavaProject() {
        this.id = UUID.randomUUID().toString();
    }

    public JavaProject(String name, String projectPath, String scriptPath) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.projectPath = projectPath;
        this.scriptPath = scriptPath;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getProjectPath() { return projectPath; }
    public void setProjectPath(String projectPath) { this.projectPath = projectPath; }

    public String getScriptPath() { return scriptPath; } // Getter mới
    public void setScriptPath(String scriptPath) { this.scriptPath = scriptPath; } // Setter mới

    public String getIconPath() { return iconPath; }
    public void setIconPath(String iconPath) { this.iconPath = iconPath; }
}