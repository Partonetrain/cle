package info.partonetrain.cle.mixin;

import info.partonetrain.cle.CleUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import org.millenaire.culture.VillagerType;
import org.millenaire.entity.MillVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MillVillager.class)
public class MillVillagerMixin extends PathfinderMob {

    protected MillVillagerMixin(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "updateClothTextures", at=@At("HEAD"), cancellable = true)
    public void cle$updateClothTextures(VillagerType vType, CallbackInfo ci){
        MillVillager self = (MillVillager) (Object) this;
        if(CleUtils.isTestingMillager(self)){
            ci.cancel();
        }
    }

    @Inject(method = "isLocalMerchant", at=@At("HEAD"), cancellable = true)
    public void cle$isLocalMerchant(CallbackInfoReturnable<Boolean> cir){
        MillVillager self = (MillVillager) (Object) this;
        if(CleUtils.isTestingMillager(self)){
            cir.setReturnValue(false);
        }
    }
}
