package info.partonetrain.cle.mixin.client;

import info.partonetrain.cle.CleConfig;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.millenaire.network.ClientPayloadHandler;
import org.millenaire.network.SpeechChatPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPayloadHandler.class)
public class ClientPayloadHandlerMixin {

    @Inject(method = "handleSpeechChat", at=@At("HEAD"), cancellable = true)
    private static void cle$handleSpeechChat(SpeechChatPayload payload, IPayloadContext context, CallbackInfo ci){
        if(CleConfig.DISABLE_SPEECH_CHAT.getAsBoolean()) {
            ci.cancel();
        }
    }
}
