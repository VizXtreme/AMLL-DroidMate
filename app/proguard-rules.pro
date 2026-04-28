# ============================================================================
# ProGuard / R8 代码混淆和压缩规则
# ============================================================================
# 
# **ProGuard 是什么？**
# - 代码压缩：移除未使用的代码，减小 APK 体积
# - 代码混淆：重命名类、方法、字段，增加反编译难度
# - 代码优化：优化字节码，提升性能
# 
# **为什么需要配置？**
# - Kotlin/Compose 使用反射，某些类不能被混淆
# - 网络库（Ktor、OkHttp）需要保持特定类名
# - 序列化库需要保留元数据
# 
# **性能优化策略**：
# - 启用代码压缩和优化
# - Release 构建中移除 Timber 日志
# - 优化次数设置为 5 次（平衡速度和质量）
# ============================================================================

# ==================== 基础混淆设置 ====================
# 不禁用混淆（与默认行为一致），但保持可读的类名
-dontobfuscate

# 优化次数：5 次（推荐值，平衡速度和质量）
-optimizationpasses 5

# 排除特定优化：避免过度优化导致的问题
# - code/allocation/variable: 变量分配优化
# - field/removal/writeonly: 只读字段移除
# - class/merging/*: 类合并优化
-optimizations !code/allocation/variable,!field/removal/writeonly,!class/merging/*

# ==================== 保留应用入口点和数据模型 ====================
# 注意：避免使用过于宽泛的规则（影响 100+ 类），只保留必要的类

# MainActivity：应用主入口，必须保留
-keep class io.github.zeehan2005.scoremuse.MainActivity { *; }

# Service 层：后台服务组件，必须保留
-keep class io.github.zeehan2005.scoremuse.service.** { *; }

# UI Screens：Compose 界面组件，必须保留
-keep class io.github.zeehan2005.scoremuse.ui.screens.** { *; }

# Domain Models：数据模型，用于序列化和数据库
-keep class io.github.zeehan2005.scoremuse.domain.model.** { *; }

# (移除 blanket package rule，让 shrinker 自动修剪未使用的类)

# ==================== Jetpack Compose 保留规则 ====================
# 之前的规则 `-keep class androidx.** { *; }` 在 lint 分析中没有匹配到成员
# 已移除或收窄该规则，避免不必要的代码保留

# ==================== Kotlin 反射支持 ====================
# 保留 Kotlin Metadata 注解的 valueOf 和 values 方法
# 这些方法用于枚举类和数据类的反射操作
-keepclassmembers class kotlin.Metadata {
    *** valueOf(...);
    *** values();
}

# ==================== Kotlinx 序列化支持 ====================
# 允许压缩但保留必要的序列化类

# 保留序列化库核心类（允许压缩）
-keep,allowshrinking class kotlinx.serialization.** { *; }

# 保留自动生成的 $$serializer 类（用于自定义类型序列化）
-keep,allowshrinking class **$$serializer { *; }

# 保留 Companion 对象的 INSTANCE 字段（单例模式支持）
-keepclassmembers class **$Companion {
    *** INSTANCE;
}

# ==================== 移除 Release 构建中的 Timber 日志 ====================
# 假设 Timber 调用没有副作用，可以安全移除
# 这能减小 APK 体积并提升性能
-assumenosideeffects class timber.log.Timber {
    public static *** d(...);  # Debug 日志
    public static *** v(...);  # Verbose 日志
    public static *** i(...);  # Info 日志
}

# ==================== Ktor 网络库保留规则 ====================
# 允许压缩但保留实际使用的类
-keep,allowshrinking class io.ktor.** { *; }

# ==================== OkHttp & Okio 保留规则 ====================
# 仅保留实际使用的类，防止运行时 ClassNotFoundException

# 忽略警告：这些库可能包含 Android 不存在的类
-dontwarn okhttp3.**
-dontwarn okio.**

# 保留核心 HTTP 客户端类（允许混淆和压缩）
-keep,allowobfuscation,allowshrinking class okhttp3.OkHttpClient { *; }
-keep,allowobfuscation,allowshrinking class okhttp3.Request { *; }
-keep,allowobfuscation,allowshrinking class okhttp3.Response { *; }
-keep,allowobfuscation,allowshrinking class okhttp3.RequestBody { *; }
-keep,allowobfuscation,allowshrinking class okhttp3.ResponseBody { *; }
-keep,allowobfuscation,allowshrinking class okhttp3.MediaType { *; }
-keep,allowobfuscation,allowshrinking class okhttp3.Headers { *; }

# 保留 Okio I/O 库核心类
-keep,allowobfuscation,allowshrinking class okio.BufferedSource { *; }
-keep,allowobfuscation,allowshrinking class okio.BufferedSink { *; }
-keep,allowobfuscation,allowshrinking class okio.ByteString { *; }

# ==================== OkHttp连接池优化 ====================
# 保留 cleanup 方法（用于连接池管理）
-keepclassmembers class okhttp3.ConnectionPool {
    public *** cleanup();
}

# ==================== Timber 日志库保留规则 ====================
# 虽然我们在 release 中移除了日志调用，但仍需保留 Timber 类本身
-keep class timber.** { *; }

# ==================== Retrofit 反射支持 ====================
# Platform 类会通过 Class.forName 检查 Java 平台特性
# 这些类在 Android 上不存在，但不要报错
-dontnote retrofit2.Platform

# Java 8+ 平台支持（不要警告缺失的类）
-dontwarn retrofit2.Platform$Java8

# 保留签名属性：用于反射解析泛型类型信息
# Converter 和 Adapter 依赖这些信息
-keepattributes Signature

# 保留声明的异常：用于自定义 Retrofit Call 适配器
-keepattributes Exceptions
