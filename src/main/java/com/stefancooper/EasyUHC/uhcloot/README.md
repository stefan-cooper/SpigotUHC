# UHC Loot

This file provides the details about the UHC Loot feature

Index:

- [Configuration](#configuration)
- [What is UHC Loot?](#what-is-uhc-loot)
- [Nether chest](#nether-chest)
- [Low-Tier Loot](#low-tier-loot)
- [Mid-Tier Loot](#mid-tier-loot)
- [High-Tier Loot](#high-tier-loot)

## Configuration

```properties
# Enable loot chest
loot.chest.enabled=false|true
# How frequently loot is regenerated (in seconds)
loot.chest.frequency=300
# % odds of a high loot item spawning (per spin)
loot.chest.high.loot.odds=5
# % odds of a mid loot item spawning (per spin)
loot.chest.mid.loot.odds=40
# items/spins per gen
loot.chest.spins.per.gen=5
# time in seconds before the first loot chest is spawned after the UHC has started (default 0 seconds)
loot.chest.grace.period=0
# X coordinate for the loot chest
loot.chest.x.range=-75,75
# Z coordinate for the loot chest
loot.chest.z.range=-75,75
```

## What is UHC Loot?

UHC Loot is a feature which spawns in a chest around a configured area that generates varied loot for players to claim.

The UHC Loot chest will spawn at the highest possible Y block within the configured area.

When a high tier loot item spawns in the chest, a message is alerted into the server. Additionally, the 'ITEM_GOAT_HORN_SOUND_7' is played to each player.

A UHC Loot chest can be identified from a distance by the purple beam of particles that appears above it.

## Nether chest

The nether loot chest spawns around the same area as the overworld chest, with the exception that it will always spawn within the 32-48 Y block range.

A nether loot chest has no alert for a high tier loot item.

The nether loot chest spawns double the items as the overworld chest, thus has double the odds of getting a high tier loot chest.

## Low-tier Loot

- APPLE
- x4 IRON_INGOT
- x3 STRING
- x8 COAL
- BOOK
- OBSIDIAN
- x5 COOKED_BEEF
- NETHERITE_HOE
- BUCKET
- x5 GUNPOWDER
- GOAT_HORN
- x8 EXPERIENCE_BOTTLE
- x8 NETHER_WART
- CAULDRON
- x32 ARROW
- x3 WIND_CHARGE
- x16 SNOWBALL
- ORANGE_HARNESS
- SADDLE
- x8 PAPER
- x5 PUFFERFISH

## Mid-tier Loot

- x4 TNT 
- SPYGLASS
- x3 DIAMOND
- IRON_CHESTPLATE (THORNS II, PROTECTION III)
- IRON_BOOTS (FEATHER FALLING III, PROTECTION II)
- IRON_HELMET (RESPIRATION III, PROTECTION II)
- IRON_LEGGINGS (SWIFT_SNEAK III, PROTECTION II)
- BOOKSHELF
- x32 SPECTRAL_ARROW
- DIAMOND_HORSE_ARMOR
- DIAMOND_NAUTILUS_ARMOR
- ENDER_PEARL
- GOLD_BLOCK
- ANVIL
- BREWING_STAND
- POTION OF FIRE RESISTANCE
- POTION OF INVISIBILITY
- POTION OF SWIFTNESS
- SPLASH_POTION OF SLOWNESS
- SPLASH_POTION OF WEAKNESS
- DRIED_GHAST
- HORSE_SPAWN_EGG
- END_CRYSTAL
- GOLDEN_SWORD (SHARPNESS V, KNOCKBACK II)

## High-tier Loot

The following items can be found in from a high tier spin:

- MACE
- BOW (INFINITY)
- DIAMOND_SWORD (FIRE ASPECT I)
- DIAMOND_AXE (SHARPNESS I)
- PLAYER_HEAD
- DIAMOND_BLOCK
- TRIDENT (LOYALTY I)
- ELYTRA
- POTION OF STRENGTH
- POTION OF HEALING
- SPLASH_POTION OF INSTANT HEALTH
- SPLASH_POTION OF INSTANT DAMAGE