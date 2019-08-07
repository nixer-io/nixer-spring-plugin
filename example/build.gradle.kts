plugins {
    java
    id("io.spring.dependency-management") version "1.0.6.RELEASE"
}

apply(plugin = "io.spring.dependency-management")

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:2.1.6.RELEASE")
    }
}

dependencies {
    implementation(project(":core"))
    compile("org.springframework.boot", "spring-boot")
    implementation("org.springframework.boot", "spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot", "spring-boot-starter-actuator")
    implementation("org.springframework.boot", "spring-boot-starter-security")
    implementation("org.springframework.boot", "spring-boot-starter-web")

    implementation("io.micrometer", "micrometer-registry-influx", "1.2.0")
    runtimeOnly("com.h2database", "h2")
}
