package com.vinhtt.jarlauncher.view;

import com.vinhtt.jarlauncher.viewmodel.MainViewModel;
import com.vinhtt.jarlauncher.viewmodel.ProjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.File;

public class ProjectCellController {
    @FXML private Label nameLabel;
    @FXML private javafx.scene.image.ImageView iconView;

    private ProjectViewModel projectViewModel;
    private MainViewModel mainViewModel;

    // Ảnh mặc định (cần thêm file này vào resources nếu muốn đẹp, hoặc dùng text placeholder)
    private static final Image DEFAULT_ICON = new Image("https://upload.wikimedia.org/wikipedia/en/3/30/Java_programming_language_logo.svg");

    public void setData(ProjectViewModel projectVM, MainViewModel mainVM) {
        this.projectViewModel = projectVM;
        this.mainViewModel = mainVM;

        // Data Binding
        nameLabel.textProperty().bindBidirectional(projectVM.nameProperty());

        // Icon Binding
        if (projectVM.iconProperty().get() != null) {
            iconView.setImage(projectVM.iconProperty().get());
        } else {
            iconView.setImage(DEFAULT_ICON);
        }

        // Listen change để update icon nếu user đổi
        projectVM.iconProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) iconView.setImage(newVal);
        });

        setupContextMenu();
    }

    private void setupContextMenu() {
        ContextMenu menu = new ContextMenu();

        MenuItem renameItem = new MenuItem("Rename");
        renameItem.setOnAction(e -> handleRename());

        MenuItem changeIconItem = new MenuItem("Change Icon");
        changeIconItem.setOnAction(e -> handleChangeIcon());

        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(e -> handleDelete());

        menu.getItems().addAll(renameItem, changeIconItem, new SeparatorMenuItem(), deleteItem);

        nameLabel.getParent().setOnContextMenuRequested(e ->
                menu.show(nameLabel.getParent(), e.getScreenX(), e.getScreenY())
        );
    }

    @FXML
    private void handleMouseClick(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
            try {
                mainViewModel.launchProject(projectViewModel);
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Lỗi khởi chạy: " + e.getMessage());
                alert.show();
            }
        }
    }

    private void handleRename() {
        TextInputDialog dialog = new TextInputDialog(projectViewModel.nameProperty().get());
        dialog.setTitle("Rename Project");
        dialog.setHeaderText("Nhập tên mới:");
        dialog.showAndWait().ifPresent(newName -> {
            projectViewModel.nameProperty().set(newName);
            mainViewModel.saveData();
        });
    }

    private void handleChangeIcon() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(nameLabel.getScene().getWindow());
        if (file != null) {
            projectViewModel.setIconPath(file.getAbsolutePath());
            mainViewModel.saveData();
        }
    }

    private void handleDelete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc muốn xóa dự án này?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                mainViewModel.deleteProject(projectViewModel);
            }
        });
    }
}