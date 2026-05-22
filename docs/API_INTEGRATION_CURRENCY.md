# API_INTEGRATION_CURRENCY.md

# Tích hợp API quy đổi tiền tệ cho Aries - Budget Management

Tài liệu này mô tả một nhiệm vụ API cụ thể cho dự án **Aries - Budget Management**:  
**quy đổi giao dịch ngoại tệ về tiền tệ chuẩn của app để thống kê thu/chi chính xác.**

---

## 1. Mục tiêu tích hợp API

Trong app quản lý chi tiêu, người dùng có thể nhập giao dịch bằng nhiều loại tiền tệ khác nhau, ví dụ:

- VND
- USD
- EUR
- JPY
- KRW

Để thống kê chính xác, app cần quy đổi các giao dịch đó về một đơn vị tiền tệ chuẩn, ví dụ **VND** hoặc **USD**.

Ví dụ:

```text
Người dùng nhập:
- 10 USD
- Danh mục: Ăn uống
- Ngày: 2026-05-21

App cần:
- Lấy tỷ giá USD -> VND
- Quy đổi số tiền
- Lưu cả số tiền gốc và số tiền đã quy đổi
- Dùng số tiền đã quy đổi để tính tổng chi, phần trăm danh mục và báo cáo tháng
```

---

## 2. API được chọn

API đề xuất:

```text
Frankfurter Currency API
```

Lý do chọn:

- Miễn phí.
- Không cần API key.
- Có endpoint lấy tỷ giá mới nhất.
- Có endpoint lấy tỷ giá lịch sử.
- Phù hợp cho demo học tập và tính năng quy đổi tiền tệ cơ bản.

Base URL:

```text
https://api.frankfurter.dev
```

API version nên dùng:

```text
/v2
```

---

## 3. Tính năng cụ thể trong app

Tên tính năng:

```text
Currency Conversion for Transactions
```

Mục tiêu:

```text
Khi người dùng nhập giao dịch bằng ngoại tệ, app tự quy đổi sang tiền tệ mặc định để thống kê.
```

Ví dụ use case:

```text
Người dùng nhập 10 USD.
Tiền tệ mặc định của app là VND.
App gọi API lấy tỷ giá USD/VND.
App tính convertedAmount.
App lưu transaction với amount gốc và convertedAmount.
StatisticsScreen dùng convertedAmount để tính tổng.
```

---

## 4. Dữ liệu cần thêm vào Transaction model

Nên mở rộng `Transaction` model như sau:

```kotlin
data class Transaction(
    val id: Int,
    val title: String,
    val amount: Double,
    val currency: String,
    val convertedAmount: Double,
    val convertedCurrency: String,
    val exchangeRate: Double,
    val category: String,
    val type: TransactionType,
    val date: String,
    val note: String = ""
)
```

Giải thích:

| Field | Ý nghĩa |
|---|---|
| `amount` | Số tiền gốc người dùng nhập |
| `currency` | Loại tiền gốc, ví dụ USD |
| `convertedAmount` | Số tiền sau khi quy đổi |
| `convertedCurrency` | Tiền tệ chuẩn của app, ví dụ VND |
| `exchangeRate` | Tỷ giá tại thời điểm quy đổi |
| `date` | Ngày giao dịch, dùng để lấy tỷ giá lịch sử nếu cần |

---

## 5. Endpoint cần dùng

### 5.1. Lấy tỷ giá một cặp tiền tệ

```http
GET https://api.frankfurter.dev/v2/rate/{base}/{quote}
```

Ví dụ:

```http
GET https://api.frankfurter.dev/v2/rate/USD/EUR
```

Response mẫu:

```json
{
  "base": "USD",
  "quote": "EUR",
  "rate": 0.92,
  "date": "2026-05-21"
}
```

### 5.2. Lấy danh sách tiền tệ hỗ trợ

```http
GET https://api.frankfurter.dev/v2/currencies
```

Dùng endpoint này để kiểm tra API có hỗ trợ loại tiền người dùng chọn hay không.

---

## 6. Luồng xử lý trong app

```text
AddEditTransactionScreen
↓
Người dùng nhập amount + currency
↓
ViewModel gọi CurrencyRepository
↓
CurrencyRepository gọi Frankfurter API
↓
API trả về exchangeRate
↓
ViewModel tính convertedAmount
↓
ViewModel lưu Transaction
↓
StatisticsScreen dùng convertedAmount để thống kê
```

