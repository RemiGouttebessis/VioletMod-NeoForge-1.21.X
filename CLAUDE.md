# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
./gradlew build              # Compile and package the mod (outputs to build/libs/)
./gradlew runClient          # Launch Minecraft client in dev environment
./gradlew runServer          # Launch Minecraft server in dev environment
./gradlew runData            # Run data generation (outputs to src/generated/resources/)
./gradlew runGameTestServer  # Run game tests
```

- **Java 21** is required
- **Minecraft 1.21.1**, **NeoForge 21.1.224**, **Parchment mappings 2024.11.17**
- Mod ID: `violetmod`, Author: Imaire, License: MIT

## Current Workflow Notes

- Use `TODO.md` for roadmap and planned features
- Use `TASKS.md` for the current implementation target
- Prefer small, incremental changes that preserve existing machine and registry patterns
- put each change in `LOG.md` and at the end of your entire awnser to prompt write a small commit message in the log like `commit : <Message>`

## Architecture Overview

A NeoForge energy-based technology mod (v0.0.1, early development). Two machines currently implemented: `LogicalComputer` and `VioletExtractor`.

### Registry Pattern

All registries use NeoForge's `DeferredRegister` in `registry/`:
- `ModBlocks`, `ModItems`, `ModBlockEntities`, `ModCreativeTabs`, `ModDataComponents`

New blocks/items/block entities must be registered there, then wired into `VioletMod.java` (for capability registration) and the appropriate creative tab.

### Block Entity Pattern

Both machines follow this exact pattern:
1. Extend `BlockEntity`, hold a `ModEnergyStorage` field
2. Implement `getCapability()` returning energy storage for any side
3. Register a static `serverTick()` method via `getTicker()` — all game logic lives here
4. Serialize/deserialize energy to NBT in `saveAdditional()` / `loadAdditional()`

`serverTick()` logic: check energy → if enough, consume it and increment progress → if max progress reached, reset and execute action → otherwise mark inactive. Call `setChanged()` after mutations.

### Energy System

`ModEnergyStorage` (extends NeoForge's `EnergyStorage`):
- One-way input only — `canExtract()` returns `false`
- Accepts a `Runnable` callback invoked on every energy change (used to trigger block state updates)
- Serialize with `serializeNBT()` / `deserializeNBT()`

Capabilities are registered in `VioletMod.java` via `RegisterCapabilitiesEvent` — every new block entity with energy needs an entry there.

### Configuration System

`config/MachineConfig.java` is the active pattern — use static inner classes, one per machine:
```java
public static class MyMachine {
    public static final ForgeConfigSpec.IntValue CAPACITY;
    // ... defined in a static initializer block with range validation
}
```
`Config.java` is legacy/unused — ignore it.

Configs are registered in `ModConfigs.java` and loaded at mod startup. Access values with `.get()` at runtime (not at class load time).

### Block Visual States (VioletExtractor Pattern)

`VioletExtractor` uses an `EnumProperty<VioletExtractorVisualState>` on the block state with 5 states (POWERLESS, IDLE, OBSIDIAN, CRYING, CRYING_RUNNING), each mapped to its own model JSON. The block also has `HorizontalDirectionalBlock.FACING`.

When adding new visual states: update the enum, add block state JSON entries, add model JSON files, add textures.

### Data Components (Itemstack Energy Display)

`ModDataComponents.ENERGY_STORED` (Integer) persists energy on itemstacks. Used in custom `BlockItem` subclasses (see `LogicalComputerBlockItem`) to show energy tooltips via `appendHoverText()`. Synced via `ByteBufCodecs.INT`.

### Assets

- Block states: `src/main/resources/assets/violetmod/blockstates/`
- Models: `src/main/resources/assets/violetmod/models/block/` and `models/item/`
- Textures: `src/main/resources/assets/violetmod/textures/block/`
- Translations: `assets/violetmod/lang/en_us.json` and `fr_fr.json` — both must be updated for new content
- Loot tables: `src/main/resources/data/violetmod/loot_table/blocks/`

### Empty Packages (Planned)

- `network/` — no packets implemented yet; client-server sync not needed until GUIs are added
- `datagen/` — no data generation yet; all assets are hand-authored
- `client/` — no client-only code yet