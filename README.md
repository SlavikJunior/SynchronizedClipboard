# SynchronizedClipboard

Нативное Android-приложение для E2E-шифрованной синхронизации буфера обмена между устройствами пользователя. Реалтайм-синхронизация, история, управление сессиями устройств. Ведётся миграция на Kotlin Multiplatform (iOS).

## Стек технологий

- **Kotlin** 2.4.10 | **AGP** 9.3.1 | **Compose BOM** 2026.06.01
- **Kotlin Multiplatform**: 5 модулей мигрированы (cache, di, network, database, crypto), iOS в разработке
- **Navigation**: Navigation 3 + Nav3Router
- **DI**: Koin 4.x (Compiler Plugin)
- **Сеть**: Ktor Client (OkHttp/Darwin) + kotlinx.serialization
- **Локальное хранилище**: Room KMP 2.8.4 + DataStore
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

Требования: JDK 21, Android SDK 37, macOS + Xcode для iOS-сборки, `ANDROID_HOME` настроен.

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

- `NetworkModule` — `HttpClient` (Ktor + OkHttp/Darwin)
- `DatabaseModule` — Room `AppDatabase` + `ClipboardDao`
- `CryptoModule` — `CryptoManager` (AES-256-GCM)
- `DiModule` — 4 `CoroutineDispatcher` через `@Named` (`io_dispatcher`, `main_dispatcher`, `default_dispatcher`, `main_immediate_dispatcher`)
- `CacheModule` — `ReactiveCache<String, Any>` + типизированный `ClipboardLRU` в `ClipboardModule`
- `AuthModule`, `ClipboardModule`, `DevicesModule`, `SettingsModule` — фича-модули
- `SettingsDataStore` — DataStore preferences для настроек

Запуск Koin в `SyncClipApplication.kt`:
```kotlin
startKoin {
    androidLogger()
    androidContext(this@SyncClipApplication)
    modules(
        jsonModule,
        NetworkModule().networkModule(),
        DatabaseModule().databaseModule(),
        CryptoModule().cryptoModule(),
        DiModule().diModule(),
        CacheModule().cacheModule(),
        AuthModule().authModule(),
        ClipboardModule().clipboardModule(),
        DevicesModule().devicesModule(),
        SettingsModule().settingsModule(),
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
- `SettingsRoute` — настройки (`tabTitleRes = R.string.tab_settings`)

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
| `syncclip.android.feature` | Не используется (мёртвый) |
| `syncclip.koin` | Koin Compiler Plugin 1.1.0 |

## Модульная карта

```
:app                              # Приложение: Koin startup, RootNavHost
:core:navigation                  # Route : NavKey, tabTitleRes
:core:designsystem                # Тема, Scaffold, BottomBar, TopAppBar, FAB, AlertDialog, Loading/Error/Empty
:core:network                     # ✅ KMP: Ktor HttpClient, OkHttp/Darwin, PlatformConfig
:core:database                    # Room AppDatabase, DatabaseModule, ClipboardItemEntity, ClipboardDao
:core:crypto                      # ✅ KMP: AES-256-GCM, Android AndroidKeyStore/JCA, iOS CryptoKit/whyoleg
:core:di                          # ✅ KMP: CoroutineDispatcher qualifiers (expect/actual ioDispatcher)
:core:cache                       # ✅ KMP: ReactiveCache<K,V> + LruReactiveCache (Mutex thread-safety)
:feature:auth:{api,impl}          # Авторизация
:feature:clipboard:{api,impl}     # Буфер обмена + Bottom Navigation + Share Target + Quick Tile
:feature:devices:{api,impl}       # Устройства + Bottom Navigation
:feature:settings:{api,impl}      # Настройки (тема, история, выход) + DataStore
build-logic/convention            # 5 convention-плагинов + AndroidModuleConfig
```

✅ — Kotlin Multiplatform модули (Android + iOS)

## Kotlin Multiplatform (KMP)

Пять модулей мигрированы на KMP:

- **`:core:cache`** — `ReactiveCache`, `LruReactiveCache` с `Mutex` для потокобезопасности
- ****:core:di** — `CoroutineDispatcher` providers с `expect/actual` для `ioDispatcher()` (Android: IO, iOS: Default)
- **`:core:network`** — Ktor Client с platform-specific engines:
  - Android: OkHttp engine
  - iOS: Darwin engine (CFNetwork)
  - `Logger` expect/actual: Android → `android.util.Log`, iOS → `NSLog`
  - `PlatformConfig` expect/actual: Android → `10.0.2.2:8080` (emulator), iOS → `localhost:8080` (simulator)
- **`:core:database`** — Room KMP: общий `AppDatabase`, platform-specific `databaseBuilder()` (Android: `SupportSQLiteOpenHelper`; iOS: `BundledSQLiteDriver`). Диспатчер запросов приходит параметром из Koin (`@Named("io_dispatcher")` из `:core:di`)
- **`:core:crypto`** — AES-256-GCM: Android — AndroidKeyStore/JCA (ключ неэкспортируем, therefore JCA); iOS — CryptoKit через `dev.whyoleg.cryptography` с `runBlocking`-мостом suspend-API к sync-контракту. Общий `CipherTextCodec` фиксирует формат `Base64(IV):Base64(cipherText)` — шифротексты кроссплатформенны

**Source path:**
- Android-only → `src/main/java/...`
- KMP → `src/{commonMain,androidMain,iosMain}/kotlin/...`

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

### Настройки
- Выбор темы (System/Light/Dark) — персистится в DataStore
- Срок хранения истории — персистится в DataStore
- Выход из аккаунта

## Системные интеграции

### Share Target
- `ShareActivity` (`ComponentActivity`, `KoinComponent`) в `:feature:clipboard:impl/presentation/system/`.
- Обрабатывает `ACTION_SEND` (`text/plain`), сохраняет через `AddClipboardItemUseCase` (`sourceDevice = "Share"`).
- Прозрачная тема `Theme.SyncClip.Transparent`.

### Quick Settings Tile
- `ClipboardTileService` (`TileService`) + `TileActionActivity` (`ComponentActivity`, `KoinComponent`).
- `onClick()` → `PendingIntent.getActivity(...).send()` → читает системный `ClipboardManager`, сохраняет через `AddClipboardItemUseCase` (`sourceDevice = "Quick Tile"`).
- `TileActionActivity` — `exported="false"`, `taskAffinity=""`, прозрачная тема.

## E2E шифрование

- Модуль `:core:crypto` (KMP: commonMain/androidMain/iosMain).
- `CryptoManager` — интерфейс `encrypt`/`decrypt`, единый для платформ.
- Реализации: **Android** — JCA + AndroidKeyStore (alias `syncclip_clipboard_key`), **iOS** — CryptoKit через `dev.whyoleg.cryptography` (ключ в памяти процесса, переходное состояние до деривации из парольной фразы).
- Формат: `Base64(IV):Base64(cipherText)` — контракт в общем `CipherTextCodec`, покрытом commonTest. Записи, созданные до KMP-миграции, читаются без изменений.
- При ошибке расшифровки — `"🔒 [Ошибка расшифровки]"`.
- В `:feature:clipboard:impl` шифрование/расшифровка происходит в `ClipboardRepositoryImpl` (через `CryptoManager`). UseCase'ы работают с доменными моделями, репозиторий отвечает за E2E-шифрование.

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

- `AppDatabase` в `:core:database` (`entities = [DummyEntity::class, ClipboardItemEntity::class]`, version 2).
- `ClipboardItemEntity` — `@Entity(tableName = "clipboard_items")` с `id`, `encryptedText`, `timestamp`, `sourceDevice`, `isPinned`.
- `ClipboardDao` — только `suspend` методы без Flow: `getAll()`, `insert()`, `deleteById()`.
- Явная миграция `MIGRATION_1_2` (`DROP TABLE IF EXISTS dummy_table`).
- DI через `DatabaseModule`.

### DataStore

- `SettingsDataStore` в `:feature:settings:impl` использует `preferencesDataStore(name = "settings")`.
- Хранит: тему (`AppTheme`), количество дней истории, email пользователя.
- `DataStoreSettingsRepository` реализует `SettingsRepository`.

### Ktor Client (KMP)

- Platform-specific engine: OkHttp (Android), Darwin (iOS).
- ContentNegotiation: `kotlinx.serialization.Json` (ignoreUnknownKeys, isLenient).
- Logging на уровне HEADERS.

### WebSocket

- `KtorNetworkSyncApi` (`@Single`) в `:core:network` — точка интеграции WebSocket в клиент.
- Platform-specific адрес:
  - Android (emulator): `ws://10.0.2.2:8080/sync`
  - Android (device): требуется реальный IP
  - iOS (simulator): `ws://localhost:8080/sync`
  - iOS (device): требуется реальный IP
