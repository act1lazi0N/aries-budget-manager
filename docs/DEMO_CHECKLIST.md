# Checklist demo và nghiệm thu

Tài liệu này dùng để kiểm tra trước khi nộp bài hoặc thuyết trình.

## 1. Kịch bản demo

1. Mở app ở Home: chứng minh tổng thu, tổng chi, số dư và giao dịch gần đây.
2. Chuyển sang Transactions: chứng minh danh sách giao dịch dùng LazyColumn.
3. Bấm nút thêm giao dịch: chứng minh Navigation sang Add/Edit screen.
4. Nhập sai dữ liệu: chứng minh TextField error, Snackbar hoặc AlertDialog.
5. Nhập đúng và lưu: chứng minh thêm giao dịch thành công và UI tự cập nhật.
6. Sửa giao dịch: chứng minh edit hoạt động.
7. Xóa giao dịch: chứng minh delete và AlertDialog xác nhận.
8. Mở Statistics: chứng minh tổng, trung bình, min/max, group theo danh mục và cảnh báo ngân sách.
9. Mở Settings: chứng minh export/import CSV hoặc JSON.
10. Mở About: chứng minh có màn hình giới thiệu app.
11. Xoay màn hình: chứng minh dữ liệu không mất.
12. Mở Logcat: chứng minh lifecycle callback.

## 2. Lifecycle callback cần có

- `onCreate`
- `onStart`
- `onResume`
- `onPause`
- `onStop`
- `onDestroy`

## 3. Checklist build

- [ ] App build được.
- [ ] Không lỗi import.
- [ ] Không crash khi chạy.
- [ ] Không crash khi chuyển màn.
- [ ] Không crash khi thêm/sửa/xóa giao dịch.
- [ ] Không mất dữ liệu khi xoay màn hình.

## 4. Checklist nộp bài

- [ ] README đã cập nhật.
- [ ] Docs đã đầy đủ.
- [ ] Code đã merge vào `main`.
- [ ] Có ảnh/video demo nếu cần.
- [ ] Có ảnh Logcat lifecycle.
- [ ] Có tag bản nộp nếu cần.

Lệnh tạo tag:

```bash
git tag v1.0-submission
git push origin v1.0-submission
```

## 5. Bằng chứng QA của Person 4

- [ ] Màn hình Statistics hiển thị tổng số giao dịch, tổng thu, tổng chi, số dư, trung bình, nhỏ nhất, lớn nhất, tổng theo danh mục, phần trăm danh mục và cảnh báo vượt ngân sách.
- [ ] Màn hình Settings có mục nhập/xuất CSV và JSON.
- [ ] Màn hình About mở được từ Settings và quay lại được bằng điều hướng back.
- [ ] Logcat với bộ lọc `AriesLifecycle` hiển thị `onCreate`, `onStart`, `onResume`, `onPause`, `onStop` và `onDestroy`.
- [ ] Xoay emulator sau khi thêm giao dịch và xác nhận thống kê vẫn khớp với danh sách giao dịch.
- [ ] Chụp ảnh màn hình Statistics, Settings, About và Logcat lifecycle trước khi nộp bài.
