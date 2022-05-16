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

tasks.create<JavaExec>("runCustomerClient"){
    classpath = java.sourceSets["main"].runtimeClasspath
    mainClass.value("CustomerClient")
}

tasks.create<JavaExec>("runTechSupportClient"){
    classpath = java.sourceSets["main"].runtimeClasspath
    mainClass.value("TechSupportClient")
}