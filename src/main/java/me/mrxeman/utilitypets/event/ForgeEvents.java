package me.mrxeman.utilitypets.event;

import me.mrxeman.utilitypets.entity.FurnyEntity;
import me.mrxeman.utilitypets.entity.LucasTheSpiderEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import static me.mrxeman.utilitypets.Utilitypets.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents {
    @SubscribeEvent
    public static void onLivingHurt(@NotNull LivingHurtEvent event) {
        LivingEntity victim = event.getEntity();
        DamageSource source = event.getSource();

        Entity attacker = source.getEntity();
        if (attacker instanceof LucasTheSpiderEntity) {
            victim.addEffect(new MobEffectInstance(MobEffects.POISON, 400, 0)); // 20 seconds * 20 ticks
        }
        if (attacker instanceof FurnyEntity) {
            victim.setSecondsOnFire(4);
        }
    }
}
