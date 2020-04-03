import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    id("java")
    id("org.springframework.boot") version "2.2.6.RELEASE"
}

apply(from = "../../config/it-config.gradle.kts")

dependencies {
    implementation(project(":nixer-plugin-core"))
    implementation(project(":nixer-plugin-captcha"))
    implementation(project(":nixer-plugin-pwned-check"))
    implementation(project(":nixer-plugin-stigma"))
    implementation("org.springframework.boot", "spring-boot")
    implementation("org.springframework.boot", "spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot", "spring-boot-starter-actuator")
    implementation("org.springframework.boot", "spring-boot-starter-security")
    implementation("org.springframework.boot", "spring-boot-starter-web")

    implementation("io.micrometer", "micrometer-registry-influx")
    runtimeOnly("com.h2database", "h2")

    implementation("com.fasterxml.jackson.datatype", "jackson-datatype-jsr310")

    testImplementation("org.springframework", "spring-test")
    testImplementation("org.springframework.integration", "spring-integration-test")
    testImplementation("org.springframework.security", "spring-security-test")
    testImplementation("org.springframework.boot", "spring-boot-starter-test") {
        exclude(module = "junit-vintage-engine")
        exclude(module = "junit")
    }
}

tasks.getByName<BootJar>("bootJar") {
    mainClassName = "io.nixer.example.NixerPluginApplication"
}

tasks.getByName<BootRun>("bootRun") {
    main = "io.nixer.example.NixerPluginApplication"
}
