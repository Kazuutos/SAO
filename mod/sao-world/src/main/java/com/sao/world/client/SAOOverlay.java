package com.sao.world.client;

import com.sao.world.SAOWorldMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = SAOWorldMod.MODID, value = Dist.CLIENT)
public class SAOOverlay {
    private static final int NOTIF_TICKS = 80;
    private static final int MAX_NOTIFS = 4;
    private static final List<Notification> NOTIFICATIONS = new ArrayList<>();
    private static final ResourceLocation SLOT = new ResourceLocation(SAOWorldMod.MODID, "textures/gui/sao_slot.png");
    private static final ResourceLocation ICONS_MENU = new ResourceLocation(SAOWorldMod.MODID, "textures/gui/sao_icons_menu.png");
    private static final ResourceLocation HEALTH_BASE = new ResourceLocation(SAOWorldMod.MODID, "textures/gui/healthbarbase.png");
    private static final ResourceLocation HEALTH_FILL = new ResourceLocation(SAOWorldMod.MODID, "textures/gui/healthbar.png");
    private static final ResourceLocation SAO_FONT = new ResourceLocation(SAOWorldMod.MODID, "sao_font");
    private static final Map<ResourceLocation, ResourceLocation> STATUS_ICONS = new HashMap<>();
    private static boolean screenOpen = false;

    private record Notification(String text, int ttl) {
    }

