package me.mrxeman.utilitypets.entity;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import me.mrxeman.utilitypets.Config;
import me.mrxeman.utilitypets.utils.TimeUnits;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Set;

public class FurnyEntity extends BaseEntity implements ContainerEntity {
    private final RecipeManager.CachedCheck<Container, ? extends AbstractCookingRecipe> quickCheck;
    private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap<>();
    private boolean isSmelting = false;

    private static final EntityDataAccessor<Boolean> DATA_IS_SMELTING = SynchedEntityData.defineId(FurnyEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_IS_CHARGING = SynchedEntityData.defineId(FurnyEntity.class, EntityDataSerializers.BOOLEAN);

    private int turboTime;

    protected NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
    int litTime;
    int litDuration;
    int cookingProgress;
    int cookingTotalTime;
    protected final ContainerData dataAccess = new ContainerData() {
        public int get(int p_58431_) {
            return switch (p_58431_) {
                case 0 -> FurnyEntity.this.litTime;
                case 1 -> FurnyEntity.this.litDuration;
                case 2 -> FurnyEntity.this.cookingProgress;
                case 3 -> FurnyEntity.this.cookingTotalTime;
                default -> 0;
            };
        }

        public void set(int p_58433_, int p_58434_) {
            switch (p_58433_) {
                case 0:
                    FurnyEntity.this.litTime = p_58434_;
                    break;
                case 1:
                    FurnyEntity.this.litDuration = p_58434_;
                    break;
                case 2:
                    FurnyEntity.this.cookingProgress = p_58434_;
                    break;
                case 3:
                    FurnyEntity.this.cookingTotalTime = p_58434_;
                    break;
            }

        }

        public int getCount() {
            return 4;
        }
    };

    public FurnyEntity(EntityType<? extends TamableAnimal> p_21803_, Level p_21804_) {
        super(p_21803_, p_21804_);
        this.quickCheck = RecipeManager.createCheck(RecipeType.SMELTING);
        this.refreshDimensions();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getTurboTime() > 0)
            this.setTurboTime(this.getTurboTime() - 1);
        if (this.level().isClientSide) {
            this.refreshDimensions();
            return;
        }
        boolean updateRequired = false;
        if (this.isLit()) {
            --this.litTime;
        }

        ItemStack fuel = this.items.get(1); // fuel
        boolean anySmeltable = !this.items.get(0).isEmpty(); // is anything to smelt
        boolean anyFuel = !fuel.isEmpty(); // is there any fuel
        if (this.isLit() || anySmeltable) {
            Recipe<?> recipe = null;
            if (anySmeltable) {
                recipe = this.quickCheck.getRecipeFor(this, this.level()).orElse(null);
            }

            int maxStackSize = this.getMaxStackSize();
            if (!this.isLit() && this.canBurn(this.level().registryAccess(), recipe, this.items, maxStackSize) && anyFuel) {
                this.litTime = this.getBurnDuration(fuel);
                this.litDuration = this.litTime;
                if (this.isLit()) {
                    updateRequired = true;
                    if (fuel.hasCraftingRemainingItem())
                        this.items.set(1, fuel.getCraftingRemainingItem());
                    else {
                        fuel.shrink(1);
                        if (fuel.isEmpty()) {
                            this.items.set(1, fuel.getCraftingRemainingItem());
                        }
                    }
                }
            }

            if (this.canBurn(this.level().registryAccess(), recipe, this.items, maxStackSize)) {
                if (this.isLit())
                    this.cookingProgress += (this.cookingTotalTime / 2);
                else
                    ++this.cookingProgress;
                if (this.cookingProgress >= this.cookingTotalTime) {
                    this.cookingProgress = 0;
                    this.cookingTotalTime = getTotalCookTime(this.level(), this);
                    if (this.burn(this.level().registryAccess(), recipe, this.items, maxStackSize)) {
                        this.setRecipeUsed(recipe);
                    }
                }
                updateRequired = true;
            } else {
                this.cookingProgress = 0;
            }
        }

        this.isSmelting = isLit() || anySmeltable;

        if (this.isSmelting != this.entityData.get(DATA_IS_SMELTING)) {
            updateRequired = true;
        }

        if (updateRequired) {
            setChanged();
        }
    }

