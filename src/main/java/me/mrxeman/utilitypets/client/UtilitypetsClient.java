package me.mrxeman.utilitypets.client;

import me.mrxeman.utilitypets.client.renderers.FireCoalRenderer;
import me.mrxeman.utilitypets.entity.ModEntities;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static me.mrxeman.utilitypets.Utilitypets.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class UtilitypetsClient {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(ModEntities.FIRECOAL.get(), FireCoalRenderer::new);
    }

}
