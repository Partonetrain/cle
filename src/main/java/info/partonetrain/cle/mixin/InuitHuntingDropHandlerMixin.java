package info.partonetrain.cle.mixin;

import info.partonetrain.cle.Cle;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import org.millenaire.item.InuitHuntingDropHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InuitHuntingDropHandler.class)
public class InuitHuntingDropHandlerMixin {

    @Inject(method = "onLivingDrops", at=@At("HEAD"), cancellable = true)
    private static void cle$onLivingDrops(LivingDropsEvent event, CallbackInfo ci){
        LivingEntity entity = event.getEntity();
        if (entity.getType().is(Cle.DISALLOWED_HUNTING)){
            ci.cancel();
        }
    }
}
