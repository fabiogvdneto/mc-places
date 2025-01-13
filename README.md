# Introduction

Places is a minecraft paper plugin that gives players the ability
to teleport to pre-defined locations, called places. Places are
identified by a name and are attached to a given location. In
order to create a place, you can either create a warp or create a
home, depending on what you want.

**Warps** are public places designed to be accessed by everyone,
as long as the player has permission to do so. Is the server
administrator's responsibility to create, delete, and configure
the warps.

**Homes**, on the other hand, are private places owned by someone.
The owner has full control over the homes he creates, and can decide
whether other players can visit their homes or not.

### Features

- Warps (public places).
- Homes (private places).
- Per-player home limit (permission-based).
- Per-player teleportation delay/warmup (permission-based).
- Efficient detection of movement and damage during teleportation.
- Prevent command execution while teleporting.
- Cancel teleportation before it happens.
- Send teleportation requests to other players (/tpa).
- Data auto-saving (automatically save data to prevent data loss on crashes).
- Data auto-purging (automatically delete data that is no longer needed).
- All messages are customizable.
- All messages support json-style formatting (powered by Adventure API).

### Commands

| Name                   | Description                               |
|------------------------|-------------------------------------------|
| `spawn`                | Teleport to the spawn.                    |
| `warps`                | List all warps available to you.          |
| `warp <name>`          | Teleport to the specified warp.           |
| `setwarp <name>`       | Create a new warp.                        |
| `delwarp <name>`       | Delete a warp.                            |
| `homes`                | List all your homes.                      |
| `home <name> [player]` | Teleport to the specified home.           |
| `sethome <name>`       | Create a new (private) home.              |
| `delhome <name>`       | Delete a home.                            |
| `tpa <player>`         | Send a teleportation request to a player. |
| `tphere <player>`      | Teleport a player to your location.       |
| `tpaccept [player]`    | Accept a teleportation request.           |
| `tpdeny [player]`      | Deny a teleportation request.             |
| `tpcancel`             | Cancel the teleportation.                 |

### Permissions

| Name                              | Description                                             |
|-----------------------------------|---------------------------------------------------------|
| places.admin                      | All permissions.                                        |
| places.command.setwarp            | Allow to execute /setwarp.                              |
| places.command.delwarp            | Allow to execute /delwarp.                              |
| places.command.delhome.others     | Allow to execute /delhome to other players.             |
| places.command.sethome.others     | Allow to execute /sethome to other players.             |
| places.command.tpa                | Allow to execute /tpa.                                  |
| places.command.tphere             | Allow to execute /tphere.                               |
| places.warps.warp.<name>          | Allow to teleport to <name> even if the warp is closed. |
| places.homes.limit.<number>       | Allow to create a maximum of <number> homes.            |
| places.teleporter.delay.<seconds> | Wait <seconds> before actually teleporting.             |

### TODO

- Toggle (disable/enable) teleportation requests.
- Ignore teleportation requests from specific players.