    public static Runnable register() {
        return () -> {
            // no-op placeholder to satisfy DistExecutor supplier
        };
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        if (!event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        tickNotifications();

        GuiGraphics gui = event.getGuiGraphics();
        int width = event.getWindow().getGuiScaledWidth();
        int height = event.getWindow().getGuiScaledHeight();

        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        float ratio = Math.min(health / maxHealth, 1.0f);

        int texW = 408;
        int texH = 34;
        int x = width / 2 - texW / 2;
        int y = height - 64;

        gui.blit(HEALTH_BASE, x, y, 0, 0, texW, texH, texW, texH);
        int filled = (int) (texW * ratio);
        if (filled > 0) {
            gui.blit(HEALTH_FILL, x, y, 0, 0, filled, texH, texW, texH);
        }

        var hpLabel = saoText("HP " + (int) health + " / " + (int) maxHealth);
        int textX = width / 2 - mc.font.width(hpLabel) / 2;
        gui.drawString(mc.font, hpLabel, textX, y + 10, 0xFFFFFF, true);

        renderBuffs(gui, mc, x, y + texH + 4, player);
        renderParty(gui, mc, width);
        renderCrystals(gui, mc, width, height, player);
        renderNotifications(gui, mc, width);
        renderStatusIcons(gui, mc, width);
        renderExpBar(gui, mc, width, height, player);
    }

    private static void renderParty(GuiGraphics gui, Minecraft mc, int screenWidth) {
        Player player = mc.player;
        if (player == null || mc.level == null) return;
        List<Player> nearby = mc.level.players().stream()
                .filter(p -> p != player && p.isAlive() && !p.isSpectator() && p.distanceTo(player) <= 32.0F)
                .sorted(Comparator.comparingDouble(player::distanceTo))
                .limit(3)
                .collect(Collectors.toList());
        int startX = 12;
        int startY = 14;
        int barWidth = 92;
        int barHeight = 6;
        for (int i = 0; i < nearby.size(); i++) {
            Player mate = nearby.get(i);
            float ratio = Math.min(mate.getHealth() / mate.getMaxHealth(), 1.0f);
            int y = startY + (i * 14);
            gui.fill(startX - 2, y - 2, startX + barWidth + 2, y + barHeight + 2, 0x88091019);
            gui.fill(startX, y, startX + (int) (barWidth * ratio), y + barHeight, 0xFF4CE1FF);
            gui.drawString(mc.font, mate.getName(), startX + barWidth + 6, y - 1, 0xC0E7FF, false);
        }
    }

    private static void renderBuffs(GuiGraphics gui, Minecraft mc, int x, int y, Player player) {
        List<MobEffectInstance> effects = player.getActiveEffects().stream().toList();
        var xpLabel = saoText(xpText(player));
        gui.drawString(mc.font, xpLabel, x, y, 0xFFD769, false);

        if (effects.isEmpty()) {
            gui.drawString(mc.font, saoText("Buffs: none"), x, y + 10, 0xA0E6FF, false);
            return;
        }
        int offset = 0;
        gui.drawString(mc.font, saoText("Buffs:"), x, y + 10, 0xA0E6FF, false);
        for (MobEffectInstance effect : effects) {
            String name = effect.getDescriptionId().replace("effect.minecraft.", "");
            String label = name + " " + (effect.getAmplifier() + 1);
            var comp = saoText(label);
            gui.drawString(mc.font, comp, x + 6 + offset, y + 20, 0x9EE2F6, false);
            offset += mc.font.width(comp) + 8;
            if (offset > 160) break;
        }
    }

    private static String xpText(Player player) {
        int xpNeeded = player.getXpNeededForNextLevel();
        float xpProgress = xpNeeded > 0 ? player.experienceProgress : 0f;
        return "EXP " + player.experienceLevel + " (" + (int) (xpProgress * 100) + "%)";
    }

    private static void renderCrystals(GuiGraphics gui, Minecraft mc, int width, int height, Player player) {
        int heal = player.getInventory().countItem(com.sao.world.SAOWorldMod.HEAL_CRYSTAL.get());
        int ret = player.getInventory().countItem(com.sao.world.SAOWorldMod.RETURN_CRYSTAL.get());
        var text = saoText("Crystals H:" + heal + " R:" + ret);
        int w = mc.font.width(text);
        int x = width - w - 24;
        int y = height - 66;
        gui.blit(SLOT, x - 18, y - 2, 0, 0, 16, 16, 16, 16);
        gui.blit(ICONS_MENU, x - 18, y - 2, 0, 0, 16, 16, 256, 256);
        gui.fill(x - 2, y - 4, x + w + 4, y + 12, 0xAA0E1217);
        gui.drawString(mc.font, text, x, y, 0x6CF0FF, false);
    }

    private static void renderNotifications(GuiGraphics gui, Minecraft mc, int width) {
        int y = 16;
        for (Notification notification : NOTIFICATIONS) {
            int alpha = (int) (255 * Math.min(1.0, notification.ttl / (double) NOTIF_TICKS));
            int color = (alpha << 24) | 0x00E6FF;
            var text = saoText(notification.text());
            int w = mc.font.width(text);
            int x = width - w - 32;
            gui.blit(ICONS_MENU, x - 16, y - 2, 0, 0, 16, 16, 256, 256);
            gui.drawString(mc.font, text, x + 4, y + 2, color, false);
            y += 12;
        }
    }

    private static void tickNotifications() {
        if (NOTIFICATIONS.isEmpty()) return;
        for (int i = 0; i < NOTIFICATIONS.size(); i++) {
            Notification n = NOTIFICATIONS.get(i);
            NOTIFICATIONS.set(i, new Notification(n.text(), n.ttl() - 1));
        }
        NOTIFICATIONS.removeIf(n -> n.ttl() <= 0);
    }

    private static void addNotification(String text) {
        NOTIFICATIONS.add(0, new Notification(text, NOTIF_TICKS));
        if (NOTIFICATIONS.size() > MAX_NOTIFS) {
            NOTIFICATIONS.remove(NOTIFICATIONS.size() - 1);
        }
        playUiSound(SAOWorldMod.UI_MENU_POPUP.get());
    }

    @SubscribeEvent
    public static void onItemPickup(EntityItemPickupEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (event.getEntity() == mc.player) {
            String name = event.getItem().getItem().getHoverName().getString();
            int count = event.getItem().getItem().getCount();
            addNotification("Loot +" + count + " " + name);
            playUiSound(SAOWorldMod.UI_CONFIRM.get());
        }
    }

    @SubscribeEvent
    public static void onKill(LivingDeathEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (event.getSource().getEntity() == mc.player) {
            addNotification("Kill: " + event.getEntity().getName().getString());
            playUiSound(SAOWorldMod.UI_MESSAGE.get());
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (event.getEntity() == mc.player) {
            playUiSound(SAOWorldMod.UI_PARTICLES_DEATH.get());
        }
    }

    @SubscribeEvent
    public static void onScreenOpen(ScreenEvent.Opening event) {
        boolean nowOpen = event.getNewScreen() != null;
        if (nowOpen && !screenOpen) {
            playUiSound(SAOWorldMod.UI_MENU_POPUP.get());
        } else if (!nowOpen && screenOpen) {
            playUiSound(SAOWorldMod.UI_DIALOG_CLOSE.get());
        }
        screenOpen = nowOpen;
    }

    private static void playUiSound(SoundEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        mc.getSoundManager().play(SimpleSoundInstance.forUI(event, 1.0f));
    }

    private static net.minecraft.network.chat.Component saoText(String text) {
        return net.minecraft.network.chat.Component.literal(text).withStyle(style -> style.withFont(SAO_FONT));
    }

    private static void renderStatusIcons(GuiGraphics gui, Minecraft mc, int width) {
        Player player = mc.player;
        if (player == null) return;
        var effects = player.getActiveEffects();
        if (effects.isEmpty()) return;
        int x = width - 20;
        int y = 48;
        for (MobEffectInstance effect : effects) {
            ResourceLocation key = BuiltInRegistries.MOB_EFFECT.getKey(effect.getEffect());
            ResourceLocation icon = key != null ? STATUS_ICONS.get(key) : null;
            if (icon == null) {
                icon = STATUS_ICONS.get(new ResourceLocation(SAOWorldMod.MODID, "default"));
            }
            if (icon != null) {
                gui.blit(icon, x, y, 0, 0, 16, 16, 16, 16);
            }
            y += 18;
        }
    }

    static {
        STATUS_ICONS.put(new ResourceLocation("minecraft", "poison"), new ResourceLocation(SAOWorldMod.MODID, "textures/gui/status/poison.png"));
        STATUS_ICONS.put(new ResourceLocation("minecraft", "blindness"), new ResourceLocation(SAOWorldMod.MODID, "textures/gui/status/blindness.png"));
        STATUS_ICONS.put(new ResourceLocation("minecraft", "slowness"), new ResourceLocation(SAOWorldMod.MODID, "textures/gui/status/slow.png"));
        STATUS_ICONS.put(new ResourceLocation("minecraft", "weakness"), new ResourceLocation(SAOWorldMod.MODID, "textures/gui/status/weakness.png"));
        STATUS_ICONS.put(new ResourceLocation("minecraft", "wither"), new ResourceLocation(SAOWorldMod.MODID, "textures/gui/status/poison.png"));
        STATUS_ICONS.put(new ResourceLocation(SAOWorldMod.MODID, "default"), new ResourceLocation(SAOWorldMod.MODID, "textures/gui/status/base.png"));
    }

    private static void renderExpBar(GuiGraphics gui, Minecraft mc, int width, int height, Player player) {
        int barWidth = 182;
        int barHeight = 8;
        int x = width / 2 - barWidth / 2;
        int y = height - 28;
        gui.fill(x - 2, y - 2, x + barWidth + 2, y + barHeight + 2, 0x66091019);
        float progress = player.experienceProgress;
        int filled = (int) (progress * barWidth);
        gui.fill(x, y, x + filled, y + barHeight, 0xFF47C5FF);
        gui.fill(x + filled, y, x + barWidth, y + barHeight, 0xFF0E1217);
        String xpLabel = "EXP " + player.experienceLevel + " (" + (int) (progress * 100) + "%)";
        gui.drawString(mc.font, saoText(xpLabel), width / 2 - mc.font.width(xpLabel) / 2, y - 10, 0xFFFFFF, true);
    }
}
