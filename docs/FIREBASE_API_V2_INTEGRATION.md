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

After the file exists locally, the team may enable the Google Services plugin in `app/build.gradle.kts`:

```kotlin
plugins {
    id("com.google.gms.google-services")
}
```

Do not enable this before the local config exists, because Android builds can fail when `google-services.json` is missing.

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
