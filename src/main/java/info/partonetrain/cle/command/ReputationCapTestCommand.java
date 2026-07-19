package info.partonetrain.cle.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import info.partonetrain.cle.CleConfig;
import info.partonetrain.cle.ReputationCapSaveData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.millenaire.village.*;

import java.util.ArrayList;
import java.util.List;

//i got tired of creating new worlds and testing it manually so i went on a bit of a rabbit hole adventure to make this :)
public class ReputationCapTestCommand {

    private static final List<ScheduledRepTest> activeTests = new ArrayList<>();

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        if (CleConfig.REPUTATIION_CAP_PER_DAY.getAsInt() <= 0) {
            return;
        }

        event.getDispatcher().register(
                Commands.literal("cle")
                        .then(Commands.literal("reputation_cap_test")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("reputation", IntegerArgumentType.integer(-8192, 8192))
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1, 100))
                                        .executes(context -> {

                                            if (context.getSource().isPlayer()) {
                                                ServerPlayer sp = context.getSource().getPlayer();

                                                VillageSavedData savedData = VillageSavedData.get((ServerLevel) sp.level());
                                                Village village = savedData.getVillageManager().findNearestVillage(sp.blockPosition(), 500.0);

                                                if (village == null) {
                                                    context.getSource().sendFailure(Component.translatable("command.millenaire.error.no_village_radius", 500));
                                                    return 0;
                                                }

                                                int arg1 = IntegerArgumentType.getInteger(context, "reputation");
                                                int arg2 = IntegerArgumentType.getInteger(context, "amount");
                                                activeTests.add(new ScheduledRepTest(context.getSource(), sp, village, arg1, arg2));

                                                context.getSource().sendSystemMessage(Component.literal("Started reputation cap test: " + arg1 + " reputation, " + arg2 + " times, every 3 seconds"));
                                                return 1;
                                            } else {
                                                context.getSource().sendFailure(Component.literal("Must be a player executed command"));
                                                return 0;
                                            }
                                        })
                                ))));
    }

    @SubscribeEvent
    public void onServerTickEventPost(ServerTickEvent.Post event) {
        List<ScheduledRepTest> finished = new ArrayList<>();
        for (ScheduledRepTest t : activeTests) {
            t.tick();
            if (t.iterationsLeft == 0){
                finished.add(t);
            }
        }
        activeTests.removeAll(finished);
    }

    private static class ScheduledRepTest {
        private static final int INTERVAL_TICKS = 60;

        private final CommandSourceStack source;
        private final ServerPlayer player;
        private final Village village;
        private final int reputationArg;
        private final int runs;

        private int iterationsLeft;
        private int ticksUntilNext = INTERVAL_TICKS;

        ScheduledRepTest(CommandSourceStack source, ServerPlayer player, Village village, int reputationArg, int runs) {
            this.source = source;
            this.player = player;
            this.village = village;
            this.reputationArg = reputationArg;
            this.runs = runs;
            this.iterationsLeft = runs;
        }

        void tick() {
            if (--ticksUntilNext > 0) {
                return;
            }
            ticksUntilNext = INTERVAL_TICKS;

            ReputationCapSaveData rcsd = ReputationCapSaveData.getInstance(source.getLevel());
            int capBefore = rcsd.getAccumulatedRep(player.getUUID(), village.getId().uuid());
            int repBefore = village.getReputation().get(player.getUUID());

            int newRep = village.adjustReputation(source.getLevel(), player.getUUID(), reputationArg);

            int diff = newRep - repBefore;

            source.sendSystemMessage(Component.literal("Added " + reputationArg));
            source.sendSystemMessage(Component.literal("Village rep went from " + repBefore + " to " + newRep + " (" + village.getReputation().get(player.getUUID()) + ")"));
            source.sendSystemMessage(Component.literal("Difference: " + diff));
            source.sendSystemMessage(Component.literal("Reputation in save went from " + capBefore + " to " + rcsd.getAccumulatedRep(player.getUUID(), village.getId().uuid())));
            source.sendSystemMessage(Component.literal("-----"));

            iterationsLeft--;

            if (iterationsLeft <= 0) { //test is done!
                List<ReputationCapSaveData.ReputationCap> list = rcsd.players.get(player.getUUID());

                source.sendSystemMessage(Component.literal("Reputation Caps: "));
                MutableComponent caps = Component.empty();
                for (var l : list) {
                    caps.append(" -" + l.toString() + "\n");
                }
                source.sendSystemMessage(caps);

                source.sendSuccess(() -> Component.literal("Reputation cap tested for " + reputationArg + " reputation " + runs + " times"), false);
            }

        }
    }
}