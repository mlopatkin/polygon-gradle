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

plugins {
    java
    `maven-publish`
    signing
}

java {
    withJavadocJar()
    withSourcesJar()
}

signing {
    useGpgCmd()

    if (BuildEnvironment.isCi) {
        val signingKey = findProperty("polygon_plugin_signing_key")
        val keyPassword = findProperty("polygon_plugin_signing_key_password") ?: ""
        if (signingKey != null) {
            useInMemoryPgpKeys(signingKey.toString(), keyPassword.toString())
        }
    }
}


publishing {
    publications.all {
        if (this is MavenPublication) {
            pom {
                name.set("${groupId}:${artifactId}")
                description.set(provider {
                    if (project.description.isNullOrEmpty()) {
                        throw GradleException("Expecting a description for project $project")
                    }
                    project.description
                })

                url.set("https://github.com/mlopatkin/polygon-gradle")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        name.set("Mikhail Lopatkin")
                        email.set("me@mlopatkin.name")
                        url.set("https://github.com/mlopatkin")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/mlopatkin/polygon-gradle.git")
                    developerConnection.set(
                            "scm:git:ssh://github.com:mlopatkin/polygon-gradle.git")
                    url.set("https://github.com/mlopatkin/polygon-gradle/")
                }
            }
        }
    }
}
