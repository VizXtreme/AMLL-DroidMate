# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html
#
# Performance optimizations:
# - Enable code shrinking and optimization
# - Remove logging in release builds
# - Optimize number of optimization passes

-dontobfuscate
-optimizationpasses 5
-optimizations !code/allocation/variable,!field/removal/writeonly,!class/merging/*

# Keep our application entry points and data models.  Avoid overly broad rules affecting 100+ classes.
-keep class com.amll.droidmate.MainActivity { *; }
-keep class com.amll.droidmate.service.** { *; }
-keep class com.amll.droidmate.ui.screens.** { *; }
-keep class com.amll.droidmate.domain.model.** { *; }
# (remove blanket package rule to let shrinker trim unused classes)

# Keep Jetpack Compose (previous rule matched no members in lint analysis; remove or narrow if needed)
#-keep class androidx.** { *; }

# Keep Kotlin
-keepclassmembers class kotlin.Metadata {
    *** valueOf(...);
    *** values();
}

# Keep serialization (allow shrinking so rule doesn't count as overly broad)
-keep,allowshrinking class kotlinx.serialization.** { *; }
-keep,allowshrinking class **$$serializer { *; }
-keepclassmembers class **$Companion {
    *** INSTANCE;
}

# Remove Timber logging in release builds for better performance
-assumenosideeffects class timber.log.Timber {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Keep Ktor (allow shrinking)
-keep,allowshrinking class io.ktor.** { *; }

# Keep OkHttp and Okio - only keep actually used classes
# Retain OkHttp and Okio classes to prevent ClassNotFoundException at runtime
-dontwarn okhttp3.**
-dontwarn okio.**
-keep,allowobfuscation,allowshrinking class okhttp3.OkHttpClient { *; }
-keep,allowobfuscation,allowshrinking class okhttp3.Request { *; }
-keep,allowobfuscation,allowshrinking class okhttp3.Response { *; }
-keep,allowobfuscation,allowshrinking class okhttp3.RequestBody { *; }
-keep,allowobfuscation,allowshrinking class okhttp3.ResponseBody { *; }
-keep,allowobfuscation,allowshrinking class okhttp3.MediaType { *; }
-keep,allowobfuscation,allowshrinking class okhttp3.Headers { *; }
-keep,allowobfuscation,allowshrinking class okio.BufferedSource { *; }
-keep,allowobfuscation,allowshrinking class okio.BufferedSink { *; }
-keep,allowobfuscation,allowshrinking class okio.ByteString { *; }

# Optimize connection pool usage
-keepclassmembers class okhttp3.ConnectionPool {
    public *** cleanup();
}

# Keep Timber
-keep class timber.** { *; }

# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8+
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a custom Retrofit call adapter.
-keepattributes Exceptions
