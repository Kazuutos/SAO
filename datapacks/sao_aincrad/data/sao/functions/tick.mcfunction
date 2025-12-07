# Teleport pads tick: move player standing on pads to arena/spawn
execute as @a at @s if block ~ ~-1 ~ minecraft:light_blue_stained_glass run tp @s 200 65 0
execute as @a at @s if block ~ ~-1 ~ minecraft:lime_stained_glass run tp @s 0 66 0
