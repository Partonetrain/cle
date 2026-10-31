package info.partonetrain.cle.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import info.partonetrain.cle.Cle;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.millenaire.building.BuildingInstance;
import org.millenaire.building.BuildingPlanSet;
import org.millenaire.config.MillenaireServerConfig;
import org.millenaire.network.BuildingProjectRequestPayload;
import org.millenaire.village.*;
import org.millenaire.world.BuildingLocationFinder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(BuildingProjectRequestPayload.class)
public class BuildingProjectRequestPayloadMixin {

    @Inject(method = "lambda$handleOnServer$0", at= @At(value = "INVOKE", target = "Lorg/millenaire/village/ControlledProjectsService;formatDistanceDirection(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)Ljava/lang/String;"))
    private static void cle$logLocationClash(IPayloadContext context, BuildingProjectRequestPayload payload, CallbackInfo ci, @Local(name = "planSet") BuildingPlanSet planSet, @Local(name = "evaluation") BuildingLocationFinder.AnchorEvaluation evaluation){
        Cle.LOGGER.info("--- Extra Location Clash Information ---");
        Cle.LOGGER.info("XYZ: " + (evaluation.errorPos() == null ? "null" : evaluation.errorPos().toShortString()));
        Cle.LOGGER.info("config default clear margin is set to " + MillenaireServerConfig.getDefaultClearMargin());
        Village v = Village.resolve((ServerLevel) context.player().level(), new VillageId(UUID.fromString(payload.villageUuid())));
        BuildingInstance bi = v.getBuildingAt(evaluation.errorPos());
        Cle.LOGGER.info("Building at errorPos: ");
        Cle.LOGGER.info(" - planSet nativeName=" + planSet.nativeName() + ", clearMargins: " + planSet.clearMargins());
        Cle.LOGGER.info(" - planId=" + bi.getPlanId().toString() + ", planSetId=" + bi.getPlanSetId().toString());
        Cle.LOGGER.info(" - building level=" + bi.getLevel() + ", UUID=" + bi.getId().uuid().toString());
    }
}
