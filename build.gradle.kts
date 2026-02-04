plugins {
    `java-library`
    alias(libs.plugins.teavm) // order matters?
}

val thisVersion = "0.4.1"

group = "run.slicer"
version = "$thisVersion-${libs.versions.vineflower.get()}"
description = "A JavaScript port of the Vineflower decompiler."

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://teavm.org/maven/repository")
}

val vineflower by configurations.creating
configurations.api.configure { extendsFrom(vineflower) }

dependencies {
    vineflower(libs.vineflower)
    compileOnly(libs.teavm.core)

    // expand plugins into the compilation classpath
    implementation(provider {
        zipTree(vineflower.singleFile).filter { it.extension == "jar" }
    })
}

java.toolchain {
    languageVersion = JavaLanguageVersion.of(21)
}

teavm.js {
    mainClass = "run.slicer.vf.Main"
    moduleType = org.teavm.gradle.api.JSModuleType.ES2015
    /*obfuscated = false
    optimization = org.teavm.gradle.api.OptimizationLevel.NONE*/
}

teavm.wasmGC {
    mainClass = "run.slicer.vf.Main"
    modularRuntime = true
    /*obfuscated = false
    optimization = org.teavm.gradle.api.OptimizationLevel.NONE
    disassembly = true*/
}

/*tasks.disasmWasmGC {
    html = false
}*/

tasks {
    register<Copy>("copyDist") {
        group = "build"
        dependsOn(generateJavaScript)

        from(
            "README.md", "LICENSE", "LICENSE-VF", "vf.js", "vf.d.ts",
            copyWasmGCRuntime, generateWasmGC
        )
        into("dist")

        duplicatesStrategy = DuplicatesStrategy.INCLUDE

        doLast {
            copy {
                from(generateJavaScript)
                into("dist")
                rename("vf.js", "vf.runtime.js")

                duplicatesStrategy = DuplicatesStrategy.INCLUDE
            }
        }
        doLast {
            file("dist/package.json").writeText(
                """
                    {
                      "name": "@run-slicer/vf",
                      "version": "${project.version}",
                      "description": "A JavaScript port of the Vineflower decompiler (https://github.com/Vineflower/vineflower).",
                      "main": "vf.js",
                      "types": "vf.d.ts",
                      "keywords": [
                        "decompiler",
                        "java",
                        "decompilation",
                        "vf",
                        "vineflower"
                      ],
                      "author": "run-slicer",
                      "license": "Apache License 2.0, MIT"
                    }
                """.trimIndent()
            )
        }
    }

    build {
        dependsOn("copyDist")
    }

    clean {
        delete("dist")
    }
}
