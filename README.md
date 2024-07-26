# AdvancedAntiKillAura
AdvancedAntiKillAura is a powerful and sophisticated Minecraft spigot plugin designed to safeguard your server from the menace of Kill Aura and other combat-related hacks.

---

## Installation

1. Download the `AdvancedAntiKillAura.jar` from [here](https://www.spigotmc.org/resources/advancedantikillaura.118365/)

2. Add the `.jar` file into your plugins folder.

3. Restart the server to enable the plugin.

## Plugins configuration

After making your changes, restart the server to apply them.

* config.yml

        # Change it to true to enable the plugin
        enable: false

        # Set to true to kick the player for using kill aura
        kick-player: true

        # Message to be shown to the player when they are kicked
        kick-message: "You are kicked for using kill aura"

        # Set to true to permanently ban the player for using kill aura
        ban-player: false

        # Message to be shown to the player when they are banned
        ban-message: "You are banned for using kill aura"

        # Set to true to temporarily ban the player for using kill aura (You will need essentialsX for this to work)
        tempban-player: false

        # Duration of the temporary ban in seconds
        seconds: 1200

        # Message to be shown to the player when they are temporarily banned
        tempban-message: "You are banned for using kill aura"

## Permissions

 * antikillaura.ignore
    
    * Players with this permission are ignored from the plugin.
    

`Note`: Players with op are ignored by the plugin.
