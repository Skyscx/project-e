plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.shadow)
}

group = "me.skyscx"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlinx.coroutines.jdk8)
}
