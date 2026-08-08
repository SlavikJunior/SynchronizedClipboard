# SynchronizedClipboard

Нативное Android-приложение для E2E-шифрованной синхронизации буфера обмена между устройствами пользователя. Реалтайм-синхронизация, история, управление сессиями устройств.

## Стек технологий

- **Kotlin** 2.4.10 | **AGP** 9.3.1 | **Compose BOM** 2026.06.01
- **Navigation**: Navigation 3 + Nav3Router
- **DI**: Koin 4.x (Compiler Plugin)
- **Сеть**: Ktor Client + kotlinx.serialization
- **Локальное хранилище**: Room KMP 2.8.4 + DataStore (планируется)
- **Архитектура**: Clean Architecture + MVI

## Быстрый старт

```bash
# Сборка debug APK
./gradlew assembleDebug

# Установка на устройство
./gradlew :app:installDebug

# Тесты
./gradlew :app:testDebugUnitTest
```

Требования: JDK 21, Android SDK 37, `ANDROID_HOME` настроен.

## Архитектура

### Clean Architecture

Проект использует жёсткое разделение на модули с контрактом `:api` / `:impl`:

- **`:api`** — только контракты: интерфейсы UseCase/Repository, domain-модели (чистые Kotlin data classes без аннотаций), `@Serializable` DTO, Route.
- **`:impl`** — `internal` реализации UseCase и Repository, UI (Composable, ViewModel), навигационные entry providers. **Единственный public export** — `xxxNavEntry()`.

Правила:
- Один класс/интерфейс на файл.
- Лимит файла 500 LOC.
- Каждый `@Composable` имеет `@Preview`.
- Нет хардкода строк — весь текст через `res/values/strings.xml`.

### MVI (Model-View-Intent)

Каждая фича имеет строгую иерархию:

| Слой | Описание |
|---|---|
| **State** | `ScreenState<T>` (`Idle`, `Loading`, `Success<T>`, `Empty(message)`, `Error(message)`) — источник правды для UI |
| **Event** | `XxxEvent` — пользовательские действия (`OnFabClicked`, `OnItemDeleted`) |
| **Effect** | `XxxEffect` — one-shot side effects (`ShowToast`, `ShowSnackbar`, `NavigateToMain`) |
| **ViewModel** | `@KoinViewModel internal class XxxViewModel` — StateFlow + Channel |
| **Screen** | Stateful composable создаёт ViewModel, собирает state/effect |
| **Content** | Stateless composable, `when(state)` → LoadingContent/ErrorContent/EmptyContent/SuccessContent |

### State Hoisting

```kotlin
// Stateful (создаёт ViewModel, собирает state)
@Composable
internal fun ClipboardScreen(onNavigateToTab: (Route) -> Unit) {
    val viewModel = koinViewModel<ClipboardViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    // ...
    ClipboardScreenContent(state = state, onEvent = viewModel::handleEvent)
}

// Stateless (рендерит UI по state)
private fun ClipboardScreenContent(state: ScreenState<ClipboardState>, onEvent: (ClipboardEvent) -> Unit) {
    when (state) {
        is ScreenState.Loading -> ClipboardLoadingContent()
        is ScreenState.Error -> ClipboardErrorContent(message = state.message, onRetry = { TODO() })
        is ScreenState.Empty -> ClipboardEmptyContent(message = state.message)
        is ScreenState.Success -> ClipboardSuccessContent(items = state.data.items, onEvent = onEvent)
        is ScreenState.Idle -> Unit
    }
}
```

### DI (Koin 4.x)

Используется **Koin Compiler Plugin** (Kotlin Compiler Plugin, не KSP). Модули регистрируются через `@Module @ComponentScan`:

- `NetworkModule` — `HttpClient` (Ktor + OkHttp)
- `DatabaseModule` — Room `AppDatabase`
- `AuthModule`, `ClipboardModule`, `DevicesModule` — фича-модули

Запуск Koin в `SyncClipApplication.kt`:
```kotlin
startKoin {
    androidLogger()
    androidContext(this@SyncClipApplication)
    modules(
        jsonModule,
        NetworkModule().networkModule,
        DatabaseModule().databaseModule,
        AuthModule().authModule,
        ClipboardModule().clipboardModule,
        DevicesModule().devicesModule,
    )
}
```

### Навигация (Navigation 3 + Nav3Router)

Базовый контракт в `:core:navigation`:
```kotlin
interface Route : NavKey {
    val tabTitleRes: Int? get() = null
}
```

