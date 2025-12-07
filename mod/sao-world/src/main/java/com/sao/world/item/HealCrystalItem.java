package com.sao.world.item;

import com.sao.world.SAOWorldMod;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class HealCrystalItem extends Item {
    public HealCrystalItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            player.heal(player.getMaxHealth());
            player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 20 * 20, 1)); // 20s absorption
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20 * 5, 2)); // short regen boost
            player.displayClientMessage(Component.literal("[SAO] Cristal de soin utilise."), true);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SAOWorldMod.UI_CONFIRM.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
