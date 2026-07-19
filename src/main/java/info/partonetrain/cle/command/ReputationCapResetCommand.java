package info.partonetrain.cle.command;

import info.partonetrain.cle.CleConfig;
import info.partonetrain.cle.CleUtils;
import info.partonetrain.cle.ReputationCapSaveData;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.millenaire.village.Village;
import org.millenaire.village.VillageSavedData;

public class ReputationCapResetCommand {

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        if (CleConfig.REPUTATIION_CAP_PER_DAY.getAsInt() <= 0) {
            return;
        }

        event.getDispatcher().register(
                Commands.literal("cle")
                        .then(Commands.literal("reset_reputation_cap")
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> {
                                    if (context.getSource().isPlayer()) {
                                        ServerPlayer sp = context.getSource().getPlayer();

                                        VillageSavedData savedData = VillageSavedData.get((ServerLevel) sp.level());
                                        Village village = savedData.getVillageManager().findNearestVillage(sp.blockPosition(), 500.0);

                                        if (village == null) {
                                            context.getSource().sendFailure(Component.translatable("command.millenaire.error.no_village_radius", 500));
                                            return 0;
                                        }

                                        ReputationCapSaveData rcsd = ReputationCapSaveData.getInstance((ServerLevel) sp.level());
                                        boolean yes = rcsd.reset(sp.getUUID(), village.getId().uuid());
                                        if(yes){
                                            context.getSource().sendSuccess(() -> Component.literal("Reset reputation cap for " + village.getVillageName()), false);
                                            return 1;
                                        }
                                        else{
                                            context.getSource().sendFailure(Component.literal("Either you had no reputation cap with " + village.getVillageName() + ", or you had no reputation caps at all"));
                                            return 0;
                                        }


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
