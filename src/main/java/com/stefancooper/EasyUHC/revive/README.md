# Revive

Index:

- [Configuration](#configuration)
- [Reviving](#reviving)

## Configuration

```properties
# Enable revive. False by default
revive.enabled=false|true
# HP that the revived player will start on (default: 4 (2 hearts))
revive.hp=4
# HP that the revived player will lose permanently on each revive (default 4 (2 hearts))
revive.lose.max.health=4
# revive with any player head. True by default
revive.any.head=false|true
```

## Reviving

Players can revive teammates after they have died.

They can be revived by placing a player head on an armor stand.

`revive.any.head` decides if they need the specific player head to revive the player, or not.