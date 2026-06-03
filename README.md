# KrossChat

> **🚧 Work in progress** — Authentication flow is complete; real-time chat feature is currently
> being implemented.

KrossChat is a **Kotlin Multiplatform** real-time messaging application targeting **Android**, **iOS**, and **Desktop (JVM)**. The entire UI is built with **Compose Multiplatform**, meaning one codebase powers all three platforms.

---

## What the app does

| Feature                | Status         | Description                                                             |
|------------------------|----------------|-------------------------------------------------------------------------|
| **Registration**       | ✅ Done         | Email + password sign-up with client-side validation and error feedback |
| **Login**              | ✅ Done         | Email + password sign-in with session token storage via DataStore       |
| **Email verification** | ✅ Done         | Post-registration verification screen with deep-link handling           |
| **Forgot password**    | ✅ Done         | Password reset request screen with deep-link handling                   |
| **Reset password**     | ✅ Done         | New password entry screen reached via deep link                         |
| **Session management** | ✅ Done         | Auth state check on startup, session expiration handling                |
| **Navigation**         | ✅ Done         | Auth → Chat flow with type-safe routes and deep-link support            |
| **Chat list**          | 🚧 In progress | Conversation list screen (scaffold and ViewModel wired)                 |
| **WebSocket chat**     | 🚧 In progress | Real-time messaging via Ktor WebSocket                                  |
| **Room persistence**   | 🚧 In progress | Offline-first message storage with Room                                 |

---

## Tech stack

| Concern              | Library / Tool                                  | Version       |
|----------------------|-------------------------------------------------|---------------|
| Language             | Kotlin / KMP                                    | 2.2.0         |
| UI                   | Compose Multiplatform                           | 1.9.0-beta01  |
| Navigation           | Jetbrains Navigation Compose                    | 2.9.0-beta04  |
| Networking           | Ktor (OkHttp on Android, Darwin on iOS)         | 3.2.3         |
| Dependency Injection | Koin                                            | 4.1.0         |
| Local database       | Room (KMP)                                      | 2.7.2         |
| Token storage        | DataStore                                       | 1.1.7         |
| Async                | Kotlinx Coroutines                              | 1.10.2        |
| Serialization        | Kotlinx Serialization                           | 1.9.0         |
| Date/time            | Kotlinx Datetime                                | 0.7.1         |
| Image loading        | Coil 3                                          | 3.3.0         |
| Permissions          | Moko Permissions                                | 0.19.1        |
| Logging              | Kermit                                          | 2.1.0         |
| Push notifications   | Firebase BOM                                    | 34.0.0        |
| Adaptive layouts     | Material3 Adaptive                              | 1.2.0-alpha04 |
| Build secrets        | BuildKonfig                                     | 0.17.1        |
| Build system         | AGP + Gradle convention plugins (`build-logic`) | 8.11.1        |

---

## Module structure

```
KrossChat/
├── build-logic/               # Gradle convention plugins
│   └── convention/            # KmpLibraryPlugin, CmpLibraryPlugin, RoomPlugin, …
│
├── core/
│   ├── domain/                # Result<T,E>, DataError, AuthService interface, SessionStorage,
│   │                          #   User, PasswordValidator, KrossChatLogger
│   ├── data/                  # Ktor HttpClient factory, KtorService, DataStoreSessionStorage,
│   │                          #   DTOs + mappers, Kermit logging, Koin wiring
│   ├── presentation/          # DeviceConfiguration, UiText, ObserveAsEvents, clearFocusOnTap,
│   │                          #   DataError → UiText mapper
│   └── designsystem/          # Material3 theme, typography, color tokens,
│                              #   KrossButton, KrossTextField, KrossPasswordTextField,
│                              #   KrossAdaptiveFormLayout, KrossAdaptiveResultLayout,
│                              #   KrossSnackBarScaffold, KrossBottomSheet, dialogs,
│                              #   KrossAvatarPhoto, KrossStackedAvatars, KrossChatBubble,
│                              #   brand icons (logo, success, failure)
│
├── feature/
│   ├── auth/
│   │   ├── domain/            # EmailValidator
│   │   └── presentation/      # Register, Login, EmailVerification, ForgotPassword,
│   │                          #   ResetPassword, RegisterSuccess screens + ViewModels,
│   │                          #   AuthGraph nav graph, deep-link handling
│   │
│   └── chat/
│       ├── domain/            # Chat entities and use-cases (scaffolded)
│       ├── data/              # WebSocket data source via Ktor (scaffolded)
│       ├── database/          # Room DAOs + entities for offline storage (scaffolded)
│       └── presentation/      # ChatList screen + ViewModel (in progress)
│
├── composeApp/                # Integration layer
│   ├── commonMain/            # App.kt, NavigationRoot, MainViewModel, Koin init,
│   │                          #   DeepLinkListener, ExternalUriHandler
│   ├── androidMain/           # MainActivity, KrossChatApplication
│   ├── iosMain/               # MainViewController
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

### Session management

`DataStoreSessionStorage` (KMP, DataStore) stores the auth token. `MainViewModel` checks session
state on startup and routes to Auth or Chat. Ktor's auth plugin handles token refresh; on expiry the
user is redirected to login.

### Deep linking

`DeepLinkListener` intercepts incoming URIs in `composeApp`. Email-verification and password-reset
links are routed to the matching screens in `AuthGraph` via `ExternalUriHandler`.

### Adaptive layouts

`KrossAdaptiveFormLayout` and `KrossAdaptiveResultLayout` respond to `DeviceConfiguration` (derived
from `WindowSizeClass`) to render mobile portrait, mobile landscape, tablet, and desktop layouts
from a single composable.

### WebSocket chat
The chat data source opens a persistent Ktor WebSocket session and exposes incoming messages as a `Flow<ChatMessage>`. The repository combines the live flow with cached Room data to deliver an offline-first experience.

### Koin DI
Each layer declares its own Koin module. All modules are assembled in `:composeApp` via `startKoin { modules(...) }`. Composables obtain ViewModels with `koinViewModel()`.

---

## Getting started

### Prerequisites

- Android Studio Meerkat or newer (with KMP plugin)
- Xcode 15+ (for iOS target)
- JDK 17+

### Local configuration

Create a `local.properties` file in the project root and add your backend API key:

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
- [x] Core design system (theme, typography, adaptive layouts, reusable components)
- [x] Core domain (Result wrapper, DataError, AuthService interface, PasswordValidator)
- [x] Core data (Ktor HttpClient factory, DataStore session storage, Kermit logging)
- [x] Auth — registration screen + email validation
- [x] Auth — login screen + session token storage
- [x] Auth — email verification screen + deep link
- [x] Auth — forgot password screen + deep link
- [x] Auth — reset password screen + deep link
- [x] Auth — session expiration handling + startup auth check
- [x] Navigation — auth → chat flow with type-safe routes
- [x] Android adaptive icons + iOS launch screen
- [ ] Chat — WebSocket session management
- [ ] Chat — Room offline persistence
- [ ] Chat — conversation list screen (in progress)
- [ ] Chat — message thread screen