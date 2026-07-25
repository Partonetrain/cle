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

@Mixin(VillageSpawnQueue.class)
public class VillageSpawnQueueMixin {

    @Inject(method = "processMainQueue", at= @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;distSqr(Lnet/minecraft/core/Vec3i;)D"), cancellable = true)
    private void processMainQueue(ServerLevel level, VillageSavedData savedData, VillageManager manager, boolean genVillages, boolean genLone, boolean log, long currentTick, CallbackInfo ci, @Local BlockPos candidate){
        if(CleConfig.MAX_DISTANCE.get() != 0) {

            double distToSpawnSq = candidate.distSqr(level.getSharedSpawnPos());
            if (distToSpawnSq > (CleConfig.MAX_DISTANCE.get() * CleConfig.MAX_DISTANCE.get())) {
                if (log) {
                    Cle.LOGGER.info("Spawn at " + candidate.toShortString() + " was rejected due to config " + CleConfig.MAX_DISTANCE.get());
                }
                ci.cancel();
            }
        }

    }

}
