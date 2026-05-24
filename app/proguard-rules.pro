# Keep our model classes
-keep class com.example.domain.model.** { *; }

# Keep Koin
-keep class org.koin.** { *; }
-dontwarn org.koin.**

# Keep Kotlin reflection
-keep class kotlin.reflect.** { *; }
-dontwarn kotlin.reflect.**

# Keep serialization
-keepattributes *Annotation*, InnerClasses
-dontwarn kotlinx.serialization.**

# Keep Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Remove logs in release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

# Keep WorkManager
-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.CoroutineWorker

# Keep our repository classes
-keep class com.example.data.repository.** { *; }
-keep class com.example.data.mock.** { *; }

# Сохраняем имена файлов и номера строк для читаемых отчётов
-keepattributes SourceFile,LineNumberTable

# Сохраняем имена пользовательских исключений
-keep public class * extends java.lang.Exception

# Firebase Crashlytics
-keep class com.google.firebase.crashlytics.** { *; }
-dontwarn com.google.firebase.crashlytics.**

# AppMetrica
-keep class io.appmetrica.analytics.** { *; }
-dontwarn io.appmetrica.analytics.**

# Наши модели
-keep class com.example.domain.model.** { *; }