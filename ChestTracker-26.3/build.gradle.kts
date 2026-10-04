@file:Suppress("UnstableApiUsage", "RedundantNullableReturnType")

import com.github.breadmoirai.githubreleaseplugin.GithubReleaseTask
import me.modmuss50.mpp.ReleaseType
import org.gradle.jvm.tasks.Jar
import org.ajoberstar.grgit.Grgit
import red.jackf.GenerateChangelogTask
import red.jackf.UpdateDependenciesTask

plugins {
    id("maven-publish")
    id("net.fabricmc.fabric-loom") version "1.15-SNAPSHOT"
    id("com.github.breadmoirai.github-release") version "2.5.2"
    id("org.ajoberstar.grgit") version "5.3.0"
    id("me.modmuss50.mod-publish-plugin") version "0.8.3"
}

val grgit: Grgit? = project.grgit

var canPublish = grgit != null && System.getenv("RELEASE") != null

fun getVersionSuffix(): String {
    return grgit?.branch?.current()?.name ?: "nogit+${properties["minecraft_version"]}"
}

group = properties["maven_group"]!!

if (System.getenv().containsKey("NEW_TAG")) {
    version = System.getenv("NEW_TAG").substring(1)
} else {
    val versionStr = "${properties["mod_version"]}+${properties["minecraft_version"]!!}"
    canPublish = false
    version = if (grgit != null) {
        "$versionStr+dev-${grgit.log()[0].abbreviatedId}"
    } else {
        "$versionStr+dev-nogit"
    }
}

val isBundlingSearchables = properties["bundle_searchables"] == "true"
val isWthitEnabled = properties["wthit_version"]?.toString()?.isNotBlank() == true

base {
    archivesName.set("${properties["archives_base_name"]}")
}

repositories {
    // Mod Menu, EMI
    maven {
        name = "TerraformersMC"
        url = uri("https://maven.terraformersmc.com/releases/")
        content {
            includeGroup("com.terraformersmc")
            includeGroup("dev.emi")
        }
    }

    // YACL
    maven {
        name = "Xander Maven"
        url = uri("https://maven.isxander.dev/releases")
        content {
            includeGroupAndSubgroups("dev.isxander")
            includeGroupAndSubgroups("org.quiltmc")
        }
    }

    // YACL Snapshots
    maven {
        name = "Xander Snapshot Maven"
        url = uri("https://maven.isxander.dev/snapshots")
        content {
            includeGroupAndSubgroups("dev.isxander")
            includeGroupAndSubgroups("org.quiltmc")
        }
    }

    // Searchables
    maven {
        name = "BlameJared"
        url = uri("https://maven.blamejared.com")
        content {
            includeGroupAndSubgroups("com.blamejared.searchables")
        }
    }

    // Where Is It, Jade
    maven {
        name = "Modrinth Maven"
        url = uri("https://api.modrinth.com/maven")
        content {
            includeGroup("maven.modrinth")
        }
    }

    // Shulker Box Tooltip
    maven {
        name = "MisterPeModder"
        url = uri("https://maven.misterpemodder.com/libs-release/")
        content {
            includeGroupAndSubgroups("com.misterpemodder")
        }
    }

    // WTHIT
    maven {
        url  = uri("https://maven2.bai.lol")
        content {
            includeGroupAndSubgroups("lol.bai")
            includeGroupAndSubgroups("mcp.mobius.waila")
        }
    }
}

java {
    withSourcesJar()
}

loom {
    splitEnvironmentSourceSets()

    // The WTHIT integration is compiled out while no 26.3 build of wthit-api exists.
    // Set `wthit_version` in gradle.properties to build it again.
    if (!isWthitEnabled) {
        sourceSets["client"].java.exclude("red/jackf/chesttracker/impl/compat/mods/wthit/**")
    }

    mods {
        create("chesttracker") {
            sourceSet(sourceSets["client"])
        }
    }

    log4jConfigs.from(file("log4j2.xml"))

    runConfigs.configureEach {
        this.programArgs.addAll("--username JackFred".split(" "))
    }

    accessWidenerPath.set(file("src/client/resources/chesttracker.accesswidener"))
}

