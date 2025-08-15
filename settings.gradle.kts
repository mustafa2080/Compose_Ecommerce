pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        // Alternative repositories for better connectivity
        maven { url = uri("https://jcenter.bintray.com") }
        maven { url = uri("https://maven.google.com") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
        // Alternative repositories for better connectivity
        maven { url = uri("https://jcenter.bintray.com") }
        maven { url = uri("https://maven.google.com") }
        gradlePluginPortal()
    }
}

rootProject.name = "ECommerceApp"
include(":app")
