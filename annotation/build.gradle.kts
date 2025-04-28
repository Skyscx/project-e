plugins {
    alias(libs.plugins.kotlin)
}

group = "me.skyscx"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

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
    implementation(project(":protocol"))
    implementation(kotlin("stdlib"))
    compileOnly(libs.paper)
}
