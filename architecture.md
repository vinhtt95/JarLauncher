# Architecture Design Document: Jar Launcher Manager (macOS)

**Phiên bản:** 1.0  
**Ngày tạo:** 2023-10-27  
**Nền tảng:** macOS  
**Ngôn ngữ:** Java 17+ / JavaFX

---

## 1. Tổng quan kiến trúc

Dự án được xây dựng dựa trên mẫu thiết kế **MVVM (Model-View-ViewModel)** để tách biệt rõ ràng giữa giao diện người dùng (UI) và logic nghiệp vụ. Hệ thống tuân thủ chặt chẽ các nguyên lý **SOLID**, đặc biệt là việc sử dụng Interfaces để đảm bảo tính lỏng lẻo (loosely coupled) và khả năng mở rộng.

### Công nghệ sử dụng
* **Core:** Java 17 (hoặc cao hơn).
* **UI Framework:** JavaFX.
* **Dependency Management:** Maven.
* **Data Serialization:** Jackson (hoặc Gson) để lưu trữ cấu hình JSON.
* **System Integration:** AppleScript (thông qua `ProcessBuilder`) để tương tác với Terminal macOS.

---

## 2. Sơ đồ kiến trúc (High Level)

```mermaid
graph TD
    User[User Interaction] --> View[View (FXML + Controller)]
    View -- Data Binding / Commands --> ViewModel[ViewModel]
    ViewModel -- Updates (Properties) --> View
    ViewModel --> ServiceLayer[Service Layer (Interfaces)]
    
    subgraph Services [Business Logic & Infrastructure]
        ServiceLayer --> Repo[ProjectRepository]
        ServiceLayer --> Finder[JarFinderService]
        ServiceLayer --> Launcher[ProcessLaunchService]
    end

    Repo --> Disk[JSON Data Store]
    Finder --> FileSystem[Project File System]
    Launcher --> OS[macOS Terminal]
```
## 3. Cấu trúc dự án (Project Structure)

Cấu trúc thư mục source code (`src/main/java/com/vinhtt/jarlauncher`) được tổ chức như sau:

```text
├── MainApplication.java       // Entry Point: Khởi tạo Stage, DI Container
├── model/                     // [Model]: Các lớp POJO đại diện cho dữ liệu
│   └── JavaProject.java
├── view/                      // [View]: Giao diện người dùng
│   ├── MainView.fxml
│   ├── MainViewController.java
│   ├── ProjectCell.fxml
│   └── ProjectCellController.java
├── viewmodel/                 // [ViewModel]: Logic trạng thái và cầu nối UI
│   ├── MainViewModel.java
│   └── ProjectViewModel.java
├── services/                  // [Services]: Logic nghiệp vụ cốt lõi
│   ├── interfaces/            // Abstraction (SOLID: Dependency Inversion)
│   │   ├── IProjectRepository.java
│   │   ├── IJarFinderService.java
│   │   └── IProcessLaunchService.java
│   └── impl/                  // Concrete Implementation
│       ├── JsonProjectRepository.java
│       ├── MavenJarFinderService.java
│       └── MacOSProcessLaunchService.java
└── util/                      // Các tiện ích chung
    └── ImageUtils.java
```
## 4. Chi tiết thành phần

### 4.1. Model (M)
Chứa dữ liệu thô, không chứa logic nghiệp vụ hay logic giao diện.
* **`JavaProject`**:
    * `id`: UUID (định danh duy nhất).
    * `name`: Tên hiển thị.
    * `projectPath`: Đường dẫn thư mục gốc dự án.
    * `jarPath`: Đường dẫn file .jar thực thi.
    * `iconPath`: Đường dẫn file icon tùy chỉnh (optional).

### 4.2. View (V)
Chịu trách nhiệm hiển thị và bắt sự kiện người dùng. View **không** xử lý logic, chỉ chuyển tiếp hành động tới ViewModel.
* **`MainViewController`**:
    * Khởi tạo `MainViewModel`.
    * Bind `TilePane` với `ObservableList<ProjectViewModel>` từ ViewModel.
    * Xử lý sự kiện click nút "Add" -> gọi `viewModel.addNewProject()`.
* **`ProjectCellController`**:
    * Đại diện cho từng ô dự án trong Grid.
    * Bind `Label`, `ImageView` với properties của `ProjectViewModel`.
    * Xử lý sự kiện: Double-click (Launch), Right-click (Context Menu: Rename, Change Icon, Delete).