**Routes:**
- `AuthRoute` — экран авторизации (без BottomBar, `tabTitleRes = null`)
- `ClipboardRoute` — буфер обмена (`tabTitleRes = R.string.tab_clipboard`)
- `DevicesRoute` — устройства (`tabTitleRes = R.string.tab_devices`)

**Tab switching** — предотвращает дубликаты в backstack:
```kotlin
val onNavigateToTab: (Route) -> Unit = { route ->
    if (backStack.lastOrNull() != route) {
        backStack.clear()
        backStack.add(route)
    }
}
```

**BackHandler:**
- В `DevicesScreen` — переключает на `ClipboardRoute`.
- В `ClipboardScreen` (корневой таб) — системный Back закроет приложение (AuthRoute удалён из backstack при успешном входе).

### Convention Plugins

5 кастомных плагинов в `build-logic/convention`:

| Plugin ID | Назначение |
|---|---|
| `syncclip.android.application` | `com.android.application` + compileSdk 37, minSdk 24, targetSdk 36, Java 21 |
| `syncclip.android.library` | `com.android.library` + конфиг модуля |
| `syncclip.android.compose` | Compose BOM 2026.06.01, material3/ui/ui-graphics/ui-tooling-preview, debug tooling |
| `syncclip.android.feature` | Library + core-ktx, lifecycle-runtime-ktx, activity-compose |
| `syncclip.koin` | Koin Compiler Plugin 1.1.0 |

## Модульная карта

```
:app                              # Приложение: Koin startup, RootNavHost
:core:navigation                  # Route : NavKey, tabTitleRes
:core:designsystem                # Тема, Scaffold, BottomBar, TopAppBar, FAB, AlertDialog, Loading/Error/Empty
:core:network                     # Ktor HttpClient, NetworkModule
:core:database                    # Room AppDatabase, DatabaseModule
:feature:auth:{api,impl}          # Авторизация
:feature:clipboard:{api,impl}     # Буфер обмена + Bottom Navigation
:feature:devices:{api,impl}       # Устройства + Bottom Navigation
build-logic/convention            # 5 convention-плагинов + AndroidModuleConfig
```

## Функционал

### Авторизация
- Вход по логину/паролю.
- Вход через Google Sign-In.
- Валидация полей.
- Кэширование формы при ошибке (не сбрасывает введённые данные).
- Переход к буферу обмена после успешного входа.

### Буфер обмена
- Лента истории элементов.
- FAB — добавление нового элемента.
- Копирование элемента (Toast «Скопировано»).
- Удаление с undo (Snackbar «Удалено / Отменить»).
- Закрепление/открепление элементов.
- Пустое состояние с иконкой.
- BottomBar с табами «Буфер» / «Устройства».

### Устройства
- Список привязанных устройств.
- Индикатор «Это устройство».
- Подтверждение перед отвязкой (AlertDialog).
- Защита от отвязки текущего устройства (Toast «Нельзя отвязать текущее устройство»).
- Уведомление об успешной отвязке (Toast «Устройство «X» отвязано»).
- Обработка ошибок загрузки.

### Планы развития
- `:feature:settings` — настройки профиля, уведомлений, темы.
- **WebSocket/SSE** реалтайм-синхронизация буфера между устройствами.
- **E2E шифрование** — клиентское шифрование перед отправкой.
- TileService (Quick Settings Tile).
- Share Target (ACTION_SEND).

## UI и Дизайн-система

### SyncClipTheme
- Единая точка темы, Material 3.
- Material You dynamic color на Android 12+.
- Цветовая палитра: `SyncClipPrimary`, `SyncClipSecondary`, `SyncClipTertiary`, `SyncClipSurface`, `SyncClipBackground`, `SyncClipError` + тёмные варианты.
- Типографика: `SyncClipTypography` (titleLarge, bodyLarge, labelLarge).

### Переиспользуемые компоненты

| Компонент | Назначение |
|---|---|
| `SyncClipScaffold` | Корневой Scaffold с опциональным `snackbarHostState` |
| `SyncClipBottomBar` | `NavigationBar` + `BottomNavTab` (берёт заголовок из `route.tabTitleRes`) |
| `SyncClipTopAppBar` | `CenterAlignedTopAppBar` с `@StringRes titleRes` |
| `SyncClipFab` | `FloatingActionButton` с `primaryContainer` цветом |
| `SyncClipAlertDialog` | Переиспользуемый диалог подтверждения |
| `SyncClipLoadingView` | Индикатор загрузки |
| `SyncClipErrorView` | Ошибка с сообщением и кнопкой «Повторить» |
| `SyncClipEmptyView` | Пустое состояние с иконкой |

