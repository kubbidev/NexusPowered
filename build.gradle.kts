plugins {
    id("java")
    id("java-library")
    alias(libs.plugins.shadow)
}

// store the version as a variable,
// as we use it several times
val fullVersion = "1.0.0"

// project settings
group = "me.kubbidev.nexuspowered"
version = "1.0-SNAPSHOT"

base {
    archivesName.set("nexuspowered")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    // include source in when publishing
    withSourcesJar()
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")

    // optional dependencies
    compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")
}

// building task operations
tasks.processResources {
    filesMatching("plugin.yml") {
        expand("pluginVersion" to fullVersion)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.shadowJar {
    archiveFileName = "NexusPowered-${fullVersion}.jar"

    dependencies {
        include(dependency("me.kubbidev.nexuspowered:.*"))
    }

    manifest {
        attributes["paperweight-mappings-namespace"] = "mojang"
    }
}

artifacts {
    archives(tasks.shadowJar)
}