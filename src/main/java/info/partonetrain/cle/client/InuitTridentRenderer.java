package info.partonetrain.cle.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import info.partonetrain.cle.Cle;
import info.partonetrain.cle.entity.ThrownInuitTridentEntity;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRenderer;
import org.jetbrains.annotations.NotNull;

public class InuitTridentRenderer extends EntityRenderer<ThrownInuitTridentEntity> {
    public static final ResourceLocation TRIDENT_LOCATION = ResourceLocation.fromNamespaceAndPath(Cle.MODID, "textures/entity/inuit_trident.png");
    //this texture really could be better but it's impossible to find a model for it for blockbench
    //the vanilla model is harcoded, ⬇️️
    private final TridentModel model;
    //i tried making a new model myself but couldn't figure out how to export it as a model instead of an entitymodel.
    public InuitTridentRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new TridentModel(context.bakeLayer(ModelLayers.TRIDENT));
    }

    @Override
    public void render(ThrownInuitTridentEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot()) + 90.0F));
        VertexConsumer vertexconsumer = ItemRenderer.getFoilBufferDirect(
                buffer, this.model.renderType(this.getTextureLocation(entity)), false, entity.isFoil()
        );
        this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(ThrownInuitTridentEntity thrownInuitTridentEntity) {
        return TRIDENT_LOCATION;
    }

}
