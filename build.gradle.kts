plugins {
    id("java-library")
    id("com.gradleup.shadow") version "8.3.5"
    id("maven-publish")
}

group = "dev.siebrenvde"
version = "0.3.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.quiltmc.org/repository/release/") {
        name = "quilt-repo"
    }
}

var quiltVersion: String = "1.3.2"

dependencies {
    api("org.quiltmc:quilt-config:$quiltVersion")
    api("org.quiltmc.quilt-config.serializers:toml:$quiltVersion")
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks.shadowJar {
    archiveClassifier.set("")

    exclude("META-INF/**")
    exclude("LICENSE")
    exclude("org/jetbrains/**")
    exclude("org/intellij/**")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
        repositories.maven {
            val repo = if (version.toString().endsWith("-SNAPSHOT")) "snapshots" else "releases"
            url = uri("https://repo.siebrenvde.dev/${repo}/")
            name = "siebrenvde"
            credentials(PasswordCredentials::class)
        }
    }
}

val targetJavaVersion = 17
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    }
}
