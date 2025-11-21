plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    kotlin("plugin.serialization") version "1.9.23"
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

dependencies {
    implementation(libs.google.api.services.sheets)
    implementation(libs.google.api.client)
    implementation(libs.google.oauth.client)
    implementation(libs.google.http.client.gson)

    implementation(libs.okhttp)
    implementation(libs.kotlinx.serialization.json)
}