### Правила UI
- Каждый экран — в `SyncClipScaffold`.
- BottomBar только для экранов с `tabTitleRes != null`.
- State hoisting: ViewModel → stateless composable.
- Edge-to-edge (`enableEdgeToEdge()`).

## Данные и сеть

### Room KMP

- `AppDatabase` в `:core:database` (заглушка `DummyEntity` для генерации).
- `fallbackToDestructiveMigration()` для MVP.
- DI через `DatabaseModule`.

### Ktor Client

- OkHttp engine.
- ContentNegotiation: `kotlinx.serialization.Json` (ignoreUnknownKeys, isLenient).
- Logging на уровне BODY для debug.

### Fake-репозитории

Для MVP все данные — in-memory `MutableStateFlow`:

- **Clipboard**: 4 стабовых элемента (текст, timestamp, sourceDevice, isPinned).
- **Devices**: 3 устройства (Pixel 8 Pro текущее, MacBook Pro, iPhone 15).

### Модели данных

**Domain** (чистые Kotlin, без аннотаций):
```kotlin
data class ClipboardItem(
    val id: String,
    val text: String,
    val timestamp: Long,
    val sourceDevice: String,
    val isPinned: Boolean,
)

data class DeviceItem(
    val id: String,
    val name: String,
    val os: String,
    val isCurrentDevice: Boolean,
    val isOnline: Boolean,
    val lastSyncTimestamp: Long,
)
```

**DTO** (`@Serializable`, в `:impl`):
- `ClipboardItemData` — маппер `toDomain()` / `toData()`.
- `DeviceItemData` — маппер `toDomain()` / `toData()`.

## Интеграция с бекендом

### Общая стратегия

- **REST API** — синхронные операции: auth, CRUD clipboard, управление устройствами.
- **WebSocket** — реалтайм-синхронизация clipboard и presence устройств.
- **Ktor Client** — единственная точка входа в сеть. Все запросы идут через `HttpClient` из `NetworkModule`.
- **Аутентификация** — JWT `accessToken` / `refreshToken`, заголовок `Authorization: Bearer <token>`.
- **Обработка ошибок** — на уровне Repository обёртываются в `Result<T>` или `Flow<ScreenState>`, ViewModel переводит в `ScreenState.Error`.
- **E2E шифрование** — клиент шифрует содержимое буфера перед отправкой; сервер хранит только ciphertext.

---

### Auth — REST эндпоинты

| Метод | Эндпоинт | Параметры | Ответ |
|---|---|---|---|
| `POST` | `/api/v1/auth/register` | Body: `{ login: String, password: String }` | `AuthResponse` |
| `POST` | `/api/v1/auth/login` | Body: `{ login: String, password: String }` | `AuthResponse` |
| `POST` | `/api/v1/auth/google` | Body: `{ googleIdToken: String }` | `AuthResponse` |
| `POST` | `/api/v1/auth/logout` | — | `204 No Content` |
| `POST` | `/api/v1/auth/refresh` | Body: `{ refreshToken: String }` | `AuthResponse` |

**AuthResponse:**
```json
{
  "accessToken": "string",
  "refreshToken": "string",
  "expiresIn": 3600,
  "user": {
    "id": "uuid",
    "email": "string",
    "displayName": "string?",
    "createdAt": "ISO8601"
  }
}
```

---

### Clipboard — REST эндпоинты

| Метод | Эндпоинт | Параметры | Ответ |
|---|---|---|---|
| `GET` | `/api/v1/clipboard/items` | Query: `page: Int`, `pageSize: Int` | `ClipboardListResponse` |
| `POST` | `/api/v1/clipboard/items` | Body: `{ text: String, sourceDeviceId: String, isPinned: Boolean }` | `ClipboardItemDto` |
| `DELETE` | `/api/v1/clipboard/items/{id}` | Path: `id` | `204 No Content` |
| `PATCH` | `/api/v1/clipboard/items/{id}/pin` | Path: `id`, Body: `{ isPinned: Boolean }` | `ClipboardItemDto` |

**ClipboardListResponse:**
```json
{
  "items": [
    {
      "id": "uuid",
      "text": "encrypted_or_plaintext",
      "timestamp": "ISO8601",
      "sourceDeviceId": "uuid",
      "sourceDeviceName": "string",
      "isPinned": false,
      "isDeleted": false
    }
  ],
  "total": 42,
  "page": 1,
  "pageSize": 50
}
```

