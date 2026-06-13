package info.partonetrain.cle.mixin;

import info.partonetrain.cle.CleConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.millenaire.block.LockedChestBlock;
import org.millenaire.block.ModBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HopperBlockEntity.class)
public class HopperBlockEntityMixin {
    @Inject(method = "getBlockContainer", at=@At("HEAD"), cancellable = true)
    private static void cle$getBlockContainer(Level level, BlockPos pos, BlockState state, CallbackInfoReturnable<Container> cir){
        if(CleConfig.PREVENT_HOPPER_INTERACTIONS.getAsBoolean()){
            if(state.is(ModBlocks.LOCKED_CHEST)){
                cir.setReturnValue(null);
            }
        }
    }
}
