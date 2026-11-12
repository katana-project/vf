import org.teavm.gradle.api.JSModuleType
import org.teavm.gradle.api.OptimizationLevel

plugins {
    `java-library`
    alias(libs.plugins.teavm) // order matters?
}

val thisVersion = "0.6.4"

group = "run.slicer"
version = "$thisVersion-${libs.versions.vineflower.get()}"
description = "A JavaScript port of the Vineflower decompiler."

repositories {
    mavenCentral()
    maven("https://teavm.org/maven/repository")
}

val vineflower: Configuration by configurations.creating
configurations.api.configure { extendsFrom(vineflower) }

dependencies {
    vineflower(libs.vineflower)
    teavmAnnotationProcessor(libs.teavm.extension.annotation.processor) // hmm?
    testImplementation(platform(libs.junit.bom))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // expand plugins into the compilation classpath
    implementation(provider {
        zipTree(vineflower.singleFile).filter { it.extension == "jar" }
    })
}

java.toolchain {
    languageVersion = JavaLanguageVersion.of(21)
}

val debugging = false // set to true if you want an unobfuscated build for debugging
teavm {
    js {
        mainClass = "run.slicer.vf.JSMain"
        moduleType = JSModuleType.ES2015
        obfuscated = !debugging
        if (debugging) {
            optimization = OptimizationLevel.NONE
        }

        asyncEnabled = true
    }

    wasmGC {
        mainClass = "run.slicer.vf.WASMMain"
        modularRuntime = true
        obfuscated = !debugging
        disassembly = debugging
        if (debugging) {
            optimization = OptimizationLevel.NONE
        }
    }
}

tasks {
    disasmWasmGC {
        html = false
    }

    register<Copy>("copyDist") {
        group = "build"
        description = "Copies the necessary files to the dist directory for distribution."
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

    test {
        useJUnitPlatform()

        dependsOn("copyDist") // comment out if you want to edit the output and test
    }

    build {
        dependsOn("copyDist")
    }

    clean {
        delete("dist")
    }
}
