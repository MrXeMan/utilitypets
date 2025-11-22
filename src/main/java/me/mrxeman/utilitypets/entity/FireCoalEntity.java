package me.mrxeman.utilitypets.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class FireCoalEntity extends Fireball {
    public FireCoalEntity(EntityType<? extends FireCoalEntity> p_36833_, Level p_36834_) {
        super(p_36833_, p_36834_);
    }

    public FireCoalEntity(Level p_37375_, LivingEntity p_37376_, double p_37377_, double p_37378_, double p_37379_) {
        super(ModEntities.FIRECOAL.get(), p_37376_, p_37377_, p_37378_, p_37379_, p_37375_);
    }

    @SuppressWarnings("unused")
    public FireCoalEntity(Level p_37367_, double p_37368_, double p_37369_, double p_37370_, double p_37371_, double p_37372_, double p_37373_) {
        super(ModEntities.FIRECOAL.get(), p_37368_, p_37369_, p_37370_, p_37371_, p_37372_, p_37373_, p_37367_);
    }

    @Override
    protected void onHit(@NotNull HitResult p_37260_) {
        super.onHit(p_37260_);
        if (!this.level().isClientSide) {
            Entity entity = this.getOwner();
            if (entity instanceof FurnyEntity furny) {
                int explosionPower = (furny.getTurboTime() > 0 ? 1 : -1);
                if (explosionPower > 0)
                    this.level().explode(this, this.getX(), this.getY(), this.getZ(), (float) explosionPower, false, Level.ExplosionInteraction.MOB);
            }
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult p_37259_) {
        super.onHitEntity(p_37259_);
        if (!this.level().isClientSide) {
            Entity entity = p_37259_.getEntity();
            Entity entity1 = this.getOwner();
            int i = entity.getRemainingFireTicks();
            entity.setSecondsOnFire(4);
            if (entity instanceof FurnyEntity furny) {
                int explosionPower = (furny.getTurboTime() > 0 ? 1 : -1);
                if (explosionPower > 0)
                    this.level().explode(this, this.getX(), this.getY(), this.getZ(), (float) explosionPower, false, Level.ExplosionInteraction.MOB);
            }
            if (!entity.hurt(this.damageSources().fireball(this, entity1), 5.0F)) {
                entity.setRemainingFireTicks(i);
            } else if (entity1 instanceof LivingEntity) {
                this.doEnchantDamageEffects((LivingEntity)entity1, entity);
            }
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }
}
