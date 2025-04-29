plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.ksp)
//    alias(libs.plugins.kapt)
    alias(libs.plugins.shadow)
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

    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":api"))
    ksp(libs.dagger.compiler)

    ksp(project(":processor"))
}

tasks {
    jar { enabled = false }
    build { dependsOn(shadowJar) }
    shadowJar { archiveFileName.set("bank.jar") }
}

tasks.withType<com.google.devtools.ksp.gradle.KspTask> {

    doFirst {
        project.delete(project.layout.buildDirectory)
        project.layout.buildDirectory.get().asFile.mkdirs()
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    withSourcesJar()
}

tasks {
    withType<JavaCompile>().configureEach { options.encoding = "UTF-8" }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "21"
        }
    }
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
}
