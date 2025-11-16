package me.mrxeman.utilitypets.event;

import me.mrxeman.utilitypets.entity.LucasTheSpiderEntity;
import me.mrxeman.utilitypets.entity.ModEntities;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import static me.mrxeman.utilitypets.Utilitypets.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {
    @SubscribeEvent
    public static void entityAttributeEvent(@NotNull EntityAttributeCreationEvent event) {
        event.put(ModEntities.LUCAS_THE_SPIDER.get(), LucasTheSpiderEntity.createAttributes().build());
    }
}