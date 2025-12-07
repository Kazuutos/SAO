# SAO starter island + gate
# Platform
fill -32 64 -32 32 64 32 minecraft:stone_bricks
fill -31 65 -31 31 65 31 minecraft:smooth_stone_slab[type=double]

# Outer wall
fill -32 65 -32 -32 70 32 minecraft:stone_brick_wall
fill 32 65 -32 32 70 32 minecraft:stone_brick_wall
fill -32 65 -32 32 70 -32 minecraft:stone_brick_wall
fill -32 65 32 32 70 32 minecraft:stone_brick_wall

# Inner ring lights
setblock 0 65 0 minecraft:beacon
fill -1 64 -1 1 64 1 minecraft:iron_block
fill -4 65 0 -4 67 0 minecraft:sea_lantern
fill 4 65 0 4 67 0 minecraft:sea_lantern
fill 0 65 4 0 67 4 minecraft:sea_lantern
fill 0 65 -4 0 67 -4 minecraft:sea_lantern

# Gate pillars
fill -6 65 -10 -6 74 10 minecraft:stone_bricks
fill 6 65 -10 6 74 10 minecraft:stone_bricks
fill -6 74 -10 6 74 10 minecraft:dark_oak_planks
fill -6 75 -10 6 75 10 minecraft:lantern

# Spawn pad and info
setworldspawn 0 66 0
summon armor_stand 0 66 2 {CustomName:'{"text":"Aincrad Gate"}',CustomNameVisible:1b,Invisible:1b,Invulnerable:1b,NoGravity:1b}

# Paths
fill -32 64 0 -40 64 0 minecraft:stone_bricks
fill 32 64 0 40 64 0 minecraft:stone_bricks
fill 0 64 -32 0 64 -40 minecraft:stone_bricks
fill 0 64 32 0 64 40 minecraft:stone_bricks

# Teleport pads placeholders (for future floors)
fill -24 64 0 -22 64 0 minecraft:light_blue_stained_glass
fill 24 64 0 22 64 0 minecraft:lime_stained_glass
fill 0 64 -24 0 64 -22 minecraft:magenta_stained_glass
fill 0 64 24 0 64 22 minecraft:orange_stained_glass

# Trees & d?cor
fill -16 65 -16 -16 69 -16 minecraft:oak_log
fill -16 70 -16 -12 73 -12 minecraft:oak_leaves
fill 16 65 16 16 69 16 minecraft:oak_log
fill 12 70 12 16 73 16 minecraft:oak_leaves

# Boss arena marker
fill 60 64 0 70 64 10 minecraft:deepslate_tiles
setblock 65 65 5 minecraft:lodestone

# Illfang arena build (basic ring) at 200 64 0
fill 180 63 -20 220 63 20 minecraft:deepslate_tiles
fill 180 64 -20 220 64 20 minecraft:deepslate_bricks
fill 181 65 -19 219 65 19 minecraft:air
fill 180 65 -20 220 68 20 minecraft:deepslate_brick_wall
fill 180 65 -20 220 65 -20 minecraft:deepslate_brick_wall
fill 180 65 20 220 65 20 minecraft:deepslate_brick_wall
fill 180 66 -20 220 66 20 minecraft:polished_blackstone_slab
setblock 200 64 0 minecraft:lodestone
summon armor_stand 200 65 0 {CustomName:'{"text":"Illfang Arena"}',CustomNameVisible:1b,Invisible:1b,Invulnerable:1b,NoGravity:1b}

# Server rules QoL
gamerule keepInventory true
gamerule doImmediateRespawn true
gamerule spawnRadius 0
gamerule reducedDebugInfo true
gamerule sendCommandFeedback false
gamerule doWeatherCycle false
