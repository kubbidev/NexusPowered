import org.gradle.api.tasks.testing.logging.TestLogEvent
import java.io.ByteArrayOutputStream

plugins {
    id("java")
    id("java-library")
    alias(libs.plugins.shadow)
    id("maven-publish")
}

group = "me.kubbidev"
version = "1.0-SNAPSHOT"

base {
    archivesName.set("nexuspowered")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")

    // optional dependencies
    compileOnly("org.jetbrains:annotations:26.0.2")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.3.0")

    // test
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.4")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.11.4")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.11.4")
    testImplementation("org.testcontainers:junit-jupiter:1.20.4")
    testImplementation("org.mockito:mockito-core:5.14.2")
    testImplementation("org.mockito:mockito-junit-jupiter:5.14.2")
}

fun determinePatchVersion(): Int {
    // get the name of the last tag
    val tagInfo = ByteArrayOutputStream()
    exec {
        commandLine("git", "describe", "--tags")
        standardOutput = tagInfo
    }
    val tagString = String(tagInfo.toByteArray())
    if (tagString.contains("-")) {
        return tagString.split("-")[1].toInt()
    }
    return 0
}

val majorVersion = "1"
val minorVersion = "0"
val patchVersion = determinePatchVersion()
val releaseVersion = "$majorVersion.$minorVersion"
val projectVersion = "$releaseVersion.$patchVersion"

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "nexuspowered"
            version = releaseVersion

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

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand("pluginVersion" to projectVersion)
    }
}

tasks.shadowJar {
    archiveFileName = "NexusPowered-${projectVersion}.jar"
    mergeServiceFiles()
    dependencies {
        include(dependency("me.kubbidev:.*"))
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