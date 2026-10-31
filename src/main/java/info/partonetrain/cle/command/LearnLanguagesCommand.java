package info.partonetrain.cle.command;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.millenaire.culture.Culture;
import org.millenaire.culture.ModCultures;
import org.millenaire.village.PlayerCultureReputation;

import java.util.Map;

public class LearnLanguagesCommand {

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("cle")
                        .then(Commands.literal("learn_languages")
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> {
                                    if (context.getSource().isPlayer()) {
                                        ServerPlayer sp = context.getSource().getPlayer();

                                        PlayerCultureReputation cultureRep = PlayerCultureReputation.get((ServerLevel) sp.level());

                                        Map<ResourceLocation, Culture> cultures = ModCultures.getAllCultures();
                                        int count = 0;
                                        for(ResourceLocation rl : cultures.keySet()){
                                            cultureRep.addLanguageKnowledge(sp.getUUID(), rl, 500);
                                            context.getSource().sendSystemMessage(Component.literal("Maxed-out language knowledge for " + rl.toString()));
                                            count++;
                                        }

                                        context.getSource().sendSystemMessage(Component.literal("Gained controlled villages for " + count + " cultures").withStyle(ChatFormatting.ITALIC));

                                        return 1;
                                    }
                                    else {
                                        context.getSource().sendFailure(Component.literal("Must be a player executed command"));
                                        return 0;
                                    }

                                }
                        )));
    }
}
