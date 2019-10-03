import groovy.lang.Closure
import org.gradle.util.ConfigureUtil

val optional: Closure<*> by extra

fun <T : Any> Closure<*>.toAction(): Action<T> =
        ConfigureUtil.configureUsing(this)

plugins {
    `java-library`
    `maven-publish`
    signing
    id("io.spring.dependency-management") version "1.0.6.RELEASE"
    id("nebula.optional-base") version "5.0.3"
}

tasks.named("compileJava") {
    dependsOn("processResources")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:2.1.6.RELEASE")
    }
}
dependencies {
    annotationProcessor("org.springframework.boot", "spring-boot-autoconfigure-processor")
    annotationProcessor("org.springframework.boot", "spring-boot-configuration-processor")

    implementation(project(":nixer-plugin-core"))

    implementation("com.google.guava", "guava", "28.0-jre") // consider removing (cache, immutable collections)
    implementation("javax.validation", "validation-api")
    implementation("javax.servlet", "javax.servlet-api", "3.1.0")
    implementation("com.fasterxml.jackson.core", "jackson-annotations") // for captcha api
    implementation("com.fasterxml.jackson.core", "jackson-databind") // for captcha api

    implementation("org.springframework.boot", "spring-boot-autoconfigure")
    implementation("org.springframework.boot", "spring-boot-actuator", dependencyConfiguration = optional.toAction()) // optional

    implementation("org.springframework", "spring-web")

    implementation("org.springframework.security", "spring-security-web")
    implementation("org.springframework.security", "spring-security-config")
    implementation("org.apache.httpcomponents", "httpclient", "4.5.9")

    implementation("org.springframework.boot", "spring-boot-starter-validation")

    implementation("io.micrometer", "micrometer-core", "1.2.0", dependencyConfiguration = optional.toAction())

    testImplementation("org.springframework", "spring-test")
    testImplementation("org.springframework.boot", "spring-boot-starter-web")
    testImplementation("org.springframework.boot", "spring-boot-starter-test") {
        exclude(module = "junit")
    }
    testImplementation("org.springframework.security", "spring-security-test")
}
