import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import com.adarshr.gradle.testlogger.theme.ThemeType

plugins {
    java
    application
    id("jacoco")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("org.sonarqube") version "5.1.0.4882"
    id("com.adarshr.test-logger") version "3.2.0"
}

group = "be.howest.adria"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val vertxVersion = "4.5.9"
val junitJupiterVersion = "5.9.1"

val launcherClassName = "be.howest.adria.main.Main"

val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"

application {
    mainClass.set(launcherClassName)
    applicationDefaultJvmArgs = listOf("--add-opens", "java.base/java.time=ALL-UNNAMED")
}

dependencies {
    implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
    implementation("io.vertx:vertx-web-client")
    implementation("io.vertx:vertx-jdbc-client")
    implementation("io.vertx:vertx-web")
    implementation("io.vertx:vertx-web-openapi")
    implementation("io.vertx:vertx-web-openapi-router")
    implementation("io.vertx:vertx-sockjs-service-proxy")
    implementation("org.xerial:sqlite-jdbc:3.34.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.4")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("nl.martijndwars:web-push:5.1.0")
    implementation("org.bouncycastle:bcprov-jdk15on:1.70")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.3")
    testImplementation("org.bouncycastle:bcprov-jdk15on:1.70")
    testImplementation("io.vertx:vertx-junit5")
    testImplementation("org.bouncycastle:bcprov-jdk15on:1.70")
    testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
    testImplementation("com.google.code.gson:gson:2.8.9")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.named<ShadowJar>("shadowJar") {
    archiveClassifier.set("fat")
    mergeServiceFiles()
    manifest {
        attributes(
            "Main-Class" to launcherClassName
        )
    }
}

tasks.sonarqube {
    group = "_ignore"
}

tasks.sonar {
    group = "_ignore"
}

tasks.jacocoTestCoverageVerification {
    group = "_ignore"
}

tasks.test {
    group = "_ignore"
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events = setOf(PASSED, SKIPPED, FAILED)
    }
    extensions.configure(JacocoTaskExtension::class) {
        isEnabled = true
    }
}

val integrationTestSourceSet = sourceSets.create("integrationTest") {
    compileClasspath += sourceSets["main"].output + configurations.testCompileClasspath.get()
    runtimeClasspath += sourceSets["main"].output + configurations.testRuntimeClasspath.get()
    resources.srcDir(sourceSets["main"].resources.srcDirs)
}

configurations[integrationTestSourceSet.implementationConfigurationName].extendsFrom(configurations.testImplementation.get())
configurations[integrationTestSourceSet.runtimeOnlyConfigurationName].extendsFrom(configurations.testRuntimeOnly.get())
val integrationTest = tasks.register<Test>("integrationTest") {
    testClassesDirs = integrationTestSourceSet.output.classesDirs
    classpath = integrationTestSourceSet.runtimeClasspath
    shouldRunAfter(tasks.test)
    testLogging {
        events = setOf(PASSED, SKIPPED, FAILED)
    }
    extensions.configure(JacocoTaskExtension::class) {
        isEnabled = true
        destinationFile = file("$buildDir/jacoco/integrationTest.exec")
    }
}

tasks.jacocoTestReport {
    group = "_ignore"
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    dependsOn(tasks.test, integrationTest)
    executionData(
        tasks.test.get(),
        integrationTest.get()
    )
    sourceDirectories.setFrom(files(sourceSets.main.get().allSource.srcDirs))
    classDirectories.setFrom(files(sourceSets.main.get().output))
}

tasks.check {
    dependsOn(integrationTest)
}

testlogger {
    theme = ThemeType.MOCHA_PARALLEL
    showPassed = true
    showSkipped = false
    showFailed = true
    showStandardStreams = false
    showSummary = true
    showExceptions = true
    showStackTraces = false
    slowThreshold = 2000
}
