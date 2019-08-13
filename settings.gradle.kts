pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}
rootProject.name = "nixer-spring-plugin"

// Below code auto-detects subprojects and include them with name based and build filename, ex. `example.build.kts` becomes :example.

var buildFiles: ConfigurableFileTree = fileTree(rootDir) {
    include("**/*.gradle.kts")
    exclude("build", "**/gradle", "settings.gradle.kts", "buildSrc", "/build.gradle.kts", ".*", "out")
    exclude("**/grails3")
}

var rootDirPath = rootDir.absolutePath + File.separator
buildFiles.forEach { buildFile: File ->

    val isDefaultName = "build.gradle.kts".equals(buildFile.name)
    if(isDefaultName) {
        val buildFilePath = buildFile.parentFile.absolutePath
        val projectPath = buildFilePath.replace(rootDirPath, "").replace(File.separator, ":")
        include(projectPath)
    } else {
        val projectName = buildFile.name.replace(".gradle.kts", "");
        val projectPath = ":" + projectName;
        include(projectPath)
        val project = findProject(projectPath)
        project?.name = projectName
        project?.projectDir = buildFile.parentFile
        project?.buildFileName = buildFile.name
    }
}
