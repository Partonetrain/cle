package info.partonetrain.cle.command;

import info.partonetrain.cle.AuditUtils;
import info.partonetrain.cle.mixin.*;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.millenaire.culture.ModCultures;
import org.millenaire.culture.VillagerType;
import org.millenaire.quest.Quest;
import org.millenaire.quest.QuestVillagerDef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuditCommand {
    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("cle")
                        .then(Commands.literal("audit")
                                .then(Commands.literal("quests")
                                        .requires(source -> source.hasPermission(2))
                                        .executes(context -> {
                                                    if (context.getSource().isPlayer()) {
                                                        context.getSource().sendSystemMessage(Component.literal("Auditing quests, please wait...").withStyle(ChatFormatting.ITALIC));

                                                        Map<String, String> questsAndErrors = new HashMap<>();
                                                        Map<String, Quest> quests = QuestRegistryAccessor.cle$getQuests();
                                                        Map<ResourceLocation, VillagerType> allVillagerTypes = ModCultures.getAllVillagerTypes();

                                                        for (String s : quests.keySet()) {
                                                            Quest q = quests.get(s);
                                                            StringBuilder errors = new StringBuilder();

                                                            List<QuestVillagerDef> defs = q.villagerDefs();
                                                            for(QuestVillagerDef qvd : defs){
                                                                List<String> villagerTypes = qvd.villagerTypes();
                                                                //check if the villager type actually exists
                                                                for(String vtString : villagerTypes){
                                                                    String namespace = "millenaire"; //TODO do submods use their own namespace for villager types?
                                                                    ResourceLocation villagerTypeRL = ResourceLocation.fromNamespaceAndPath(namespace, vtString);
                                                                    if(!allVillagerTypes.containsKey(villagerTypeRL)){
                                                                        errors.append("villager type ").append(vtString).append(" for key ").append(qvd.key()).append(" does not exist").append("\n");
                                                                    }
                                                                }
                                                            }

                                                            if(!errors.isEmpty()){
                                                                questsAndErrors.put(s, errors.toString());
                                                            }

                                                        }

                                                        if(!questsAndErrors.isEmpty()){
                                                            List<String> logLines = new ArrayList<>();
                                                            logLines.add("Found " + quests.size() + " quest errors");
                                                            for(String q : questsAndErrors.keySet()){
                                                                logLines.add("--- errors for quest: " + q + " ---");
                                                                String errors = questsAndErrors.get(q);
                                                                String[] split = errors.split("\n");
                                                                for(String s : split){
                                                                    logLines.add("\t-> " + s);
                                                                }

                                                            }

                                                            String result = AuditUtils.writeLog("cle_audit_quests", logLines);
                                                            context.getSource().sendSystemMessage(Component.literal(result).withStyle(ChatFormatting.ITALIC));
                                                        }
                                                        else{
                                                            context.getSource().sendSystemMessage(Component.literal("No errors found, no log was written.").withStyle(ChatFormatting.ITALIC));
                                                        }

                                                        return 1;
                                                    } else {
                                                        context.getSource().sendFailure(Component.literal("Must be a player executed command"));
                                                        return 0;
                                                    }

                                                }
                                        ))));
    }

}
