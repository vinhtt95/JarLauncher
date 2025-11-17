package com.vinhtt.jarlauncher;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle; // Thêm import này

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("view/MainView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 700); // Kích thước scene mới

        // Load file CSS Dark Mode
        scene.getStylesheets().add(getClass().getResource("dark-theme.css").toExternalForm());

        // Thử nghiệm với StageStyle.TRANSPARENT để xem hiệu ứng trong suốt (cần thêm config JavaFX)
        // Nếu không có config đặc biệt, nó chỉ làm cho cửa sổ không có viền và màu nền root vẫn hiển thị
        // stage.initStyle(StageStyle.TRANSPARENT);

        stage.setTitle("Jar Launcher Manager");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}