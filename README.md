# ChestTracker_Update

Workspace for a maintained **Chest Tracker** build on Minecraft **26.3** (Fabric).

## Layout

| Path | What it is |
|---|---|
| [`ChestTracker-26.3/`](ChestTracker-26.3) | **The active Gradle project.** Builds, runs and publishes the mod |
| `resources/original/ChestTracker-1.21.4/` | Untouched reference copy of the original mod (JackFred, last updated for 1.21.4) |
| `resources/port/ChestTracker-26.3/` | Untouched reference copy of the unofficial 26.3 port (ponuing), the base for the active project |

Each reference copy holds both the extracted folder and the `.zip` it came from, so the nesting
(`resources/<name>/<name>/…`) is expected. They are kept as-is for diffing and are not part of the build.

## Quick start

Requires **JDK 25**.

```shell
cd ChestTracker-26.3
./gradlew build        # gradlew.bat build on Windows
```

The jar lands in `ChestTracker-26.3/build/libs/`, with Where Is It and Searchables nested inside it. Run
`./gradlew runClient` for a dev client.

## How the active project differs from the port

The port was a working but token-dependent snapshot. This version:

- builds with **no credentials** — Where Is It comes from the Modrinth maven and JackFredLib is vendored in `libs/`,
  instead of both coming from GitHub Packages with a `GITHUB_TOKEN`;
- gates the WTHIT integration on the `wthit_version` property instead of a hardcoded source set exclusion, and hides
  the WTHIT config toggle while the integration is not compiled in (there is no 26.3 build of `wthit-api`);
- drops the unused `dotenv` plugin and unused maven repositories, and fixes the POM `properties` resolution;
- is re-branded as its own project (mod name, contact links, POM, publishing targets) while crediting JackFred
  (original) and ponuing (26.3 port).

See [`ChestTracker-26.3/changelogs/3.0.0.md`](ChestTracker-26.3/changelogs/3.0.0.md) for the full list.

Before publishing, `github_owner` / `github_repo` / `github_url` and the Modrinth / CurseForge project IDs in
[`ChestTracker-26.3/gradle.properties`](ChestTracker-26.3/gradle.properties) must point at your own projects.

## Credits

Original mod by [JackFred](https://github.com/JackFred2/ChestTracker), 26.3 port by
[ponuing](https://github.com/ponuing/ChestTracker). Licensed under [LGPL-3.0-only](LICENSE).