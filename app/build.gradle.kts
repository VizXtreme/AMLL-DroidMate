import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject
import org.gradle.process.ExecOperations
import java.io.File
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.provider.Provider

// ============================================================================
// DroidMate Android 应用构建配置
// ============================================================================
// 这个文件定义了 Android 应用的编译、打包和依赖配置。
// 
// **主要功能**：
// 1. 自定义 Gradle 任务（构建前端资源）
// 2. Android 编译配置（SDK 版本、Java 版本等）
// 3. 依赖管理（库版本控制）
// 4. APK 命名和版本号生成
// 
// **关键插件**：
// - com.android.application: Android 应用插件
// - kotlin("plugin.serialization"): Kotlin 序列化支持
// - kotlin("plugin.compose"): Jetpack Compose 支持
// ============================================================================

plugins {
    // Android 应用插件：将 Kotlin 项目编译为 Android APK
    id("com.android.application")
    
    // Kotlin 序列化插件：支持 @Serializable 注解
    kotlin("plugin.serialization")
    
    // Jetpack Compose 插件：支持 Compose UI
    kotlin("plugin.compose")
}

// ============================================================================
// 自定义 Gradle 任务：构建前端资源
// ============================================================================
// 
// **功能说明**：
// 这个任务负责在 Android 构建之前，先编译前端 TypeScript/React 代码，
// 生成 AMLL 歌词渲染所需的 Web 资源（HTML、CSS、JS）。
// 
// **工作流程**：
// 1. 检查 frontend 目录是否存在
// 2. Windows: 执行 build-android.ps1 PowerShell 脚本
// 3. Linux/macOS: 执行 pnpm run build:android
// 4. 生成的资源自动复制到 app/src/main/assets/amll/
// 
// **增量构建支持**：
// - 监听 frontend/src 目录变化
// - 只有源文件变化时才重新构建
// ============================================================================
/**
 * 构建前端资源的自定义 Gradle 任务
 * 
 * 这个任务使用 ExecOperations 在构建过程中调用外部命令（pnpm 或 PowerShell）
 * 来编译前端代码并生成 Web 资源。
 * 
 * @param execOperations Gradle 提供的执行操作接口，用于运行外部进程
 */
abstract class BuildFrontendTask @Inject constructor(
    private val execOperations: ExecOperations
) : DefaultTask() {
    
    // ==================== 任务输入输出配置 ====================
    
    /**
     * 前端源代码目录（增量构建的输入）
     * 
     * Gradle 会监控这个目录的变化，只有文件变化时才执行任务
     * PathSensitivity.RELATIVE: 只关心相对路径，不关心绝对路径
     */
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val frontendSrcDir: DirectoryProperty
    
    /**
     * 输出目录（用于 up-to-date 检查）
     * 
     * Gradle 会比较输出目录的时间戳，判断是否需要重新执行任务
     */
    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty
    
    /**
     * 根项目目录（内部使用，不参与 up-to-date 检查）
     * 
     * @Internal: 标记为内部属性，不影响任务状态
     */
    @get:Internal
    abstract val rootProjectDir: DirectoryProperty
    
    init {
        // 任务分组：在 Gradle 任务列表中归类到 "frontend" 组
        group = "frontend"
        // 任务描述：在 gradle tasks 命令中显示的说明
        description = "Build frontend assets using pnpm"
    }
    
    /**
     * 执行前端构建的核心逻辑
     * 
     * 这个函数会在每次执行 buildFrontend 任务时被调用。
     * 它会根据操作系统选择合适的构建命令。
     */
    @TaskAction
    fun buildFrontend() {
        // Step 1: 获取项目根目录和前端目录路径
        val rootDir = rootProjectDir.get().asFile
        val frontendDir = File(rootDir, "frontend")
        val scriptsDir = File(rootDir, "scripts")
        // 检测操作系统（Windows 需要特殊处理）
        val isWindows = System.getProperty("os.name").lowercase().contains("windows")
        
        // Step 2: 检查前端目录是否存在，不存在则跳过构建
        if (!frontendDir.exists()) {
            logger.warn("Frontend directory not found, skipping build")
            return
        }
        
        logger.info("Building frontend in ${frontendDir.absolutePath}")
        
        // Step 3: 根据操作系统选择构建命令
        val command = if (isWindows) {
            // Windows: 使用 PowerShell 执行构建脚本
            listOf("powershell", "-ExecutionPolicy", "Bypass", "-File", 
                File(scriptsDir, "build-android.ps1").absolutePath)
        } else {
            // Linux/macOS: 直接使用 pnpm 命令
            listOf("pnpm", "run", "build:android")
        }
        
        // Step 4: 执行构建命令
        execOperations.exec {
            // 设置工作目录为 frontend 目录
            workingDir(frontendDir)
            // 执行命令
            commandLine(command)
        }
    }
}

