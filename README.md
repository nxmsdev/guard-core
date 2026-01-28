# GuardCore

An advanced PaperMC world protection and modification plugin for Minecraft 1.21.

## Features

- **Block Despawn** - Automatic removal of player-placed blocks after configurable time
- **Block Destruction Protection** - Only player-placed blocks can be destroyed
- **Disallowed Blocks** - Prevent placing specific blocks
- **Disallowed Entities** - Prevent spawning specific entities
- **Entity Limits** - Limit the number of specific entities per world
- **Entity Spawn Times** - Restrict entity spawning to specific real-world hours
- **Entity Spawn Points** - Define custom spawn points for entities
- **Water/Lava Flow Control** - Enable or disable fluid spreading
- **Redstone Mechanism Blocking** - Block redstone components usage
- **Admin Bypass System** - Comprehensive bypass commands for administrators
- **Language Support** - Polish and English, selectable in config.yml
- **Per-World Configuration** - All settings are configurable per world

## Permissions

| Permission | Description |
|:-----------|:------------|
| guardcore.command | Access to /guardcore and /gc commands |
| guardcore.set | Access to /gc set commands |
| guardcore.add | Access to /gc add commands |
| guardcore.remove | Access to /gc remove commands |
| guardcore.info | Access to /gc info commands |
| guardcore.help | Access to /gc help command |
| guardcore.reload | Access to /gc reload command |
| guardcore.bypass | Access to /gc bypass commands |
| guardcore.admin | Grants access to all GuardCore commands |

## Commands

Main commands: /guardcore or /gc

### Set Commands

| Command | Description |
|:--------|:------------|
| /gc set blockDespawn <world> <true/false> | Enable/disable block despawn |
| /gc set blockDespawnTime <world> <time> | Set block despawn time (e.g., 1d2h30m15s) |
| /gc set blockDestruction <world> <true/false> | Enable/disable block destruction protection |
| /gc set waterFlow <world> <true/false> | Enable/disable water flow |
| /gc set lavaFlow <world> <true/false> | Enable/disable lava flow |
| /gc set blockRedstoneMechanism <world> <true/false> | Enable/disable redstone blocking |
| /gc set entitySpawnTime <world> <entity> <from> <to> | Set entity spawn time range (e.g., 08:00 20:00) |

### Add Commands

| Command | Description |
|:--------|:------------|
| /gc add entityLimit <world> <entity> <limit> | Set entity limit |
| /gc add entitySpawnPoint <world> <entity> <name> | Add entity spawn point at your location |
| /gc add disallowedEntity <world> <entity> | Add entity to disallowed list |
| /gc add disallowedBlock <world> <block> | Add block to disallowed list |

### Remove Commands

| Command | Description |
|:--------|:------------|
| /gc remove entityLimit <world> <entity> | Remove entity limit |
| /gc remove entitySpawnTime <world> <entity> | Remove entity spawn time restriction |
| /gc remove entitySpawnPoint <world> <name> | Remove entity spawn point |
| /gc remove disallowedEntity <world> <entity> | Remove entity from disallowed list |
| /gc remove disallowedBlock <world> <block> | Remove block from disallowed list |

### Info Commands

| Command | Description |
|:--------|:------------|
| /gc info blockDespawn <world> | Show block despawn settings |
| /gc info blockDestruction <world> | Show block destruction settings |
| /gc info waterFlow <world> | Show water flow settings |
| /gc info lavaFlow <world> | Show lava flow settings |
| /gc info blockRedstoneMechanism <world> | Show redstone mechanism settings |
| /gc info entityLimit <world> | Show all entity limits for world |
| /gc info entityLimit <world> <entity> | Show entity limit for specific entity |
| /gc info entitySpawnTime <world> <entity> | Show entity spawn time settings |
| /gc info entitySpawnPoint <world> | Show all spawn points for world |
| /gc info entitySpawnPoint <world> <name> | Show specific spawn point details |
| /gc info disallowedEntity <world> | Show disallowed entities list |
| /gc info disallowedBlock <world> | Show disallowed blocks list |

### Bypass Commands

| Command | Description |
|:--------|:------------|
| /gc bypass disallowedBlocks [true/false] | Bypass disallowed blocks restriction |
| /gc bypass blockDespawn [true/false] | Bypass block despawn (blocks won't disappear) |
| /gc bypass blockDestruction [true/false] | Bypass block destruction protection |
| /gc bypass waterFlow [true/false] | Bypass water flow restriction |
| /gc bypass lavaFlow [true/false] | Bypass lava flow restriction |

### Other Commands

| Command | Description |
|:--------|:------------|
| /gc help | Show help message |
| /gc help <command> | Show help for specific command |
| /gc reload | Reload configuration and messages |

## Configuration

### Language Selection

In config.yml:
```
language: pl   # or en
```

### Time Format

Block despawn time supports multiple formats:

- 1d - 1 day
- 2h - 2 hours
- 30m - 30 minutes
- 15s - 15 seconds
- 500ms - 500 milliseconds
- 20t - 20 ticks
- Combined: 1d2h30m15s - 1 day, 2 hours, 30 minutes, 15 seconds

### Entity Spawn Time Format

Uses 24-hour real-world time format:

- 08:00 - 8:00 AM
- 20:30 - 8:30 PM

### Example World Configuration

worlds:
world:
blockDespawn:
enabled: true
time: "1d0h0m0s"
waterFlow: true
lavaFlow: true
blockRedstoneMechanism: false
blockDestruction: false
entityLimits:
ZOMBIE: 50
SKELETON: 50
entitySpawnTimes:
PHANTOM:
from: "22:00"
to: "06:00"
disallowedEntities:
- WITHER
disallowedBlocks:
- BEDROCK
- BARRIER

## Bypass System

The bypass system allows administrators to temporarily override restrictions:

- Bypass is disabled by default for all features
- Bypass settings are per-player and stored in memory
- Bypass settings are cleared on player quit and server restart
- Blocks placed with bypass enabled maintain their bypass status (saved to config)

### Bypass Behavior

| Bypass | Behavior when enabled |
|:-------|:----------------------|
| disallowedBlocks | Can place blocks from disallowed list |
| blockDespawn | Placed blocks will never despawn |
| blockDestruction | Can destroy any block (even non-player-placed) |
| waterFlow | Water placed by player will spread |
| lavaFlow | Lava placed by player will spread |

## Other

Author: [nxmsdev](https://github.com/nxmsdev)

Website: [nxms.dev](https://www.nxms.dev)

License: [MIT](https://choosealicense.com/licenses/mit/)