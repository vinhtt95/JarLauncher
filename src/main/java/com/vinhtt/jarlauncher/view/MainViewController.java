package com.vinhtt.jarlauncher.view;

import com.vinhtt.jarlauncher.services.impl.JsonProjectRepository;
import com.vinhtt.jarlauncher.services.impl.MacOSProcessLaunchService;
import com.vinhtt.jarlauncher.services.impl.RunScriptFinderService;
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
        // Dependency Injection
        viewModel = new MainViewModel(
                new JsonProjectRepository(),
                new RunScriptFinderService(),
                new MacOSProcessLaunchService()
        );

        // Lắng nghe thay đổi của list (Add, Remove) và gọi refreshGrid
        viewModel.getProjects().addListener((ListChangeListener<ProjectViewModel>) change -> {
            refreshGrid();
        });

        // Vẽ grid lần đầu tiên
        refreshGrid();
    }

    /**
     * Vẽ lại toàn bộ grid. Xóa tất cả và thêm lại các project card,
     * sau đó thêm "Add Card" vào cuối.
     */
    private void refreshGrid() {
        projectGrid.getChildren().clear();

        // Thêm tất cả project card
        for (ProjectViewModel p : viewModel.getProjects()) {
            addProjectTile(p);
        }

        // Thêm "Add Project Card" vào cuối
        addTheAddCard();
    }

    /**
     * Tải FXML của ProjectCell, bind data và thêm vào grid.
     */
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

    /**
     * Tải FXML của AddProjectCard, gán sự kiện click, và thêm vào grid.
     */
    private void addTheAddCard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddProjectCard.fxml"));
            Parent root = loader.load();

            // Gán sự kiện click cho "Add Card"
            root.setOnMouseClicked(event -> handleAddProject());

            projectGrid.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Hàm này bây giờ được gọi bởi "Add Card".
     */
    private void handleAddProject() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Chọn thư mục dự án (chứa run.sh)");
        File selectedDirectory = directoryChooser.showDialog(projectGrid.getScene().getWindow());

        if (selectedDirectory != null) {
            try {
                viewModel.addNewProject(selectedDirectory);
                // ListChangeListener sẽ tự động kích hoạt refreshGrid()
            } catch (Exception e) {
                showErrorDialog(e.getMessage());
            }
        }
    }

    /**
     * Hiển thị lỗi với style dark mode
     */
    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.getDialogPane().getStylesheets().add(
                getClass().getResource("/com/vinhtt/jarlauncher/dark-theme.css").toExternalForm()
        );
        alert.show();
    }
}