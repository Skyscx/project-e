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
    implementation(project(":annotation"))
    implementation(project(":bank"))

    //kspTest(project(":ksp"))


    implementation(libs.symbol.processing.api)
    implementation(libs.auto.service.annotations)
    implementation(libs.commons.lang)
    implementation(libs.kotlinpoet)
    implementation(libs.kotlinpoet.ksp)
    implementation(libs.dagger)
    implementation(libs.paper)
    ksp(libs.auto.service.ksp)
}

ksp {
    arg("autoserviceKsp.verify", "true")
    arg("autoserviceKsp.verbose", "true")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-opt-in=com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview"
}