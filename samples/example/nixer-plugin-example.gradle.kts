import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    id("java")
//    id("com.gradle.build-scan") version "2.0.2"
    id("org.springframework.boot") version "2.0.5.RELEASE"
    id("io.spring.dependency-management") version "1.0.6.RELEASE"
}


sourceSets {
    create("integration-test") {
        java {
            compileClasspath += sourceSets.main.get().output
            runtimeClasspath += sourceSets.main.get().output

            srcDir("src/integration-test/java")
        }
    }
}

val integrationTestImplementation by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}

configurations["integrationTestRuntimeOnly"].extendsFrom(configurations.testRuntimeOnly.get())

val integrationTest = task<Test>("integrationTest") {
    description = "Runs integration tests."
    group = "verification"

    testClassesDirs = sourceSets["integration-test"].output.classesDirs
    classpath = sourceSets["integration-test"].runtimeClasspath
    shouldRunAfter("test")

    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.check { dependsOn(integrationTest) }

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:2.1.6.RELEASE")
    }
}

dependencies {
    implementation(project(":nixer-plugin-core"))
    compile("org.springframework.boot", "spring-boot")
    implementation("org.springframework.boot", "spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot", "spring-boot-starter-actuator")
    implementation("org.springframework.boot", "spring-boot-starter-security")
    implementation("org.springframework.boot", "spring-boot-starter-web")

    implementation("io.micrometer", "micrometer-registry-influx", "1.2.0")
    runtimeOnly("com.h2database", "h2")

    testImplementation("org.springframework", "spring-test")
    testImplementation("org.springframework.security", "spring-security-test")
    testImplementation("org.springframework.boot", "spring-boot-starter-test") {
        exclude(module = "junit")
    }
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", "5.3.2")
}

tasks.getByName<BootJar>("bootJar") {
    mainClassName = "eu.xword.nixer.nixerplugin.example.NixerPluginApplication"
}

tasks.getByName<BootRun>("bootRun") {
    main = "eu.xword.nixer.nixerplugin.example.NixerPluginApplication"
}
