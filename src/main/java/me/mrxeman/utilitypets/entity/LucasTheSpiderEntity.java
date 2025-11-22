package me.mrxeman.utilitypets.entity;

import me.mrxeman.utilitypets.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class LucasTheSpiderEntity extends BaseEntity {
    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(LucasTheSpiderEntity.class, EntityDataSerializers.BYTE);
    public int stringTime = 1200;

    public LucasTheSpiderEntity(EntityType<? extends TamableAnimal> p_21803_, Level p_21804_) {
        super(p_21803_, p_21804_);
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.25F)
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.ATTACK_SPEED, 1.0D);
    }

    @Override
    public boolean canBreed() {
        return true;
    }

    @Override
    public float standingEyeHeight(Pose pose, @NotNull EntityDimensions entityDimensions) {
        return entityDimensions.height * 0.7f;
    }

    @Override
    public EntityType<LucasTheSpiderEntity> createOffspring() {
        return ModEntities.LUCAS_THE_SPIDER.get();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        if (compoundTag.contains("stringTime")) {
            this.stringTime = compoundTag.getInt("stringTime");
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("stringTime", this.stringTime);
    }

    public boolean onClimbable() {
        return this.isClimbing();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            this.setClimbing(this.horizontalCollision);
            if (--this.stringTime <= 0) {
                if (this.random.nextInt(5) == 0) {
                    this.spawnAtLocation(Items.STRING);
                    this.gameEvent(GameEvent.ENTITY_PLACE);
                }
                this.stringTime = 1200;
            }
        }
    }

    public boolean isClimbing() {
        return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
    }

    public void setClimbing(boolean p_33820_) {
        byte b0 = this.entityData.get(DATA_FLAGS_ID);
        if (p_33820_) {
            b0 = (byte)(b0 | 1);
        } else {
            b0 = (byte)(b0 & -2);
        }

        this.entityData.set(DATA_FLAGS_ID, b0);
    }

    protected void playStepSound(@NotNull BlockPos p_30415_, @NotNull BlockState p_30416_) {
        this.playSound(SoundEvents.SPIDER_STEP, 0.15F, 1.0F);
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.SPIDER_AMBIENT;
    }

    protected SoundEvent getHurtSound(@NotNull DamageSource p_33814_) {
        return SoundEvents.SPIDER_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.SPIDER_DEATH;
    }

    @Override
    protected @NotNull Set<Item> getFavoriteItems() {
        return Config.lucasFavoriteItems;
    }
}
