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

tasks.create<JavaExec>("runCustomerClient"){
    doFirst {
        if (project.hasProperty("port")) {
            systemProperty("example.port", port)
        }
    }
    classpath = java.sourceSets["main"].runtimeClasspath
    mainClass.value("com.teamdev.jxbrowser.examples.CustomerClient")
}

tasks.create<JavaExec>("runTechSupportClient"){
    doFirst {
        if (project.hasProperty("port")) {
            systemProperty("example.port", port)
        }
    }
    classpath = java.sourceSets["main"].runtimeClasspath
    mainClass.value("com.teamdev.jxbrowser.examples.TechSupportClient")
}
