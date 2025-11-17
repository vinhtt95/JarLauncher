package com.vinhtt.jarlauncher.viewmodel;

import com.vinhtt.jarlauncher.model.JavaProject;
import com.vinhtt.jarlauncher.services.interfaces.IJarFinderService;
import com.vinhtt.jarlauncher.services.interfaces.IProcessLaunchService;
import com.vinhtt.jarlauncher.services.interfaces.IProjectRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MainViewModel {
    private final ObservableList<ProjectViewModel> projects = FXCollections.observableArrayList();

    // Services
    private final IProjectRepository repository;
    private final IJarFinderService finderService;
    private final IProcessLaunchService launchService;

    public MainViewModel(IProjectRepository repository,
                         IJarFinderService finderService,
                         IProcessLaunchService launchService) {
        this.repository = repository;
        this.finderService = finderService;
        this.launchService = launchService;
        loadProjects();
    }

    public ObservableList<ProjectViewModel> getProjects() {
        return projects;
    }

    private void loadProjects() {
        List<JavaProject> loaded = repository.loadProjects();
        projects.addAll(loaded.stream().map(ProjectViewModel::new).collect(Collectors.toList()));
    }

    public void addNewProject(File projectDir) {
        Optional<String> jarPath = finderService.findJarInProject(projectDir.getAbsolutePath());
        if (jarPath.isPresent()) {
            JavaProject newProject = new JavaProject(projectDir.getName(), projectDir.getAbsolutePath(), jarPath.get());
            projects.add(new ProjectViewModel(newProject));
            saveData();
        } else {
            throw new RuntimeException("Không tìm thấy file JAR trong thư mục target!");
        }
    }

    public void launchProject(ProjectViewModel vm) throws Exception {
        launchService.launchProject(vm.getModel());
    }

    public void deleteProject(ProjectViewModel vm) {
        projects.remove(vm);
        saveData();
    }

    public void saveData() {
        List<JavaProject> models = projects.stream()
                .map(ProjectViewModel::getModel)
                .collect(Collectors.toList());
        repository.saveProjects(models);
    }
}