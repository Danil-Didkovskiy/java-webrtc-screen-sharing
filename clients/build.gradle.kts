plugins {
    id("java")
    id("com.teamdev.jxbrowser.gradle") version "0.0.3"
}

group = "com.teamdev"
version = "1.0-SNAPSHOT"

jxbrowser {
    version = "7.24"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(jxbrowser.currentPlatform())
    implementation(jxbrowser.swing())
}

val port: String by project

val tasksMap = mapOf(
    "runStreamer" to "com.teamdev.jxbrowser.examples.Streamer",
    "runReceiver" to "com.teamdev.jxbrowser.examples.Receiver"
)

tasksMap.forEach { (taskName, className) ->
    tasks.create<JavaExec>(taskName) {
        doFirst {
            systemProperty("server.port", if (project.hasProperty("port")) port else "3000")
        }
        classpath = java.sourceSets["main"].runtimeClasspath
        mainClass.value(className)
    }
}
