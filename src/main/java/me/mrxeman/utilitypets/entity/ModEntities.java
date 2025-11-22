package me.mrxeman.utilitypets.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static me.mrxeman.utilitypets.Utilitypets.MOD_ID;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MOD_ID);

    public static final RegistryObject<EntityType<LucasTheSpiderEntity>> LUCAS_THE_SPIDER =
            ENTITY_TYPES.register("lucas_the_spider",
                    () -> EntityType.Builder.of(LucasTheSpiderEntity::new, MobCategory.MONSTER)
                            .sized(1.0f, 0.8f)
                            .build(ResourceLocation.fromNamespaceAndPath(MOD_ID, "lucas_the_spider").toString()));

    public static final RegistryObject<EntityType<FurnyEntity>> FURNY =
            ENTITY_TYPES.register("furny",
                    () -> EntityType.Builder.of(FurnyEntity::new, MobCategory.MONSTER)
                            .sized(1.0f, 1.0f)
                            .build(ResourceLocation.fromNamespaceAndPath(MOD_ID, "furny").toString()));

    public static final RegistryObject<EntityType<FireCoalEntity>> FIRECOAL =
            ENTITY_TYPES.register("firecoal",
                    () -> EntityType.Builder.<FireCoalEntity>of(FireCoalEntity::new, MobCategory.MISC)
                            .sized(0.33f,  0.33f)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build(ResourceLocation.fromNamespaceAndPath(MOD_ID, "firecoal").toString()));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }

}
