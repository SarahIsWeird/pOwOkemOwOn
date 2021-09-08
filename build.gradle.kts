import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
}

group = "com.sarahisweird"
version = "1.0"

repositories {
    mavenCentral()
}

val discordKtVersion: String by project
val kotlinCsvVersion: String by project
val exposedVersion: String by project
val mysqlConnectorVersion: String by project

dependencies {
    implementation("me.jakejmattson:DiscordKt:$discordKtVersion")
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:$kotlinCsvVersion")

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")

    implementation("mysql:mysql-connector-java:$mysqlConnectorVersion")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}