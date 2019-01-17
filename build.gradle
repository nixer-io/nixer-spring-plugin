import buildConfig.libraries
import eu.xword.gradle.plugins.JavaConfigPlugin

apply plugin: JavaConfigPlugin

dependencies {
    compile project(":bloom-lib")

    compile libraries.docopt        // see: https://github.com/docopt/docopt.java
}

rootProject.copyDistribution.from fileTree("src/main/dist")
