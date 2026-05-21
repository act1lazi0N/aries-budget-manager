# Aries - Budget Management

**Aries - Budget Management** là ứng dụng quản lý thu chi cá nhân được xây dựng bằng **Kotlin**, **Jetpack Compose**, **MVVM**, **Room Database** và **Material Design 3**.

Ứng dụng giúp người dùng ghi lại các khoản thu/chi, phân loại giao dịch theo danh mục, theo dõi ngân sách, xem báo cáo tháng và thống kê tình hình chi tiêu một cách trực quan.

## Mục tiêu dự án

Dự án tập trung xây dựng một ứng dụng Android đáp ứng các yêu cầu cơ bản:

- Có giao diện đồ họa bằng Jetpack Compose.
- Có ít nhất 3 màn hình.
- Dùng Material Design 3 và MaterialTheme.
- Dùng Navigation Compose để điều hướng giữa các màn hình.
- Dùng mô hình MVVM.
- Dữ liệu được quản lý bằng ViewModel và có thể mở rộng sang Room Database.
- Có CRUD giao dịch: thêm, sửa, xóa.
- Có thống kê: tổng thu, tổng chi, số dư, trung bình, min/max và phân loại theo danh mục.
- Có validation dữ liệu nhập.
- Có import/export dữ liệu bằng CSV hoặc JSON.
- Có màn hình About.
- Có ghi Logcat vòng đời Activity để minh chứng lifecycle callback.

## Chức năng chính

### 1. Dashboard / Home

Hiển thị tổng quan tài chính của người dùng:

- Tổng số dư.
- Tổng thu.
- Tổng chi.
- Chi tiêu tháng hiện tại.
- Danh mục chi nhiều nhất.
- Giao dịch gần đây.

### 2. Transactions

Hiển thị danh sách giao dịch bằng `LazyColumn`.

Người dùng có thể:

- Xem danh sách giao dịch.
- Tìm kiếm hoặc lọc giao dịch.
- Chọn giao dịch để chỉnh sửa.
- Xóa giao dịch với hộp thoại xác nhận.

### 3. Add / Edit Transaction

Cho phép người dùng nhập hoặc chỉnh sửa giao dịch.

Thông tin giao dịch gồm:

- Tên giao dịch.
- Số tiền.
- Loại giao dịch: thu nhập hoặc chi tiêu.
- Danh mục.
- Ví thanh toán.
- Ngày giao dịch.
- Ghi chú.

Validation cần có:

- Số tiền không được để trống.
- Số tiền phải lớn hơn 0.
- Phải chọn danh mục.
- Ghi chú không quá dài.

### 4. Statistics

Hiển thị thống kê tài chính:

- Tổng số giao dịch.
- Tổng thu.
- Tổng chi.
- Số dư.
- Giá trị trung bình.
- Giao dịch lớn nhất.
- Giao dịch nhỏ nhất.
- Tổng chi theo danh mục.
- Phần trăm chi tiêu theo danh mục.
- Cảnh báo khi vượt ngân sách.

### 5. Settings

Chứa các chức năng phụ:

- Xuất dữ liệu CSV.
- Nhập dữ liệu CSV.
- Xuất dữ liệu JSON.
- Nhập dữ liệu JSON.
- Xóa toàn bộ dữ liệu nếu cần.
- Điều hướng đến màn hình About.

### 6. About

Giới thiệu thông tin ứng dụng, công nghệ sử dụng và nhóm phát triển.

## Công nghệ sử dụng

- Kotlin
- Jetpack Compose
- Material Design 3
- Navigation Compose
- MVVM
- ViewModel
- Room Database
- CSV / JSON import-export
- Git & GitHub

## Kiến trúc tổng quan

```text
UI / Jetpack Compose
↓
ViewModel
↓
Repository
↓
Room Database hoặc dữ liệu mẫu
```

Giải thích nhanh:

- **UI / Compose**: chỉ hiển thị dữ liệu và nhận sự kiện người dùng.
- **ViewModel**: quản lý State, xử lý logic, tính toán thống kê.
- **Repository**: trung gian lấy/lưu dữ liệu.
- **Room Database**: lưu dữ liệu cục bộ lâu dài.

## Cấu trúc thư mục đề xuất

```text
com.example.budgetbuddy
├── data
│   ├── SampleData.kt
│   ├── TransactionRepository.kt
│   ├── CsvExporter.kt
│   └── JsonExporter.kt
│
├── model
│   ├── Transaction.kt
│   ├── TransactionType.kt
│   ├── Category.kt
│   ├── Wallet.kt
│   └── Budget.kt
│
├── ui
│   ├── components
│   ├── navigation
│   ├── screen
│   │   ├── home
│   │   ├── transaction
│   │   ├── statistics
│   │   ├── settings
│   │   └── about
│   └── theme
│
├── viewmodel
│   ├── BudgetViewModel.kt
│   └── BudgetUiState.kt
│
└── MainActivity.kt
```

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
    └── feature/statistics-settings-about
```

Nguyên tắc:

- Không push trực tiếp vào `main`.
- Không push trực tiếp vào `develop` nếu chưa thống nhất.
- Mỗi thành viên làm trên branch riêng.
- Hoàn thành thì tạo Pull Request vào `develop`.
- `develop` chạy ổn mới merge vào `main`.

## Thành viên và nhiệm vụ chính

| Vai trò | Phụ trách |
|---|---|
| Người 1 | Core, Data, Repository, ViewModel, logic thống kê |
| Người 2 | Theme, Navigation, HomeScreen, TransactionListScreen |
| Người 3 | Add/Edit transaction, validation, CSV/JSON import-export |
| Người 4 | Statistics, Settings, About, Logcat lifecycle, testing |

Chi tiết xem tại: [TASK_ASSIGNMENT.md](docs/TASK_ASSIGNMENT.md)

## Checklist nộp bài

- [ ] App build được.
- [ ] Có ít nhất 3 màn hình.
- [ ] Có Compose function.
- [ ] Có LazyColumn hoặc LazyGrid.
- [ ] Có MaterialTheme.
- [ ] Có Material Design 3.
- [ ] Có Navigation Compose.
- [ ] Back button hoạt động đúng.
- [ ] Có MVVM.
- [ ] ViewModel tách khỏi UI.
- [ ] Xoay màn hình không mất dữ liệu.
- [ ] Có CRUD: thêm, sửa, xóa.
- [ ] Có About screen.
- [ ] Có import/export CSV hoặc JSON.
- [ ] Có thống kê tổng, trung bình, min/max và phân loại nhóm.
- [ ] Có validation input.
- [ ] Có Snackbar, AlertDialog hoặc TextField error.
- [ ] Có Logcat lifecycle ít nhất 4 callback.
- [ ] Có ảnh minh chứng Logcat.

## Tên dự án

**Aries - Budget Management**

Tên repo gợi ý:

```text
aries-budget-manager
```
