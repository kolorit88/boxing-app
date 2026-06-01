ОБЯЗАТЕЛЬНЫЕ КРИТЕРИИ — 15 БАЛЛОВ
Чистая архитектура — 5 б
domain/model/Match.kt — доменная модель (бой)

domain/model/MatchStatus.kt — enum статусов (запланирован, идёт, завершён, отменён)

domain/repository/MatchRepository.kt — интерфейс репозитория

domain/usecase/GetUpcomingMatchesUseCase.kt — получение предстоящих боёв

domain/usecase/SearchMatchesUseCase.kt — поиск боёв

domain/usecase/GetArchiveMatchesUseCase.kt — архив боёв

data/repository/MockMatchRepository.kt — реализация репозитория

data/mock/MockDataProvider.kt — мок-данные (боксёры, рейтинги, статистика)

feature/home/HomeViewModel.kt — презентационный слой

feature/search/SearchViewModel.kt — презентационный слой

feature/match/MatchViewModel.kt — презентационный слой

app/di/AppModule.kt — Koin DI

app/build.gradle.kts — 5 модулей: app, core, data, domain, feature

Фоновые задачи и сервисы — 3 б
data/worker/MatchesSyncWorker.kt — WorkManager периодическая задача (каждые 6 часов) с Constraints

app/service/MatchesSyncService.kt — Service

app/receiver/BootReceiver.kt — BroadcastReceiver для перезапуска синхронизации

app/FootballApp.kt → BoxingApp.kt — запуск WorkManager

app/AndroidManifest.xml — регистрация компонентов

Анимации в Jetpack Compose — 2 б
feature/home/HomeScreen.kt — AnimatedVisibility, slideInHorizontally, animateFloatAsState, animateColorAsState, expandVertically, AnimatedContent

feature/search/SearchScreen.kt — slideInVertically, rotate, Crossfade, AnimatedVisibility

XML разметка и интеграция Compose — 2 б
res/layout/activity_info.xml — XML с Toolbar, TextView, ComposeView

app/InfoActivity.kt — Activity с XML и ComposeView

Gradle конфигурация сборок — 2 б
app/build.gradle.kts — buildTypes debug/release, productFlavors dev/prod, разные applicationId/URL/флаги

app/proguard-rules.pro — ProGuard правила

Качество кода и UX — 1 б
feature/home/HomeState.kt — состояния Loading/Success/Error

feature/search/SearchState.kt — состояния с isLoading/error

feature/match/MatchDetailState.kt — состояния Loading/Success/Error

feature/home/HomeViewModel.kt — onFailure обработка

feature/search/SearchViewModel.kt — onFailure обработка

feature/match/MatchViewModel.kt — onFailure обработка

data/repository/MockMatchRepository.kt — try-catch с Result.failure
