import groovy.lang.Closure
import org.gradle.util.ConfigureUtil

val optional: Closure<*> by extra

fun <T : Any> Closure<*>.toAction(): Action<T> =
        ConfigureUtil.configureUsing(this)

plugins {
    //    `java-library`
    java
    id("io.spring.dependency-management") version "1.0.6.RELEASE"
    id("nebula.optional-base") version "5.0.3"
    `maven-publish`
    signing
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

    implementation("com.google.guava", "guava", "28.0-jre") // remove (cache, immutable collections)
    implementation("org.apache.httpcomponents", "httpclient", "4.5.9", dependencyConfiguration = optional.toAction()) // optional 
    implementation("com.nimbusds", "nimbus-jose-jwt", "7.5.1") // required for stigma tokens optional
    implementation("javax.servlet", "javax.servlet-api", "3.1.0")
    implementation("javax.validation", "validation-api")
    implementation("com.fasterxml.jackson.core", "jackson-annotations")

    implementation("org.springframework.boot", "spring-boot-autoconfigure")
    implementation("org.springframework.boot", "spring-boot-actuator", dependencyConfiguration = optional.toAction()) // optional

    implementation("org.springframework", "spring-web")

    implementation("org.springframework", "spring-jdbc") // optional, required for stigma jdbc storage

    implementation("org.springframework.security", "spring-security-web")
    implementation("org.springframework.security", "spring-security-config")

    implementation("io.micrometer", "micrometer-core", "1.2.0", dependencyConfiguration = optional.toAction())

    testImplementation("org.springframework", "spring-test")
    testImplementation("org.springframework.boot", "spring-boot-starter-validation")
    testImplementation("org.springframework.boot", "spring-boot-starter-web")
    testImplementation("org.springframework.boot", "spring-boot-starter-test") {
        exclude(module = "junit")
    }
    testImplementation("org.springframework.security", "spring-security-test")
}

tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allJava)
}

tasks.register<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    from(tasks.javadoc.get().destinationDir)
}


publishing {

    publications {
        create<MavenPublication>("nixer-spring-plugin") {
            from(components["java"])

            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
        }
    }

    repositories {

        maven {
            name = "myRepo"
            url = uri("file://${buildDir}/repo")
        }
    }
//    repositories {
//        mavenLocal()
//    }
}

signing {
    sign(publishing.publications["nixer-spring-plugin"])
}