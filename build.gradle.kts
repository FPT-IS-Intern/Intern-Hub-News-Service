plugins {
    java
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.dependency.management) apply false
    alias(libs.plugins.jib) apply false
}

allprojects {
    group = "com.intern.hub.news"
    version = "1.0.6"

    repositories {
        mavenLocal()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

subprojects {
    apply {
        plugin("java")
        plugin("io.spring.dependency-management")
        plugin("org.springframework.boot")
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(25))
        }
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.addAll(
            listOf(
                "-parameters",
                "-Xlint:unchecked",
                "-Xlint:deprecation"
            )
        )
    }

    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }


    tasks.jar {
        enabled = true
    }

    tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
        enabled = false
    }

    tasks.test {
        useJUnitPlatform()
    }
}

tasks.jar {
    enabled = false
}
