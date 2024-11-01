# SpigotUHC

Run a UHC on your Spigot Server.

Want to contribute? See [Contributing](#contributing)

## Installing

1. Download the plugin - we don't currently have any accessible download links. Contact @stefan.cooper or any other contributor to retrieve a .jar file

2. Add the plugin to your `plugins` folder in your Spigot server

## Configure your UHC

You can configure your UHC in the server (see [Configuring](#configuring)) or you can create a `uhc_config.properties` file inside your `plugins` folder. One of these will be created anyway after starting the server with this plugin installed.

The following configurations are available for managing your UHC:

```properties
# Name of the minecraft overworld
world.name=world
# Name of the minecraft nether world
nether.world.name=world_nether
# Name of the minecraft end world
end.world.name=world_end
# Difficulty of the game when UHC is live
difficulty=EASY
# Countdown to start the game after UHC start command issued
countdown.timer.length=5
# Grace period time (in seconds) before PVP is enabled
grace.period.timer=600
# Minimum distance (in blocks) that teams/players will be spread at start of UHC
spread.min.distance=500
# World border center X coord
world.border.center.x=0
# World border center Z coord
world.border.center.z=0
# Final size of the world border at the end of the UHC
world.border.final.size=500
# Grace period time (in seconds) before the border will begin to shrink
world.border.grace.period=3600
# Initial size world border at start of the UHC
world.border.initial.size=2000
# Time (in seconds) to shrink from the initial size to the final size
world.border.shrinking.period=7200
# Action to undertake when a player dies ("spectate" | "kick")
on.death.action=spectate
```

Optional properties:

Teams:

```properties
# Team Blue players (comma seperated e.g team.orange=player1,player2) - Note: This is caps sensitive
team.blue=shurf
# Team Orange players (comma seperated e.g team.orange=player1,player2) - Note: This is caps sensitive
team.orange=JawadAJamil
# Team Red players (comma seperated e.g team.orange=player1,player2) - Note: This is caps sensitive
team.red=badTHREEEK
# Team Green players (comma seperated e.g team.orange=player1,player2) - Note: This is caps sensitive
team.green=chuckle
# Team Yellow players (comma seperated e.g team.orange=player1,player2) - Note: This is caps sensitive
team.yellow=StetoGuy
# Team Pink players (comma seperated e.g team.orange=player1,player2) - Note: This is caps sensitive
team.pink=SimplySquare
# Team Purple players (comma seperated e.g team.orange=player1,player2) - Note: This is caps sensitive
team.purple=Rking42
```

Revive:

```properties
# Enable revive
revive.enabled=true
# HP that the revived player will start on (default: 2 hearts)
revive.hp=4
# HP that the revived player will lose permanently on each revive (default 2 hearts)
revive.lose.max.health=4
# How long it takes for the revive to happen
revive.time=90
# X coordinate for the center of the revive location
revive.location.x=-30
# Y coordinate for the center of the revive location
revive.location.y=64
# Z coordinate for the center of the revive location
revive.location.z=11
# Diameter/size of the revive location
revive.location.size=10
```

Miscellaneous:

```properties
# (optional) drop player heads who are killed that can be crafted into golden apples
player.head.golden.apple=false|true
# (optional) show the current progress of the world border in the boss bar
world.border.in.bossbar=false|true
# (optional) enable timestamps of notable events
enable.timestamps=false|true
# (optional) generate a random final location within the initial world border
random.final.location=false|true
```

## Commands

The following commands are available in game:

### Configuring

#### Set config value:

`/uhc set world.border.initial.size=500`

#### Set multiple config values:

`/uhc set world.border.initial.size=500 world.border.final.size=250`

#### View full config:

`/uhc view config`

#### View a specific config value:

`/uhc view world.border.initial.size`

### Running

#### Start the UHC:

`/uhc start`

#### Start a UHC midway/resume a UHC

`/uhc resume <minutes>`

e.g: `/uhc resume 30`

#### Manually enable/disable PVP:

`/uhc pvp <true|false>`

e.g: `/uhc pvp true`

#### Late start player midway during a UHC

`/uhc latestart <username>`

e.g: `/uhc latestart shurf`

#### End/cancel the UHC:

`/uhc cancel`

## Contributing

Contact @stefan.cooper for information about contributing. This is an open source project so if you feel like you want to add something, just raise a PR!

### Prereqs

- Java 21
- Maven
- Git

### Getting started

1. Clone the repo

2. Run the following command to build the spigot server dev env

   ```
   REFRESH_BUILD=true ./setup_server.sh
   ```

   Note: This may take a long time (10-15min)

### Running

1. Run Spigot server

   ```
   ./run_server.sh
   ```