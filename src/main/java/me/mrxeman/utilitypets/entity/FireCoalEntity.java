package me.mrxeman.utilitypets.entity;

import net.minecraft.world.damagesource.DamageSource;
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

    @Override
    protected void onHit(@NotNull HitResult p_37260_) {
        super.onHit(p_37260_);
        if (this.getOwner() instanceof FurnyEntity furny) {
            int explosionPower = (furny.getTurboTime() > 0 ? 1 : -1);
            if (explosionPower > 0)
                this.level().explode(this, this.getX(), this.getY(), this.getZ(), (float) explosionPower, false, Level.ExplosionInteraction.NONE);
        }
        if (!this.level().isClientSide) {
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        if (!this.level().isClientSide) {
            Entity hitEntity = entityHitResult.getEntity();
            Entity owner = this.getOwner();
            int i = hitEntity.getRemainingFireTicks();
            hitEntity.setSecondsOnFire(4);
            if (!hitEntity.hurt(this.damageSources().fireball(this, owner), 5.0F)) {
                hitEntity.setRemainingFireTicks(i);
            } else if (owner instanceof LivingEntity) {
                this.doEnchantDamageEffects((LivingEntity)owner, hitEntity);
            }
            this.discard();
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean hurt(@NotNull DamageSource p_36839_, float p_36840_) {
        return true;
    }
}