---

## 7. Cấu trúc file đề xuất

Thêm các file sau:

```text
data/remote/CurrencyApiService.kt
data/repository/CurrencyRepository.kt
model/CurrencyRate.kt
viewmodel/BudgetViewModel.kt
ui/screen/transaction/AddEditTransactionScreen.kt
```

Cây thư mục mở rộng:

```text
com.example.budgetbuddy
├── data
│   ├── remote
│   │   └── CurrencyApiService.kt
│   ├── repository
│   │   └── CurrencyRepository.kt
│   └── TransactionRepository.kt
│
├── model
│   ├── Transaction.kt
│   ├── TransactionType.kt
│   └── CurrencyRate.kt
│
├── viewmodel
│   └── BudgetViewModel.kt
│
└── ui
    └── screen
        └── transaction
            └── AddEditTransactionScreen.kt
```

---

## 8. Dependency cần thêm

Trong `build.gradle.kts` của module `app`, thêm Retrofit và Gson converter:

```kotlin
dependencies {
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
}
```

Nếu dùng coroutine với ViewModel:

```kotlin
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
}
```

---

## 9. Data class cho response API

File:

```text
model/CurrencyRate.kt
```

Code:

```kotlin
package com.example.budgetbuddy.model

data class CurrencyRate(
    val base: String,
    val quote: String,
    val rate: Double,
    val date: String
)
```

---

## 10. Retrofit API Service

File:

```text
data/remote/CurrencyApiService.kt
```

Code:

```kotlin
package com.example.budgetbuddy.data.remote

import com.example.budgetbuddy.model.CurrencyRate
import retrofit2.http.GET
import retrofit2.http.Path

interface CurrencyApiService {
    @GET("v2/rate/{base}/{quote}")
    suspend fun getRate(
        @Path("base") base: String,
        @Path("quote") quote: String
    ): CurrencyRate
}
```

---

## 11. Retrofit Client

Có thể tạo file riêng:

```text
data/remote/RetrofitClient.kt
```

Code:

```kotlin
package com.example.budgetbuddy.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.frankfurter.dev/"

    val currencyApiService: CurrencyApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CurrencyApiService::class.java)
    }
}
```

---

## 12. CurrencyRepository

File:

```text
data/repository/CurrencyRepository.kt
```

Code:

```kotlin
package com.example.budgetbuddy.data.repository

import com.example.budgetbuddy.data.remote.CurrencyApiService

class CurrencyRepository(
    private val apiService: CurrencyApiService
) {
    suspend fun convertAmount(
        amount: Double,
        fromCurrency: String,
        toCurrency: String
    ): Double {
        if (fromCurrency == toCurrency) {
            return amount
        }

        val rateResponse = apiService.getRate(
            base = fromCurrency,
            quote = toCurrency
        )

        return amount * rateResponse.rate
    }

    suspend fun getRate(
        fromCurrency: String,
        toCurrency: String
    ): Double {
        if (fromCurrency == toCurrency) {
            return 1.0
        }

        return apiService.getRate(
            base = fromCurrency,
            quote = toCurrency
        ).rate
    }
}
```

---

## 13. Tích hợp vào ViewModel

Trong `BudgetViewModel`, thêm logic quy đổi trước khi lưu giao dịch.

Ví dụ:

```kotlin
fun addTransactionWithCurrencyConversion(
    title: String,
    amount: Double,
    currency: String,
    defaultCurrency: String,
    category: String,
    type: TransactionType,
    date: String,
    note: String
) {
    viewModelScope.launch {
        try {
            val rate = currencyRepository.getRate(
                fromCurrency = currency,
                toCurrency = defaultCurrency
            )

            val convertedAmount = amount * rate

            val transaction = Transaction(
                id = generateId(),
                title = title,
                amount = amount,
                currency = currency,
                convertedAmount = convertedAmount,
                convertedCurrency = defaultCurrency,
                exchangeRate = rate,
                category = category,
                type = type,
                date = date,
                note = note
            )

            addTransaction(transaction)
        } catch (e: Exception) {
            updateError("Không thể lấy tỷ giá. Vui lòng thử lại.")
        }
    }
}
```

---

## 14. UI cần bổ sung

