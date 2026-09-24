package info.partonetrain.cle.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import info.partonetrain.cle.Cle;
import info.partonetrain.cle.CleConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.millenaire.village.VillageManager;
import org.millenaire.village.VillageSavedData;
import org.millenaire.world.VillageSpawnQueue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VillageSpawnQueue.class)
public class VillageSpawnQueueMixin {

    @Inject(method = "processMainQueue", at= @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;distSqr(Lnet/minecraft/core/Vec3i;)D"), cancellable = true)
    private void cle$processMainQueue1(ServerLevel level, VillageSavedData savedData, VillageManager manager, boolean genVillages, boolean genLone, boolean log, long currentTick, CallbackInfo ci, @Local BlockPos candidate){
        if(CleConfig.MAX_DISTANCE.get() != 0) {
            double distToSpawnSq = candidate.distSqr(level.getSharedSpawnPos());
            if (distToSpawnSq > (CleConfig.MAX_DISTANCE.get() * CleConfig.MAX_DISTANCE.get())) {
                if (log || Cle.DEV) {
                    Cle.LOGGER.info("Spawn at " + candidate.toShortString() + " was rejected due to max distance config " + CleConfig.MAX_DISTANCE.get());
                }
                ci.cancel();
            }
        }

        if(CleConfig.REJECTION_CHANCE.getAsDouble() != 0.0D){
            double roll = level.getRandom().nextDouble();
            if(roll < CleConfig.REJECTION_CHANCE.getAsDouble()){
                if (log || Cle.DEV) {
                    Cle.LOGGER.info("Spawn at " + candidate.toShortString() + " was rejected due to rolling " + roll + " which was < rejection chance config " + CleConfig.REJECTION_CHANCE.get());
                }
                ci.cancel();
            }
        }
    }

}
