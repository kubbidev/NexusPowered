plugins {
    id("java")
    id("java-library")
    alias(libs.plugins.shadow)
    id("maven-publish")
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

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "nexuspowered"

            from(components["java"])
            pom {
                name = "NexusPowered"
                description = "A Minecraft plugin utility to reduce boilerplate code in Bukkit plugins."
                url = "https://kubbidev.com"

                licenses {
                    license {
                        name = "Apache-2.0"
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                    }
                }

                developers {
                    developer {
                        id = "kubbidev"
                        name = "kubbi"
                        url = "https://kubbidev.com"
                        email = "kubbidev@gmail.com"
                    }
                }

                issueManagement {
                    system = "Github"
                    url = "https://github.com/kubbidev/NexusPowered/issues"
                }
            }
        }
    }
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