Trong `AddEditTransactionScreen`, thêm lựa chọn tiền tệ:

```text
Số tiền
[ 10 ]

Tiền tệ
[ USD ▼ ]

Quy đổi sang
[ VND ]

Danh mục
[ Ăn uống ▼ ]

[ Lưu giao dịch ]
```

UI nên hiển thị preview:

```text
10 USD ≈ 250,000 VND
```

Nếu chưa gọi được API:

```text
Không thể tải tỷ giá. Vui lòng kiểm tra kết nối mạng.
```

---

## 15. Validation cần có

Khi thêm giao dịch ngoại tệ:

- Số tiền không được rỗng.
- Số tiền phải lớn hơn 0.
- Currency không được rỗng.
- Không cho chọn currency không được API hỗ trợ.
- Nếu mất mạng, hiển thị Snackbar hoặc AlertDialog.
- Nếu API lỗi, không lưu transaction sai.

Thông báo lỗi gợi ý:

```text
Vui lòng nhập số tiền hợp lệ.
Không thể lấy tỷ giá. Vui lòng thử lại.
Loại tiền này hiện chưa được hỗ trợ.
```

---

## 16. Cách StatisticsScreen sử dụng dữ liệu

Khi tính thống kê, không dùng `amount` gốc nếu transaction có nhiều loại tiền tệ.

Nên dùng:

```kotlin
val totalExpense = transactions
    .filter { it.type == TransactionType.EXPENSE }
    .sumOf { it.convertedAmount }
```

Tương tự:

```kotlin
val categoryStats = transactions
    .filter { it.type == TransactionType.EXPENSE }
    .groupBy { it.category }
    .mapValues { entry ->
        entry.value.sumOf { it.convertedAmount }
    }
```

Lý do:

```text
amount là số tiền gốc.
convertedAmount là số tiền đã quy đổi về đơn vị chuẩn.
```

---

## 17. Xử lý offline

Nếu không có mạng, có 3 cách:

### Cách 1 — Không cho lưu giao dịch ngoại tệ

Hiển thị lỗi:

```text
Không có kết nối mạng để lấy tỷ giá.
```

### Cách 2 — Cho nhập thủ công tỷ giá

Thêm field:

```text
Tỷ giá tự nhập
```

### Cách 3 — Dùng tỷ giá cache gần nhất

Lưu tỷ giá gần nhất trong Room hoặc DataStore.

Khuyến nghị cho dự án sinh viên:

```text
Cách 1 hoặc Cách 2 là đủ.
```

---

## 18. Checklist hoàn thành tính năng

- [ ] Có field `currency`.
- [ ] Có field `convertedAmount`.
- [ ] Có field `convertedCurrency`.
- [ ] Có field `exchangeRate`.
- [ ] Có `CurrencyApiService`.
- [ ] Có `CurrencyRepository`.
- [ ] Có Retrofit Client.
- [ ] Add/Edit screen chọn được currency.
- [ ] ViewModel gọi API để lấy tỷ giá.
- [ ] ViewModel tính `convertedAmount`.
- [ ] StatisticsScreen dùng `convertedAmount`.
- [ ] Có validation khi API lỗi.
- [ ] Có Snackbar hoặc AlertDialog khi mất mạng.
- [ ] App không crash nếu API không phản hồi.

---

## 19. Branch đề xuất cho tính năng này

Tạo branch riêng:

```bash
git checkout develop
git pull origin develop

git checkout -b feature/currency-api-integration
git push -u origin feature/currency-api-integration
```

Khi làm xong:

```bash
git add .
git commit -m "feat: integrate currency conversion API"
git push
```

Sau đó tạo Pull Request:

```text
base: develop
compare: feature/currency-api-integration
```

---

## 20. Ghi chú quan trọng

Tính năng này không bắt buộc cho MVP nếu nhóm thiếu thời gian.

Mức ưu tiên:

```text
MVP bắt buộc:
- CRUD giao dịch
- Thống kê
- Validation
- Import/export CSV hoặc JSON
- About
- Navigation
- MVVM

Tính năng API quy đổi tiền tệ:
- Nâng cao
- Có thể đưa vào nếu core app đã ổn
```

Nếu cần demo API đơn giản, chỉ cần làm:

```text
Nhập 10 USD -> gọi API -> hiển thị số tiền quy đổi sang tiền tệ chuẩn.
```
