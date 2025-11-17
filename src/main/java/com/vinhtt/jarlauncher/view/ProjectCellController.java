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
import java.io.InputStream;

public class ProjectCellController {
    @FXML private Label nameLabel;
    @FXML private javafx.scene.image.ImageView iconView;

    private ProjectViewModel projectViewModel;
    private MainViewModel mainViewModel;

    // Load icon default
    private static Image DEFAULT_ICON = null;
    static {
        try {
            InputStream is = ProjectCellController.class.getResourceAsStream("/java-icon.png");
            if (is != null) {
                DEFAULT_ICON = new Image(is);
            } else {
                DEFAULT_ICON = new Image("https://upload.wikimedia.org/wikipedia/en/3/30/Java_programming_language_logo.svg");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setData(ProjectViewModel projectVM, MainViewModel mainVM) {
        this.projectViewModel = projectVM;
        this.mainViewModel = mainVM;

        // Data Binding
        nameLabel.textProperty().bindBidirectional(projectVM.nameProperty());

        // Icon Binding
        updateIcon(projectVM.iconProperty().get());

        projectVM.iconProperty().addListener((obs, oldVal, newVal) -> updateIcon(newVal));

        setupContextMenu();
    }

    private void updateIcon(Image newImage) {
        if (newImage != null) {
            iconView.setImage(newImage);
        } else {
            iconView.setImage(DEFAULT_ICON);
        }
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
                showStyledError("Lỗi khởi chạy: " + e.getMessage());
            }
        }
    }

    private void handleRename() {
        TextInputDialog dialog = new TextInputDialog(projectViewModel.nameProperty().get());
        dialog.setTitle("Rename Project");
        dialog.setHeaderText("Nhập tên mới cho dự án:");
        // Áp dụng CSS cho dialog
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/com/vinhtt/jarlauncher/dark-theme.css").toExternalForm());

        dialog.showAndWait().ifPresent(newName -> {
            if (newName != null && !newName.trim().isEmpty()) {
                projectViewModel.nameProperty().set(newName);
                mainViewModel.saveData();
            }
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
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc muốn xóa dự án này?\n(File dự án gốc sẽ không bị xóa)", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText("Xóa " + projectViewModel.nameProperty().get() + "?");
        // Áp dụng CSS cho dialog
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/com/vinhtt/jarlauncher/dark-theme.css").toExternalForm());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                mainViewModel.deleteProject(projectViewModel);
            }
        });
    }

    private void showStyledError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/com/vinhtt/jarlauncher/dark-theme.css").toExternalForm());
        alert.show();
    }
}