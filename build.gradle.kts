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
    compileOnly("org.jetbrains:annotations:24.1.0")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")

    // tests
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
    testImplementation("org.testcontainers:junit-jupiter:1.19.8")
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
                url = "https://kubbidev.me"

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
                        email = "nicoladelaroche@gmail.com"
                        url = "https://kubbidev.me"
                    }
                }

                issueManagement {
                    system = "Gitlab"
                    url = "https://gitlab.com/kubbidev/nexuspowered/-/issues"
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
        include(dependency("me.kubbidev.nexuspowered:.*"))
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