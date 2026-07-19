package info.partonetrain.cle.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import info.partonetrain.cle.Cle;
import info.partonetrain.cle.CleUtils;
import info.partonetrain.cle.ReputationCapSaveData;
import net.minecraft.server.level.ServerLevel;
import org.millenaire.village.Village;
import org.millenaire.village.VillageId;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.UUID;

@Mixin(Village.class)
public class VillageMixin {

    @Mutable
    @Final
    @Shadow
    private final VillageId id; //this is just a record containing UUID
    @Shadow
    private final String villageName;

    public VillageMixin(VillageId id, String villageName) {
        this.id = id;
        this.villageName = villageName;
    }

    @ModifyArgs(method="adjustReputation", at= @At(value = "INVOKE", target = "Lorg/millenaire/village/Village;addReputation(Ljava/util/UUID;I)I"))
    public void cle$adjustReputation(Args args, @Local(argsOnly = true) ServerLevel level){
        int reputationAttemptingToAdd = args.get(1);
        if(reputationAttemptingToAdd <= 0){
            return;
        }
        else{
            long time = level.getDayTime();
            int dayNumber = (int) (time / 24000);

            UUID playerUUID = args.get(0);
            ReputationCapSaveData saved = ReputationCapSaveData.getInstance(level);
            if (saved.canPlayerGainMoreRepToday(playerUUID, id.uuid(), dayNumber)) {
                int modifiedRep = saved.add(playerUUID, id.uuid(), reputationAttemptingToAdd, dayNumber);
                if(reputationAttemptingToAdd < modifiedRep){
                    Cle.LOGGER.info("Capped reputation gain of UUID " + playerUUID + " from " + reputationAttemptingToAdd + " to " + modifiedRep);
                }
                args.set(1, modifiedRep);
            }
            else{
                Cle.LOGGER.info("UUID " + playerUUID + " cannot gain any more rep today");
                CleUtils.informPlayerRepCapReached(level, playerUUID, villageName);
                args.set(1, 0);
            }
        }
    }
}
