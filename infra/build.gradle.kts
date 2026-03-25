dependencies {
    implementation(project(":core"))

    implementation(libs.bundles.custom.libraries)

    implementation(libs.bundles.spring.boot.database)
    implementation(libs.spring.boot.starter.validation)

    implementation(libs.spring.boot.starter.feign)
    implementation(libs.spring.boot.starter.kafka)
    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    annotationProcessor(libs.lombok.mapstruct.binding)
    implementation(libs.audit.library)
}
