# Kiến trúc và cấu trúc thư mục

Dự án dùng kiến trúc MVVM để tách giao diện khỏi logic xử lý dữ liệu.

## 1. Kiến trúc tổng quan

```text
UI / Jetpack Compose
↓
ViewModel
↓
Repository
↓
Room Database hoặc dữ liệu mẫu
```

## 2. Vai trò từng lớp

### UI / Jetpack Compose

Phụ trách hiển thị giao diện và nhận sự kiện người dùng.

UI không nên tự xử lý logic phức tạp.

### ViewModel

Phụ trách quản lý State và xử lý logic:

- Thêm/sửa/xóa giao dịch.
- Tính tổng thu.
- Tính tổng chi.
- Tính số dư.
- Tính trung bình.
- Tính min/max.
- Tính tổng chi theo danh mục.
- Tính phần trăm từng danh mục.
- Kiểm tra vượt ngân sách.
- Đẩy State xuống UI.

### Repository

Phụ trách truy xuất dữ liệu từ Room, dữ liệu mẫu, file CSV/JSON hoặc API trong tương lai.

### Room Database

Phụ trách lưu dữ liệu cục bộ lâu dài:

- Giao dịch.
- Danh mục.
- Ví.
- Ngân sách.

## 3. Cấu trúc thư mục đề xuất

```text
com.example.budgetbuddy
├── data
├── model
├── ui
│   ├── components
│   ├── navigation
│   ├── screen
│   └── theme
├── viewmodel
└── MainActivity.kt
```

## 4. File hợp đồng cố định

Các file cần thống nhất sớm:

- `Transaction.kt`
- `TransactionType.kt`
- `Category.kt`
- `Wallet.kt`
- `Budget.kt`
- `BudgetUiState.kt`
- `Screen.kt`

## 5. Navigation

Route đề xuất:

- `home`
- `transactions`
- `add_transaction`
- `edit_transaction/{transactionId}`
- `statistics`
- `settings`
- `about`

Luồng điều hướng:

- Home -> Add Transaction.
- Transactions -> Add Transaction.
- Transactions -> Edit Transaction.
- Settings -> About.
- Settings -> Import/Export.
