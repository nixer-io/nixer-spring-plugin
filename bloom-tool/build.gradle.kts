import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.50"
    application
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation(project(":bloom-filter"))

    implementation("com.github.ajalt:clikt:2.1.0") // CLI parsing, see: https://ajalt.github.io/clikt/
    implementation("com.google.guava:guava:22.0")

    testImplementation("junit:junit:4.12")
    testImplementation("pl.pragmatists:JUnitParams:1.0.6")
    testImplementation("org.assertj:assertj-core:3.6.2")

    testRuntimeOnly("org.junit.vintage", "junit-vintage-engine", "5.3.2")
}

application {
    mainClassName = "eu.xword.nixer.bloom.BloomToolMainKt"
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "eu.xword.nixer.bloom.BloomToolMainKt"
    }
}
