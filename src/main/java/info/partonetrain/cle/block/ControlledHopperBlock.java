package info.partonetrain.cle.block;

import info.partonetrain.cle.Cle;
import info.partonetrain.cle.blockentity.ControlledHopperBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class ControlledHopperBlock extends HopperBlock {
    public ControlledHopperBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ControlledHopperBlockEntity(pos, state);
    }

    //needed for ticking
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, Cle.CONTROLLED_HOPPER_BLOCK_ENTITY_TYPE.get(), HopperBlockEntity::pushItemsTick);
        //pushItemsTick has a mixin
    }


}
