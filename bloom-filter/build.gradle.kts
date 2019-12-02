plugins {
    java
}

val jacksonVersion: String = "2.7.8"

dependencies {
    // TODO externalize versions
    // TODO consider making dependencies of this subproject managed by Spring Boot BOM
    //  as it dependency of other subproject which is already BOM-managed anyway

    val guavaVersion: String by rootProject.extra
    implementation("com.google.guava", "guava", guavaVersion)
    implementation("com.fasterxml.jackson.core:jackson-annotations:${jacksonVersion}")
    implementation("com.fasterxml.jackson.core:jackson-core:${jacksonVersion}")
    implementation("com.fasterxml.jackson.core:jackson-databind:${jacksonVersion}")

    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.5.2")
    testImplementation("org.junit.jupiter", "junit-jupiter-params", "5.5.2")
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", "5.5.2")

    testImplementation("org.assertj:assertj-core:3.13.2")
}
