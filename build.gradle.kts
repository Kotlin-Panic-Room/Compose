import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    `java-library`
}

group = "com.maple"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    // JavaCV
    implementation("org.bytedeco:javacv-platform:1.5.6")
    // JNA
    implementation("net.java.dev.jna:jna:5.8.0")
    implementation("net.java.dev.jna:jna-platform:5.8.0")

    implementation("com.1stleg:jnativehook:2.1.0")

    //JACKSON
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.5")

}

kotlin {
    jvm {
        jvmToolchain(17)
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "compose"
            packageVersion = "1.0.0"
        }
    }
}
