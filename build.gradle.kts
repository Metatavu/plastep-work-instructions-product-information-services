import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.allopen") version "1.7.22"
    id("io.quarkus")
    id("org.openapi.generator") version "6.2.1"
    id("org.sonarqube") version "3.5.0.2730"
}

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project
val targetEnvironment: String by project
val jaxrsFunctionalTestBuilderVersion: String by project
val wiremockVersion: String by project

dependencies {
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
    implementation("io.quarkus:quarkus-kotlin")
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-oidc")
    implementation("io.quarkus:quarkus-resteasy-jackson")

    if (targetEnvironment == "lambda") {
        implementation("io.quarkus:quarkus-amazon-lambda-http")
    }

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.squareup.okhttp3:okhttp")

    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("fi.metatavu.jaxrs.testbuilder:jaxrs-functional-test-builder:$jaxrsFunctionalTestBuilderVersion")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("com.github.tomakehurst:wiremock-jre8:$wiremockVersion")
}

group = "fi.metatavu.plastep.productinformation"
version = "1.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

sourceSets["main"].java {
    srcDir("build/generated/api-spec/src/main/kotlin")
    srcDir("build/generated/lemon-client/src/main/kotlin")
}

sourceSets["test"].java {
    srcDir("build/generated/api-client/src/main/kotlin")
}

tasks.withType<Test> {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
}

allOpen {
    annotation("javax.ws.rs.Path")
    annotation("javax.enterprise.context.ApplicationScoped")
    annotation("io.quarkus.test.junit.QuarkusTest")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
    kotlinOptions.javaParameters = true
}

val generateLemonClient = tasks.register("generateLemonClient", GenerateTask::class){
    setProperty("generatorName", "kotlin")
    setProperty("library", "jvm-okhttp3")
    setProperty("inputSpec",  "$rootDir/lemon/swagger.yaml")
    setProperty("outputDir", "$buildDir/generated/lemon-client")
    setProperty("packageName", "fi.metatavu.plastep.lemon.client")
    setProperty("templateDir", "$rootDir/lemon/templates")
    this.configOptions.put("dateLibrary", "string")
    this.configOptions.put("collectionType", "array")
    this.configOptions.put("serializationLibrary", "jackson")
    this.configOptions.put("enumPropertyNaming", "UPPERCASE")
    this.configOptions.put("additionalModelTypeAnnotations", "@io.quarkus.runtime.annotations.RegisterForReflection")
}

val generateApiSpec = tasks.register("generateApiSpec",GenerateTask::class){
    setProperty("generatorName", "kotlin-server")
    setProperty("inputSpec",  "$rootDir/plastep-work-instructions-product-information-services-spec/swagger.yaml")
    setProperty("outputDir", "$buildDir/generated/api-spec")
    setProperty("apiPackage", "fi.metatavu.plastep.productinformation.spec")
    setProperty("invokerPackage", "fi.metatavu.plastep.productinformation.invoker")
    setProperty("modelPackage", "fi.metatavu.plastep.productinformation.model")

    this.configOptions.put("library", "jaxrs-spec")
    this.configOptions.put("dateLibrary", "java8")
    this.configOptions.put("enumPropertyNaming", "UPPERCASE")
    this.configOptions.put("interfaceOnly", "true")
    this.configOptions.put("useCoroutines", "false")
    this.configOptions.put("returnResponse", "true")
    this.configOptions.put("useSwaggerAnnotations", "false")
    this.configOptions.put("additionalModelTypeAnnotations", "@io.quarkus.runtime.annotations.RegisterForReflection")
}

val generateApiClient = tasks.register("generateApiClient",GenerateTask::class){
    setProperty("generatorName", "kotlin")
    setProperty("library", "jvm-okhttp3")
    setProperty("inputSpec",  "$rootDir/plastep-work-instructions-product-information-services-spec/swagger.yaml")
    setProperty("outputDir", "$buildDir/generated/api-client")
    setProperty("packageName", "fi.metatavu.plastep.productinformation.client")
    this.configOptions.put("dateLibrary", "string")
    this.configOptions.put("collectionType", "array")
    this.configOptions.put("serializationLibrary", "jackson")
    this.configOptions.put("enumPropertyNaming", "UPPERCASE")
    this.configOptions.put("additionalModelTypeAnnotations", "@io.quarkus.runtime.annotations.RegisterForReflection")
}

tasks.named("compileKotlin") {
    dependsOn(generateApiSpec, generateLemonClient)
}

tasks.named("compileTestKotlin") {
    dependsOn(generateApiClient)
}

sonarqube {
    properties {
        property("sonar.projectKey", "Metatavu_plastep-product-information-services")
        property("sonar.organization", "metatavu-oy")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}