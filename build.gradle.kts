import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.5"
    id("maven-publish")
}

group = "dev.siebrenvde"
version = "0.1.0"

repositories {
    mavenCentral()
    maven("https://maven.quiltmc.org/repository/release/") {
        name = "quilt-repo"
    }
}

configurations {
    create("shade")
    compileOnly.get().extendsFrom(configurations["shade"])
}
val shade = configurations.getByName("shade")

var quiltVersion: String = "1.3.2"

dependencies {
    shade("org.quiltmc:quilt-config:$quiltVersion")
    shade("org.quiltmc.quilt-config.serializers:toml:$quiltVersion")
}

java {
    withSourcesJar()
}

tasks.withType(ShadowJar::class) {
    archiveClassifier.set("")

    configurations = listOf(shade)

    exclude("META-INF/**")
    exclude("LICENSE")
    exclude("org/jetbrains/**")
    exclude("org/intellij/**")

    relocate("org.quiltmc.config", "dev.siebrenvde.configlib.libs.quilt.config")
    relocate("com.electronwill", "dev.siebrenvde.configlib.libs")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
