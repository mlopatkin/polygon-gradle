/*
 * Copyright 2021 Mikhail Lopatkin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package polygon

import com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation

plugins {
    `java-gradle-plugin`

    // I don't know how to reach version catalog accessor from here
    id("com.github.johnrengelman.shadow")

    id("polygon.common")
    id("polygon.publish-common")
}

// Define a special configuration for the dependencies that has to be bundled into the main plugin
// JAR with relocation.
val shadowImplementation: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

configurations {
    // Add shadowed dependencies to the compilation classpath but not to the runtime one, so there
    // are no dependencies in the generated POM.
    compileOnly {
        extendsFrom(shadowImplementation)
    }
    // Add shadowed dependencies to the test classpath as is.
    testImplementation {
        extendsFrom(shadowImplementation)
    }
    // Clean up artifacts for apiElements and runtimeElements to ensure that the shadowed Jar will
    // be used as a main artifact and not the non-shadowed Jar which we disable. The symptom of this
    // not working is the publication task complaining about:
    //      "Artifact gradle-plugin-XX.YY.jar wasn't produced by this build"
    apiElements {
        artifacts.clear()
    }
    runtimeElements {
        artifacts.clear()
    }
}

val shadowJar = tasks.shadowJar

val relocateShadowJar = tasks.register<ConfigureShadowRelocation>("relocateShadowJar") {
    prefix = "${project.group}.shadow"
    target = shadowJar.get()
}

shadowJar.configure {
    dependsOn(relocateShadowJar)
    minimize()
    archiveClassifier.set("")
    configurations = listOf(shadowImplementation)
}
