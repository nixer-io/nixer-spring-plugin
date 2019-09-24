import buildConfig.libraries

plugins {
    id("java-config-plugin")
}

dependencies {
    compile(project(":bloom-lib"))

    compile(libraries.docopt)        // see: https://github.com/docopt/docopt.java
}

rootProject.tasks.getByName<Sync>("copyDistribution") {
    from(fileTree("src/main/dist"))
}
