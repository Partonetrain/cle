package info.partonetrain.cle.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import info.partonetrain.cle.mixin.*;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.millenaire.Millenaire;
import org.millenaire.building.BlockCostRegistry;
import org.millenaire.config.VillagerConfig;
import org.millenaire.quest.Quest;
import org.millenaire.tool.ToolCategory;
import org.millenaire.tool.ToolCategoryRegistry;

import java.util.*;

public class ShowCommand {
    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        //SIMPLE SHOWS
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
                        .then(Commands.literal("block_costs")
                                .requires(source -> source.hasPermission(2))
                                .executes(context -> {
                                            if (context.getSource().isPlayer()) {

                                                Map<String, BlockCostRegistry.BlockCost> costs = BlockCostRegistryAccessor.cle$getCosts();
                                                context.getSource().sendSystemMessage(Component.literal("Showing custom block costs:").withStyle(ChatFormatting.ITALIC)); //print this since by default there will not be any output
                                                for (String s : costs.keySet()) {
                                                    BlockCostRegistry.BlockCost cost = costs.get(s);
                                                    context.getSource().sendSystemMessage(Component.literal(s + " | " + cost.costItem() + " | qty:" + cost.quantity()));
                                                }

                                                context.getSource().sendSystemMessage(Component.literal("Finished").withStyle(ChatFormatting.ITALIC)); //same as above
                                                context.getSource().sendSystemMessage(Component.literal("Note that the majority of block costs are hardcoded (BuildingCostCalculator), this command currently can only show custom ones").withStyle(ChatFormatting.ITALIC));

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
                                .then(Commands.literal("quests")
                                        .requires(source -> source.hasPermission(2))
                                        .executes(context -> {
                                                    if (context.getSource().isPlayer()) {

                                                        Map<String, Quest> quests = QuestRegistryAccessor.cle$getQuests();
                                                        for (String s : quests.keySet()) {
                                                            Quest q = quests.get(s);
                                                            context.getSource().sendSystemMessage(Component.literal(s + ": " + q.key()));
                                                            context.getSource().sendSystemMessage(Component.literal(" * chance per hour: " + q.chancePerHour() ));
                                                            context.getSource().sendSystemMessage(Component.literal(" * max simultaneous: " + q.maxSimultaneous() ));
                                                            context.getSource().sendSystemMessage(Component.literal(" * min reputation: " + q.minReputation() ));
                                                            context.getSource().sendSystemMessage(Component.literal(" * # of steps: " + q.steps().size()) );
                                                            context.getSource().sendSystemMessage(Component.literal(" * # of villagers: " + q.villagerDefs().size()) );
                                                        }

                                                        context.getSource().sendSystemMessage(Component.literal("Note: there are more data points in quests than what is shown here").withStyle(ChatFormatting.ITALIC));

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
                                .then(Commands.literal("avoided_structures")
                                        .requires(source -> source.hasPermission(2))
                                        .executes(context -> {
                                                    if (context.getSource().isPlayer()) {

                                                        List<ResourceKey<Structure>> avoided = StructureAvoidanceAccessor.cle$getAvoidedStructureKeys();
                                                        context.getSource().sendSystemMessage(Component.literal("--- Avoided structures ---").withStyle(ChatFormatting.ITALIC));
                                                        for (ResourceKey<?> s : avoided) {
                                                            context.getSource().sendSystemMessage(Component.literal(s.location().toString()));
                                                        }
                                                        context.getSource().sendSystemMessage(Component.literal("Vanilla villages are hardcoded to be avoided too, but not in the same method as these").withStyle(ChatFormatting.ITALIC));

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
                                .then(Commands.literal("contents_of_terrain_qualifier_biome_tags")
                                        .requires(source -> source.hasPermission(2))
                                        .executes(context -> {
                                                    if (context.getSource().isPlayer()) {
                                                        RegistryAccess registryAccess = context.getSource().getLevel().registryAccess();
                                                        Registry<Biome> biomeRegistry = registryAccess.registryOrThrow(Registries.BIOME);
                                                        List<TagKey<Biome>> tags = List.of(Tags.Biomes.IS_FOREST, Tags.Biomes.IS_HILL, Tags.Biomes.IS_MOUNTAIN, Tags.Biomes.IS_DESERT, Tags.Biomes.IS_OCEAN);

                                                        context.getSource().sendSystemMessage(Component.literal("Villages that spawn in biomes in each of these tags can spawn with qualifying names, e.g., la-fôret").withStyle(ChatFormatting.ITALIC));
                                                        for (TagKey<Biome> tag : tags) {
                                                            context.getSource().sendSystemMessage(Component.literal("- " + tag.location().toString() + " -"));
                                                            Optional<HolderSet.Named<Biome>> tagHolderSet = biomeRegistry.getTag(tag);
                                                            if(tagHolderSet.isPresent()){
                                                                for(Holder<Biome> biome : tagHolderSet.get()){
                                                                    context.getSource().sendSystemMessage(Component.literal( "> " + biome.getKey().location().toString()));
                                                                }
                                                            }
                                                        }

                                                        context.getSource().sendSystemMessage(Component.literal("Lava and water lake qualifiers also exist (e.g. le-volcan, le-lac) but have different logic").withStyle(ChatFormatting.ITALIC));

                                                        return 1;
                                                    } else {
                                                        context.getSource().sendFailure(Component.literal("Must be a player executed command"));
                                                        return 0;
                                                    }
                                                }
                                        ))));


        //COMPLEX SHOWS
        event.getDispatcher().register(
                Commands.literal("cle")
                        .then(Commands.literal("show")
                                .then(Commands.literal("villager_config")
                                        .requires(source -> source.hasPermission(2))
                                        .executes(context -> {
                                            //if no arg is provided, show all configs
                                            if (context.getSource().isPlayer()) {
                                                context.getSource().sendSystemMessage(Component.literal("--- Villager configs: ---").withStyle(ChatFormatting.ITALIC));

                                                Map<String, VillagerConfig> configs = VillagerConfigAccessor.cle$getNamedConfigs();

                                                context.getSource().sendSystemMessage(Component.literal("default"));
                                                for (String s : configs.keySet()) {
                                                    context.getSource().sendSystemMessage(Component.literal(s));
                                                }

                                                context.getSource().sendSystemMessage(Component.literal("---").withStyle(ChatFormatting.ITALIC));
                                                context.getSource().sendSystemMessage(Component.literal("Use the command again with one of these names as an arg to show the specific values").withStyle(ChatFormatting.ITALIC));
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

                                                        context.getSource().sendSystemMessage(Component.literal("--- Villager Config for " + arg + " ---").withStyle(ChatFormatting.ITALIC));
                                                        context.getSource().sendSystemMessage(Component.literal("--- Food Conception ---").withStyle(ChatFormatting.ITALIC));
                                                        for (var entry : specific.getFoodConception().entrySet()) {
                                                            context.getSource().sendSystemMessage(Component.literal(entry.getKey() + " : " + entry.getValue()));
                                                        }
                                                        context.getSource().sendSystemMessage(Component.literal("--- Food Growth ---").withStyle(ChatFormatting.ITALIC));
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
                                                context.getSource().sendSystemMessage(Component.literal("--- Tool categories: ---").withStyle(ChatFormatting.ITALIC));

                                                Map<String, ToolCategory> categories = ToolCategoryRegistryAccessor.cle$getCategories();

                                                for (String s : categories.keySet()) {
                                                    context.getSource().sendSystemMessage(Component.literal(s));
                                                }

                                                context.getSource().sendSystemMessage(Component.literal("---").withStyle(ChatFormatting.ITALIC));
                                                context.getSource().sendSystemMessage(Component.literal("Use the command again with one of these names as an arg to show the specific values").withStyle(ChatFormatting.ITALIC));
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

                                                        context.getSource().sendSystemMessage(Component.literal("--- Tool entries for " + arg + " and their priorities ---").withStyle(ChatFormatting.ITALIC));
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
