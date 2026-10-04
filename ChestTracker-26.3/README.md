# Chest Tracker (26.3)

Client-sided storage system for Minecraft **26.3** on Fabric. Press **Y** to search for items, and **GRAVE** (`) to open
the GUI.

An independently maintained continuation of Chest Tracker, based on the 26.3 port work. Not affiliated with, nor
endorsed by, the original author.

## Requirements

| | |
|---|---|
| Minecraft | 26.3 (`>=26.3`) |
| Fabric Loader | `>=0.18.4` — the build targets 0.19.5 |
| Java | 25+ |
| Fabric API | required |
| [YACL](https://modrinth.com/mod/yacl) | `>=3.9.0` |

[Where Is It](https://modrinth.com/mod/where-is-it-port) (which embeds JackFredLib) and
[Searchables](https://modrinth.com/mod/searchables) are downloaded and nested into the released jar, so you do not have
to install them.

## Features

- Saving of items on the client, so it works on Realms and multiplayer servers.
- Displays custom names of containers in the world (can be disabled under the Memory Bank Settings).
- Integration with:
  - [REI](https://modrinth.com/mod/rei), [JEI](https://modrinth.com/mod/jei) and [EMI](https://modrinth.com/mod/emi) via
    Where Is It — see its usage section.
  - [Shulker Box Tooltip](https://modrinth.com/mod/shulkerboxtooltip) — show ender chest contents on the client.
  - [Jade](https://modrinth.com/mod/jade) — show the contents of the container you are looking at.
  - [Litematica](https://modrinth.com/mod/litematica) — quick search buttons, and adding ender chest and nearby
    containers to the material list.
  - [Mod Menu](https://modrinth.com/mod/modmenu) — config screen entry.
- Custom handling for [Hypixel Skyblock](https://hypixel.net/) (private island + ender chest) and Hypixel SMP.

Memories are stored client-side under `<game directory>/chesttracker`, in the backend format chosen in the config
(NBT, JSON or the bundled `MEMORY` format).

### Known limitations

- **WTHIT is not supported.** There is no build of `wthit-api` for 26.3 yet (the latest targets 26.2), so
  `src/client/java/red/jackf/chesttracker/impl/compat/mods/wthit` is excluded from compilation and its config toggle is
  hidden. To re-enable it, uncomment `wthit_version` in `gradle.properties` — the source set exclusion, the compile-only
  dependency and the config option are all wired to that property.

## Usage

Press **Y** to search by an item stack (this uses Where Is It's keybind).

Press **GRAVE** (`) to open the main GUI. In the GUI, click an item to search for it in your current dimension. Use the
search bar and its various filters to narrow down your search.

## Building

Toolchain: Gradle 9.6.0 (wrapper), Fabric Loom 1.15-SNAPSHOT, official Mojang mappings, Java 25.

```shell
git clone https://github.com/Strefiz/ChestTracker-26.3.git
cd ChestTracker-26.3
./gradlew build          # or gradlew.bat build on Windows
```

The jar is written to `build/libs/`. Loom 1.15 with official Mojang mappings produces the final jar directly, so there is
no separate `remapJar` step.

Other useful tasks:

| Task | Purpose |
|---|---|
| `./gradlew runClient` | Launch a dev client with the mod, Jade, Mod Menu and Litematica loaded |
| `./gradlew check` | Validation tasks (currently the access widener) |
| `./gradlew updateModDependencies` | Refresh the mod dependency versions in `gradle.properties` |

### Dependencies without a public maven

`libs/` holds jars used for compilation only — none of these are shipped:

| Jar | Why |
|---|---|
| `litematica-fabric-26.3-0.29.0.jar`, `malilib-fabric-26.3-0.30.1.jar` | Litematica publishes no 26.3 build to a maven |
| `jackfredlib-0.10.6+26.3.jar` + `jackfredlib-*.jar`, `httpcore-4.4.16.jar` | JackFredLib has no public maven; extracted from the published Where Is It jar, which bundles it |

JackFredLib is a jar-in-jar aggregator, so the individual module jars had to be pulled out of it for the compiler to see
`red.jackf.jackfredlib.*`. At runtime the real, nested copies from Where Is It are used instead.

`where-is-it-port` itself is resolved from the Modrinth maven, so **no tokens or private maven credentials are needed to
build**.

## Publishing

Wired for GitHub Releases, GitHub Packages maven, Modrinth and CurseForge. Before publishing:

1. Set `github_owner`, `github_repo` and `github_url` in `gradle.properties`.
2. Set `modrinth_project_id` and `curseforge_project_id`.
3. Provide `MODRINTH_TOKEN` / `CURSEFORGE_TOKEN` / `GITHUB_TOKEN` and set `RELEASE=true`.

Release notes come from commit messages prefixed with `[feat]`, `[fix]`, `[change]`, `[update]`, `[docs]` or
`[translations]`, prefixed by `changelogs/<mod_version>.md` when that file exists. `.github/workflows/publish.yml` runs
this end to end.

Translations are managed with Crowdin via `crowdin.yml`; `src/client/resources/assets/chesttracker/lang/en_us.json` is
the source of truth.

## Credits

| | |
|---|---|
| [JackFred](https://github.com/JackFred2/ChestTracker) | Original mod (up to 1.21.4) |
| [ponuing](https://github.com/ponuing/ChestTracker) | Unofficial 26.3 port, the base for this version |
| [WinTone01](https://github.com/) | Turkish translations |
| [BlameJared](https://github.com/BlameJared) | Searchables |
| [JackFred](https://github.com/JackFred2/WhereIsIt) | Where Is It |

Licensed under [LGPL-3.0-only](LICENSE).