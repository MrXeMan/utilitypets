package me.mrxeman.utilitypets.event;

import me.mrxeman.utilitypets.client.renderers.LucasTheSpiderRenderer;
import me.mrxeman.utilitypets.entity.ModEntities;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static me.mrxeman.utilitypets.Utilitypets.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(ModEntities.LUCAS_THE_SPIDER.get(), LucasTheSpiderRenderer::new);
    }
}
