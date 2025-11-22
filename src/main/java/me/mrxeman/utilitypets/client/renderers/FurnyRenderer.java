package me.mrxeman.utilitypets.client.renderers;

import me.mrxeman.utilitypets.entity.FurnyEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import static me.mrxeman.utilitypets.Utilitypets.MOD_ID;

public class FurnyRenderer extends GeoEntityRenderer<FurnyEntity> {
    public FurnyRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(ResourceLocation.fromNamespaceAndPath(MOD_ID, "furny")));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull FurnyEntity animatable) {
        if (animatable.isSmelting() || animatable.isCharging()) {
            return ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/entity/furny_lit.png");
        }
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/entity/furny_unlit.png");
    }
}
