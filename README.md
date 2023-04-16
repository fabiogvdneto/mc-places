## Description

Places is a minecraft plugin which allows server administrators to save important locations (warps) so that players can
teleport to them later.
Your players can also save their own locations (homes).
Every place (warp or home) can be either open (everyone can teleport to it) or closed (limited to some players).
Warps are global places, meaning they are not owned by someone.
In the other hand, homes are private property and if closed, only the owner and server administrators can teleport to
it.

## Features

- Support for places (warps and homes).
- Places can either be open (public) or closed (private).
- Delayed teleportation with per-second feedback.
- Movement and damage detection.
- Active command blocker.
- All messages customizable.
- Prefix `/warp` is optional (example: `/warp spawn` or `/spawn`).
- JSON messages when listing warps/homes or teleporting.

## Commands

| Syntax                    | Description                       |
|:--------------------------|:----------------------------------|
| home \<name> \[player]    | Teleport you to (your) home.      |
| homes \[player]           | List all (your) homes.            |
| sethome \<name> \[player] | Create new home.                  |
| delhome \<name> \[player] | Delete home.                      |
| warp \<name>              | Teleport to warp.                 |
| warps                     | List all warps available for you. |
| setwarp \<name>           | Create new warp.                  |
| delwarp \<name>           | Delete warp.                      |

## Permissions

| Key                     | Description                                              |
|-------------------------|----------------------------------------------------------|
| places.limit.\<integer> | The maximum number of homes the player can own.          |
| places.delay.\<seconds> | The amount of time each teleportation will take.         |
| places.warp.\<name>     | Allows the player to teleport to the warp named \<name>. |
| places.command.setwarp  | Allows the use of setwarp.                               |
| places.command.delwarp  | Allows the use of delwarp.                               |

## Configuration (settings.yml)

| Entry              |     Type     | Description                                                                  |
|--------------------|:------------:|------------------------------------------------------------------------------|
| language           |    string    | Which language to use.                                                       |
| max-home-limit     |   integer    | The maximum number of homes a player can have. See `places.limit.<integer>`. |
| max-delay          |   integer    | The maximum delay a teleportation can take. See `places.delay.<seconds>`.    |
| movement-allowed   |   boolean    | If false, movement will not be allowed during teleportation.                 |
| damage-allowed     |   boolean    | If false, damage will not be allowed during teleportation.                   |
| cmdblocker.enabled |   integer    | 0 to disable, 1 for whitelist and -1 for blacklist.                          |
| cmdblocker.list    | string array | List of commands to block (or not).                                          |

## Files

| Filename             | Description                      |
|----------------------|----------------------------------|
| settings.yml         | All settings can be found here.  |
| homes.json           | Where data is stored.            |
| language-\<code>.yml | Messages can be customized here. |
