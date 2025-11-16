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

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }

}
