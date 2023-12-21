plugins {
    id("dotastats.kotlin-application-conventions")
}

dependencies {
    implementation("org.apache.commons:commons-text")
    implementation(project(":SteamConnect"))
}

application {
    mainClass.set("dotastats.Bot.MainKt")
}
