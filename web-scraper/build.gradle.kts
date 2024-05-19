plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.serialization") version "1.6.10"

}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("it.skrape:skrapeit:1.2.2")
    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation ("com.github.kittinunf.fuel:fuel:2.3.1") // for HTTP operations
    implementation("com.github.kittinunf.fuel:fuel-coroutines:2.3.1")  // for coroutine support in Fuel



    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")

}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}