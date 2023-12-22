plugins {
    id("dotastats.kotlin-application-conventions")
}

repositories {
    mavenCentral()
    maven("https://jitpack.io/")
}

dependencies {
    implementation("ch.qos.logback:logback-classic:1.2.8")

    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
    implementation("com.github.Chew:JDA-Chewtils:a86d4de")
    implementation("com.github.discord-jda:JDA:6255450")

    implementation(project(":SteamConnect"))
}

application {
    mainClass.set("dotastats.Bot.MainKt")
}
