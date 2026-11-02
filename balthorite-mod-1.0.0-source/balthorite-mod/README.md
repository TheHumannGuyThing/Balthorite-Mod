# Balthorite Mod (NeoForge 1.21.1)

Tired of stopping at Netherite? Balthorite Mod adds a new tier after it.

## What's in it

| Thing | How you get it |
|---|---|
| **Alloy Forge** (block) | Craft a **Crafting Table** surrounded by 8 **Echo Shards** (3x3, table in the centre) |
| **Balthorite Upgrade Template** | Found in **End City** treasure chests (~25%) and **Ancient City** chests (~12% per chest). Can be duplicated: 7 Diamonds + Template + End Stone -> 2 Templates (crafting table) |
| **Balthorite Ingot** (dark, lapis-blue, fire-resistant) | In the Alloy Forge: **Template (left) + Netherite Ingot (middle) + Echo Shard (right)** |

The Alloy Forge GUI copies the smithing table's slot layout, with a dark / lapis-trimmed
look, faint "what goes here" icons in empty slots, hover hints and a glowing arrow when a recipe is ready.
Each craft consumes one of each input (like vanilla smithing).

## Build the .jar

Requirements: **JDK 21** and internet access (Gradle downloads Minecraft + NeoForge once).

**Option A - IntelliJ IDEA (easiest):** `File > Open` this folder, let Gradle sync, open the
Gradle panel and run `Tasks > build > build`.

**Option B - command line:** install Gradle 8.8+ and run, inside this folder:

    gradle build

The mod jar ends up in `build/libs/balthorite-mod-1.0.0.jar`.

**Option C - no local setup (GitHub):** push this folder to a GitHub repository. The included
workflow (`.github/workflows/build.yml`) builds the mod automatically; open the repo's
**Actions** tab, pick the latest run and download the `balthorite-mod-jar` artifact (it contains the .jar).
You can also start it by hand with **Run workflow**.

To test in-game without building: `gradle runClient`.

## Install

Put the jar into the `mods` folder of a **Minecraft 1.21.1 + NeoForge 21.1.x** instance.

## Tweaking

* Template drop rates: `src/main/resources/data/balthorite/loot_modifiers/*.json` (`chance`)
* Alloy Forge recipe: `src/main/resources/data/balthorite/recipe/balthorite_ingot.json`
  (it is a normal data-driven recipe type, `balthorite:alloy_forging`, so datapacks can add more)
* Don't want template duplication? Delete `balthorite_upgrade_template_duplication.json`.
* Colours/art: edit `tools/generate_textures.py` and run `python3 tools/generate_textures.py`
  (needs Pillow), or just replace the PNGs in `assets/balthorite/textures/`.

## Troubleshooting

* Gradle can't find plugin `net.neoforged.moddev` 2.0.78 or NeoForge `21.1.172`:
  bump the versions in `build.gradle` / `gradle.properties` to the latest available
  (keep the NeoForge version on the `21.1.x` line for Minecraft 1.21.1).
* Compile errors: NeoForge occasionally shuffles signatures between 21.1.x builds;
  every class is small and commented, so they are quick to adjust.

## Layout

    src/main/java/com/balthorite/
      BalthoriteMod.java          entry point
      registry/                   blocks, items, menu, recipe type, loot modifier, creative tab
      block/AlloyForgeBlock.java  the station
      menu/AlloyForgeMenu.java    slots + crafting logic
      client/                     screen + client registration
      recipe/                     alloy_forging recipe type
      loot/AddItemLootModifier    puts templates into chests
    src/main/resources/           models, textures, lang, recipes, loot, tags
    tools/generate_textures.py    procedural pixel art
