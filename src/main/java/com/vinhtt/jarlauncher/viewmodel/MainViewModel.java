package com.vinhtt.jarlauncher.viewmodel;

import com.vinhtt.jarlauncher.model.JavaProject;
import com.vinhtt.jarlauncher.services.interfaces.IScriptFinderService; // Sử dụng Interface mới
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

    private final IProjectRepository repository;
    private final IScriptFinderService finderService; // Đổi tên biến
    private final IProcessLaunchService launchService;

    public MainViewModel(IProjectRepository repository,
                         IScriptFinderService finderService,
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
        // Gọi service tìm run.sh
        Optional<String> scriptPath = finderService.findRunScript(projectDir.getAbsolutePath());

        if (scriptPath.isPresent()) {
            JavaProject newProject = new JavaProject(
                    projectDir.getName(),
                    projectDir.getAbsolutePath(),
                    scriptPath.get()
            );
            projects.add(new ProjectViewModel(newProject));
            saveData();
        } else {
            throw new RuntimeException("Không tìm thấy file 'run.sh' trong thư mục dự án!");
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