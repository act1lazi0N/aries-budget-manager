# Firebase & API v2 Integration

This document describes how Aries - Budget Management should move from the local MVP to v2 Firebase and API support without exposing secrets or breaking MVVM boundaries.

## What Is Prepared

- `.gitignore` blocks Firebase config, local env files, keystores, and AI-local folders.
- Top-level Gradle declares Firebase plugins with `apply false`.
- Firebase App Distribution, Auth, Firestore, and Currency API are documented as v2 options.
- No `google-services.json`, API key, token, or keystore is committed.

## Security Rules

Never commit:

```text
app/google-services.json
google-services.json
local.properties
.env
.env.local
*.keystore
*.jks
API keys
tokens
```

Before every PR, run:

```bash
git status --short
```

Confirm no secret file appears in the staged or unstaged list.

## Firebase Android Setup

Manual steps:

1. Open Firebase Console.
2. Create or choose project `aries-budget-management`.
3. Add Android app package `com.example.project_budget`.
4. Download `google-services.json`.
5. Put it at `app/google-services.json` on the local machine only.
6. Keep it untracked.

The app module should apply the Google Services plugin only when the local config exists:

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
}

if (file("google-services.json").exists()) {
    apply(plugin = "com.google.gms.google-services")
}
```

Do not force-enable this plugin before the local config exists, because Android builds can fail when `google-services.json` is missing.

## Firebase App Distribution

App Distribution is the first Firebase feature to use in v2.

Manual distribution path:

1. Build a debug or release APK.
2. Open Firebase Console.
3. Go to App Distribution.
4. Upload the APK.
5. Add tester emails or tester groups.
6. Send the release to testers.

Gradle automation can be enabled after manual upload is confirmed:

```kotlin
plugins {
    id("com.google.firebase.appdistribution")
}
```

Keep tester credentials and service-account files out of Git.

## Optional Firebase Auth

Auth is optional. Add it only if the team wants login/register.

Recommended boundary:

```text
Compose UI
↓
AuthViewModel
↓
AuthRepository
↓
FirebaseAuthDataSource
```

Do not call Firebase Auth directly from composables.

## Optional Firestore Sync

Firestore is optional. Room remains the source of truth until sync rules are designed.

Suggested collection layout:

```text
users/{userId}/transactions/{transactionId}
users/{userId}/categories/{categoryId}
users/{userId}/budgets/{budgetId}
users/{userId}/wallets/{walletId}
```

Recommended boundary:

```text
BudgetViewModel
↓
TransactionRepository
↓
Room DAO + FirebaseTransactionDataSource
```

Sync must define conflict rules before writing remote data back into Room.

## Optional Currency API

Currency API should be isolated behind repository classes.

Recommended structure if enabled:

```text
app/src/main/java/com/example/project_budget/data/remote/CurrencyApiService.kt
app/src/main/java/com/example/project_budget/data/remote/RetrofitClient.kt
app/src/main/java/com/example/project_budget/data/repository/CurrencyRepository.kt
app/src/main/java/com/example/project_budget/model/CurrencyRate.kt
```

Recommended API: Frankfurter Currency API.

Reason: basic exchange rates do not require an API key, so it fits the no-secret rule.

## Acceptance Checklist

- [ ] App builds.
- [ ] `app/google-services.json` exists locally only.
- [ ] `.gitignore` protects Firebase config and local secrets.
- [ ] App Distribution manual upload works.
- [ ] Optional Auth is behind repository/data-source classes.
- [ ] Optional Firestore is behind repository/data-source classes.
- [ ] Optional Currency API is behind repository/data-source classes.
- [ ] UI does not call Firebase or external APIs directly.
- [ ] Team confirms v2 before creating the Git tag.

## v2 Tag

Do not tag until v2 is merged and confirmed.

Command for the final release:

```bash
git tag -a v2 -m "Release v2: Firebase and API integration"
git push origin v2
```

## How To Use Firestore For Aries Sync

Use Firestore only behind the data layer. Compose screens must not import or call Firebase classes.

Recommended runtime flow:

```text
StatisticsScreen / HomeScreen / Transaction screens
-> BudgetViewModel
-> TransactionRepository
-> Room DAO + FirebaseTransactionDataSource
-> Cloud Firestore
```

### 1. Enable Firestore locally

1. In Firebase Console, open the Aries project.
2. Go to Build -> Firestore Database.
3. Create a database.
4. Start in test mode only for local development, then replace it with production rules before release.
5. Download `google-services.json` for package `com.example.project_budget`.
6. Put the file at `app/google-services.json` locally.
7. Do not commit `google-services.json`; `.gitignore` already blocks it.

### 2. Add Firestore dependency

Keep the Firebase BoM and add Firestore to `app/build.gradle.kts`:

```kotlin
implementation(platform("com.google.firebase:firebase-bom:34.13.0"))
implementation("com.google.firebase:firebase-analytics")
implementation("com.google.firebase:firebase-firestore-ktx")
```

If Auth is enabled, also add:

```kotlin
implementation("com.google.firebase:firebase-auth-ktx")
```

### 3. Use this Firestore structure

```text
users/{userId}/transactions/{transactionId}
users/{userId}/categories/{categoryId}
users/{userId}/budgets/{budgetId}
users/{userId}/wallets/{walletId}
```

For a demo without Firebase Auth, use a temporary local user id such as `demo-user`. For production, use `FirebaseAuth.getInstance().currentUser.uid` inside an Auth data source or repository, not inside Compose.

### 4. Create a Firebase data source

Create a dedicated data source, for example:

```text
app/src/main/java/com/example/project_budget/data/remote/FirebaseTransactionDataSource.kt
```

Example responsibilities:

```kotlin
class FirebaseTransactionDataSource(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun userTransactions(userId: String): CollectionReference {
        return firestore.collection("users")
            .document(userId)
            .collection("transactions")
    }
}
```

Keep mapping functions in the data layer. Convert `Transaction` to a Firestore map/data class before upload and convert remote documents back to local models before saving to Room.

### 5. Repository sync pattern

Room should remain the source of truth for UI state. Firestore sync should update Room, then `BudgetViewModel` refreshes `BudgetUiState` from the repository.

Recommended write flow:

```text
add/update/delete transaction
-> write to Room
-> push the same change to Firestore
-> refresh BudgetUiState from repository
```

Recommended pull flow:

```text
read Firestore documents
-> validate and map remote data
-> upsert into Room
-> refresh BudgetUiState from repository
```

Do not calculate statistics from Firestore documents directly in Compose. Statistics should continue to come from `BudgetUiState`, including totals, category percentages, over-budget warnings, line chart data, and pie chart data.

### 6. Suggested transaction document fields

```text
id: number or string
title: string
amount: number
currency: string
convertedAmount: number
convertedCurrency: string
exchangeRate: number
category: string
type: "INCOME" or "EXPENSE"
date: string
note: string
walletId: number
updatedAt: server timestamp
deleted: boolean
```

Use `updatedAt` for conflict resolution. Use `deleted = true` if the team wants soft delete sync; otherwise delete the document when deleting a local transaction.

### 7. Minimal Firestore security rules

Replace `demo-user` access before production. With Firebase Auth, use user-scoped rules:

```text
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId}/{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

### 8. QA checklist for Firestore

- [ ] App builds without committing `app/google-services.json`.
- [ ] UI screens do not import Firebase classes.
- [ ] Repository owns Firestore sync calls.
- [ ] Room remains the source of truth for displayed state.
- [ ] Add transaction syncs to Firestore.
- [ ] Update transaction syncs to Firestore.
- [ ] Delete transaction syncs to Firestore or marks `deleted = true`.
- [ ] Pull remote data updates Room.
- [ ] Statistics still use `BudgetUiState` after sync.
- [ ] Firestore rules prevent one user from reading another user's data.
