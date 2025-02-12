@file:Suppress("SpellCheckingInspection", "VulnerableLibrariesLocal")

val kotlinxCoroutineVersion: String by rootProject

plugins {
    kotlin("jvm")
    application
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://darkmeow.nekocurit.asia/maven/") // https://darkmeowteam.github.io/maven/
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutineVersion")

    implementation("org.apache.logging.log4j:log4j-core:2.20.0")
    implementation("com.esotericsoftware.yamlbeans:yamlbeans:1.17")

    implementation("com.github.steveice10:mcprotocollib:1.12.2-2")
    implementation("com.github.steveice10:opennbt:1.4")
    implementation("com.github.steveice10:mcauthlib:1.0")
    implementation("com.github.steveice10:packetlib:1.2")

    implementation("com.google.code.gson:gson:2.11.0")

    implementation("net.darkmeow:IRCClient-all:1.1.0205")
}

application {
    mainClass.set("cute.nahida.hyt.HytBotLauncherKt")
}

tasks {
    jar {
        manifest {
            attributes(
                "Main-Class" to "cute.nahida.hytbot.HytBotLauncherKt"
            )
        }

        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}