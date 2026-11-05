import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    kotlin("jvm") version "2.4.10"
    kotlin("plugin.allopen") version "2.4.10"
    id("io.quarkus")
    id("org.openapi.generator") version "7.14.0"
    id("org.sonarqube") version "7.5.0.8588"
}

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId = project.property("quarkusPlatformGroupId") as String
val quarkusPlatformArtifactId = project.property("quarkusPlatformArtifactId") as String
val quarkusPlatformVersion = project.property("quarkusPlatformVersion") as String
val jaxrsFunctionalTestBuilderVersion = project.property("jaxrsFunctionalTestBuilderVersion") as String
val wiremockVersion = project.property("wiremockVersion") as String
val okhttpVersion = project.property("okhttpVersion") as String

dependencies {
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
    implementation("io.quarkus:quarkus-kotlin")
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-oidc")
    implementation("io.quarkus:quarkus-rest-jackson")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.squareup.okhttp3:okhttp:$okhttpVersion")

    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("fi.metatavu.jaxrs.testbuilder:jaxrs-functional-test-builder:$jaxrsFunctionalTestBuilderVersion")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.wiremock:wiremock-standalone:$wiremockVersion")
}

group = "fi.metatavu.plastep.productinformation"
version = "1.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
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
    annotation("jakarta.ws.rs.Path")
    annotation("jakarta.enterprise.context.ApplicationScoped")
    annotation("io.quarkus.test.junit.QuarkusTest")
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
        javaParameters.set(true)
    }
}

val generatedDir = layout.buildDirectory.dir("generated")

val generateLemonClient = tasks.register("generateLemonClient", GenerateTask::class){
    setProperty("generatorName", "kotlin")
    setProperty("library", "jvm-okhttp4")
    setProperty("inputSpec",  "$rootDir/lemon/swagger.yaml")
    setProperty("outputDir", generatedDir.get().dir("lemon-client").asFile.path)
    setProperty("packageName", "fi.metatavu.plastep.lemon.client")
    this.configOptions.put("dateLibrary", "string")
    this.configOptions.put("collectionType", "array")
    this.configOptions.put("serializationLibrary", "jackson")
    this.configOptions.put("enumPropertyNaming", "UPPERCASE")
    this.configOptions.put("failOnUnknownProperties", "false")
    this.configOptions.put("additionalModelTypeAnnotations", "@io.quarkus.runtime.annotations.RegisterForReflection")
}

val generateApiSpec = tasks.register("generateApiSpec",GenerateTask::class){
    setProperty("generatorName", "kotlin-server")
    setProperty("inputSpec",  "$rootDir/plastep-work-instructions-product-information-services-spec/swagger.yaml")
    setProperty("outputDir", generatedDir.get().dir("api-spec").asFile.path)
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
    this.configOptions.put("useJakartaEe", "true")
    this.configOptions.put("additionalModelTypeAnnotations", "@io.quarkus.runtime.annotations.RegisterForReflection")
}

val generateApiClient = tasks.register("generateApiClient",GenerateTask::class){
    setProperty("generatorName", "kotlin")
    setProperty("library", "jvm-okhttp4")
    setProperty("inputSpec",  "$rootDir/plastep-work-instructions-product-information-services-spec/swagger.yaml")
    setProperty("outputDir", generatedDir.get().dir("api-client").asFile.path)
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

sonar {
    properties {
        property("sonar.projectKey", "Metatavu_plastep-product-information-services")
        property("sonar.organization", "metatavu-oy")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}
