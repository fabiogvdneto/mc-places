# Introduction

Places is a minecraft plugin that aims to provide the ability for a
player to teleport to other places, which are identified by a name,
and can be a warp, home, or any online player.

A **warp** is a public place and can only be set by server administrators.
Warps are closed by default, which means administrators and players with
the right permission can teleport to it. Open warps are available to
everyone.

A **home** is a private place and can be set by anyone. Homes are closed
by default, meaning only administrators and their owner can teleport to it.
Open homes are available to anyone through `/home <name> <player>`.

### Features

- Warps (public places).
- Homes (private places).
- Permission-based home limit.
- Permission-based teleportation delay/warmup.
- Efficient detection of movement and damage during teleportation.
- Prevent command execution while teleporting.
- Cancel teleportation before it happens.
- Send teleportation requests to other players (/tpa).
- Auto-save warps and homes.
- Auto-purge user data.
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

```
/spawn
/setspawn
/warp <name>
/warps
/setwarp <name>
/delwarp <name>
/home <name>
/homes
/sethome <name>
/delhome <name>
/tpa <player>
/tphere <player>
/tpaccept
/tpdeny
/tpcancel
```

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