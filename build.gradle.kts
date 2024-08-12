plugins {
    kotlin("jvm") version "2.0.0"
    `java-library`
}

group = "lib.quint"
version = "1.1.0"

repositories {
    mavenCentral()
}

java {
    withSourcesJar()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
