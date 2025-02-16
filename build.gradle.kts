plugins {
    id("java")
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.hibiscus"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()

    maven("https://repo.spongepowered.org/maven/")
}

dependencies {

    implementation("net.dv8tion:JDA:5.3.0")
    // https://mvnrepository.com/artifact/org.apache.commons/commons-lang3
    implementation("org.apache.commons:commons-lang3:3.4")

    implementation("org.spongepowered:configurate-yaml:4.2.0-SNAPSHOT")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    mainClass.set("com.hibiscus.hmccconverter.Main")
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = "com.hibiscus.hmccconverter.Main"
        }
    }

    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveFileName.set("HMCCConverterBot.jar")
    }
}

tasks.test {
    useJUnitPlatform()
}

