plugins {
    java
}

val jacksonVersion: String = "2.7.8"

dependencies {
    // TODO externalize versions
    val guavaVersion: String by rootProject.extra
    implementation("com.google.guava", "guava", guavaVersion)
    implementation("com.fasterxml.jackson.core:jackson-annotations:${jacksonVersion}")
    implementation("com.fasterxml.jackson.core:jackson-core:${jacksonVersion}")
    implementation("com.fasterxml.jackson.core:jackson-databind:${jacksonVersion}")

    testImplementation("junit:junit:4.12")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.3.2")
    testRuntimeOnly("org.junit.vintage", "junit-vintage-engine","5.3.2")
}