### 4.3. ViewModel (VM)
Chứa trạng thái của View và logic trình bày. Sử dụng JavaFX Properties để Binding.
* **`MainViewModel`**:
    * `ObservableList<ProjectViewModel> projects`: Danh sách dự án hiển thị.
    * `addNewProject(String path)`: Gọi `JarFinderService`, nếu thành công thì tạo Model, lưu qua `Repo`, và thêm vào list.
    * `deleteProject(ProjectViewModel p)`: Xóa khỏi list và cập nhật `Repo`.
* **`ProjectViewModel`**:
    * Wrapper cho `JavaProject`.
    * Các thuộc tính `StringProperty name`, `ObjectProperty<Image> icon` để UI bind trực tiếp.
    * Logic cập nhật tên/icon -> tự động trigger lưu dữ liệu ở MainViewModel.

### 4.4. Services (Business Logic)
Áp dụng nguyên lý **Dependency Inversion** (D trong SOLID). ViewModel chỉ giao tiếp qua Interface.

#### A. `IJarFinderService`
* **Nhiệm vụ:** Tìm kiếm file .jar hợp lệ.
* **Implementation (`MavenJarFinderService`):**
    1.  Quét thư mục `/target` trong đường dẫn dự án.
    2.  Lọc bỏ: `original-*.jar`, `*-sources.jar`, `*-javadoc.jar`.
    3.  Ưu tiên file jar có manifest hoặc file jar ngắn gọn nhất.

#### B. `IProcessLaunchService`
* **Nhiệm vụ:** Khởi chạy ứng dụng.
* **Implementation (`MacOSProcessLaunchService`):**
    * Sử dụng `ProcessBuilder` để gọi `osascript` (AppleScript).
    * **Script Template:**
        ```applescript
        tell application "Terminal"
            activate
            do script "cd \"{projectPath}\" && java -jar \"{jarPath}\""
        end tell
        ```

#### C. `IProjectRepository`
* **Nhiệm vụ:** CRUD dữ liệu (Persistence).
* **Implementation (`JsonProjectRepository`):**
    * Sử dụng thư viện Jackson.
    * Đường dẫn lưu file: `System.getProperty("user.home") + "/.jar-launcher/data.json"`.

---

## 5. Luồng dữ liệu (Data Flow)

### 5.1. Kịch bản: Thêm dự án mới
1.  **User** click "Add" -> **View** mở `DirectoryChooser`.
2.  User chọn folder -> **View** gửi path tới **MainViewModel**.
3.  **MainViewModel** gọi `IJarFinderService.findJar(path)`.
4.  **Service** trả về đường dẫn `.jar`.
5.  **MainViewModel** tạo `JavaProject` (Model) mới -> thêm vào `ObservableList`.
6.  **View** tự động cập nhật UI (nhờ Binding).
7.  **MainViewModel** gọi `IProjectRepository.save()` để ghi xuống ổ cứng.

### 5.2. Kịch bản: Chạy dự án
1.  **User** double-click item -> **View** gọi `ProjectViewModel.launch()`.
2.  **ProjectViewModel** gọi `MainViewModel.launch(this)`.
3.  **MainViewModel** gọi `IProcessLaunchService.launch(javaProject)`.
4.  **Service** thực thi AppleScript -> Mở Terminal macOS.

---

## 6. Yêu cầu kỹ thuật & Ràng buộc

1.  **Hệ điều hành:** Ứng dụng chỉ hoạt động đầy đủ chức năng trên macOS (do phụ thuộc vào AppleScript).
2.  **Java Runtime:** Cần JRE/JDK 17 trở lên để hỗ trợ tốt JavaFX và các tính năng mới của ngôn ngữ (`record`, `var`, etc nếu dùng).
3.  **Icon Handling:** Cần xử lý Exception khi load ảnh custom (nếu file ảnh bị xóa, tự động fallback về icon mặc định).
4.  **Concurrency:** Các tác vụ nặng (quét file, I/O) nên chạy trên background thread (JavaFX `Task` hoặc `CompletableFuture`) để tránh treo UI.

---

## 7. Mở rộng trong tương lai (Roadmap)

* Hỗ trợ Windows (thêm `WindowsProcessLaunchService`).
* Hiển thị trạng thái Process (Running/Stopped) - yêu cầu quản lý PID.
* Hỗ trợ truyền Arguments (VM Options, Program Arguments).
* Tích hợp giám sát log ngay trên UI thay vì mở Terminal rời.