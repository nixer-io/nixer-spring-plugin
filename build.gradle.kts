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
    }
}
configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
