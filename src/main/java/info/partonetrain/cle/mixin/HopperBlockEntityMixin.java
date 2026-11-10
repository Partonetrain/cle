package info.partonetrain.cle.mixin;

import info.partonetrain.cle.CleConfig;
import info.partonetrain.cle.CleUtils;
import info.partonetrain.cle.blockentity.ControlledHopperBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.millenaire.block.LockedChestBlockEntity;
import org.millenaire.block.ModBlocks;
import org.millenaire.village.Village;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HopperBlockEntity.class)
public class HopperBlockEntityMixin {

    //push
    @Inject(method = "ejectItems", at = @At(value = "HEAD"), cancellable = true)
    private static void cle$ejectItems(Level level, BlockPos pos, HopperBlockEntity blockEntity, CallbackInfoReturnable<Boolean> cir) {
        if (CleConfig.FIX_HOPPER_INTERACTIONS.getAsBoolean()) {
            BlockState hopperState = level.getBlockState(pos);
            BlockPos pushToPos = pos.relative(hopperState.getValue(HopperBlock.FACING));
            BlockState pushToState = level.getBlockState(pushToPos);
            if (pushToState.is(ModBlocks.LOCKED_CHEST)) {
                boolean interact = cle$shouldInteract(level, pushToPos, blockEntity);
                if(!interact){
                    cir.setReturnValue(false);
                }
            }
            //if it's not a locked chest, continue as normal
        }
    }

    //pull
    @Inject(method = "suckInItems", at = @At("HEAD"), cancellable = true)
    private static void cle$suckInItems(Level level, Hopper hopper, CallbackInfoReturnable<Boolean> cir) {
        if (CleConfig.FIX_HOPPER_INTERACTIONS.getAsBoolean()) {
            BlockPos hopperPos = BlockPos.containing(hopper.getLevelX(), hopper.getLevelY(), hopper.getLevelZ());
            BlockPos pullFromPos = BlockPos.containing(hopper.getLevelX(), hopper.getLevelY() + 1.0, hopper.getLevelZ());
            //uhh I guess these are doubles because hoppers can be entities? not relevant here but that's what the original code does
            BlockEntity blockEntity = level.getBlockEntity(hopperPos);

            if (level.getBlockState(pullFromPos).is(ModBlocks.LOCKED_CHEST)) {
                boolean interact = cle$shouldInteract(level, pullFromPos, blockEntity);
                if(!interact){
                    cir.setReturnValue(false);
                }
            }
            //if it's not a locked chest, continue as normal
        }
    }

    @Inject(method = "pushItemsTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/HopperBlockEntity;tryMoveItems(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/HopperBlockEntity;Ljava/util/function/BooleanSupplier;)Z"))
    private static void cle$pushItemsTick(Level level, BlockPos pos, BlockState state, HopperBlockEntity blockEntity, CallbackInfo ci) {
        if (blockEntity instanceof ControlledHopperBlockEntity chbe) {
            chbe.showColoredParticle();
        }
    }


    @Unique
    private static boolean cle$shouldInteract(Level level, BlockPos pushToOrPullFromPos, BlockEntity blockEntity) {
        BlockEntity beAtPos = level.getBlockEntity(pushToOrPullFromPos);
        if (beAtPos instanceof LockedChestBlockEntity lcbe) {
            if (lcbe.getBuildingId() == null) {
                //unlocked chest, allow the pull from any hopper
                return true;
            }
            else if (CleConfig.CONTROLLED_HOPPERS.getAsBoolean() && blockEntity instanceof ControlledHopperBlockEntity chbe) {
                if (level instanceof ServerLevel sl) {
                    if (chbe.villageId != null) {
                        Village village = CleUtils.getVillageFromBuildingId(sl, lcbe.getBuildingId());
                        if (village.getId().equals(chbe.villageId)) {
                            return true; //allow the push or pull
                        } else {
                            //village does not match!
                            CleUtils.addParticlesToBlock(ParticleTypes.ANGRY_VILLAGER, pushToOrPullFromPos, sl);
                            return false;
                        }
                    }
                }
            }
        }
        return false;
    }
}
