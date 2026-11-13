package info.partonetrain.cle.compat;

import dev.architectury.event.CompoundEventResult;
import dev.ftb.mods.ftbchunks.api.ClaimResult;
import dev.ftb.mods.ftbchunks.api.ClaimedChunk;
import dev.ftb.mods.ftbchunks.api.event.ClaimedChunkEvent;
import info.partonetrain.cle.CleConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import org.millenaire.village.Village;
import org.millenaire.village.VillageSavedData;

//thx to this for example https://github.com/Lightman314/LightmansCompat/blob/3f591c8dec54efbe234f71af31ffde3a98d9bd2e/src/main/java/io/github/lightman314/lightmanscompat/ftbchunks/util/ClaimInteractionHandler.java#L18
public class FTBChunksCompat {

    public static void init(){
        ClaimedChunkEvent.BEFORE_CLAIM.register(FTBChunksCompat::onClaimedChunkEvent);
    }

    private static CompoundEventResult<ClaimResult> onClaimedChunkEvent(CommandSourceStack commandSourceStack, ClaimedChunk claimedChunk) {
        if (CleConfig.DISALLOW_CLAIMING_VILLAGE.getAsBoolean()) {
            ServerLevel serverLevel = (ServerLevel) commandSourceStack.getPlayer().level();

            ChunkPos chunkPos = new ChunkPos(claimedChunk.getPos().x(), claimedChunk.getPos().z());
            BlockPos center = chunkPos.getMiddleBlockPosition(0);
            Village village = VillageSavedData.get(serverLevel)
                    .getVillageManager()
                    .findNearestVillage(center, 100.0);

            if (village != null && !village.isLoneBuilding() && village.computeVillageChunks().contains(chunkPos)) {
                if (village.isPlayerControlled() && village.getOwnerUUID() != null) {
                    if (commandSourceStack.getPlayer().getUUID() == village.getOwnerUUID()) {
                        //do nothing. allow operation
                        return CompoundEventResult.pass();
                    } else {
                        return CompoundEventResult.interruptDefault(ClaimResult.customProblem("cle.ftbchunks.too_close_to_millage"));
                    }
                } else {
                    return CompoundEventResult.interruptDefault(ClaimResult.customProblem("cle.ftbchunks.too_close_to_millage"));
                }
            }
        }
        return CompoundEventResult.pass();
    }
}
