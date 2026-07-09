package com.kongrarainforest;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import com.kongrarainforest.entity.JaguarEntity;
import com.kongrarainforest.entity.ToucanEntity;
import com.kongrarainforest.entity.KongraEntity;
import com.kongrarainforest.item.KongraArmorMaterial;

public class KongraRainforestMod implements ModInitializer {
    public static final String MOD_ID = "kongrarainforest";

    // Materials / ingredients
    public static final Item RAINFOREST_GEM = new Item(new FabricItemSettings());
    public static final Item COBRA_SCALE = new Item(new FabricItemSettings());
    public static final Item GORILLA_FANG = new Item(new FabricItemSettings());
    public static final Item KONGRA_CORE = new Item(new FabricItemSettings());

    // Armor
    public static final Item KONGRA_HELMET = new ArmorItem(KongraArmorMaterial.INSTANCE, ArmorItem.Type.HELMET, new FabricItemSettings());
    public static final Item KONGRA_CHESTPLATE = new ArmorItem(KongraArmorMaterial.INSTANCE, ArmorItem.Type.CHESTPLATE, new FabricItemSettings());
    public static final Item KONGRA_LEGGINGS = new ArmorItem(KongraArmorMaterial.INSTANCE, ArmorItem.Type.LEGGINGS, new FabricItemSettings());
    public static final Item KONGRA_BOOTS = new ArmorItem(KongraArmorMaterial.INSTANCE, ArmorItem.Type.BOOTS, new FabricItemSettings());

    // Entities
    public static final EntityType<JaguarEntity> JAGUAR = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "jaguar"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, JaguarEntity::new)
                    .dimensions(EntityDimensions.fixed(0.9f, 0.8f))
                    .trackRangeBlocks(10)
                    .build()
    );

    public static final EntityType<ToucanEntity> TOUCAN = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "toucan"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, ToucanEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.6f))
                    .trackRangeBlocks(10)
                    .build()
    );

    public static final EntityType<KongraEntity> KONGRA = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "kongra"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, KongraEntity::new)
                    .dimensions(EntityDimensions.fixed(1.8f, 3.4f))
                    .trackRangeBlocks(16)
                    .build()
    );

    // Spawn eggs
    public static final Item JAGUAR_SPAWN_EGG = new SpawnEggItem(JAGUAR, 0x3a2a12, 0xe0b04a, new FabricItemSettings());
    public static final Item TOUCAN_SPAWN_EGG = new SpawnEggItem(TOUCAN, 0x101010, 0xffa500, new FabricItemSettings());
    public static final Item KONGRA_SPAWN_EGG = new SpawnEggItem(KONGRA, 0x1a1a1a, 0x2e8b57, new FabricItemSettings());

    public static final RegistryKey<ItemGroup> GROUP_KEY = RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier(MOD_ID, "general"));
    public static final ItemGroup GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(KONGRA_CHESTPLATE))
            .displayName(Text.translatable("itemGroup.kongrarainforest.general"))
            .build();

    @Override
    public void onInitialize() {
        registerItem("rainforest_gem", RAINFOREST_GEM);
        registerItem("cobra_scale", COBRA_SCALE);
        registerItem("gorilla_fang", GORILLA_FANG);
        registerItem("kongra_core", KONGRA_CORE);

        registerItem("kongra_helmet", KONGRA_HELMET);
        registerItem("kongra_chestplate", KONGRA_CHESTPLATE);
        registerItem("kongra_leggings", KONGRA_LEGGINGS);
        registerItem("kongra_boots", KONGRA_BOOTS);

        registerItem("jaguar_spawn_egg", JAGUAR_SPAWN_EGG);
        registerItem("toucan_spawn_egg", TOUCAN_SPAWN_EGG);
        registerItem("kongra_spawn_egg", KONGRA_SPAWN_EGG);

        Registry.register(Registries.ITEM_GROUP, GROUP_KEY, GROUP);

        // Attributes
        FabricDefaultAttributeRegistry.register(JAGUAR, JaguarEntity.createJaguarAttributes());
        FabricDefaultAttributeRegistry.register(TOUCAN, ToucanEntity.createToucanAttributes());
        FabricDefaultAttributeRegistry.register(KONGRA, KongraEntity.createKongraAttributes());

        // Rain damage tick handler
        ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);
    }

    private void registerItem(String name, Item item) {
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, name), item);
    }

    private void onServerTick(MinecraftServer server) {
        for (ServerWorld world : server.getWorlds()) {
            RainforestRainHandler.tick(world);
        }
    }
}