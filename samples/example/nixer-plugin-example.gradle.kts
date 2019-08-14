import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    id("java")
//    id("com.gradle.build-scan") version "2.0.2"
    id("org.springframework.boot") version "2.0.5.RELEASE"
    id("io.spring.dependency-management") version "1.0.6.RELEASE"
}

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
}

tasks.getByName<BootJar>("bootJar") {
    mainClassName = "eu.xword.nixer.nixerplugin.example.NixerPluginApplication"
}

tasks.getByName<BootRun>("bootRun") {
    main = "eu.xword.nixer.nixerplugin.example.NixerPluginApplication"
}