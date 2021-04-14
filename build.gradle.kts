import org.jetbrains.kotlin.gradle.tasks.FatFrameworkTask
import org.jetbrains.kotlin.gradle.plugin.cocoapods.CocoapodsExtension

val ideaActive = System.getProperty("idea.active") == "true"

plugins {
    val kotlinVersion = "1.4.32"

    kotlin("multiplatform") version kotlinVersion
    kotlin("native.cocoapods") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("maven-publish")
}

val ktorVersion = "1.4.32"
val artifactId = "SharedAnalytics"

val userName = ""
val githubToken = ""
val githubRepo = "https://github.com/aleksiosdev/SharedAnalyticsExample"

val author = "Lavrinenko Aleksandr"
group = "org.manychat.analytics"


repositories {
    mavenCentral()
    google()
    jcenter()
}

publishing {
    repositories {
        maven {
            name = artifactId
            url = uri("https://maven.pkg.github.com/aleksiosdev/SharedAnalyticsExample")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                        ?: userName
                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
                        ?: githubToken
            }
        }
    }
}

kotlin {
    cocoapods {
        summary = "Shared module for Android and iOS"
        homepage = "Link to a Kotlin/Native module homepage"

        frameworkName = artifactId
    }

    val iosArm64 = iosArm64("iosArm64")
    val iosX64 = iosX64("iosX64")
    if (ideaActive) {
        iosX64("ios")
    }

    project.configure(listOf(iosArm64, iosX64)) {
        compilations {
            val main by getting {
                kotlinOptions.freeCompilerArgs = listOf("-Xobjc-generics")
            }
        }
    }

    jvm("android") {
        mavenPublication {
            artifactId = artifactId
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-common")
            }
        }

        commonTest {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-common")
                implementation("org.jetbrains.kotlin:kotlin-test-junit5:1.3.31")
                implementation("org.jetbrains.kotlin:kotlin-test:1.3.31")
            }
        }

        sourceSets {
            val iosMain = if (ideaActive) getByName("iosMain") else create("iosMain")
            iosMain.dependsOn(commonMain.get())
            iosMain.dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib")
            }
            val iosArm64Main by getting { }
            val iosX64Main by getting { }
            configure(listOf(iosArm64Main, iosX64Main)) {
                dependsOn(iosMain)
            }
        }

        val androidMain by getting {
            dependsOn(commonMain.get())
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib")
            }
        }

        val androidTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-common")
                implementation("org.jetbrains.kotlin:kotlin-test")
                implementation("org.jetbrains.kotlin:kotlin-test-junit")
            }
        }
    }

    var releaseFatFrameworkTaskName: String = "releaseFatFramework"
    var cocoapodsFrameworkDirName: String = "cocoapods"

    project.tasks.register<FatFrameworkTask>(releaseFatFrameworkTaskName) {
        baseName = artifactId
        group = "Create Universal framework"
        destinationDir = buildDir.resolve(cocoapodsFrameworkDirName)
        from(iosArm64.binaries.getFramework("RELEASE"), iosX64.binaries.getFramework("RELEASE"))

    }

    project.tasks.register<PodspecTask>("createPodSpec") {
        group = "Generate podspec"
        settings.frameworkName = artifactId
        settings.authors = author
        settings.homepage = "manychat.com"
        settings.summary = "Framework with events and send analytics logic"
        extendedSettings.githubRepo = githubRepo
        extendedSettings.prepeareCommandTask = releaseFatFrameworkTaskName
        cocoapodsBuildDirName = cocoapodsFrameworkDirName
    }
}

open class PodspecTask : DefaultTask() {

    private val specName = project.name.replace('-', '_')
    private val frameworkNameProvider: Provider<String> = project.provider { settings.frameworkName }

    @OutputFile
    val outputFile: File = project.projectDir.resolve("$specName.podspec")

    @Input
    var cocoapodsBuildDirName: String = "cocoapods"

    @get:Nested
    internal var settings: CocoapodsExtension = CocoapodsExtension(project)

    @get:Nested
    internal var extendedSettings: ExtendedCocoapodsSetting = ExtendedCocoapodsSetting(project)

    @TaskAction
    fun generate() {
        val frameworkDir = project.buildDir.resolve(cocoapodsBuildDirName).relativeTo(outputFile.parentFile).path
        val dependencies = settings.pods.map { pod ->
            val versionSuffix = if (pod.version != null) ", '${pod.version}'" else ""
            "|    spec.dependency '${pod.name}'$versionSuffix"
        }.joinToString(separator = "\n")

        outputFile.writeText(
            """
            |Pod::Spec.new do |spec|
            |    spec.name                     = '$specName'
            |    spec.version                  = '${settings.version}'
            |    spec.homepage                 = '${settings.homepage.orEmpty()}'
            |    spec.source                   = { :git => '${extendedSettings.githubRepo}', :tag => "v#{spec.version}" }
            |    spec.authors                  = '${settings.authors.orEmpty()}'
            |    spec.license                  = '${settings.license.orEmpty()}'
            |    spec.summary                  = '${settings.summary.orEmpty()}'
            |
            |    spec.static_framework         = true
            |    spec.vendored_frameworks      = "$frameworkDir/${frameworkNameProvider.get()}.framework"
            |    spec.libraries                = "c++"
            |    spec.preserve_paths           = "**/*.*"
            |    spec.module_name              = "#{spec.name}_umbrella"
            |
            $dependencies
            |
            |    spec.pod_target_xcconfig = {
            |        'KOTLIN_TARGET[sdk=iphonesimulator*]' => 'ios_x64',
            |        'KOTLIN_TARGET[sdk=iphoneos*]' => 'ios_arm',
            |    }
            |
            |    spec.prepare_command = <<-SCRIPT
            |       set -ev
            |       ./gradlew --no-daemon '${extendedSettings.prepeareCommandTask}' --stacktrace --info
            |    SCRIPT
            |end
        """.trimMargin()
        )

        logger.quiet(
            """
            Generated a podspec file at: ${outputFile.absolutePath}.
            To include it in your Xcode project:
                - locally add the following dependency snippet in your Podfile:
                
                    pod '$specName', :path => '${outputFile.parentFile.absolutePath}'
                
                - remote push changes to repository, create PR to dev. After review release branch will be created.
                  After add the following dependency snippet in your Podfile:
                  pod '$specName', :git => '${extendedSettings.githubRepo}', :tag => "v#{spec.version}" 
            """.trimIndent()
        )
    }
}

class ExtendedCocoapodsSetting(private val project: Project) {
    @Optional
    @Input
    var githubRepo: String? = null

    @Input
    var prepeareCommandTask: String = "releaseFatFramework"
}