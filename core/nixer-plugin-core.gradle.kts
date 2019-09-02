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

    implementation("com.google.guava", "guava", "28.0-jre") // consider removing (cache, immutable collections)
    implementation("org.apache.httpcomponents", "httpclient", "4.5.9", dependencyConfiguration = optional.toAction()) // optional 
    api("com.nimbusds", "nimbus-jose-jwt", "7.5.1") // required for stigma tokens optional
    api("javax.servlet", "javax.servlet-api", "3.1.0")
    api("javax.validation", "validation-api") // for captcha validator
    api("com.fasterxml.jackson.core", "jackson-annotations") // for captcha api

    api("org.springframework.boot", "spring-boot-autoconfigure")
    api("org.springframework.boot", "spring-boot-actuator", dependencyConfiguration = optional.toAction()) // optional

    api("org.springframework", "spring-web")

    api("org.springframework", "spring-jdbc") // optional, required for stigma jdbc storage

    api("org.springframework.security", "spring-security-web")
    api("org.springframework.security", "spring-security-config")

    api("io.micrometer", "micrometer-core", "1.2.0", dependencyConfiguration = optional.toAction())

    testImplementation("org.springframework", "spring-test")
    testImplementation("org.springframework.boot", "spring-boot-starter-validation")
    testImplementation("org.springframework.boot", "spring-boot-starter-web")
    testImplementation("org.springframework.boot", "spring-boot-starter-test") {
        exclude(module = "junit")
    }
    testImplementation("org.springframework.security", "spring-security-test")
}