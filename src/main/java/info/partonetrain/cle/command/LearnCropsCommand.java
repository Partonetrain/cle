package info.partonetrain.cle.command;

import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.millenaire.village.PlayerCultureReputation;

public class LearnCropsCommand {

    final String[] CROPS = { "cotton", "maize", "rice", "turmeric", "grapes", "sapling_pistachio", "sapling_olivetree", "sapling_pistachio" };

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("cle")
                        .then(Commands.literal("learn_crops")
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> {
                                    if (context.getSource().isPlayer()) {
                                        ServerPlayer sp = context.getSource().getPlayer();

                                        PlayerCultureReputation cultureRep = PlayerCultureReputation.get((ServerLevel) sp.level());
                                        for(String s : CROPS){
                                            cultureRep.learnCrop(sp.getUUID(), s);
                                            context.getSource().sendSystemMessage(Component.literal("learned " + "\"" + s + "\""));
                                        }



                                        return 1;
                                    }
                                    else {
                                        context.getSource().sendFailure(Component.literal("Must be a player executed command"));
                                        return 0;
                                    }

                                }
                        )));

    }

    //CleUtils.resetReputation(village, player);
}
