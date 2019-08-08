plugins {
    java
}

allprojects {
    group = "eu.xword.nixer"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    afterEvaluate {
        tasks.test {
            useJUnitPlatform()
            testLogging {
                events("passed", "skipped", "failed")
            }
        }

        dependencies {
            testImplementation("org.mockito", "mockito-core", "2.21.0")
            testImplementation("org.mockito", "mockito-junit-jupiter", "2.23.0")
            testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.3.2")
            testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine","5.3.2")
        }
    }
}
configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
