import org.gradle.jvm.tasks.Jar

group = rootProject.group
version = rootProject.version

plugins {
    java
    `kotlin-dsl`
    `maven-publish`
    jacoco
    kotlin("jvm") version "1.3.50"
    id("com.gradle.plugin-publish") version "0.10.0"
    id("io.gitlab.arturbosch.detekt") version "1.0.1"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

jacoco {
    toolVersion = "0.8.4"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation(gradleApi())

    implementation("commons-cli:commons-cli:1.4")
    implementation("com.google.guava:guava:27.0-jre")
    implementation("io.swagger:swagger-codegen:2.3.1")

    testImplementation("junit:junit:4.12")
}

tasks.register<Jar>("sourcesJar") {
    from(sourceSets.main.get().allJava)
    classifier = "sources"
}

// Configuration Block for the Plugin Marker artifact on Plugin Central
pluginBundle {
    website = "https://github.com/Yelp/swagger-gradle-codegen"
    vcsUrl = "https://github.com/Yelp/swagger-gradle-codegen"
    description = "A Gradle plugin to generate networking code from Swagger Specs"
    tags = listOf("swagger", "codegen", "retrofit", "android", "kotlin", "networking")

    plugins {
        getByName("com.yelp.codegen.plugin") {
            displayName = "Swagger Gradle Codegen"
        }
    }
}

detekt {
    toolVersion = "1.0.1"
    input = files("src/")
    config = files("./detekt-config.yml")
    buildUponDefaultConfig = true
    filters = ".*/resources/.*,.*/build/.*"
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
        csv.isEnabled = false
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestReport)
}

publishing {
    publications {
        create<MavenPublication>("Internal") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            credentials {
                username = project.findProperty("maven.publish.repo.user") as String
                password = project.findProperty("maven.publish.repo.pw") as String
            }
            val releasesRepoUrl = project.findProperty("maven.publish.repo.release") as String
            val snapshotsRepoUrl = project.findProperty("maven.publish.repo.snapshot") as String
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
            name = project.findProperty("maven.publish.repo.name") as String
        }
    }
}

tasks.register("releaseArtifact") {
    group = "publishing"
    description = "Publishes to configured repo in properties"
    val name = project.findProperty("maven.publish.repo.name") as String?
    setDependsOn(listOf("publishInternalPublicationTo${name?.capitalize()}Repository"))
}
