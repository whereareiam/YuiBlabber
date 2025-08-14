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

    if (project.name != "yuiplugin-common-api") {
        dependencies {
            "compileOnly"(project(":yuiplugin-common-api"))
        }
    }

    dependencies {
        "compileOnly"(rootProject.libs.yui)
        "compileOnly"(rootProject.libs.spring.boot)
        "compileOnly"(rootProject.libs.jda)

        "compileOnly"(rootProject.libs.lombok)
        "annotationProcessor"(rootProject.libs.lombok)
    }
}