dependencies {
    // To change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:${properties["minecraft_version"]}")
    implementation("net.fabricmc:fabric-loader:${properties["loader_version"]}")

    implementation("net.fabricmc.fabric-api:fabric-api:${properties["fabric-api_version"]}")

    // Where is it
    implementation("maven.modrinth:where-is-it-port:${properties["where-is-it_version"]}")
    include("maven.modrinth:where-is-it-port:${properties["where-is-it_version"]}")

    // Config
    implementation("dev.isxander:yet-another-config-lib:${properties["yacl_version"]}") {
        exclude(group = "com.terraformersmc", module = "modmenu")
    }

    // dev util
    //modLocalRuntime("dev.emi:emi-fabric:${properties["emi_version"]}")
    //modLocalRuntime("maven.modrinth:jsst:mc1.20-0.3.12")

    ////////////////
    // MOD COMPAT //
    ////////////////

    // Searchables
    compileOnly("com.blamejared.searchables:Searchables-fabric-${properties["searchables_version"]}") {
        exclude(group = "net.fabricmc.fabric-api", module = "fabric-api")
    }
    runtimeOnly("com.blamejared.searchables:Searchables-fabric-${properties["searchables_version"]}") {
        exclude(group = "net.fabricmc.fabric-api", module = "fabric-api")
    }
    if (isBundlingSearchables) include("com.blamejared.searchables:Searchables-fabric-${properties["searchables_version"]}")

    // Mod Menu
    compileOnly("com.terraformersmc:modmenu:${properties["modmenu_version"]}")
    localRuntime("com.terraformersmc:modmenu:${properties["modmenu_version"]}")

    // Shulker Box Tooltip
    compileOnly("com.misterpemodder:shulkerboxtooltip-fabric:${properties["shulkerboxtooltip_version"]}")

    //runtimeOnly("com.misterpemodder:shulkerboxtooltip-fabric:${properties["shulkerboxtooltip_version"]}")

    // WTHIT
    if (isWthitEnabled) compileOnly("mcp.mobius.waila:wthit-api:${properties["wthit_version"]}")

    //runtimeOnly("mcp.mobius.waila:wthit:${properties["wthit_version"]}")
    //runtimeOnly("lol.bai:badpackets:${properties["badpackets_version"]}")

    // Jade
    compileOnly("maven.modrinth:jade:${properties["jade_version"]}")
    localRuntime("maven.modrinth:jade:${properties["jade_version"]}")

    // Litematica / MaLiLib / JackFredLib - no public maven for 26.3, jars are vendored in libs/
    compileOnly(fileTree("libs"))
    localRuntime(fileTree("libs") { include("litematica*.jar", "malilib*.jar") })
}

tasks.withType<ProcessResources>().configureEach {
    filesMatching("fabric.mod.json") {
        expand(mapOf("version" to version))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(25)
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${properties["archivesBaseName"]}"}
    }
}

