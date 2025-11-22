package me.mrxeman.utilitypets;

import com.mojang.logging.LogUtils;
import me.mrxeman.utilitypets.entity.ModEntities;
import me.mrxeman.utilitypets.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

@SuppressWarnings("unused")
@Mod(Utilitypets.MOD_ID)
public class Utilitypets {

    public static final String MOD_ID = "utilitypets";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);
    public static final RegistryObject<CreativeModeTab> UTILITYPETS_TAB =
            CREATIVE_MODE_TABS.register("utilitypets_tab",
                    () -> CreativeModeTab.builder()
                            .icon(() -> ModItems.LUCAS_SPAWN_EGG.get().getDefaultInstance())
                            .displayItems((parameters, output) -> {
                                output.accept(ModItems.LUCAS_SPAWN_EGG.get());
                                output.accept(ModItems.FURNY_SPAWN_EGG.get());
                            })
                            .title(Component.literal("Utility Pets"))
                            .build()
            );

    public Utilitypets() {
        //noinspection removal
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        CREATIVE_MODE_TABS.register(modEventBus);
        ModEntities.register(modEventBus);
        ModItems.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

        //noinspection removal
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.serverSpec);
    }
}
