package com.vinhtt.jarlauncher.view;

import com.vinhtt.jarlauncher.services.impl.JsonProjectRepository;
import com.vinhtt.jarlauncher.services.impl.MacOSProcessLaunchService;
import com.vinhtt.jarlauncher.services.impl.RunScriptFinderService; // Import class mới
import com.vinhtt.jarlauncher.viewmodel.MainViewModel;
import com.vinhtt.jarlauncher.viewmodel.ProjectViewModel;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.TilePane;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;

public class MainViewController {
    @FXML private TilePane projectGrid;

    private MainViewModel viewModel;

    public void initialize() {
        // Inject RunScriptFinderService thay vì MavenJarFinderService
        viewModel = new MainViewModel(
                new JsonProjectRepository(),
                new RunScriptFinderService(),
                new MacOSProcessLaunchService()
        );

        // ... (Phần còn lại giữ nguyên như cũ) ...
        for (ProjectViewModel p : viewModel.getProjects()) {
            addProjectTile(p);
        }

        viewModel.getProjects().addListener((ListChangeListener<ProjectViewModel>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (ProjectViewModel p : change.getAddedSubList()) {
                        addProjectTile(p);
                    }
                }
                if (change.wasRemoved()) {
                    projectGrid.getChildren().clear();
                    for (ProjectViewModel p : viewModel.getProjects()) {
                        addProjectTile(p);
                    }
                }
            }
        });
    }

    // ... (Các phương thức addProjectTile, handleAddProject giữ nguyên) ...
    private void addProjectTile(ProjectViewModel projectVM) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ProjectCell.fxml"));
            Parent root = loader.load();
            ProjectCellController controller = loader.getController();
            controller.setData(projectVM, viewModel);
            projectGrid.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddProject() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Chọn thư mục dự án (chứa run.sh)");
        File selectedDirectory = directoryChooser.showDialog(projectGrid.getScene().getWindow());

        if (selectedDirectory != null) {
            try {
                viewModel.addNewProject(selectedDirectory);
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                alert.show();
            }
        }
    }
}