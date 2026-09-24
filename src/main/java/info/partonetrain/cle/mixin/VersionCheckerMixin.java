package info.partonetrain.cle.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import info.partonetrain.cle.Cle;
import info.partonetrain.cle.CleConfig;
import net.minecraft.server.level.ServerPlayer;
import org.millenaire.net.ModApiClient;
import org.millenaire.net.VersionChecker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.http.HttpRequest;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mixin(VersionChecker.class)
public class VersionCheckerMixin {
    @Inject(method = "onPlayerLogin", at=@At("HEAD"), cancellable = true)
    private static void cle$onPlayerLogin(ServerPlayer player, CallbackInfo ci){
        if(CleConfig.DISABLE_VERSION_CHECK.getAsBoolean()){
            Cle.LOGGER.info("Prevented version check");
            ci.cancel();
        }
    }

}
