# Hướng dẫn chia branch Git

Dự án dùng mô hình branch đơn giản để 4 người làm song song mà ít đạp code nhau.

## 1. Ý nghĩa các branch

```text
main        = bản ổn định để nộp
develop     = bản tích hợp chung
feature/*   = branch làm việc riêng của từng người
```

Không code trực tiếp trên `main`.

## 2. Sơ đồ branch

```text
main
└── develop
    ├── feature/core-viewmodel
    ├── feature/navigation-home-list
    ├── feature/add-edit-validation
    └── feature/statistics-settings-about
```

## 3. Branch của từng người

| Thành viên | Branch |
|---|---|
| Người 1 | `feature/core-viewmodel` |
| Người 2 | `feature/navigation-home-list` |
| Người 3 | `feature/add-edit-validation` |
| Người 4 | `feature/statistics-settings-about` |

## 4. Tạo branch cá nhân

```bash
git checkout develop
git pull origin develop

git checkout -b feature/ten-branch-cua-minh
git push -u origin feature/ten-branch-cua-minh
```

## 5. Quy trình làm việc hằng ngày

Trước khi code:

```bash
git checkout develop
git pull origin develop

git checkout feature/ten-branch-cua-minh
git merge develop
```

Sau khi code xong:

```bash
git add .
git commit -m "feat: mô tả phần đã làm"
git push
```

## 6. Quy tắc Pull Request

Khi làm xong một phần, tạo Pull Request:

```text
base: develop
compare: feature/ten-branch-cua-minh
```

Mẫu mô tả PR:

```text
Đã làm:
- ...

Đã test:
- App build được
- Màn hình không crash
- Chức năng hoạt động đúng

Ghi chú:
- Có sửa file chung không?
```

## 7. Thứ tự merge đề xuất

1. `feature/core-viewmodel`
2. `feature/navigation-home-list`
3. `feature/add-edit-validation`
4. `feature/statistics-settings-about`

## 8. File dễ conflict

- `MainActivity.kt`
- `AppNavHost.kt`
- `Screen.kt`
- `BudgetViewModel.kt`
- `BudgetUiState.kt`
- `Transaction.kt`
- `build.gradle.kts`
- `settings.gradle.kts`

Muốn sửa file chung thì phải báo nhóm trước.

## 9. Khi bị conflict

Conflict nghĩa là Git không biết nên giữ code của ai.

Nếu thấy đoạn:

```text
<<<<<<< HEAD
code của mình
=======
code từ người khác
>>>>>>> develop
```

Hãy giữ phần đúng, xóa các dòng `<<<<<<<`, `=======`, `>>>>>>>`.

Sau đó:

```bash
git add .
git commit -m "fix: resolve merge conflict"
git push
```
