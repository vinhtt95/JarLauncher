package com.vinhtt.jarlauncher.services.interfaces;

import com.vinhtt.jarlauncher.model.JavaProject;
import java.util.List;

public interface IProjectRepository {
    List<JavaProject> loadProjects();
    void saveProjects(List<JavaProject> projects);
}