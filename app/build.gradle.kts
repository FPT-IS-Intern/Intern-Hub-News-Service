plugins {
    alias(libs.plugins.jib)
}

dependencies {
    // Project dependencies
    implementation(project(":api"))
    implementation(project(":infra"))
    implementation(project(":core"))

    // Custom Libraries - common across all modules
    implementation(libs.bundles.custom.libraries)

    // Spring Boot dependencies
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.data.jpa)
    // Liquibase
    implementation(libs.spring.boot.starter.liquibase)

    // Lombok
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    implementation(libs.audit.library)
}

tasks.jar {
    enabled = false
}

tasks.bootJar {
    enabled = true
    archiveFileName.set("news-service.jar")
}

springBoot {
    mainClass.set("com.intern.hub.news.app.NewsServiceApplication")
}


jib {
    from {
        image = "gcr.io/distroless/base-debian12"
    }

    to {
        image = "internhub-v2/dev/news"
        tags  = setOf("latest")
    }

    container {
        entrypoint = listOf(
            "/opt/java/openjdk/bin/java",
            "-javaagent:/app/opentelemetry-javaagent.jar",
            "-cp", "/app/libs/*:/app/resources:/app/classes",
            "com.intern.hub.news.app.NewsServiceApplication"
        )

        environment = mapOf(
            "JAVA_TOOL_OPTIONS"     to "-Xmx512m -Xms256m",
            "OTEL_TRACES_EXPORTER"  to "none",
            "OTEL_METRICS_EXPORTER" to "none",
            "OTEL_LOGS_EXPORTER"    to "none",
            "OTEL_SERVICE_NAME"     to "news-service"
        )

        creationTime.set("USE_CURRENT_TIMESTAMP")
    }

    extraDirectories {
        setPaths(listOf(rootProject.file("jib-extra")))
        permissions.set(mapOf("/opt/java/openjdk/bin/**" to "755"))
    }
}
