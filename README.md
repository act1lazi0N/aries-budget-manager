# Aries - Budget Management

**Aries - Budget Management** là ứng dụng Android quản lý thu chi cá nhân, được xây dựng bằng **Kotlin**, **Jetpack Compose**, **Material Design 3**, **Navigation Compose**, **MVVM**, **Room Database**, **Retrofit** và **Firebase**.

Ứng dụng hỗ trợ ghi lại giao dịch thu/chi, phân loại theo danh mục, theo dõi số dư, thống kê tình hình tài chính, import/export CSV/JSON và đồng bộ dữ liệu giao dịch lên Firebase Firestore khi cấu hình Firebase có sẵn.

## Tính năng chính

- Dashboard tổng quan tài chính: số dư, tổng thu, tổng chi, chi tiêu tháng và giao dịch gần đây.
- Quản lý giao dịch: xem danh sách, tìm kiếm/lọc, thêm, sửa và xóa giao dịch.
- Validation dữ liệu nhập: số tiền hợp lệ, danh mục bắt buộc, ghi chú giới hạn độ dài.
- Thống kê: tổng giao dịch, tổng thu/chi, số dư, trung bình, min/max và phân nhóm theo danh mục.
- Cảnh báo ngân sách khi chi tiêu vượt mức.
- Import/export dữ liệu bằng CSV và JSON.
- Room Database để lưu dữ liệu cục bộ.
- Retrofit để gọi API tỷ giá hoặc dữ liệu từ remote service.
- Firebase Analytics và Firebase Firestore để phục vụ tracking và đồng bộ giao dịch.
- Settings và About screen.
- Ghi Logcat vòng đời Activity phục vụ kiểm tra lifecycle callback.

## Công nghệ sử dụng

- Kotlin
- Android Gradle Plugin
- Jetpack Compose
- Material Design 3
- Navigation Compose
- ViewModel / MVVM
- Room Database + KSP
- Retrofit + Gson Converter
- Firebase BoM
- Firebase Analytics
- Firebase Firestore
- Kotlin Coroutines Play Services
- CSV / JSON import-export

## Kiến trúc tổng quan

```text
UI / Jetpack Compose
        |
        v
ViewModel / UI State
        |
        v
Repository
        |
        +--> Room Database
        |
        +--> Firebase Firestore
        |
        +--> Retrofit remote API
```

Nguyên tắc triển khai:

- Compose chỉ hiển thị UI và gửi sự kiện người dùng.
- ViewModel quản lý UI state, validation và các phép tính thống kê.
- Repository là lớp trung gian giữa ViewModel và data source.
- Room là nguồn lưu trữ cục bộ chính.
- Firebase Firestore được dùng như remote data source khi `google-services.json` tồn tại.

## Cấu trúc thư mục chính

```text
app/src/main/java/com/example/project_budget
├── data
│   ├── local              # Room database, DAO, entity
│   ├── remote             # Firebase / Retrofit data source
│   ├── repository         # Repository giữa ViewModel và data source
│   ├── CsvExporter.kt
│   ├── JsonExporter.kt
│   └── SampleData.kt
├── model                  # Transaction, Category, Wallet, Budget, Currency
├── ui
│   ├── components
│   ├── navigation
│   ├── screen
│   │   ├── about
│   │   ├── home
│   │   ├── settings
│   │   ├── statistics
│   │   └── transaction
│   └── theme
├── utils
├── viewmodel
└── MainActivity.kt
```

## Firebase

Dự án đã được cấu hình để dùng Firebase ở module `app`:

- Root Gradle khai báo plugin `com.google.gms.google-services`.
- `app/build.gradle.kts` chỉ apply plugin Google Services khi file `app/google-services.json` tồn tại.
- Dependencies hiện có: Firebase BoM, Firebase Analytics, Firebase Firestore và `kotlinx-coroutines-play-services`.
- `FirebaseTransactionDataSource` lưu giao dịch theo đường dẫn Firestore dạng:

```text
users/demo-user/transactions/{transactionId}
```

### Cách thêm `google-services.json`

1. Tạo hoặc mở project trên Firebase Console.
2. Thêm Android app với package name:

```text
com.example.project_budget
```

