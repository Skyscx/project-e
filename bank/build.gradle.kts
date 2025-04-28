plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.ksp)
}

group = "me.skyscx"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
}

dependencies {
    compileOnlyApi(libs.paper)

    api(project(":annotation"))
    implementation(project(":protocol"))

    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.reactive)
    api(libs.humanize)
    api(libs.kotlinx.coroutines.jdk8)
    api(libs.dagger)
    ksp(libs.dagger.compiler)
    api(libs.jackson.module.kotlin)
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<com.google.devtools.ksp.gradle.KspTask> {

    doFirst {
        project.delete(project.layout.buildDirectory)
        project.layout.buildDirectory.get().asFile.mkdirs()
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
    withSourcesJar()
}

tasks {
    withType<JavaCompile>().configureEach { options.encoding = "UTF-8" }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
}
