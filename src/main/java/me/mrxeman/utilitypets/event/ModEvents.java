package me.mrxeman.utilitypets.event;

import me.mrxeman.utilitypets.entity.FurnyEntity;
import me.mrxeman.utilitypets.entity.LucasTheSpiderEntity;
import me.mrxeman.utilitypets.entity.ModEntities;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import static me.mrxeman.utilitypets.Utilitypets.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {
    @SubscribeEvent
    public static void entityAttributeEvent(@NotNull EntityAttributeCreationEvent event) {
        event.put(ModEntities.LUCAS_THE_SPIDER.get(), LucasTheSpiderEntity.createAttributes().build());
        event.put(ModEntities.FURNY.get(), FurnyEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void spawnReplacementEvent(@NotNull SpawnPlacementRegisterEvent event) {
        event.register(
                ModEntities.LUCAS_THE_SPIDER.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mob::checkMobSpawnRules,
                SpawnPlacementRegisterEvent.Operation.AND
        );
        event.register(
                ModEntities.FURNY.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                FurnyEntity::canSpawn,
                SpawnPlacementRegisterEvent.Operation.AND
        );
    }
}