---

### Devices — REST эндпоинты

| Метод | Эндпоинт | Параметры | Ответ |
|---|---|---|---|
| `GET` | `/api/v1/devices` | — | `List<DeviceItemDto>` |
| `DELETE` | `/api/v1/devices/{id}` | Path: `id` | `204 No Content` |
| `PATCH` | `/api/v1/devices/{id}` | Path: `id`, Body: `{ name?: String }` | `DeviceItemDto` |

**DeviceItemDto:**
```json
{
  "id": "uuid",
  "name": "string",
  "os": "string",
  "isCurrentDevice": true,
  "isOnline": true,
  "lastSyncTimestamp": "ISO8601"
}
```

---

### Realtime — WebSocket

**URL:** `wss://api.example.com/api/v1/realtime?accessToken=<JWT>`

**Подключение:**
- После успешной аутентификации.
- Heartbeat: сервер шлёт ping каждые 30 сек, клиент отвечает pong.
- Reconnect: exponential backoff (1s → 2s → 4s → … → 30s max).

**События от сервера:**

| Event Type | Payload | Назначение |
|---|---|---|
| `CLIPBOARD_ITEM_ADDED` | `ClipboardItemDto` | Новый элемент в буфере |
| `CLIPBOARD_ITEM_UPDATED` | `ClipboardItemDto` | Изменение (pin/unpin) |
| `CLIPBOARD_ITEM_DELETED` | `{ id: String }` | Удаление элемента |
| `DEVICE_STATUS_CHANGED` | `DeviceItemDto` | Онлайн/офлайн, lastSyncTimestamp |

**Пример сообщения (inbound):**
```json
{
  "type": "CLIPBOARD_ITEM_ADDED",
  "payload": {
    "id": "uuid",
    "text": "string",
    "timestamp": "ISO8601",
    "sourceDeviceId": "uuid",
    "sourceDeviceName": "string",
    "isPinned": false,
    "isDeleted": false
  }
}
```

**Пример сообщения (outbound, heartbeat):**
```json
{
  "type": "DEVICE_HEARTBEAT",
  "payload": {
    "deviceId": "uuid",
    "isOnline": true,
    "lastSyncTimestamp": "ISO8601"
  }
}
```

---

### Общие DTO (справка для бекенда)

```kotlin
// Auth
data class LoginRequest(val login: String, val password: String)
data class GoogleLoginRequest(val googleIdToken: String)
data class AuthResponse(val accessToken: String, val refreshToken: String, val expiresIn: Long, val user: UserDto)
data class UserDto(val id: String, val email: String, val displayName: String?, val createdAt: String)

// Clipboard
data class ClipboardItemDto(val id: String, val text: String, val timestamp: String, val sourceDeviceId: String, val sourceDeviceName: String, val isPinned: Boolean, val isDeleted: Boolean)
data class AddClipboardItemRequest(val text: String, val sourceDeviceId: String, val isPinned: Boolean)
data class ClipboardListResponse(val items: List<ClipboardItemDto>, val total: Int, val page: Int, val pageSize: Int)

// Devices
data class DeviceItemDto(val id: String, val name: String, val os: String, val isCurrentDevice: Boolean, val isOnline: Boolean, val lastSyncTimestamp: String)

// Realtime
data class RealtimeMessage(val type: String, val payload: JsonElement)
```

---

## Схема данных

```
User (1) ──< (N) Device
   │
   └──< (N) ClipboardItem
```

- **User**: `id (PK)`, `email`, `displayName`, `passwordHash`, `createdAt`.
- **Device**: `id (PK)`, `userId (FK)`, `name`, `os`, `deviceType`, `isCurrent`, `isOnline`, `lastSyncAt`, `createdAt`, `deletedAt`.
- **ClipboardItem**: `id (PK)`, `userId (FK)`, `deviceId (FK)`, `encryptedText`, `isPinned`, `isDeleted`, `createdAt`, `updatedAt`.

## Roadmap

- [x] `:core:navigation`, `:core:designsystem`, `:core:network`, `:core:database`
- [x] `:feature:auth`, `:feature:clipboard`, `:feature:devices`
- [ ] `:feature:settings`
- [ ] Реалтайм WebSocket/SSE синхронизация
- [ ] E2E шифрование
- [ ] TileService (Quick Settings Tile)
- [ ] Share Target (ACTION_SEND)

## Лицензия

MIT
