package me.mrxeman.utilitypets.item;

import me.mrxeman.utilitypets.entity.ModEntities;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static me.mrxeman.utilitypets.Utilitypets.MOD_ID;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static final RegistryObject<Item> LUCAS_SPAWN_EGG = ITEMS.register("lucas_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.LUCAS_THE_SPIDER, 0x616161, 0x959595,
                    new Item.Properties()));

    public static final RegistryObject<Item> FURNY_SPAWN_EGG = ITEMS.register("furny_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.FURNY, 0x616161, 0x959595,
                    new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

}
