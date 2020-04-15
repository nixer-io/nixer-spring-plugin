import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension

plugins {
    distribution
    java
    id("io.spring.dependency-management") version "1.0.8.RELEASE" apply false
}

defaultTasks("build")

allprojects {
    group = "io.nixer"
    version = "0.1.1.3"

    repositories {
        mavenCentral()
    }
}

configure(subprojects.filter { it.name.startsWith("nixer-plugin") }) {
    apply {
        plugin("java")
        plugin("io.spring.dependency-management")
    }
    configure<DependencyManagementExtension> {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:2.2.6.RELEASE")
        }
    }

    dependencies {
        // Dependencies managed by SpringBoot BOM

        testImplementation("org.junit.jupiter", "junit-jupiter-api")
        testImplementation("org.junit.jupiter", "junit-jupiter-params")
        testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine")

        testImplementation("org.assertj", "assertj-core")

        testImplementation("org.mockito", "mockito-core")
        testImplementation("org.mockito", "mockito-inline")
        testImplementation("org.mockito", "mockito-junit-jupiter")
    }
}

val guavaVersion by extra("28.0-jre")

configure(subprojects.filter {
    it.name in listOf(
            "bloom-filter",
            "nixer-plugin-core",
            "nixer-plugin-captcha",
            "nixer-plugin-pwned-check",
            "nixer-plugin-stigma"
    )
}) {

    afterEvaluate {
        project.apply(plugin = "maven-publish")
        project.apply(plugin = "signing")
        tasks.test {
            useJUnitPlatform()
            testLogging {
                events("passed", "skipped", "failed")
            }
        }

        tasks.register<Jar>("sourcesJar") {
            archiveClassifier.set("sources")
            from(sourceSets.main.get().allJava)
        }

        tasks.register<Jar>("javadocJar") {
            archiveClassifier.set("javadoc")
            from(tasks.javadoc.get().destinationDir)
        }

        tasks.named("compileJava") {
            dependsOn("processResources")
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

distributions {
    create("cloudIpRanges") {
        // Create package to be attached as GitHub release asset.
        distributionBaseName.set("ip_cloud_ranges") // The release.yml workflow depends on this name.
        version = "" // suppressed for now as the script version (showed by --version option) is not aligned yet
        contents {
            from("scripts/ip_cloud_ranges")
        }
    }
}
