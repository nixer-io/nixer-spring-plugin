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

    implementation(project(":nixer-plugin-core"))

    implementation("javax.validation", "validation-api")
    implementation("javax.servlet:javax.servlet-api")
    implementation("com.fasterxml.jackson.core", "jackson-annotations") // for captcha api
    implementation("com.fasterxml.jackson.core", "jackson-databind") // for captcha api

    implementation("org.springframework.boot", "spring-boot-autoconfigure")
    implementation("org.springframework.boot", "spring-boot-actuator", dependencyConfiguration = optional.toAction()) // optional

    implementation("org.springframework", "spring-web")

    implementation("org.springframework.security", "spring-security-web")
    implementation("org.springframework.security", "spring-security-config")
    implementation("org.apache.httpcomponents", "httpclient")

    implementation("org.springframework.boot", "spring-boot-starter-validation")

    testImplementation("org.springframework", "spring-test")
    testImplementation("org.springframework.boot", "spring-boot-starter-web")
    testImplementation("org.springframework.boot", "spring-boot-starter-test") {
        exclude(module = "junit")
    }
    testImplementation("org.springframework.security", "spring-security-test")
}