// configure the maven publication
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            pom {
                name = project.properties["mod_name"].toString()
                description = "Client side item storage - remember where you've put things"
                url = project.properties["github_url"]!!.toString()
                licenses {
                    license {
                        name = "LGPL-3.0"
                        url = "https://opensource.org/license/lgpl-3-0/"
                    }
                }
                developers {
                    developer {
                        id = project.properties["github_owner"].toString()
                        name = project.properties["github_owner"].toString()
                        url = "https://github.com/${project.properties["github_owner"]}"
                    }
                }
                scm {
                    val repoUrl = project.properties["github_url"]!!.toString()
                    connection = "scm:git:$repoUrl.git"
                    developerConnection = "scm:git:$repoUrl.git"
                    url = repoUrl
                }
            }
        }
    }

    repositories {
        if (!System.getenv().containsKey("CI")) mavenLocal()

        if (canPublish) {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/${properties["github_owner"]}/${properties["github_repo"]}")
                credentials {
                    username = System.getenv("GITHUB_ACTOR")
                    password = System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }
}

if (canPublish) {
    val lastTag = if (System.getenv("PREVIOUS_TAG") == "NONE") null else System.getenv("PREVIOUS_TAG")
    val newTag = "v$version"

    var generateChangelogTask: TaskProvider<GenerateChangelogTask>? = null

    // Changelog Generation
    if (lastTag != null) {
        val changelogHeader = if (properties.containsKey("changelogHeaderAddon")) {
            val addonProp: String = properties["changelogHeaderAddon"]!!.toString()

            if (addonProp.isNotBlank()) {
                addonProp
            } else {
                null
            }
        } else {
            null
        }

        val changelogFileText = rootProject.file("changelogs/${properties["mod_version"]}.md")
            .takeIf { it.exists() }
            ?.readText()

        generateChangelogTask = tasks.register<GenerateChangelogTask>("generateChangelog") {
            this.lastTag.set(lastTag)
            this.newTag.set(newTag)
            githubUrl.set(properties["github_url"]!!.toString())
            prefixFilters.set(properties["changelog_filter"]!!.toString().split(","))

            val bundledText = if (isBundlingSearchables) {
                """
                |Bundled:
                |  - Where Is It: ${properties["where-is-it_version"]}
                |  - Searchables: ${properties["searchables_version"]}
                |  """.trimMargin()
            } else {
                """
                |Bundled:
                |  - Where Is It: ${properties["where-is-it_version"]}
                |  """.trimMargin()
            }

            // Add a bundled block for each module version
            prologue.set(listOfNotNull(changelogHeader, changelogFileText, bundledText).joinToString(separator = "\n\n"))
        }
    }

    val changelogTextProvider = if (generateChangelogTask != null) {
        provider {
            generateChangelogTask.get().changelogFile.get().asFile.readText()
        }
    } else {
        provider {
            "No Changelog Generated"
        }
    }

    // GitHub Release
    tasks.named<GithubReleaseTask>("githubRelease") {
        generateChangelogTask?.let { dependsOn(it) }

        authorization = System.getenv("GITHUB_TOKEN")?.let { "Bearer $it" }
        owner = properties["github_owner"]!!.toString()
        repo = properties["github_repo"]!!.toString()
        tagName = newTag
        releaseName = "${properties["mod_name"]} $newTag"
        targetCommitish = grgit!!.branch.current().name
        releaseAssets.from(
            tasks["jar"].outputs.files,
            tasks["sourcesJar"].outputs.files,
        )
        subprojects.forEach {
            releaseAssets.from(
                it.tasks["jar"].outputs.files,
                it.tasks["sourcesJar"].outputs.files,
            )
        }

        body = changelogTextProvider
    }

    // Mod Platforms
    if (listOf("CURSEFORGE_TOKEN", "MODRINTH_TOKEN").any { System.getenv().containsKey(it) }) {
        publishMods {
            changelog.set(changelogTextProvider)
            type.set(when(properties["release_type"]) {
                "release" -> ReleaseType.STABLE
                "beta" -> ReleaseType.BETA
                else -> ReleaseType.ALPHA
            })
            modLoaders.add("fabric")
            modLoaders.add("quilt")
            file.set(tasks.named<Jar>("jar").get().archiveFile)

            if (System.getenv().containsKey("CURSEFORGE_TOKEN") || dryRun.get()) {
                curseforge {
                    projectId.set(properties["curseforge_project_id"]!!.toString())
                    accessToken.set(System.getenv("CURSEFORGE_TOKEN"))
                    properties["game_versions_curse"]!!.toString().split(",").forEach {
                        minecraftVersions.add(it)
                    }
                    displayName.set("${properties["prefix"]!!} ${properties["mod_name"]!!} ${version.get()}")
                    listOf("fabric-api", "yacl").forEach {
                        requires {
                            slug.set(it)
                        }
                    }
                    listOf("where-is-it-port").forEach {
                        embeds {
                            slug.set(it)
                        }
                    }
                    listOfNotNull("modmenu", "shulkerboxtooltip", "jade", if (isWthitEnabled) "wthit" else null).forEach {
                        optional {
                            slug.set(it)
                        }
                    }

                    if (isBundlingSearchables) {
                        embeds {
                            slug.set("searchables")
                        }
                    } else {
                        optional {
                            slug.set("searchables")
                        }
                    }
                    clientRequired = true
                    serverRequired = false
                }
            }

            if (System.getenv().containsKey("MODRINTH_TOKEN") || dryRun.get()) {
                modrinth {
                    accessToken.set(System.getenv("MODRINTH_TOKEN"))
                    projectId.set(properties["modrinth_project_id"]!!.toString())
                    properties["game_versions_mr"]!!.toString().split(",").forEach {
                        minecraftVersions.add(it)
                    }
                    displayName.set("${properties["mod_name"]!!} ${version.get()}")
                    listOf("fabric-api", "yacl").forEach {
                        requires {
                            slug.set(it)
                        }
                    }
                    listOf("where-is-it-port").forEach {
                        embeds {
                            slug.set(it)
                        }
                    }
                    listOfNotNull("modmenu", "shulkerboxtooltip", "jade", if (isWthitEnabled) "wthit" else null).forEach {
                        optional {
                            slug.set(it)
                        }
                    }

                    if (isBundlingSearchables) {
                        embeds {
                            slug.set("searchables")
                        }
                    } else {
                        optional {
                            slug.set("searchables")
                        }
                    }
                }
            }
        }
    }
}

tasks.register<UpdateDependenciesTask>("updateModDependencies") {
    mcVersion.set(properties["minecraft_version"]!!.toString())
    loader.set("fabric")
}
