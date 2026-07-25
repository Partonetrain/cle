package info.partonetrain.cle.mixin.client;

import info.partonetrain.cle.CleConfig;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.millenaire.client.MillenaireKeyBindings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MillenaireKeyBindings.class)
public class MillenaireKeyBindingsMixin {
    @Inject(method = "onRegisterKeyMappings", at=@At("HEAD"), cancellable = true)
    private static void cle$onRegisterKeyMappings(RegisterKeyMappingsEvent event, CallbackInfo ci){
        if(CleConfig.PREVENT_KEYBINDS.getAsBoolean()){
            ci.cancel();
        }
    }
}
