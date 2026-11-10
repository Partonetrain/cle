package info.partonetrain.cle.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import info.partonetrain.cle.CleConfig;
import org.millenaire.client.render.MillVillagerRenderer;
import org.millenaire.entity.MillVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MillVillagerRenderer.class)
public class MillVillagerRendererMixin {

    @WrapOperation(method = "renderNameTag(Lorg/millenaire/entity/MillVillager;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V", at= @At(value = "INVOKE", target = "Lorg/millenaire/entity/MillVillager;getSpeechText()Ljava/lang/String;"))
    private String cle$disableSpeechText(MillVillager instance, Operation<String> original) {
        if (CleConfig.DISABLE_SPEECH_NAMETAG.getAsBoolean()) {
            return null;
        }
        return original.call(instance);
    }
}
