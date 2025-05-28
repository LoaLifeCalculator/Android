import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "1.9.0"
}

android {
    namespace = "jun.watson"
    compileSdk = 35

    // local.properties에서 keystore 정보 읽어오기
    val keystoreProps = Properties().apply {
        val propsFile = rootProject.file("local.properties")
        if (propsFile.exists()) {
            load(FileInputStream(propsFile))
        }
    }

    // release용 signingConfigs 정의
    signingConfigs {
        create("release") {
            storeFile     = file(keystoreProps["KEYSTORE_PATH"]    as String)
            storePassword = keystoreProps["KEYSTORE_PASSWORD"]     as String
            keyAlias      = keystoreProps["KEY_ALIAS"]             as String
            keyPassword   = keystoreProps["KEY_PASSWORD"]          as String
        }
    }

    // 프로젝트 루트의 local.properties를 읽어오는 유틸
    val localProps = gradleLocalProperties(rootDir, providers)
    // 키가 없으면 "undefined"를 기본값으로 사용
    val searchUrl: String = localProps.getProperty("SEARCH_URL", "undefined")
    val resourceUrl: String = localProps.getProperty("RESOURCE_URL", "undefined")

    buildFeatures {
        // BuildConfig에 커스텀 필드를 넣을 수 있게 허용
        buildConfig = true

        // Compose 사용하고 계시니 그대로 두시면 됩니다
        compose = true
    }

    defaultConfig {
        applicationId = "jun.watson"
        minSdk = 24
        targetSdk = 35
        versionCode = 2
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "SEARCH_URL", "\"$searchUrl\"")
        buildConfigField("String", "RESOURCE_URL", "\"$resourceUrl\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // AndroidX & Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Ktor BOM for aligned versions
    implementation(platform(libs.ktor.bom))

    // Ktor client dependencies (all versions managed by the BOM)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    // Kotlinx Serialization
    implementation(libs.kotlinx.serialization.json)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
