plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

repositories {
    mavenCentral()
}


val vertxVersion = "4.0.2"
val junitVersion = "4.13.2"

sourceSets.getByName("main") {
    java.srcDir("src/main/java")
    java.srcDir("src/main/kotlin")
    java.srcDir("src/main/resources")
    java.srcDir("src/main/configs")
}

application {
    mainClassName = "io.vertx.core.Launcher"
}

val mainVerticleName = "fi.johannes.vertx.MainVerticle"
val watchForChange = "src/**/*"
val doOnChange = "./gradlew classes"

setGroup("fi.johannes.bilot")
setVersion("0.1-SNAPSHOT")

java {
    setSourceCompatibility(org.gradle.api.JavaVersion.VERSION_11)
    setTargetCompatibility(org.gradle.api.JavaVersion.VERSION_11)
}

dependencies {

    implementation("io.vertx:vertx-reactive-streams:$vertxVersion")
    implementation("io.vertx:vertx-rx-java2:$vertxVersion")
    implementation("io.vertx:vertx-web-client:$vertxVersion")
    implementation("io.vertx:vertx-kafka-client:$vertxVersion")
    implementation("io.vertx:vertx-config:$vertxVersion")
    implementation("io.vertx:vertx-config-yaml:$vertxVersion")
    implementation("org.apache.kafka:kafka-clients:2.5.1")
//    deployerJars("org.apache.maven.wagon:wagon-webdav:1.0-beta-2")
    implementation("io.vertx:vertx-web:$vertxVersion")
    implementation("org.slf4j:slf4j-api:1.7.25")
    implementation("org.slf4j:slf4j-simple:1.7.25")

    testImplementation("io.vertx:vertx-junit5:$vertxVersion")
    testImplementation(group= "junit", name= "junit", version= junitVersion)
    testImplementation(group= "org.json", name= "json", version= "20180130")
    testImplementation("org.mockito:mockito-core:2.+")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.12")
    annotationProcessor("org.projectlombok:lombok:1.18.12")

    testCompileOnly("org.projectlombok:lombok:1.18.12")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.12")
}
tasks {
    withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
        getArchiveClassifier()
        manifest {
            attributes["Main-Verticle"] = mainVerticleName
        }
        mergeServiceFiles()
    }

    test {
        useJUnitPlatform()
    }

}