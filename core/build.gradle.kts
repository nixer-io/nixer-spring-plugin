plugins {
    //    `java-library`
    java
    id("io.spring.dependency-management") version "1.0.6.RELEASE"
}


dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:2.1.6.RELEASE")
    }
}
dependencies {

    implementation("com.google.guava", "guava", "28.0-jre")
    implementation("org.apache.httpcomponents", "httpclient", "4.5.9")
    implementation("com.nimbusds", "nimbus-jose-jwt", "7.5.1")
    implementation("javax.servlet", "javax.servlet-api", "3.1.0")

    implementation("javax.validation", "validation-api")
    implementation("com.fasterxml.jackson.core", "jackson-annotations")
    implementation("org.springframework.boot", "spring-boot-autoconfigure")
    implementation("org.springframework.boot", "spring-boot-actuator")
    annotationProcessor("org.springframework.boot", "spring-boot-autoconfigure-processor")
    annotationProcessor("org.springframework.boot", "spring-boot-configuration-processor")
    implementation("org.springframework", "spring-web")
    implementation("org.springframework.security", "spring-security-web")
    implementation("org.springframework.security", "spring-security-config")
    implementation("org.springframework", "spring-jdbc")
    implementation("io.micrometer", "micrometer-core", "1.2.0")

    testCompile("org.junit.jupiter", "junit-jupiter-engine", "5.3.2")
    testCompile("org.mockito", "mockito-core", "2.21.0")
    testCompile("org.mockito", "mockito-junit-jupiter", "2.23.0")

    testImplementation("org.springframework", "spring-test")
    testImplementation("org.springframework.boot", "spring-boot-starter-validation")
    testImplementation("org.springframework.boot", "spring-boot-starter-web")
    testImplementation("org.springframework.boot", "spring-boot-starter-test") {
        exclude(module = "junit")
    }
    testImplementation("org.springframework.security", "spring-security-test")
}
