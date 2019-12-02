import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.50"
    application
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation(project(":bloom-filter"))

    implementation("com.github.ajalt:clikt:2.1.0") // CLI parsing, see: https://ajalt.github.io/clikt/
    val guavaVersion: String by rootProject.extra
    implementation("com.google.guava", "guava", guavaVersion)

    testImplementation("junit:junit:4.12")
    testImplementation("pl.pragmatists:JUnitParams:1.0.6")
    testImplementation("com.github.stefanbirkner:system-rules:1.19.0")

    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", "5.5.2")
    testRuntimeOnly("org.junit.vintage", "junit-vintage-engine", "5.5.2")

    testImplementation("org.assertj:assertj-core:3.13.2")

    testImplementation("org.mockito", "mockito-core", "3.1.0")
    testImplementation("org.mockito", "mockito-junit-jupiter", "3.1.0")
}

application {
    mainClassName = "io.nixer.bloom.cli.BloomToolMainKt"
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
