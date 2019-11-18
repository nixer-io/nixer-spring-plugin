plugins {
    `java-library`
}

dependencies {
    annotationProcessor("org.springframework.boot", "spring-boot-autoconfigure-processor")
    annotationProcessor("org.springframework.boot", "spring-boot-configuration-processor")

    implementation(project(":nixer-plugin-core"))
    implementation(project(":bloom-filter"))

    implementation("org.springframework", "spring-web")
}
