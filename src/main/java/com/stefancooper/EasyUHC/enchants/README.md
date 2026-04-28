# Enchantments

Index:

- [Configuration](#configuration)
- [Shields](#shields)
- [TNT](#tnt)

## Configuration

```properties
# enable shield enchantments
additional.enchants.shield=false|true
# enable TNT enchantments
additional.enchants.tnt=false|true
```

## Shields

Shields can have the following enchantments applied.

`Knockback I` -> `Knockback II` - Applies knockback effect to enemy attacks when blocking

`Thorns I` -> `Thorns III` - Applies thorns effect to enemy attacks when blocking

These enchantments are scaled with bookcases as such:

| Bookcases    | Max Knockback Available | Max Thorns Available |
|--------------|-------------------------|----------------------|
| 0 Bookcases  | Knockback I             | Thorns I             |
| 1 Bookcase   | Knockback II            | Thorns II            |
| 2+ Bookcases | Knockback II            | Thorns III           |

* Note - If `evolving.shields.enabled` is set to `true`, it will override and make enchanting shields via the enchanting table not possible.

## TNT

TNT can have the following enchantments applied.

`Quickboom I` -> `Quickboom IV` - Decrease the fuse time of your TNT.

`Blastwave I` -> `Blastwave IV` - Increase the blast and damage of your TNT.

These enchantments are scaled with bookcases as such:

| Bookcases    | Max Quickboom Available | Max Blastwave Available |
|--------------|-------------------------|-------------------------|
| 0 Bookcases  | Quickboom I             | Blastwave II            |
| 1 Bookcase   | Quickboom III           | Blastwave III           |
| 2+ Bookcases | Quickboom IV            | Blastwave IV            |