// ============================================================================
// 注册并配置 buildFrontend 任务
// ============================================================================
val buildFrontendProvider = tasks.register("buildFrontend", BuildFrontendTask::class.java) {
    // 只设置前端源码目录为输入 - 这是我们要监控变化的内容
    frontendSrcDir.set(File(rootProject.projectDir, "frontend/src"))
    
    // 设置输出目录用于 up-to-date 检查
    outputDir.set(File(rootProject.projectDir, "app/src/main/assets/amll"))
    
    // 设置根项目目录供任务执行时使用
    rootProjectDir.set(rootProject.layout.projectDirectory)
}

// ============================================================================
// 构建依赖关系：在 preBuild 之前先执行 buildFrontend
// ============================================================================
// preBuild 是 Android 构建的标准前置任务，在它之前先构建前端资源
tasks.named("preBuild") {
    dependsOn(buildFrontendProvider)
}

// Copy Material Symbols variable font from .project into res/font before resource merge.
// This avoids committing a binary into VCS while still bundling the recommended MD3 icon font.
val copyMaterialSymbols = tasks.register<Copy>("copyMaterialSymbols") {
    val srcFile = rootProject.file(".project/MaterialSymbolsOutlined-VariableFont_FILL,GRAD,opsz,wght.ttf")
    if (srcFile.exists()) {
        from(srcFile)
        into(file("src/main/res/font"))
        rename { "material_symbols_outlined.ttf" }
    } else {
        doLast { logger.warn("Material Symbols font not found at ${srcFile.absolutePath}, skipping copy") }
    }
}

// Ensure the font is copied before resource merging (preBuild is executed before that)
tasks.named("preBuild") {
    dependsOn(copyMaterialSymbols)
}

// ============================================================================
// 版本号生成器（使用时间戳）
// ============================================================================
// Provider API 实现懒加载：只有在真正需要时才计算时间戳
// 确保每次构建都有新的版本号，便于区分不同构建版本
val buildTimestampProvider: Provider<String> = providers.provider {
    // 格式：yyyyMMddHHmmss (例如：20260401123456)
    SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(Date())
}

// ============================================================================
// Android 应用配置
// ============================================================================
android {
    // ==================== 基本配置 ====================
    // 包名：应用的唯一标识符（用于 Google Play、安装等）
    namespace = "com.amll.droidmate"
    // 编译 SDK 版本：使用最新 SDK 以获得新特性支持
    compileSdk = 36

    defaultConfig {
        // 应用 ID：设备的唯一标识（可以与 namespace 不同）
        applicationId = "com.amll.droidmate"
        
        // 最低支持的 Android 版本（API 26 = Android 8.0）
        minSdk = 26
        
        // 目标 SDK：针对最新版本优化
        targetSdk = 36
        
        // 版本号：整数，每次发布递增（Google Play 要求）
        versionCode = 1
        
        // 版本名称：显示给用户的版本信息（使用时间戳格式）
        versionName = "Alpha ${buildTimestampProvider.get()}" // 版本号
        
        // 使用支持库处理 VectorDrawable（兼容旧版本）
        vectorDrawables.useSupportLibrary = true

        // 测试运行器：AndroidX 测试框架
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        // Release 构建类型（生产环境）
        release {
            // 是否启用代码压缩（ProGuard/R8）
            isMinifyEnabled = false
            
            // ProGuard 配置文件
            proguardFiles(
                // 使用默认的优化规则
                getDefaultProguardFile("proguard-android-optimize.txt"),
                // 项目自定义规则
                "proguard-rules.pro"
            )
        }
    }
    // Java 兼容性配置
    compileOptions {
        // 源代码 Java 版本
        sourceCompatibility = JavaVersion.VERSION_11
        // 目标字节码 Java 版本
        targetCompatibility = JavaVersion.VERSION_11
    }

    // Jetpack Compose 功能开关
    buildFeatures {
        // 启用 Compose UI 支持
        compose = true
    }

    // Lint 静态检查配置
    lint {
        // 禁用的检查列表
        disable += listOf(
            // FullBackupContent: 我们已经配置了 backup_rules.xml，不需要额外检查
            "FullBackupContent"
            // NetworkSecurityConfig 保持启用，以检查网络安全问题
        )
    }
}

// ============================================================================
// APK 文件命名配置
// ============================================================================
// 使用现代 Gradle API (androidComponents) 为每个构建变体自定义 APK 文件名
// 格式：AMLL-DroidMate-Alpha-时间戳.apk
androidComponents {
    onVariants { variant ->
        variant.outputs.forEach { output ->
            // 重命名 APK 文件（仅适用于 VariantOutputImpl 类型）
            (output as? com.android.build.api.variant.impl.VariantOutputImpl)?.outputFileName?.set(
                "AMLL-DroidMate-Alpha-${buildTimestampProvider.get()}.apk" //版本号
            )
        }
    }
}

