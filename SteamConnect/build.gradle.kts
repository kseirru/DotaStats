plugins {
    id("dotastats.kotlin-application-conventions")
}

repositories {
    mavenCentral()
    maven("https://jitpack.io/")
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.0")
    implementation("com.fasterxml.jackson.core:jackson-core:2.16.0")
    implementation("com.google.protobuf:protobuf-java:3.25.1")
    implementation("org.apache.commons:commons-lang3:3.14.0")
    implementation("org.java-websocket:Java-WebSocket:1.5.5")
    implementation("com.squareup.moshi:moshi-kotlin:1.14.0")
    implementation("com.github.corese4rch:cvurl-io:1.5.1")
    implementation("com.github.PlatinumDigitalGroup:JVDF:ba5141c")
    implementation("org.bouncycastle:bcprov-jdk15on:1.70")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.slf4j:slf4j-simple:2.0.9")
    implementation("org.slf4j:slf4j-api:2.0.9")

    implementation(files("/libs/steam-client-1.3.2.jar"))
}