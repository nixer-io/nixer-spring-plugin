import groovy.lang.Closure
import org.gradle.util.ConfigureUtil

val optional: Closure<*> by extra

fun <T : Any> Closure<*>.toAction(): Action<T> =
        ConfigureUtil.configureUsing(this)

plugins {
    `java-library`
    id("nebula.optional-base") version "5.0.3"
}

dependencies {
    annotationProcessor("org.springframework.boot", "spring-boot-autoconfigure-processor")
    annotationProcessor("org.springframework.boot", "spring-boot-configuration-processor")

    val guavaVersion: String by rootProject.extra
    implementation("com.google.guava", "guava", guavaVersion) // consider removing (cache, immutable collections)

    api("com.nimbusds", "nimbus-jose-jwt", "7.5.1") // required for stigma tokens optional
    api("javax.servlet:javax.servlet-api")
    api("com.fasterxml.jackson.core", "jackson-annotations") // for captcha api
    api("com.fasterxml.jackson.core", "jackson-databind") // for captcha api

    api("org.springframework.boot", "spring-boot-autoconfigure")
    api("org.springframework.boot", "spring-boot-actuator", dependencyConfiguration = optional.toAction()) // optional

    api("org.springframework", "spring-web")

    api("org.springframework", "spring-jdbc") // optional, required for stigma jdbc storage

    api("org.springframework.security", "spring-security-web")
    api("org.springframework.security", "spring-security-config")

    api("io.micrometer:micrometer-core")
    api("io.searchbox:jest", dependencyConfiguration = optional.toAction())

    implementation("com.fasterxml.jackson.datatype", "jackson-datatype-jsr310", dependencyConfiguration = optional.toAction())

    testImplementation("org.springframework", "spring-test")
    testImplementation("org.springframework.boot", "spring-boot-starter-web")
    testImplementation("org.springframework.boot", "spring-boot-starter-test") {
        exclude(module = "junit")
    }
    testImplementation("org.springframework.security", "spring-security-test")
}
