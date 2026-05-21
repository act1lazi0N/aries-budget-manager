# Hướng dẫn setup dự án

Tài liệu này dành cho thành viên mới, kể cả người chưa biết Git.

Lưu ý: Toàn bộ `ten-branch-cua-minh` đã được đề cập tại [Hướng dẫn phân nhánh](BRANCHING_GUIDE.md). Hãy đọc chúng trước khi đặt tên.
## 1. Cài đặt cần có

Mỗi thành viên cần cài:

- Android Studio
- Git
- Tài khoản GitHub

Kiểm tra Git đã cài chưa:

```bash
git --version
```

Nếu hiện ra `git version ...` là đã cài thành công.

## 2. Clone repo về máy

Mở Git Bash hoặc Terminal:

```bash
git clone https://github.com/act1lazi0N/aries-budget-manager.git
cd aries-budget-manager
```

## 3. Mở project trong Android Studio

1. Mở Android Studio.
2. Chọn **Open**.
3. Chọn thư mục `aries-budget-manager`.
4. Đợi Gradle sync.
5. Bấm Run để chạy app.

Nếu Android Studio hỏi **Trust Project**, chọn **Trust Project**.

## 4. Lấy code mới nhất từ nhóm

Trước khi code, luôn chạy:

```bash
git checkout develop
git pull origin develop
```

Sau đó quay về branch cá nhân:

```bash
git checkout feature/ten-branch-cua-minh
git merge develop
```

## 5. Sau khi code xong

Kiểm tra file thay đổi:

```bash
git status
```

Lưu thay đổi:

```bash
git add .
git commit -m "feat: mô tả ngắn phần đã làm"
git push
```

Ví dụ:

```bash
git add .
git commit -m "feat: add transaction form validation"
git push
```

## 6. Khi làm xong một phần

Lên GitHub tạo Pull Request:

```text
base: develop
compare: feature/branch-cua-minh
```

Không tạo Pull Request vào `main`.

## 7. Lỗi thường gặp

### Chưa cấu hình Git username/email

```bash
git config --global user.name "Ten cua ban"
git config --global user.email "email@example.com"
```

### Không push được

Kiểm tra đang ở đúng branch chưa:

```bash
git branch
```

Nếu chưa đúng:

```bash
git checkout feature/ten-branch-cua-minh
```


