# Porting Villager Trade Reroller to Minecraft 26.2

## What I changed

1. **`main/resources/fabric.mod.json`**
   - `minecraft` dependency: `~26.1.2` → `~26.2`
   - `fabricloader` dependency: `>=0.19.2` → `>=0.19.3`

2. **`gradle.properties`** (new version, replace your existing file)
   - `minecraft_version` → `26.2`
   - `loader_version` → `0.19.3`
   - `fabric_api_version` → `0.154.0+26.2` (latest release as of this writing)
   - `yacl_version` → `3.9.5+26.2-fabric`
   - `modmenu_version` → `20.0.0-beta.4` (this is currently the newest build for 26.2 — ModMenu hasn't
     cut a full stable release for 26.2 yet, only betas. Check
     https://modrinth.com/mod/modmenu/versions for anything newer before you build.)

3. **`build.gradle` / `settings.gradle`** — I didn't have these files (you only sent `src/main`), so I
   couldn't edit them directly. If your `loom` plugin version is pinned in `settings.gradle` (e.g.
   `id 'fabric-loom' version '1.16-SNAPSHOT'`), bump it to **Loom 1.17**, and make sure your Gradle
   wrapper is on **9.5.1** (`gradlew wrapper --gradle-version 9.5.1`) — these are the tooling versions
   Fabric recommends for building against 26.2. If your `mappings` block uses
   `loom.officialMojangMappings()` (which your access widener's `official` header suggests it does),
   no separate mappings version is needed — it tracks `minecraft_version` automatically.

## What I did *not* need to change

I went through every source file (`MerchantOffersPacketMixin`, `MerchantScreenHandlerAccessor`,
`RerollController`, `TradeUtil`, `OffersStore`, `HotbarUtil`, `RerollerHud`, the YACL config screen, etc.)
against everything documented for the 26.2 "Chaos Cubed" release — the official patch notes, the wiki's
bug-fix list, and the Fabric API/YACL/ModMenu changelogs for this version. None of it touches:
- `ClientboundMerchantOffersPacket` / `MerchantOffers` / `MerchantOffer` / `MerchantMenu`
- `EnchantmentHelper`, `Registries.ENCHANTMENT`
- `Inventory.selected`, `GuiGraphicsExtractor` / `HudElement`

26.2's modder-facing changes are centered on the new Vulkan rendering backend, the entity-predicate JSON
format, and some fluid/tag APIs — none of which this mod uses. So the actual Java logic in this mod should
carry over unchanged; this looks like a version-bump port rather than a rewrite.

## What I could *not* verify

I don't have network access or a Minecraft/Fabric toolchain in my environment, so I could not actually
run `gradlew build` or launch the game against 26.2 to confirm this compiles and works. Please:
1. Apply these changes (or use the zip I've put together with them already applied).
2. Run `./gradlew build` yourself with a real 26.2 dev environment.
3. Test in-game, particularly the trade-reading mixin and HUD, since those are the most version-sensitive
   parts of the mod.

If the build fails on something specific, paste me the error and I'll help fix it.
