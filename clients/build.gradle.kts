/*
 * Copyright 2000-2022 TeamDev.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

val tasksMap = mapOf(
    "runStreamer" to "com.teamdev.jxbrowser.examples.Streamer",
    "runReceiver" to "com.teamdev.jxbrowser.examples.Receiver"
)

tasksMap.forEach { (taskName, className) ->
    tasks.create<JavaExec>(taskName) {
        val port: String? by project

        doFirst {
            systemProperty("server.port", port ?: "3000")
        }
        classpath = java.sourceSets["main"].runtimeClasspath
        mainClass.value(className)
    }
}