3. Tải file `google-services.json` từ Firebase Console.
4. Đặt file vào đúng vị trí:

```text
app/google-services.json
```

5. Sync Gradle và build lại app.

### Lưu ý bảo mật

`google-services.json` là file cấu hình theo môi trường/máy local, không nên commit lên Git. Dự án đã ignore các file sau trong `.gitignore`:

```text
app/google-services.json
google-services.json
local.properties
.env
.env.local
*.keystore
*.jks
```

Nếu máy chưa có `app/google-services.json`, app vẫn build được vì plugin Google Services được apply có điều kiện. Các chức năng phụ thuộc Firebase chỉ hoạt động khi file cấu hình hợp lệ tồn tại.

## Yêu cầu môi trường

- Android Studio phiên bản mới hỗ trợ Kotlin/Compose hiện tại.
- JDK 11 hoặc cao hơn.
- Android SDK với compile SDK 36.
- Kết nối Internet nếu cần tải dependency Gradle hoặc sử dụng Firebase/remote API.

## Cách chạy dự án

1. Clone repository.
2. Mở project bằng Android Studio.
3. Kiểm tra `local.properties` đã trỏ đúng Android SDK.
4. Nếu cần Firebase, thêm `app/google-services.json` như hướng dẫn ở trên.
5. Sync Gradle.
6. Chạy app trên emulator hoặc thiết bị Android thật.

Build bằng Android Studio qua Gradle task `app:assembleDebug`. Nếu nhóm bổ sung Gradle Wrapper vào repo, có thể chạy `./gradlew assembleDebug` hoặc `gradlew.bat assembleDebug` tùy hệ điều hành.

## Tài liệu dự án

Các tài liệu chi tiết nằm trong thư mục [`docs`](docs/):

- [Hướng dẫn setup dự án](docs/SETUP_GUIDE.md)
- [Hướng dẫn chia branch Git](docs/BRANCHING_GUIDE.md)
- [Phân chia nhiệm vụ nhóm](docs/TASK_ASSIGNMENT.md)
- [Kiến trúc và cấu trúc thư mục](docs/ARCHITECTURE.md)
- [Yêu cầu và checklist chấm điểm](docs/PROJECT_REQUIREMENTS.md)
- [Checklist demo và nghiệm thu](docs/DEMO_CHECKLIST.md)

## Quy trình làm việc nhóm

Nhóm sử dụng mô hình branch:

```text
main
└── develop
    ├── feature/core-viewmodel
    ├── feature/navigation-home-list
    ├── feature/add-edit-validation
    ├── feature/statistics-settings-about
    └── feature/firebase-api-v2
```

Nguyên tắc:

- Không push trực tiếp vào `main`.
- Không push trực tiếp vào `develop` nếu chưa thống nhất.
- Mỗi thành viên làm trên branch riêng.
- Hoàn thành thì tạo Pull Request vào `develop`.
- `develop` chạy ổn mới merge vào `main`.
- Không commit secret, keystore, `.env`, `local.properties`, `google-services.json` hoặc thư mục AI local.

## Checklist nộp bài

- [x] App build được.
- [x] Có ít nhất 3 màn hình.
- [x] Có Compose function.
- [x] Có LazyColumn hoặc LazyGrid.
- [x] Có MaterialTheme.
- [x] Có Material Design 3.
- [x] Có Navigation Compose.
- [x] Back button hoạt động đúng.
- [x] Có MVVM.
- [x] ViewModel tách khỏi UI.
- [x] Có Repository giữa ViewModel và data source.
- [x] Có Room/local data.
- [x] Có Firebase config local bằng `app/google-services.json` nếu demo Firebase.
- [x] Xoay màn hình không mất dữ liệu.
- [x] Có CRUD: thêm, sửa, xóa.
- [x] Có About screen.
- [x] Có import/export CSV hoặc JSON.
- [x] Có thống kê tổng, trung bình, min/max và phân loại nhóm.
- [x] Có validation input.
- [x] Có Snackbar, AlertDialog hoặc TextField error.
- [x] Có Logcat lifecycle ít nhất 4 callback.
- [x] Có ảnh minh chứng Logcat.

## Tên dự án

**Aries - Budget Management**

Tên repository:

```text
aries-budget-manager
```
