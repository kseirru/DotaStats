plugins {
    id("dotastats.kotlin-application-conventions")
}

repositories {
    mavenCentral()
    maven("https://jitpack.io/")
}

dependencies {
    implementation("ch.qos.logback:logback-classic:1.4.12")

    implementation("com.github.minndevelopment:jda-ktx:9370cb1")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
    implementation("com.github.Chew:JDA-Chewtils:a86d4de")
    implementation("com.github.discord-jda:JDA:6255450")

    implementation(files("/libs/steam-client-1.3.2.jar"))
    implementation(project(":SteamConnect"))
}

application {
    mainClass.set("dotastats.Bot.MainKt")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>{
    options.encoding = "UTF-8"
}
