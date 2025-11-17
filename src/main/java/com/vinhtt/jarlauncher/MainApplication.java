package com.vinhtt.jarlauncher;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("view/MainView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 700); // Kích thước scene mới

        // Load file CSS Dark Mode
        scene.getStylesheets().add(getClass().getResource("dark-theme.css").toExternalForm());

        try {
            // SỬA Ở ĐÂY:
            // 1. Thêm dấu '/' ở đầu để tìm từ thư mục gốc resources
            // 2. Sửa 'app_icon.png' thành 'app-icon.png' cho đúng tên file
            InputStream iconStream = getClass().getResourceAsStream("/app-icon.png");

            if (iconStream != null) {
                stage.getIcons().add(new Image(iconStream));

                // Code cho macOS Dock icon
                if (java.awt.Taskbar.isTaskbarSupported()) {
                    java.awt.Taskbar taskbar = java.awt.Taskbar.getTaskbar();
                    if (taskbar.isSupported(java.awt.Taskbar.Feature.ICON_IMAGE)) {
                        // Đọc lại stream vì stream cũ đã bị 'new Image()' đọc hết
                        java.awt.Image awtImage = javax.imageio.ImageIO.read(
                                Objects.requireNonNull(getClass().getResourceAsStream("/app-icon.png"))
                        );
                        taskbar.setIconImage(awtImage);
                    }
                }
            } else {
                System.err.println("Không tìm thấy file icon tại đường dẫn /app-icon.png");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        stage.setTitle("Jar Launcher Manager");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}