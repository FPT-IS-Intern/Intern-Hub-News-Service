dependencies {
    implementation(libs.bundles.custom.libraries)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)
    annotationProcessor(libs.lombok.mapstruct.binding)
    implementation(libs.audit.library)
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
}


