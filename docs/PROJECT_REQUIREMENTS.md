# Yêu cầu và checklist chấm điểm

Tài liệu này tổng hợp các tiêu chí cần bám sát khi làm dự án Aries - Budget Management.

## 1. UI/UX

- [ ] Ứng dụng đồ họa GUI.
- [ ] Có ít nhất 3 màn hình.
- [ ] Giao diện xây dựng bằng Compose function.
- [ ] Danh sách dữ liệu dùng LazyColumn hoặc LazyGrid.
- [ ] Áp dụng MaterialTheme thống nhất màu, font, shape.
- [ ] Dùng Material Design 3.
- [ ] Điều hướng mượt bằng Navigation Compose.
- [ ] Back button hoạt động hợp lý.

## 2. Kiến trúc MVVM và xử lý sự kiện

- [ ] Thiết kế theo kiến trúc MVVM.
- [ ] ViewModel tách khỏi UI.
- [ ] Logic và dữ liệu đặt trong ViewModel.
- [ ] Dữ liệu chạy một chiều từ ViewModel xuống UI.
- [ ] Xoay màn hình không mất dữ liệu.
- [ ] UI tự cập nhật khi State thay đổi.
- [ ] Sự kiện người dùng như click, nhập liệu được xử lý đúng.
- [ ] Override và ghi Logcat ít nhất 4 callback vòng đời.
- [ ] Có ảnh chụp Logcat minh họa thứ tự callback.

## 3. Chức năng

- [ ] Dashboard / Home.
- [ ] Transaction list.
- [ ] Add transaction.
- [ ] Edit transaction.
- [ ] Delete transaction.
- [ ] Statistics.
- [ ] Settings.
- [ ] About.
- [ ] Import/export CSV hoặc JSON.

## 4. Thống kê

- [ ] Tổng số bản ghi.
- [ ] Tổng thu.
- [ ] Tổng chi.
- [ ] Số dư.
- [ ] Giá trị trung bình.
- [ ] Min/Max.
- [ ] Phân loại theo nhóm/danh mục.
- [ ] Phần trăm chi tiêu theo danh mục.
- [ ] Cảnh báo vượt ngân sách.

## 5. Validation

- [ ] Số tiền không được để trống.
- [ ] Số tiền phải lớn hơn 0.
- [ ] Phải chọn danh mục.
- [ ] Ghi chú không được quá dài.
- [ ] Hiển thị lỗi bằng TextField error.
- [ ] Hiển thị lỗi bằng Snackbar hoặc AlertDialog nếu cần.

## 6. Material Design 3 components nên dùng

- [ ] Scaffold.
- [ ] TopAppBar.
- [ ] NavigationBar.
- [ ] NavigationBarItem.
- [ ] FloatingActionButton.
- [ ] Card.
- [ ] OutlinedTextField.
- [ ] Button.
- [ ] FilterChip.
- [ ] AlertDialog.
- [ ] SnackbarHost.

## 7. Scope nên giữ

Chỉ tập trung vào:

- Nhập khoản thu/chi.
- Chọn danh mục.
- CRUD giao dịch.
- Thống kê.
- Báo cáo tháng.
- Cảnh báo ngân sách.
- Import/export CSV hoặc JSON.
- About screen.

Không mở rộng quá sớm sang AI, OCR hóa đơn, đồng bộ ngân hàng thật hoặc backend phức tạp.
