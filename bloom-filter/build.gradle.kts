import buildConfig.libraries

plugins {
    id("java-config-plugin")
}

dependencies {
    compile(libraries.guava)
    compile(libraries.jackson.annotations)
    compile(libraries.jackson.core)
    compile(libraries.jackson.databind)
}
