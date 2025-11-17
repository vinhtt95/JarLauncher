package com.vinhtt.jarlauncher.view;

import com.vinhtt.jarlauncher.services.impl.JsonProjectRepository;
import com.vinhtt.jarlauncher.services.impl.MacOSProcessLaunchService;
import com.vinhtt.jarlauncher.services.impl.MavenJarFinderService;
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
        // Dependency Injection (Thủ công cho đơn giản)
        viewModel = new MainViewModel(
                new JsonProjectRepository(),
                new MavenJarFinderService(),
                new MacOSProcessLaunchService()
        );

        // Load dữ liệu ban đầu
        for (ProjectViewModel p : viewModel.getProjects()) {
            addProjectTile(p);
        }

        // Lắng nghe thay đổi của list để update UI
        viewModel.getProjects().addListener((ListChangeListener<ProjectViewModel>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (ProjectViewModel p : change.getAddedSubList()) {
                        addProjectTile(p);
                    }
                }
                if (change.wasRemoved()) {
                    // Xóa tile tương ứng (Logic đơn giản: clear và render lại, hoặc tìm node để xóa)
                    // Để đơn giản cho demo: render lại toàn bộ khi xóa
                    projectGrid.getChildren().clear();
                    for (ProjectViewModel p : viewModel.getProjects()) {
                        addProjectTile(p);
                    }
                }
            }
        });
    }

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
        directoryChooser.setTitle("Chọn thư mục dự án Java");
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