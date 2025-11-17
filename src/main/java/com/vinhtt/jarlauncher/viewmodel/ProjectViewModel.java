package com.vinhtt.jarlauncher.viewmodel;

import com.vinhtt.jarlauncher.model.JavaProject;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

import java.io.File;
import java.io.FileInputStream;

public class ProjectViewModel {
    private final JavaProject model;
    private final StringProperty name = new SimpleStringProperty();
    private final ObjectProperty<Image> icon = new SimpleObjectProperty<>();

    public ProjectViewModel(JavaProject model) {
        this.model = model;
        this.name.set(model.getName());
        loadIcon();
    }

    private void loadIcon() {
        try {
            if (model.getIconPath() != null && !model.getIconPath().isEmpty()) {
                File imgFile = new File(model.getIconPath());
                if (imgFile.exists()) {
                    this.icon.set(new Image(new FileInputStream(imgFile)));
                    return;
                }
            }
            // Load default icon (cần có file java-icon.png trong resources hoặc dùng dummy)
            // Ở đây dùng null để View tự xử lý fallback nếu muốn
            this.icon.set(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JavaProject getModel() {
        // Cập nhật model trước khi trả về
        model.setName(name.get());
        return model;
    }

    public void setIconPath(String path) {
        model.setIconPath(path);
        loadIcon();
    }

    public StringProperty nameProperty() { return name; }
    public ObjectProperty<Image> iconProperty() { return icon; }
}