    private boolean canBurn(RegistryAccess registryAccess, @Nullable Recipe<?> recipe, NonNullList<ItemStack> items, int maxStackSize) {
        if (!items.get(0).isEmpty() && recipe != null) {
            //noinspection unchecked
            ItemStack itemstack = ((Recipe<ContainerEntity>) recipe).assemble(this, registryAccess);
            if (itemstack.isEmpty()) {
                return false;
            } else {
                ItemStack itemstack1 = items.get(2);
                if (itemstack1.isEmpty()) {
                    return true;
                } else if (!ItemStack.isSameItem(itemstack1, itemstack)) {
                    return false;
                } else if (itemstack1.getCount() + itemstack.getCount() <= maxStackSize && itemstack1.getCount() + itemstack.getCount() <= itemstack1.getMaxStackSize()) { // Forge fix: make furnace respect stack sizes in furnace recipes
                    return true;
                } else {
                    return itemstack1.getCount() + itemstack.getCount() <= itemstack.getMaxStackSize(); // Forge fix: make furnace respect stack sizes in furnace recipes
                }
            }
        } else {
            return false;
        }
    }

    private boolean burn(RegistryAccess registryAccess, @Nullable Recipe<?> recipe, NonNullList<ItemStack> items, int maxStackSize) {
        if (recipe != null && this.canBurn(registryAccess, recipe, items, maxStackSize)) {
            ItemStack smeltable = items.get(0);
            //noinspection unchecked
            ItemStack fuel = ((Recipe<ContainerEntity>) recipe).assemble(this, registryAccess);
            ItemStack result = items.get(2);
            if (result.isEmpty()) {
                items.set(2, fuel.copy());
            } else if (result.is(fuel.getItem())) {
                result.grow(fuel.getCount());
            }

            if (smeltable.is(Blocks.WET_SPONGE.asItem()) && !items.get(1).isEmpty() && items.get(1).is(Items.BUCKET)) {
                items.set(1, new ItemStack(Items.WATER_BUCKET));
            }

            smeltable.shrink(1);
            return true;
        } else {
            return false;
        }
    }

