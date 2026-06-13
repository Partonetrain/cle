package info.partonetrain.cle.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class InuitTridentBlockEntityWithoutLevelRenderer extends BlockEntityWithoutLevelRenderer {
    private TridentModel tridentModel;

    public InuitTridentBlockEntityWithoutLevelRenderer(BlockEntityRendererProvider.Context context) {
        super(context.getBlockEntityRenderDispatcher(), context.getModelSet());
        this.tridentModel = new TridentModel(context.getModelSet().bakeLayer(ModelLayers.TRIDENT));
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
                             MultiBufferSource buffer, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.scale(1.0F, -1.0F, -1.0F);
        VertexConsumer vertexConsumer = ItemRenderer.getFoilBufferDirect(
                buffer, this.tridentModel.renderType(InuitTridentRenderer.TRIDENT_LOCATION), false, stack.hasFoil()
        );
        this.tridentModel.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay);
        poseStack.popPose();
    }
}
