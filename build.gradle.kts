defaultTasks("build", "shadowJar")

allprojects {
    version = (System.getenv("VERSION") ?: "dev")

    apply(plugin = "java")

    tasks.withType<JavaCompile> {
        sourceCompatibility = JavaVersion.VERSION_23.toString()
        targetCompatibility = JavaVersion.VERSION_23.toString()
    }
}

subprojects {
    repositories {
        mavenCentral()
        mavenLocal()
    }

    if (project.name != "yuiblabber-common-api") {
        dependencies {
            "compileOnly"(project(":yuiblabber-common-api"))
        }
    }

    dependencies {
        "compileOnly"(rootProject.libs.bundles.yui)
        "compileOnly"(rootProject.libs.bundles.spring)
        "compileOnly"(rootProject.libs.jda)

        "compileOnly"(rootProject.libs.lombok)
        "annotationProcessor"(rootProject.libs.lombok)
    }
}