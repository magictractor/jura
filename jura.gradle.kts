version = "0.0.1-SNAPSHOT"
group = "uk.co.magictractor"

repositories {
    mavenCentral()
}

plugins {
    `java-library`
    `jacoco`
    
    // TODO! restore checkstyle plugin with config copied from JUnit5
    //`checkstyle`
    
    // See https://docs.gradle.org/current/userguide/publishing_maven.html
    `maven-publish`
}

dependencies {
    implementation("org.slf4j:slf4j-api:1.7.26")
    implementation("org.junit.jupiter:junit-jupiter-api:5.4.2")
    implementation("org.junit.jupiter:junit-jupiter-engine:5.4.2")
    implementation("org.junit.platform:junit-platform-launcher:1.4.2")
    implementation("org.assertj:assertj-core:3.11.1")

    // Use of JUnit4's @SelectClasses, @ExcludeTags etc is optional, so use compileOnly.
    // JUnitSuiteSuiteAnnotationReader should not be used if this project is not present.    
    compileOnly("org.junit.platform:junit-platform-suite-api:1.4.2")
    testImplementation("org.junit.platform:junit-platform-suite-api:1.4.2")
    testImplementation("ch.qos.logback:logback-classic:1.2.3")

    // MoreObjects is used for toString() implementations
    implementation("com.google.guava:guava:27.0.1-jre")
}

// See https://docs.gradle.org/current/userguide/build_environment.html
//tasks.withType<JavaCompile>().configureEach {
tasks.withType<JavaCompile> {
    options.compilerArgs = listOf("-Xdoclint:none", "-Xlint:none", "-nowarn")
}

tasks.withType<Test> {
    useJUnitPlatform() {
        filter {
            excludeTags("within-suite")
        }
    }
    finalizedBy("jacocoTestReport")
}

publishing {
    publications {
        create<MavenPublication>("${rootProject.name}") {
            from(components["java"])
        }
    }

    repositories {
        mavenLocal()
    }
}