// ============================================================================
// 依赖管理配置
// ============================================================================
dependencies {
    // ==================== Compose BOM (Bill of Materials) ====================
    // 使用 BOM 统一管理所有 Compose 相关库的版本，避免版本冲突
    implementation(platform("androidx.compose:compose-bom:2026.03.00"))
    implementation("androidx.compose.material3:material3:1.4.0")
    androidTestImplementation(platform("androidx.compose:compose-bom:2026.03.00"))

    // ==================== AndroidX 核心库 ====================
    // Kotlin 扩展函数，提供更简洁的 API
    implementation("androidx.core:core-ktx:1.18.0")
    
    // 启动屏支持（Android 12+ 原生启动屏 API）
    // 1.2.0 是最新稳定版（1.1.0 从未发布正式版）
    implementation("androidx.core:core-splashscreen:1.2.0")
    
    // Lifecycle 运行时和 ViewModel（支持 Compose）
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")
    
    // Activity Compose 集成（使 Activity 支持 Compose）
    implementation("androidx.activity:activity-compose:1.13.0")
    
    // 媒体播放支持（用于获取音乐播放信息）
    implementation("androidx.media:media:1.7.1")
    
    // 调色板提取（从专辑封面提取颜色）
    implementation("androidx.palette:palette:1.0.0")

    // ==================== Media3 UI 组件 ====================
    // 提供 DefaultTimeBar 和其他播放器控制组件
    implementation("androidx.media3:media3-ui:1.0.0")
    
    // WebView 支持（用于 AMLL 歌词渲染）
    implementation("androidx.webkit:webkit:1.8.0")
    // ==================== Jetpack Compose UI ====================
    // 版本号由 BOM 统一管理，不需要单独指定
    // Compose UI 核心功能
    implementation("androidx.compose.ui:ui")
    // UI 图形绘制（Canvas、路径等）
    implementation("androidx.compose.ui:ui-graphics")
    // UI 工具预览（@Preview 注解支持）
    implementation("androidx.compose.ui:ui-tooling-preview")
    // Material Design 3 组件库
    implementation("androidx.compose.material3:material3")
    // Google Material 设计组件（非 Compose 版本）
    implementation("com.google.android.material:material:1.11.0")
    // Material 图标扩展库（更多图标选择）
    implementation("androidx.compose.material:material-icons-extended")
    
    // ==================== 图片加载 ====================
    // Coil: Kotlin 编写的图片加载库，支持 Compose
    implementation("io.coil-kt:coil-compose:2.7.0")

    // ==================== Ktor 网络客户端（3.x 版本） ====================
    // Ktor 核心库（HTTP 客户端）
    implementation("io.ktor:ktor-client-core:3.4.1")
    // OkHttp 引擎（Android 平台实现）
    implementation("io.ktor:ktor-client-okhttp:3.4.1")
    // 内容协商（JSON/XML 序列化）
    implementation("io.ktor:ktor-client-content-negotiation:3.4.1")
    // 序列化支持
    implementation("io.ktor:ktor-client-serialization:3.4.1")
    // Kotlinx JSON 序列化器
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.4.1")

    // ==================== JSON 序列化 ====================
    // Kotlinx Serialization: Kotlin 原生的 JSON 序列化库
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.10.0")

    // ==================== 中文简繁转换 ====================
    // OpenCC4J: 用于改进歌曲匹配（处理简体/繁体中文）
    implementation("com.github.houbb:opencc4j:1.6.0")

    // ==================== 协程（异步编程） ====================
    // Android 平台协程（包含主线程调度器）
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    // 协程核心库
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    // ==================== 日志框架 ====================
    // Timber: JakeWharton 开发的轻量级日志库
    implementation("com.jakewharton.timber:timber:5.0.1")

    // ==================== 数据库（Room ORM） ====================
    // Room 运行时库（SQLite 对象映射）
    implementation("androidx.room:room-runtime:2.8.4")
    // Room Kotlin 扩展（Flow、协程支持）
    implementation("androidx.room:room-ktx:2.8.4")

    // ==================== 测试库 ====================
    // JUnit4: Java/Kotlin 单元测试框架
    testImplementation("junit:junit:4.13.2")
    
    // 协程测试支持（TestDispatcher 等）
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    
    // Ktor Mock 引擎（单元测试用）
    testImplementation("io.ktor:ktor-client-mock:3.4.1")
    testImplementation("io.ktor:ktor-client-mock-jvm:3.4.1")
    
    // Android 仪器化测试（在模拟器/真机上运行）
    androidTestImplementation("io.ktor:ktor-client-mock:3.4.1")
    androidTestImplementation("io.ktor:ktor-client-mock-jvm:3.4.1")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    
    // Compose UI 测试框架
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    
    // 调试工具（Compose 预览和测试辅助）
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}