# Phân chia nhiệm vụ nhóm

Dự án được chia theo module để hạn chế conflict và dễ ghép code.

## Tổng quan phân công

| Thành viên | Vai trò | Phần chính |
|---|---|---|
| Người 1 | Core / Data / ViewModel | Model, Repository, UiState, ViewModel, CRUD logic, statistics logic |
| Người 2 | UI / Navigation | Theme, Navigation, Home, Transaction List, LazyColumn |
| Người 3 | CRUD / Form | Add/Edit transaction, validation, CSV/JSON import-export |
| Người 4 | Statistics / QA | Statistics, Settings, About, Logcat lifecycle, testing |

## Người 1 — Core / Data / ViewModel

### Phụ trách

```text
model/
data/
viewmodel/
```

### File chính

- `model/Transaction.kt`
- `model/TransactionType.kt`
- `model/Category.kt`
- `model/Wallet.kt`
- `model/Budget.kt`
- `data/SampleData.kt`
- `data/TransactionRepository.kt`
- `viewmodel/BudgetUiState.kt`
- `viewmodel/BudgetViewModel.kt`

### Nhiệm vụ

- Tạo data class cho Transaction, Category, Wallet, Budget.
- Tạo dữ liệu mẫu ban đầu.
- Viết Repository quản lý danh sách giao dịch.
- Viết ViewModel giữ toàn bộ State.
- Viết logic thêm/sửa/xóa giao dịch.
- Viết logic thống kê: tổng số giao dịch, tổng thu, tổng chi, số dư, trung bình, min/max, nhóm theo danh mục, phần trăm theo danh mục, cảnh báo vượt ngân sách.
- Đảm bảo xoay màn hình không mất dữ liệu.

### Sản phẩm bàn giao

`BudgetViewModel` có:

- `uiState`
- `addTransaction()`
- `updateTransaction()`
- `deleteTransaction()`
- `getTransactionById()`
- `validateTransaction()`
- `calculateStatistics()`

## Người 2 — Navigation / Theme / Home / Transaction List

### Phụ trách

```text
ui/theme/
ui/navigation/
ui/screen/home/
ui/screen/transaction/TransactionListScreen.kt
ui/components/
```

### Nhiệm vụ

- Setup MaterialTheme.
- Setup Navigation Compose.
- Tạo Bottom Navigation.
- Tạo HomeScreen.
- Tạo TransactionListScreen.
- Dùng LazyColumn để hiển thị giao dịch.
- Tạo FloatingActionButton để đi tới màn thêm giao dịch.
- Bấm vào item giao dịch thì đi tới màn sửa.
- Back button hoạt động đúng.

## Người 3 — Add/Edit / Validation / Import Export

### Phụ trách

```text
ui/screen/transaction/AddEditTransactionScreen.kt
data/CsvExporter.kt
data/JsonExporter.kt
```

### Nhiệm vụ

- Làm màn hình thêm giao dịch.
- Làm màn hình sửa giao dịch.
- Tạo form nhập số tiền, loại giao dịch, danh mục, ví, ngày, ghi chú.
- Validate input: số tiền không rỗng, số tiền > 0, phải chọn danh mục, ghi chú không quá dài.
- Hiển thị lỗi bằng TextField error, Snackbar hoặc AlertDialog.
- Gọi ViewModel để add/update.
- Làm export/import CSV hoặc JSON.

## Người 4 — Statistics / Settings / About / Testing / Logcat

### Phụ trách

```text
ui/screen/statistics/
ui/screen/settings/
ui/screen/about/
testing evidence
Logcat lifecycle evidence
```

### Nhiệm vụ

- Làm StatisticsScreen.
- Hiển thị tổng số giao dịch, tổng thu, tổng chi, số dư, trung bình, giao dịch lớn nhất, giao dịch nhỏ nhất, phân loại theo danh mục.
- Làm SettingsScreen.
- Làm AboutScreen.
- Thêm Logcat vòng đời: `onCreate`, `onStart`, `onResume`, `onPause`, `onStop`, `onDestroy`.
- Chụp ảnh minh chứng Logcat.
- Test app theo checklist chấm điểm.
