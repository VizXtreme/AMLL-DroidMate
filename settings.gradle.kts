// ============================================================================
// Gradle 项目级设置文件
// ============================================================================
// 
// **文件作用**：
// settings.gradle.kts 是 Gradle 项目的入口文件，负责配置整个项目的结构。
// 它在任何构建任务执行之前运行，用于初始化项目设置。
// 
// **主要功能**：
// 1. 配置插件仓库（从哪里下载 Gradle 插件）
// 2. 管理依赖解析策略（如何查找第三方库）
// 3. 定义项目名称和包含的模块
// ============================================================================
// ==================== 插件管理配置 ====================
// 定义 Gradle 插件的来源仓库
pluginManagement {
    repositories {
        // Google 仓库：Android 相关插件（如 Android Gradle Plugin）
        google()
        
        // Maven Central：通用的 Java/Kotlin 库
        mavenCentral()
        
        // Gradle Plugin Portal：Gradle 官方插件市场
        gradlePluginPortal()
    }
}

// ==================== 依赖解析管理 ====================
// 控制项目中所有依赖的下载来源
dependencyResolutionManagement {
    // 依赖解析模式：FAIL_ON_PROJECT_REPOS
    // 含义：如果项目在子模块中定义了额外的仓库，则构建失败
    // 目的：强制统一依赖来源，避免版本冲突和安全问题
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    
    repositories {
        // 阿里云镜像（加速国内访问）
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        
        // Google 仓库：AndroidX、Material Design 等官方库
        google()
        
        // Maven Central：主流的开源 Java/Kotlin 库
        mavenCentral()
        
        // JitPack：GitHub 项目的 CDN（用于获取不在 Maven 中央仓库的库）
        // 示例：com.github.User:Repo:Version
        maven { url = uri("https://jitpack.io") }
    }
}

// ==================== 项目基本信息 ====================
// 根项目名称（用于 IDE 显示和构建输出）
rootProject.name = "DroidMate"

// 包含的子模块
// ":app" 表示包含 app 目录下的模块（主要的 Android 应用模块）
include(":app")
