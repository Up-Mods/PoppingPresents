pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.neoforged.net") {
            name = "NeoForge"
        }
        exclusiveContent {
            forRepository {
                maven("https://maven.fabricmc.net") {
                    name = "Fabric"
                }
            }
            filter {
                includeGroupAndSubgroups("net.fabricmc")
                includeGroup("fabric-loom")
            }
        }
    }

    includeBuild("build-logic")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

// This should match the mod ID so that the published coordinates can be sensible
rootProject.name = "PoppingPresents"
listOf("Common", "Fabric", "NeoForge").forEach {
    include(it)
    project(":$it").name = "${rootProject.name}-$it"
}

val env: Map<String, String?> = System.getenv()

buildCache {
    remote<HttpBuildCache> {
        url = uri("https://ci-cache.uuid.gg/cache")
        if (env["CI"] == "true" && env["GRADLE_BUILD_CACHE_USER"] != null && env["GRADLE_BUILD_CACHE_TOKEN"] != null) {
            isPush = true
            credentials {
                username = env["GRADLE_BUILD_CACHE_USER"]
                password = env["GRADLE_BUILD_CACHE_TOKEN"]
            }
        }
    }
}
