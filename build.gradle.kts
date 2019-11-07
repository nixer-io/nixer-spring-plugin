plugins {
    java
}

defaultTasks("build")

allprojects {
    group = "io.nixer"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    afterEvaluate {
        project.apply(plugin="maven-publish")
        project.apply(plugin="signing")
        tasks.test {
            useJUnitPlatform()
            testLogging {
                events("passed", "skipped", "failed")
            }
        }

        dependencies {
            testImplementation("org.mockito", "mockito-core", "2.21.0")
            testImplementation("org.mockito", "mockito-junit-jupiter", "2.23.0")
            testImplementation("org.assertj:assertj-core:3.11.1")
            testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.3.2")
            testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine","5.3.2")
        }

        tasks.register<Jar>("sourcesJar") {
            archiveClassifier.set("sources")
            from(sourceSets.main.get().allJava)
        }

        tasks.register<Jar>("javadocJar") {
            archiveClassifier.set("javadoc")
            from(tasks.javadoc.get().destinationDir)
        }

        configure<PublishingExtension> {
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
        }

        configure<SigningExtension> {
            configure<PublishingExtension> {
                sign(this.publications["nixer-spring-plugin"])
            }
        }

        configure<JavaPluginConvention> {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }

    }
}
