plugins {
    `java-library`
}

dependencies {
    annotationProcessor("org.springframework.boot", "spring-boot-autoconfigure-processor")
    annotationProcessor("org.springframework.boot", "spring-boot-configuration-processor")

    implementation(project(":nixer-plugin-core"))

    val guavaVersion: String by rootProject.extra
    implementation("com.google.guava", "guava", guavaVersion)

    testImplementation("org.springframework", "spring-test")
}
