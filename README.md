# KrossChat

KrossChat is a **Kotlin Multiplatform** real-time messaging application targeting **Android** and **iOS**. The entire UI is built with **Compose Multiplatform**, meaning one codebase powers both platforms.

---

## Features

- Registration
- Login
- Email verification
- Forgot password
- Reset password
- Session management
- Navigation
- Chat list
- WebSocket chat
- Room persistence

---

## Tech stack

| Concern              | Library / Tool                                  | Version    |
|----------------------|-------------------------------------------------|------------|
| Language             | Kotlin / KMP                                    | 2.3.20     |
| UI                   | Compose Multiplatform                           | 1.11.0     |
| Navigation           | Jetbrains Navigation Compose                    | 2.9.2      |
| Networking           | Ktor (OkHttp on Android, Darwin on iOS)         | 3.5.0      |
| Dependency Injection | Koin                                            | 4.1.0      |
| Local database       | Room (KMP)                                      | 2.8.4      |
| Token storage        | DataStore                                       | 1.2.1      |
| Async                | Kotlinx Coroutines                              | 1.11.0     |
| Serialization        | Kotlinx Serialization                           | 1.11.0     |
| Date/time            | Kotlinx Datetime                                | 0.8.0      |
| Image loading        | Coil 3                                          | 3.4.0      |
| Permissions          | Moko Permissions                                | 0.20.1     |
| Logging              | Kermit                                          | 2.1.0      |
| Push notifications   | Firebase BOM                                    | 34.14.0    |
| Adaptive layouts     | Material3 Adaptive                              | 1.2.0      |
| Build secrets        | BuildKonfig                                     | 0.17.1     |
| Build system         | AGP + Gradle convention plugins (`build-logic`) | 8.13.2     |

---

## Module structure

```
KrossChat/
├── build-logic/
│   └── convention/
│
├── core/
│   ├── domain/
│   ├── data/
│   ├── presentation/
│   └── designsystem/
│
├── feature/
│   ├── auth/
│   │   ├── domain/
│   │   └── presentation/
│   │
│   └── chat/
│       ├── domain/
│       ├── data/
│       ├── database/          ← schema: feature/chat/database/README.md
│       └── presentation/
│
├── composeApp/
│   ├── commonMain/
│   ├── androidMain/
│   └── iosMain/
│
└── iosApp/
```

---
## Database schema

The chat database uses Room (KMP). See **[feature/chat/database/README.md](feature/chat/database/README.md)** for the full entity schema, relationships, and DAO reference.

![Database Schema](feature/chat/database/schema.svg)

---
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
from `WindowSizeClass`) to render mobile portrait, mobile landscape, and tablet layouts from a single
composable.

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

For iOS, open `iosApp/iosApp.xcodeproj` in Xcode and run on a simulator or device.

---