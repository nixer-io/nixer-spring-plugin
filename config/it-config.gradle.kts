apply(plugin = "java")

configure<SourceSetContainer> {
    create("integration-test") {
        java {
            compileClasspath += project.the<SourceSetContainer>()["main"].output
            runtimeClasspath += project.the<SourceSetContainer>()["main"].output

            java.srcDir("src/integration-test/java")
        }
    }
}

val integrationTestImplementation by configurations.getting {
    extendsFrom(configurations["testImplementation"])
}

configurations["integrationTestRuntimeOnly"].extendsFrom(configurations["testRuntimeOnly"])

tasks.register<Test>("integrationTest") {
    description = "Runs integration tests."
    group = "verification"

    testClassesDirs = project.the<SourceSetContainer>()["integration-test"].output.classesDirs
    classpath = project.the<SourceSetContainer>()["integration-test"].runtimeClasspath
    shouldRunAfter("test")

    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.named("check") {
    dependsOn("integrationTest")
}
