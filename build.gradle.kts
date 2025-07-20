import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("java")
    alias(libs.plugins.shadow)
    id("maven-publish")
}

group = "me.kubbidev"
version = "2.0.1"

base {
    archivesName.set("nexuspowered")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    withSourcesJar()
}

repositories {
    mavenCentral()
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.7-R0.1-SNAPSHOT")

    // https://mvnrepository.com/artifact/org.jetbrains/annotations
    compileOnly("org.jetbrains:annotations:26.0.2")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.3.0")

    // Unit tests
    testImplementation("org.testcontainers:junit-jupiter:1.20.4")
    testImplementation("org.mockito:mockito-core:5.14.2")
    testImplementation("org.mockito:mockito-junit-jupiter:5.14.2")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.4")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.11.4")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.11.4")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "nexuspowered"

            from(components["java"])
            pom {
                name = "NexusPowered"
                description = "A Minecraft plugin utility to reduce boilerplate code in Bukkit plugins."
                url = "https://github.com/kubbidev/NexusPowered"

                licenses {
                    license {
                        name = "CC BY-NC-SA 4.0"
                        url = "https://creativecommons.org/licenses/by-nc-sa/4.0/"
                    }
                }

                developers {
                    developer {
                        id = "kubbidev"
                        name = "kubbi"
                        url = "https://kubbidev.me"
                    }
                }

                issueManagement {
                    system = "GitHub"
                    url = "https://github.com/kubbidev/NexusPowered/issues"
                }
            }
        }
    }
    repositories {
        maven(url = "https://nexus.kubbidev.me/repository/maven-releases/") {
            name = "kubbidev-releases"
            credentials(PasswordCredentials::class) {
                username = System.getenv("GRADLE_KUBBIDEV_RELEASES_USER")
                    ?: property("kubbidev-releases-user") as String?

                password = System.getenv("GRADLE_KUBBIDEV_RELEASES_PASS")
                    ?: property("kubbidev-releases-pass") as String?
            }
        }
    }
}

configurations {
    named("testImplementation") {
        extendsFrom(configurations.getByName("compileOnly"))
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand("pluginVersion" to "$version")
    }
}

tasks.shadowJar {
    archiveFileName = "NexusPowered-$version.jar"
    mergeServiceFiles()
    dependencies {
        include(dependency("me.kubbidev:.*"))
    }

    manifest {
        attributes["paperweight-mappings-namespace"] = "mojang"
    }
}

tasks.publish {
    dependsOn(tasks.shadowJar)
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Test>().configureEach {
    testLogging {
        events = setOf(TestLogEvent.PASSED, TestLogEvent.FAILED, TestLogEvent.SKIPPED)
    }
}

artifacts {
    archives(tasks.shadowJar)
}