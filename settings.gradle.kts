pluginManagement {
	repositories {
		gradlePluginPortal()
		mavenCentral()
		google()
	}
}
plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

dependencyResolutionManagement {
	repositories {
		mavenCentral()
		google()
		maven { url = uri("https://jitpack.io") }
		maven("https://repo.papermc.io/repository/maven-public/") {
			name = "papermc-repo"
		}
		maven("https://oss.sonatype.org/content/groups/public/") {
			name = "sonatype"
		}
	}
}

rootProject.name = "project-e"

include(":annotation")
include(":bank")
include(":protocol")
include(":processor")
include(":api")
