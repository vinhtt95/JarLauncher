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
            // Lưu ý: Đảm bảo đường dẫn trùng khớp với nơi bạn để file trong resources
            InputStream iconStream = getClass().getResourceAsStream("app_icon.png");
            if (iconStream != null) {
                stage.getIcons().add(new Image(iconStream));

                // Dành riêng cho macOS (để icon hiển thị trên Dock khi chạy bằng IDE/Jar)
                // Yêu cầu JDK 9 trở lên (Dự án bạn đang dùng JDK 21 nên OK)
                if (java.awt.Taskbar.isTaskbarSupported()) {
                    java.awt.Taskbar taskbar = java.awt.Taskbar.getTaskbar();
                    if (taskbar.isSupported(java.awt.Taskbar.Feature.ICON_IMAGE)) {
                        java.awt.Image awtImage = javax.imageio.ImageIO.read(Objects.requireNonNull(MainApplication.class.getResourceAsStream("app-icon.png")));
                        taskbar.setIconImage(awtImage);
                    }
                }
            } else {
                System.err.println("Không tìm thấy file icon!");
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