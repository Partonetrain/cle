package info.partonetrain.cle.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import info.partonetrain.cle.mixin.ToolCategoryRegistryAccessor;
import info.partonetrain.cle.mixin.VillagerConfigAccessor;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.millenaire.Millenaire;
import org.millenaire.config.VillagerConfig;
import org.millenaire.tool.ToolCategory;
import org.millenaire.tool.ToolCategoryRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShowCommand {
    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("cle")
                        .then(Commands.literal("show")
                        .then(Commands.literal("goals")
                                .requires(source -> source.hasPermission(2))
                                .executes(context -> {
                                            if (context.getSource().isPlayer()) {

                                                List<ResourceLocation> getAllIds = Millenaire.getGoalRegistry().getAllIds();
                                                for (ResourceLocation rl : getAllIds) {
                                                    context.getSource().sendSystemMessage(Component.literal(rl.toString()));
                                                }

                                                return 1;
                                            } else {
                                                context.getSource().sendFailure(Component.literal("Must be a player executed command"));
                                                return 0;
                                            }

                                        }
                                ))));

        event.getDispatcher().register(
                Commands.literal("cle")
                        .then(Commands.literal("show")
                                .then(Commands.literal("villager_config")
                                        .requires(source -> source.hasPermission(2))
                                        .executes(context -> {
                                            //if no arg is provided, show all configs
                                            if (context.getSource().isPlayer()) {
                                                context.getSource().sendSystemMessage(Component.literal("--- Villager configs: ---"));

                                                Map<String, VillagerConfig> configs = VillagerConfigAccessor.cle$getNamedConfigs();

                                                context.getSource().sendSystemMessage(Component.literal("default"));
                                                for (String s : configs.keySet()) {
                                                    context.getSource().sendSystemMessage(Component.literal(s));
                                                }

                                                context.getSource().sendSystemMessage(Component.literal("---"));
                                                context.getSource().sendSystemMessage(Component.literal("Use the command again with one of these names as an arg to show the specific values"));
                                                return 1;
                                            } else {
                                                context.getSource().sendFailure(Component.literal("Must be a player executed command"));
                                                return 0;
                                            }
                                        })
                                        .then(Commands.argument("name", StringArgumentType.word())
                                                .suggests((context, builder) -> {
                                                    Map<String, VillagerConfig> configs = VillagerConfigAccessor.cle$getNamedConfigs();

                                                    List<String> list = new ArrayList<>(configs.keySet());
                                                    list.add("default");
                                                    return SharedSuggestionProvider.suggest(list, builder);
                                                })
                                                .executes(context -> {
                                                    //if an arg is provided, show the details of that config
                                                    if (context.getSource().isPlayer()) {
                                                        String arg = StringArgumentType.getString(context, "name");

                                                        //returns default if arg is invalid
                                                        VillagerConfig specific = VillagerConfig.get(arg);

                                                        context.getSource().sendSystemMessage(Component.literal("--- Villager Config for " + arg + " ---"));
                                                        context.getSource().sendSystemMessage(Component.literal("--- Food Conception ---"));
                                                        for (var entry : specific.getFoodConception().entrySet()) {
                                                            context.getSource().sendSystemMessage(Component.literal(entry.getKey() + " : " + entry.getValue()));
                                                        }
                                                        context.getSource().sendSystemMessage(Component.literal("--- Food Growth ---"));
                                                        for (var entry : specific.getFoodGrowth().entrySet()) {
                                                            context.getSource().sendSystemMessage(Component.literal(entry.getKey() + " : " + entry.getValue()));
                                                        }

                                                        return 1;
                                                    } else {
                                                        context.getSource().sendFailure(Component.literal("Must be a player executed command"));
                                                        return 0;
                                                    }
                                                })))));

        event.getDispatcher().register(
                Commands.literal("cle")
                        .then(Commands.literal("show")
                                .then(Commands.literal("tool_categories")
                                        .requires(source -> source.hasPermission(2))
                                        .executes(context -> {
                                            if (context.getSource().isPlayer()) {
                                                context.getSource().sendSystemMessage(Component.literal("--- Tool categories: ---"));

                                                Map<String, ToolCategory> categories = ToolCategoryRegistryAccessor.cle$getCategories();

                                                for (String s : categories.keySet()) {
                                                    context.getSource().sendSystemMessage(Component.literal(s));
                                                }

                                                context.getSource().sendSystemMessage(Component.literal("---"));
                                                context.getSource().sendSystemMessage(Component.literal("Use the command again with one of these names as an arg to show the specific values"));
                                                return 1;
                                            } else {
                                                context.getSource().sendFailure(Component.literal("Must be a player executed command"));
                                                return 0;
                                            }
                                        })
                                        .then(Commands.argument("name", StringArgumentType.word())
                                                .suggests((context, builder) -> {
                                                    Map<String, ToolCategory> categories = ToolCategoryRegistryAccessor.cle$getCategories();

                                                    List<String> list = new ArrayList<>(categories.keySet());
                                                    return SharedSuggestionProvider.suggest(list, builder);
                                                })
                                                .executes(context -> {
                                                    //if an arg is provided, show the details of that category
                                                    if (context.getSource().isPlayer()) {
                                                        String arg = StringArgumentType.getString(context, "name");

                                                        ToolCategory specific = ToolCategoryRegistry.get(arg);
                                                        if(specific == null){
                                                            context.getSource().sendFailure(Component.literal("No such tool category " + arg));
                                                            return 0;
                                                        }

                                                        context.getSource().sendSystemMessage(Component.literal("--- Tool entries for " + arg + " and their priorities ---"));
                                                        for(ToolCategory.ToolEntry te : specific.items()){
                                                            context.getSource().sendSystemMessage(Component.literal("> " + te.itemId()  + ": " + te.priority()));
                                                        }


                                                        return 1;
                                                    } else {
                                                        context.getSource().sendFailure(Component.literal("Must be a player executed command"));
                                                        return 0;
                                                    }
                                                })))));
    }

}
