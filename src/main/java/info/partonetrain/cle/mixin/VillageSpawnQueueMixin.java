package info.partonetrain.cle.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import info.partonetrain.cle.Cle;
import info.partonetrain.cle.CleConfig;
import info.partonetrain.cle.ParsedConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.Structure;
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

        /* MILLENAIRE ALREADY HAS CODE FOR THIS
        if(!ParsedConfigs.minDistanceConfigs.isEmpty()){
            for(ParsedConfigs.MinDistanceConfig mdc : ParsedConfigs.minDistanceConfigs){
                HolderSet<Structure> structureHolderSet = mdc.isTag() ? //contains structures, either all the ones in the tag or just the one
                        level.registryAccess().registryOrThrow(Registries.STRUCTURE).getOrCreateTag(mdc.structureTag()) :
                        HolderSet.direct(level.registryAccess().registryOrThrow(Registries.STRUCTURE).getHolderOrThrow(mdc.structure()));

                int searchDistance = (int) Math.sqrt(mdc.minDistance());
                //the following is the same way LocateCommand does it
                Pair<BlockPos, Holder<Structure>> nearestStructure = level.getChunkSource().getGenerator().findNearestMapStructure(level, structureHolderSet, candidate, searchDistance, false);
                //potential issue: there's technically nothing preventing structures in the same tag from being close together and findNearestMapStructure only finding the first one,
                //and then the millage spawning inside/replacing the farther one
                //let's hope that doesn't come up...

                if(nearestStructure != null){
                    int realDist = (int) candidate.distSqr(nearestStructure.getFirst());

                    if(realDist <= mdc.minDistance()){
                        String logged = mdc.isTag() ? "#" + mdc.structureTag().location().toString() : mdc.structure().location().toString();
                        if (log || Cle.DEV) {
                            Cle.LOGGER.info("Spawn at " + candidate.toShortString() + " was rejected due to a " + logged + " being too close (configured distance: " + mdc.minDistance() + " <= actual distance: " + realDist + ")");
                        }
                        ci.cancel();
                    }
                }

            }
        }
        */
    }

}
