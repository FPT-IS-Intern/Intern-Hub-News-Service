dependencies {
    implementation(project(":core"))

    implementation(libs.bundles.custom.libraries)

    implementation(libs.bundles.spring.boot.all)



    implementation(libs.openapi.doc)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

