import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.shadow)
}

tasks.withType<ShadowJar> {
    archiveBaseName.set(rootProject.name)
    archiveClassifier.set("")

    val defaultDestination = rootProject.layout.buildDirectory.dir("libs")

    val customOutputDir = if (project.hasProperty("output")) {
        project.layout.dir(project.provider { File(project.property("output").toString()) })
    } else {
        null
    }

    destinationDirectory.set(customOutputDir ?: defaultDestination)
}

dependencies {
    // include all projects
    rootProject.subprojects.forEach { subproject ->
        if (subproject.name != "yuiplugin-bootstrap") {
            "implementation"(project(":${subproject.name}"))
        }
    }
}