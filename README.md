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
# Name of the minecraft world
world.name=world
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
```

Optional properties:

```properties
# Action to undertake when a player dies ("spectate" | "kick")
on.death.action=spectate
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
# (optional) drop player heads who are killed that can be crafted into golden apples
player.head.golden.apple=false|true
# (optional) show the current progress of the world border in the boss bar
world.border.in.bossbar=false|true
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