- Входящий поток: `observeIncomingItems(): Flow<EncryptedClipboardDto>` — постоянный слушатель с реконнектом и exponential backoff (1s → 30s).
- Исходящий поток: `sendItem(item: EncryptedClipboardDto)` — открывает короткий сеанс для отправки одного элемента.
- Внутренний `MutableSharedFlow<EncryptedClipboardDto>` (extraBufferCapacity = 64) буферизирует входящие сообщения между WebSocket-сессией и consumer'ом.
- `NetworkModule` регистрирует `HttpClient` с плагином `WebSockets` и экспонирует `networkModule()` для Koin.

### Репозитории

- **Clipboard** (`ClipboardRepositoryImpl`): Write-Through Cache через `ReactiveCache` (`:core:cache`). Источник истины для UI — in-memory LRU-кеш с `MutableStateFlow`. Room используется как глухое хранилище без Flow. Кеш прогревается из БД при первом `observeClipboard()`. Мутации (`add/delete/pin`) сразу попадают в кеш, запись в БД — fire-and-forget корутина. Входящие элементы из WebSocket расшифровываются через `CryptoManager` и записываются в кеш + Room. Исходящие элементы отправляются в сеть после записи в кеш/БД.
- **Devices**: 3 устройства (Pixel 8 Pro текущее, MacBook Pro, iPhone 15).
- **Settings** (`DataStoreSettingsRepository`): DataStore preferences для темы и дней истории.

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

## Roadmap

- [x] `:core:navigation` — `Route : NavKey` + `tabTitleRes`
- [x] `:core:designsystem` — `SyncClipTheme`, `ScreenState`, state views, `SyncClipScaffold`, `SyncClipBottomBar`
- [x] `:core:network` + `:core:database` — Ktor + Room KMP (Koin DI)
- [x] `:core:crypto` — AES-256-GCM/AndroidKeyStore, E2E шифрование
- [x] `:core:di` — CoroutineDispatcher qualifiers
- [x] `:core:cache` — ReactiveCache + Write-Through Cache для clipboard
- [x] `:feature:auth` — авторизация
- [x] `:feature:clipboard` — буфер обмена + Bottom Navigation + Share Target + Quick Tile + Write-Through Cache
- [x] `:feature:devices` — управление устройствами + Bottom Navigation
- [x] `:feature:settings` — настройки (тема, история, выход) + DataStore
- [x] WebSocket E2E-синхронизация
- [x] KMP: `:core:cache`, `:core:di`, `:core:network`, `:core:database`, `:core:crypto`
- [ ] iOS UI на Compose Multiplatform

## Лицензия

MIT