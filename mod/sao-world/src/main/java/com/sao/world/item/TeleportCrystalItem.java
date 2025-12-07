package com.sao.world.item;

import com.sao.world.SAOWorldMod;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class TeleportCrystalItem extends Item {
    public TeleportCrystalItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && level instanceof ServerLevel server) {
            var spawnPos = server.getSharedSpawnPos();
            int spawnY = server.getHeight(Heightmap.Types.MOTION_BLOCKING, spawnPos.getX(), spawnPos.getZ());
            Vec3 spawn = new Vec3(spawnPos.getX() + 0.5D, spawnY, spawnPos.getZ() + 0.5D);
            player.teleportTo(spawn.x, spawn.y, spawn.z);
            player.fallDistance = 0.0F;
            player.displayClientMessage(Component.literal("[SAO] Cristal de retour utilise."), true);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SAOWorldMod.UI_ORB_DROPDOWN.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
