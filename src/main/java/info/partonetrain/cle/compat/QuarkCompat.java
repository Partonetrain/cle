package info.partonetrain.cle.compat;

import info.partonetrain.cle.CleConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.millenaire.block.ModBlocks;
import org.millenaire.village.PlayerCultureReputation;
import org.violetmoon.quark.api.event.SimpleHarvestEvent;
import org.violetmoon.quark.content.tweaks.module.SimpleHarvestModule;

public class QuarkCompat {

    //very nice that there's an event for this. i was originally going to make a mixin
    @SubscribeEvent
    public void onSimpleHarvest(SimpleHarvestEvent event){
        if(event.entity instanceof ServerPlayer serverPlayer && !serverPlayer.isFakePlayer()){
            if(BuiltInRegistries.BLOCK.getKey(event.blockState.getBlock()).getNamespace().equals("millenaire")) {
                if(!canPlayerSimpleHarvest(serverPlayer, event.blockState)){
                    serverPlayer.sendSystemMessage(Component.translatable("message.millenaire.crop_planting_knowledge"));
                    event.setCanceled(true);
                }
            }
        }
        else{
            if(!CleConfig.SIMPLE_HARVEST_NON_PLAYERS.getAsBoolean()){
                event.setCanceled(true);
            }
        }
    }

    public static boolean canPlayerSimpleHarvest(ServerPlayer serverPlayer, BlockState state){
        PlayerCultureReputation cultureRep = PlayerCultureReputation.get((ServerLevel) serverPlayer.level());
        String cropKey = "";

        if(state.is(ModBlocks.CROP_COTTON.get())){
            cropKey = "cotton";
        }
        else if (state.is(ModBlocks.CROP_MAIZE.get())){
            cropKey = "maize";
        }
        else if (state.is(ModBlocks.CROP_RICE.get())){
            cropKey = "rice";
        }
        else if (state.is(ModBlocks.CROP_TURMERIC.get())){
            cropKey = "turmeric";
        }
        else if (state.is(ModBlocks.CROP_VINE.get())){
            cropKey = "grapes";
        }

        return cultureRep.hasLearnedCrop(serverPlayer.getUUID(), cropKey);
    }
}
