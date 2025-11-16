package me.mrxeman.utilitypets.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import me.mrxeman.utilitypets.entity.LucasTheSpiderEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import static me.mrxeman.utilitypets.Utilitypets.MOD_ID;

public class LucasTheSpiderRenderer extends GeoEntityRenderer<LucasTheSpiderEntity> {
    public LucasTheSpiderRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(ResourceLocation.fromNamespaceAndPath(MOD_ID, "lucas_the_spider"), "head"));
    }

    @Override
    public void render(@NotNull LucasTheSpiderEntity entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.4f, 0.4f, 0.4f);
        }

        poseStack.scale(0.7f, 0.7f, 0.7f);

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
