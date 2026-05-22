# KrossChat

> **🚧 Work in progress** — Authentication feature is functional; real-time chat feature is currently being implemented.

KrossChat is a **Kotlin Multiplatform** real-time messaging application targeting **Android**, **iOS**, and **Desktop (JVM)**. The entire UI is built with **Compose Multiplatform**, meaning one codebase powers all three platforms.

---

## What the app does

| Feature | Status | Description |
|---|---|---|
| **Authentication** | ✅ In progress | User registration with email + password, client-side validation, error feedback via snackbar |
| **Real-time Chat** | 🚧 In progress | WebSocket-based messaging using Ktor, offline-first with Room local persistence |

---

## Tech stack

| Concern | Library / Tool |
|---|---|
| Language | Kotlin 2.2.0 |
| UI | Compose Multiplatform 1.9.0-beta01 |
| Networking | Ktor 3.2.3 (OkHttp on Android, Darwin on iOS) |
| Dependency Injection | Koin 4.1.0 |
| Local database | Room 2.7.2 (KMP) |
| Async | Kotlinx Coroutines 1.10.2 |
| Logging | Kermit |
| Build secrets | BuildKonfig (API key from `local.properties`) |
| Build system | Gradle 8 + convention plugins (`build-logic`) |

---

## Module structure

The project follows a **multi-module clean architecture** split into `core` and `feature` groups.

```
KrossChat/
├── build-logic/               # Gradle convention plugins
│   └── convention/            # KmpLibraryPlugin, CmpLibraryPlugin, RoomPlugin, …
│
├── core/
│   ├── domain/                # Pure business logic — Result<T,E>, DataError, AuthService interface
│   ├── data/                  # Ktor HttpClient factory, KtorService, Koin DI wiring, BuildKonfig
│   ├── presentation/          # Shared ViewModels and UI models (no platform dep)
│   └── designsystem/          # Design tokens, Material3 theme, reusable Compose components
│
├── feature/
│   ├── auth/
│   │   ├── domain/            # EmailValidator, PasswordValidationState
│   │   └── presentation/      # RegisterScreen, RegisterViewModel, RegisterState/Action/Event
│   │
│   └── chat/
│       ├── domain/            # Chat use-cases and entities (in progress)
│       ├── data/              # WebSocket data source via Ktor (in progress)
│       ├── database/          # Room DAOs + entities for offline storage (in progress)
│       └── presentation/      # Chat screens and ViewModels (in progress)
│
├── composeApp/                # Integration layer: nav graph wiring, Koin module assembly
│   ├── androidMain/           # Android entry point (MainActivity)
│   ├── iosMain/               # iOS entry point
│   └── jvmMain/               # Desktop entry point
│
└── iosApp/                    # Xcode project — Swift/SwiftUI iOS shell
```

### Dependency flow

```
core:domain  ←─────────────────────────────────┐
     ↓                                          │
core:data                                       │ no upward deps
     ↓                                          │
core:presentation   core:designsystem           │
          ↓                ↓                    │
    feature:*:domain ──────────────────────────►┘
          ↓
    feature:*:data   feature:*:database
          ↓
    feature:*:presentation
          ↓
       composeApp   (wires everything together)
```

### Convention plugins

| Plugin | Adds automatically |
|---|---|
| `convention.kmp.library` | kotlinx-serialization, kotlin-test |
| `convention.cmp.library` | + Material3, Compose icons, Lifecycle, ViewModel |
| `convention.cmp.feature` | + Koin, Compose Navigation, SavedStateHandle |
| `convention.room` | + KSP Room processors for Android & iOS |
| `convention.buildKonfig` | Generates `BuildKonfig.API_KEY` from `local.properties` |

---

## Architecture patterns

### MVI presentation layer
Every screen follows the **State / Action / Event** contract:
- `State` — immutable UI state rendered by the composable
- `Action` — user intents sent to the ViewModel
- `Event` — one-shot side effects (navigation, snackbar) observed with `ObserveAsEvents`

### Typed error handling
`core:domain` exposes a `Result<D, E : Error>` wrapper and a `DataError` sealed hierarchy (Network, Local). No raw exceptions cross layer boundaries.

### WebSocket chat
The chat data source opens a persistent Ktor WebSocket session and exposes incoming messages as a `Flow<ChatMessage>`. The repository combines the live flow with cached Room data to deliver an offline-first experience.

### Koin DI
Each layer declares its own Koin module. All modules are assembled in `:composeApp` via `startKoin { modules(...) }`. Composables obtain ViewModels with `koinViewModel()`.

---

## Getting started

### Prerequisites
- Android Studio Hedgehog or newer (with KMP plugin)
- Xcode 15+ (for iOS target)
- JDK 17+

### Local configuration
Create a `local.properties` file in the project root (next to `gradle.properties`) and add your backend API key:

```properties
API_KEY=your_api_key_here
```

### Build & run

```bash
# Android debug APK
./gradlew :composeApp:assembleDebug

# Desktop JVM app
./gradlew :composeApp:run

# Unit tests (all modules)
./gradlew testDebugUnitTest

# Lint
./gradlew lint
```

For iOS, open `iosApp/iosApp.xcodeproj` in Xcode and run on a simulator or device.

---

## Project status

- [x] Project scaffold (KMP + Compose Multiplatform, convention plugins, version catalog)
- [x] Core design system (theme, typography, reusable components)
- [x] Core domain (Result wrapper, DataError, AuthService interface)
- [x] Core data (Ktor HttpClient factory, Kermit logging, Koin wiring)
- [x] Auth — registration screen (MVI, email/password validation, Ktor call)
- [ ] Auth — login screen
- [ ] Auth — token storage (DataStore)
- [ ] Chat — WebSocket session management
- [ ] Chat — Room offline persistence
- [ ] Chat — conversation list & message thread screens
- [ ] Navigation — auth → chat flow
