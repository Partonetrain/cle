package info.partonetrain.cle.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import info.partonetrain.cle.Cle;
import info.partonetrain.cle.item.AlternativeInuitTrident;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.neoforged.neoforge.client.ClientHooks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//this class is basically copied directly from https://github.com/Mrbysco/TieredTridents/blob/1.21.1/src/main/java/com/mrbysco/tieredtridents/mixin/ItemRendererMixin.java
//no need to reinvent the wheel right?
//tridents are super weird.
@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin implements ResourceManagerReloadListener {

    @Shadow
    @Final
    private ItemModelShaper itemModelShaper;

    @Shadow
    @Final
    private static ModelResourceLocation TRIDENT_MODEL;

    @Shadow
    public abstract void renderModelLists(BakedModel model, ItemStack stack, int combinedLight, int combinedOverlay, PoseStack poseStack, VertexConsumer buffer);

    @Shadow
    @Final
    public static ModelResourceLocation TRIDENT_IN_HAND_MODEL;

    @Inject(
            method = "render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z", ordinal = 0),
            cancellable = true
    )
    public void render(ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack,
                       MultiBufferSource buffer, int combinedLight, int combinedOverlay, BakedModel p_model, CallbackInfo ci) {
        if (itemStack.getItem() instanceof AlternativeInuitTrident) {
            ModelResourceLocation modelLocation = ModelResourceLocation.standalone(Cle.ALTERNATIVE_INUIT_TRIDENT.getId().withPrefix("item/"));
            BakedModel tridentModel = this.itemModelShaper.getModelManager().getModel(modelLocation);

            tridentModel = ClientHooks.handleCameraTransforms(poseStack, tridentModel, displayContext, leftHand);
            poseStack.translate(-0.5F, -0.5F, -0.5F);
            boolean flag1;
            if (displayContext != ItemDisplayContext.GUI && !displayContext.firstPerson() && itemStack.getItem() instanceof BlockItem) {
                Block block = ((BlockItem) itemStack.getItem()).getBlock();
                flag1 = !(block instanceof HalfTransparentBlock) && !(block instanceof StainedGlassPaneBlock);
            } else {
                flag1 = true;
            }
            for (var model : tridentModel.getRenderPasses(itemStack, flag1)) {
                for (var rendertype : model.getRenderTypes(itemStack, flag1)) {
                    VertexConsumer vertexconsumer;
                    if (flag1) {
                        vertexconsumer = ItemRenderer.getFoilBufferDirect(buffer, rendertype, true, itemStack.hasFoil());
                    } else {
                        vertexconsumer = ItemRenderer.getFoilBuffer(buffer, rendertype, true, itemStack.hasFoil());
                    }

                    this.renderModelLists(model, itemStack, combinedLight, combinedOverlay, poseStack, vertexconsumer);
                }
            }

            poseStack.popPose();
            ci.cancel();
        }
    }

    @Inject(
            method = "getModel(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;I)Lnet/minecraft/client/resources/model/BakedModel;",
            at = @At("HEAD"),
            cancellable = true
    )
    public void getModel(ItemStack stack, Level level, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
        if (stack.getItem() instanceof AlternativeInuitTrident) {
            BakedModel bakedmodel = this.itemModelShaper.getModelManager().getModel(ModelResourceLocation.standalone(Cle.ALTERNATIVE_INUIT_TRIDENT.getId().withPrefix("item/").withSuffix("_in_hand")));

            ClientLevel clientlevel = level instanceof ClientLevel ? (ClientLevel) level : null;
            BakedModel bakedmodel1 = bakedmodel.getOverrides().resolve(bakedmodel, stack, clientlevel, entity, seed);
            cir.setReturnValue(bakedmodel1 == null ? this.itemModelShaper.getModelManager().getMissingModel() : bakedmodel1);
        }
    }
}