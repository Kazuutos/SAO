package com.sao.world;

import com.mojang.logging.LogUtils;
import com.sao.world.client.SAOOverlay;
import com.sao.world.entity.FloorOneBoss;
import com.sao.world.item.HealCrystalItem;
import com.sao.world.item.TeleportCrystalItem;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

@Mod(SAOWorldMod.MODID)
public class SAOWorldMod {
    public static final String MODID = "saoworld";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MODID);

    // Core items (swords, crystals)
    public static final RegistryObject<Item> ANNEAL_BLADE = ITEMS.register("anneal_blade",
            () -> new SwordItem(Tiers.DIAMOND, 3, -2.2f, new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> DEMONIC_SWORD = ITEMS.register("demonic_sword",
            () -> new SwordItem(Tiers.NETHERITE, 4, -2.4f, new Item.Properties().rarity(Rarity.RARE).fireResistant()));
    public static final RegistryObject<Item> ELUCIDATOR = ITEMS.register("elucidator",
            () -> new SwordItem(Tiers.NETHERITE, 4, -2.3f, new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> DARK_REPULSER = ITEMS.register("dark_repulser",
            () -> new SwordItem(Tiers.NETHERITE, 4, -2.3f, new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> BLACK_ONE = ITEMS.register("black_one",
            () -> new SwordItem(Tiers.NETHERITE, 5, -2.5f, new Item.Properties().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> BLUE_ROSE_SWORD = ITEMS.register("blue_rose_sword",
            () -> new SwordItem(Tiers.NETHERITE, 4, -2.2f, new Item.Properties().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> HEAL_CRYSTAL = ITEMS.register("heal_crystal",
            () -> new HealCrystalItem(new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> RETURN_CRYSTAL = ITEMS.register("return_crystal",
            () -> new TeleportCrystalItem(new Item.Properties().stacksTo(16).rarity(Rarity.RARE)));

    // Entities
    public static final RegistryObject<EntityType<FloorOneBoss>> FLOOR_ONE_BOSS = ENTITIES.register("floor_one_boss",
            () -> EntityType.Builder.<FloorOneBoss>of(FloorOneBoss::new, MobCategory.MONSTER)
                    .sized(0.6f, 2.1f)
                    .clientTrackingRange(10)
                    .build(res("floor_one_boss").toString()));

    public static final RegistryObject<ForgeSpawnEggItem> FLOOR_ONE_BOSS_SPAWN_EGG = ITEMS.register("floor_one_boss_spawn_egg",
            () -> new ForgeSpawnEggItem(FLOOR_ONE_BOSS, 0x2b2f3a, 0xd7463f, new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<CreativeModeTab> SAO_TAB = TABS.register("sao_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .title(Component.translatable("itemGroup.saoworld.sao_tab"))
            .icon(() -> ANNEAL_BLADE.get().getDefaultInstance())
            .displayItems((params, output) -> {
                output.accept(ANNEAL_BLADE.get());
                output.accept(DEMONIC_SWORD.get());
                output.accept(ELUCIDATOR.get());
                output.accept(DARK_REPULSER.get());
                output.accept(BLACK_ONE.get());
                output.accept(BLUE_ROSE_SWORD.get());
                output.accept(HEAL_CRYSTAL.get());
                output.accept(RETURN_CRYSTAL.get());
                output.accept(FLOOR_ONE_BOSS_SPAWN_EGG.get());
            })
            .build());

    // UI sounds
    public static final RegistryObject<SoundEvent> UI_CONFIRM = registerSound("ui_confirm");
    public static final RegistryObject<SoundEvent> UI_DIALOG_CLOSE = registerSound("ui_dialog_close");
    public static final RegistryObject<SoundEvent> UI_MENU_POPUP = registerSound("ui_menu_popup");
    public static final RegistryObject<SoundEvent> UI_MESSAGE = registerSound("ui_message");
    public static final RegistryObject<SoundEvent> UI_ORB_DROPDOWN = registerSound("ui_orb_dropdown");
    public static final RegistryObject<SoundEvent> UI_PARTICLES_DEATH = registerSound("ui_particles_death");

    public SAOWorldMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modEventBus);
        ENTITIES.register(modEventBus);
        TABS.register(modEventBus);
        SOUNDS.register(modEventBus);

        modEventBus.addListener(this::registerEntityAttributes);
        modEventBus.addListener(ClientOnly::initClient);

        MinecraftForge.EVENT_BUS.register(this);

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> SAOOverlay::register);
    }

    private void registerEntityAttributes(EntityAttributeCreationEvent event) {
        AttributeSupplier attributes = FloorOneBoss.createAttributes().build();
        event.put(FLOOR_ONE_BOSS.get(), attributes);
    }

    private static ResourceLocation res(String path) {
        return new ResourceLocation(MODID, path);
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientOnly {
        @SubscribeEvent
        public static void initClient(FMLClientSetupEvent event) {
            event.enqueueWork(() -> EntityRenderers.register(FLOOR_ONE_BOSS.get(), ZombieRenderer::new));
        }
    }

    private static RegistryObject<SoundEvent> registerSound(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(res(name)));
    }
}
