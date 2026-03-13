FROM eclipse-temurin:25.0.2_10-jdk AS build
WORKDIR /app

COPY gradlew gradlew.bat build.gradle.kts settings.gradle.kts ./
COPY gradle/ gradle/

COPY api/build.gradle.kts api/
COPY core/build.gradle.kts core/
COPY infra/build.gradle.kts infra/
COPY app/build.gradle.kts app/

RUN chmod +x gradlew

RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew dependencies --no-daemon

COPY api/src api/src
COPY core/src core/src
COPY infra/src infra/src
COPY app/src app/src

RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew --no-daemon bootJar

RUN jdeps --ignore-missing-deps -q \
    --recursive \
    --multi-release 25 \
    --print-module-deps \
    --class-path 'app/build/libs/*' \
    app/build/libs/news-service.jar > deps.txt

RUN jlink \
    --add-modules $(cat deps.txt),java.base,java.logging,java.naming,java.desktop,java.management,java.security.jgss,java.instrument,java.sql,java.compiler,jdk.crypto.ec,jdk.unsupported \
    --compress zip-9 \
    --strip-debug \
    --no-header-files \
    --no-man-pages \
    --output /custom-jre


FROM busybox:1.36 AS otel

ARG OTEL_VERSION=2.25.0
WORKDIR /otel

RUN wget -O opentelemetry-javaagent.jar \
  https://repo1.maven.org/maven2/io/opentelemetry/javaagent/opentelemetry-javaagent/${OTEL_VERSION}/opentelemetry-javaagent-${OTEL_VERSION}.jar


# Runtime stage
FROM gcr.io/distroless/base-debian12

WORKDIR /app

COPY --from=build /custom-jre /opt/java/openjdk
COPY --from=build /app/app/build/libs/news-service.jar ./app.jar
COPY --from=otel /otel/opentelemetry-javaagent.jar ./opentelemetry-javaagent.jar

ENV OTEL_TRACES_EXPORTER=none
ENV OTEL_METRICS_EXPORTER=none
ENV OTEL_LOGS_EXPORTER=none
ENV OTEL_SERVICE_NAME=news-service

ENV JAVA_TOOL_OPTIONS="-Xmx512m -Xms256m -javaagent:/app/opentelemetry-javaagent.jar"
ENTRYPOINT ["/opt/java/openjdk/bin/java", "-jar", "app.jar"]