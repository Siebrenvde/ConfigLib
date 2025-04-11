plugins {
    id("java-library")
    id("maven-publish")
}

group = "dev.siebrenvde"
version = "0.3.0"

repositories {
    mavenCentral()
    maven("https://maven.quiltmc.org/repository/release/") {
        name = "quilt-repo"
    }
}

var quiltVersion: String = "1.3.2"

dependencies {
    compileOnlyApi("org.jspecify:jspecify:1.0.0")
    api("org.quiltmc:quilt-config:$quiltVersion")
    implementation("org.quiltmc.quilt-config.serializers:toml:$quiltVersion")
    implementation("org.quiltmc.quilt-config.serializers:json5:$quiltVersion")
    implementation("org.quiltmc.parsers:json:0.3.1")
}

java {
    withSourcesJar()
    withJavadocJar()
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