    public void setRecipeUsed(@Nullable Recipe<?> p_58345_) {
        if (p_58345_ != null) {
            ResourceLocation resourcelocation = p_58345_.getId();
            this.recipesUsed.addTo(resourcelocation, 1);
        }
    }

    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand interactionHand) {
        if (isOwnedBy(player) && (player.getMainHandItem().is(Config.furnyTurboItem) || player.getOffhandItem().is(Config.furnyTurboItem)) && this.getTurboTime() < Config.furnyTurboMaxTime) {
            this.addParticlesAroundSelf();
            this.addTurboTime(Config.furnyTurboTimeAdded);
            this.usePlayerItem(player, interactionHand, (player.getMainHandItem().is(Config.furnyTurboItem) ? player.getMainHandItem() : player.getOffhandItem()));
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        if (isOwnedBy(player) && isOrderedToSit() && player.isShiftKeyDown()) {
            player.openMenu(this);
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        return super.mobInteract(player, interactionHand);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.2D, getIngredients(), false));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(10, new FurnyLookGoal(this));
        this.goalSelector.addGoal(11, new FurnyShootFirecoalGoal(this));
        this.goalSelector.addGoal(12, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(15, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(8, new ResetUniversalAngerTargetGoal<>(this, true));
    }

    public boolean isCharging() {
        return this.entityData.get(DATA_IS_CHARGING);
    }

    public void setCharging(boolean p_32759_) {
        this.entityData.set(DATA_IS_CHARGING, p_32759_);
    }

    @SuppressWarnings("DataFlowIssue")
    public void setTurboTime(int amount, @NotNull TimeUnits timeUnit) {
        this.turboTime = timeUnit.toTicks(amount);
        if (this.turboTime > 0) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.5d);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(4d);
        } else {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25d);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(2d);
        }
    }

    public void setTurboTime(int amount) {
        this.setTurboTime(amount, TimeUnits.TICK);
    }

    public void addTurboTime(int amount, @NotNull TimeUnits timeUnit) {
        this.setTurboTime(timeUnit.toTicks(amount) + getTurboTime());
    }

    public void addTurboTime(int amount) {
        this.addTurboTime(amount, TimeUnits.TICK);
    }

    public int getTurboTime() {
        return this.turboTime;
    }

    @Override
    public boolean canBreed() {
        return false;
    }

    @Override
    public float standingEyeHeight(Pose pose, @NotNull EntityDimensions entityDimensions) {
        return entityDimensions.height * 0.75f;
    }

    @Override
    public EntityType<? extends BaseEntity> createOffspring() {
        return ModEntities.FURNY.get();
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public @NotNull ItemStack getItem(int p_18941_) {
        return this.items.get(p_18941_);
    }

    @Override
    public @NotNull ItemStack removeItem(int p_18942_, int p_18943_) {
        return ContainerHelper.removeItem(this.items, p_18942_, p_18943_);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int p_18951_) {
        return ContainerHelper.takeItem(this.items, p_18951_);
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack itemStack) {
        ItemStack itemstack = this.items.get(slot);
        boolean flag = !itemStack.isEmpty() && ItemStack.isSameItemSameTags(itemstack, itemStack);
        this.items.set(slot, itemStack);
        if (itemStack.getCount() > this.getMaxStackSize()) {
            itemStack.setCount(this.getMaxStackSize());
        }

        if (slot == 0 && !flag) {
            this.cookingTotalTime = getTotalCookTime(this.level(), this);
            this.cookingProgress = 0;
            this.setChanged();
        }
    }

    @Override
    public void setChanged() {
        this.entityData.set(DATA_IS_SMELTING, this.isSmelting);
    }

    @Override
    public boolean stillValid(@NotNull Player p_18946_) {
        return this.isChestVehicleStillValid(p_18946_);
    }

    @Override
    public void die(@NotNull DamageSource p_21809_) {
        super.die(p_21809_);
        this.chestVehicleDestroyed(p_21809_, this.level(), this);
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    public void setLootTable(@Nullable ResourceLocation p_219926_) {
    }

    @Override
    public void setLootTableSeed(long p_219925_) {
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_IS_SMELTING, false);
        this.entityData.define(DATA_IS_CHARGING, false);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        ContainerHelper.saveAllItems(compoundTag, this.items);
        compoundTag.putInt("BurnTime", this.litTime);
        compoundTag.putInt("CookTime", this.cookingProgress);
        compoundTag.putInt("CookTimeTotal", this.cookingTotalTime);
        CompoundTag compoundtag = new CompoundTag();
        this.recipesUsed.forEach((p_187449_, p_187450_) -> compoundtag.putInt(p_187449_.toString(), p_187450_));
        compoundTag.put("RecipesUsed", compoundtag);
        compoundTag.putInt("TurboTime", this.turboTime);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        ContainerHelper.loadAllItems(compoundTag, this.items);
        this.litTime = compoundTag.getInt("BurnTime");
        this.cookingProgress = compoundTag.getInt("CookTime");
        this.cookingTotalTime = compoundTag.getInt("CookTimeTotal");
        this.litDuration = this.getBurnDuration(this.items.get(1));
        CompoundTag compoundtag = compoundTag.getCompound("RecipesUsed");

        for(String s : compoundtag.getAllKeys()) {
            this.recipesUsed.put(ResourceLocation.parse(s), compoundtag.getInt(s));
        }

        this.turboTime = compoundTag.getInt("TurboTime");
    }

    @Override
    public @NotNull NonNullList<ItemStack> getItemStacks() {
        return this.items;
    }

    @Override
    public void clearItemStacks() {
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
    }

    @Override
    protected @NotNull Set<Item> getFavoriteItems() {
        return Config.furnyFavoriteItems;
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int p_39954_, @NotNull Inventory inventory, @NotNull Player player) {
        return new FurnaceMenu(p_39954_, inventory, this, this.dataAccess);
    }

    private static int getTotalCookTime(Level p_222693_, FurnyEntity p_222694_) {
        return p_222694_.quickCheck.getRecipeFor(p_222694_, p_222693_).map(AbstractCookingRecipe::getCookingTime).orElse(200);
    }

    public boolean isLit() {
        return litTime > 0;
    }

    public boolean isSmelting() {
        if (this.level().isClientSide) {
            return this.entityData.get(DATA_IS_SMELTING);
        } else {
            return this.isSmelting;
        }
    }

    protected int getBurnDuration(@NotNull ItemStack p_58343_) {
        if (p_58343_.isEmpty()) {
            return 1;
        } else {
            return net.minecraftforge.common.ForgeHooks.getBurnTime(p_58343_, RecipeType.SMELTING);
        }
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.25F)
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.ATTACK_SPEED, 1.0D);
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose p_21047_) {
        EntityDimensions entityDimensions = super.getDimensions(p_21047_);
        return !this.isInSittingPose() ? entityDimensions.scale(1.0f, 1.45f) : entityDimensions;
    }

    protected void addParticlesAroundSelf() {
        for(int i = 0; i < 5; ++i) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;
            this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY(), this.getRandomZ(1.0D), d0, d1, d2);
        }
    }

    public static boolean canSpawn(EntityType<? extends Mob> entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
        if (!(levelAccessor instanceof ServerLevelAccessor serverLevelAccessor)) {
            return false;
        }
        boolean base = Mob.checkMobSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, randomSource);
        boolean isMineshaft = serverLevelAccessor.getLevel().structureManager().getStructureWithPieceAt(blockPos, BuiltinStructures.MINESHAFT).isValid();

        return base && isMineshaft;
    }

    static class FurnyShootFirecoalGoal extends Goal {
        private final FurnyEntity furny;
        public int chargeTime;
        private final int totalChargeTime = Config.furnyChargeTime;
        private final int cooldown = Config.furnyCooldown - totalChargeTime;

        public FurnyShootFirecoalGoal(FurnyEntity furny) {
            this.furny = furny;
        }

        @Override
        public boolean canUse() {
            return this.furny.getTarget() != null && this.furny.getTarget().isAlive() && this.furny.hasLineOfSight(this.furny.getTarget());
        }

        @Override
        public void start() {
            this.chargeTime = 0;
        }

        @Override
        public void stop() {
            this.furny.setCharging(false);
            this.furny.setTarget(null);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity target = this.furny.getTarget();
            if (target != null) {
                if (target.distanceToSqr(this.furny) < 2048f && this.furny.hasLineOfSight(target)) {
                    Level level = this.furny.level();
                    if (this.chargeTime % 2 == 0 && this.furny.getTurboTime() > 0) {
                        this.chargeTime += 2;
                    } else {
                        this.chargeTime++;
                    }

                    if (this.chargeTime >= totalChargeTime) {
                        Vec3 vec3 = this.furny.getViewVector(1.0F);
                        double x = target.getX() - (this.furny.getX() + vec3.x * 1.25D);
                        double y = target.getY(0.5D) - (0.5D + this.furny.getY(0.5D));
                        double z = target.getZ() - (this.furny.getZ() + vec3.z * 1.25D);

                        FireCoalEntity firecoal = new FireCoalEntity(level, this.furny, x, y, z);
                        firecoal.setPos(this.furny.getX() + vec3.x, this.furny.getY(0.5D) + 0.25D, firecoal.getZ() + vec3.z);
                        level.addFreshEntity(firecoal);
                        this.chargeTime = -cooldown;
                    }
                } else if (this.chargeTime > 0) {
                    this.chargeTime--;
                }

                this.furny.setCharging(true);
            }
        }
    }

    static class FurnyLookGoal extends Goal {
        private final FurnyEntity furny;

        public FurnyLookGoal(FurnyEntity p_32762_) {
            this.furny = p_32762_;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        public boolean canUse() {
            return true;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            if (this.furny.getTarget() == null) {
                Vec3 vec3 = this.furny.getDeltaMovement();
                this.furny.setYRot(-((float) Mth.atan2(vec3.x, vec3.z)) * (180F / (float)Math.PI));
                this.furny.yBodyRot = this.furny.getYRot();
            } else {
                LivingEntity target = this.furny.getTarget();
                if (target.distanceToSqr(this.furny) < 2048f) {
                    double d1 = target.getX() - this.furny.getX();
                    double d2 = target.getZ() - this.furny.getZ();
                    this.furny.setYRot(-((float)Mth.atan2(d1, d2)) * (180F / (float)Math.PI));
                    this.furny.yBodyRot = this.furny.getYRot();
                }
            }

        }
    }
}
