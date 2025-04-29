@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    `java-library`
    `maven-publish`
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.shadow) apply false
    alias(libs.plugins.ksp) apply false
}

group = "me.skyscx"
version = "1.0"

allprojects {
    repositories {
        mavenCentral()
        mavenLocal()

        maven ("https://jitpack.io")
        maven ("https://oss.sonatype.org/content/groups/public/")
        maven ("https://papermc.io/repo/repository/maven-public/")
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
        withSourcesJar()
    }

    tasks {
        withType<JavaCompile>().configureEach { options.encoding = "UTF-8" }
        withType<KotlinCompile>().configureEach {
            kotlinOptions {
                jvmTarget = "21"
                freeCompilerArgs = listOf(
                    "-Xlambdas=indy",
                    "-Xno-param-assertions",
                    "-Xno-receiver-assertions",
                    "-Xno-call-assertions",
                    "-Xbackend-threads=0",
                    "-Xassertions=always-disable",
                    "-Xuse-fast-jar-file-system",
                    "-Xsam-conversions=indy"
                )
            }
        }
    }
}
