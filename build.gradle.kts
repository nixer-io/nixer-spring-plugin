plugins {
    java
}

defaultTasks("build")

allprojects {
    group = "io.nixer"
    version = "0.1.0.1-SNAPSHOT"

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
                create<MavenPublication>("nixerSpringPlugin") {
                    from(components["java"])

                    artifact(tasks["sourcesJar"])
                    artifact(tasks["javadocJar"])

                    pom {
                        name.set("nixer-spring-plugin")
                        description.set("Nixer plugin for Spring framework")
                        url.set("https://github.com/nixer-io/nixer-spring-plugin")

                        licenses {
                            license {
                                name.set("The Apache License, Version 2.0")
                                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                            }
                        }
                        developers {
                            developer {
                                id.set("j-bron")
                                name.set("Jan Broniowski")
                                email.set("jan@broniow.ski")
                            }
                            developer {
                                id.set("gcwiak")
                                name.set("Grzegorz Ćwiak")
                                email.set("grzegorz.cwiak@crosswordcybersecurity.com ")
                            }
                            developer {
                                id.set("smifun")
                                name.set("Kamil Wójcik")
                                email.set("kamil.wojcik@crosswordcybersecurity.com ")
                            }
                        }
                        scm {
                            connection.set("scm:git:git://github.com:nixer-io/nixer-spring-plugin.git")
                            developerConnection.set("scm:git:git@github.com:nixer-io/nixer-spring-plugin.git")
                            url.set("https://github.com/nixer-io/nixer-spring-plugin")
                        }
                    }
                }
            }

            repositories {
                maven {
                    credentials {
                        val ossrhUsername: String by project
                        val ossrhPassword: String by project

                        username = ossrhUsername
                        password = ossrhPassword
                    }

                    val releasesRepoUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2"
                    val snapshotsRepoUrl = "https://oss.sonatype.org/content/repositories/snapshots"
                    url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
                }
            }
        }

        configure<SigningExtension> {
            configure<PublishingExtension> {
                sign(this.publications["nixerSpringPlugin"])
            }
        }

        configure<JavaPluginConvention> {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }

    }
}
