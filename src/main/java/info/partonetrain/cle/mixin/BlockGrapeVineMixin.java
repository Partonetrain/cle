package info.partonetrain.cle.mixin;

import info.partonetrain.cle.CleConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.millenaire.block.BlockGrapeVine;
import org.millenaire.item.ModItems;
import org.millenaire.village.PlayerCultureReputation;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockGrapeVine.class)
public abstract class BlockGrapeVineMixin extends BushBlock { //abstract so no-impl codec()

    protected BlockGrapeVineMixin(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if(CleConfig.GRAPE_VINE_HARVEST.getAsBoolean() && player instanceof ServerPlayer serverPlayer && !serverPlayer.isFakePlayer()) {
            PlayerCultureReputation cultureRep = PlayerCultureReputation.get((ServerLevel) serverPlayer.level());
            if(cultureRep.hasLearnedCrop(serverPlayer.getUUID(), "grapes")) {
                int i = state.getValue(BlockGrapeVine.AGE);
                boolean fullyGrown = i == 7;
                if (i >= 4) {
                    popResource(level, pos, new ItemStack(ModItems.GRAPES.asItem(), 1 + (fullyGrown ? 1 : 0)));
                    level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
                    BlockState blockstate = state.setValue(BlockGrapeVine.AGE, 1);
                    level.setBlock(pos, blockstate, 2);
                    level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, blockstate));
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }else{
                serverPlayer.sendSystemMessage(Component.translatable("message.millenaire.crop_planting_knowledge"));
                return InteractionResult.PASS;
            }
        }

        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

}
