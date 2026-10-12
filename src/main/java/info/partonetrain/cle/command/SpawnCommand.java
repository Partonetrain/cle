package info.partonetrain.cle.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.jetbrains.annotations.NotNull;
import org.millenaire.DisplayUtils;
import org.millenaire.culture.Gender;
import org.millenaire.culture.ModCultures;
import org.millenaire.culture.VillagerType;
import org.millenaire.entity.MillVillager;
import org.millenaire.entity.ModEntities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpawnCommand {
    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("cle")
                        .then(Commands.literal("spawn")
                        .then(Commands.literal("villager_types")
                                .then(Commands.argument("filter", StringArgumentType.word())
                                        .suggests((context, builder) -> {
                                            return SharedSuggestionProvider.suggest(List.of("all", "male_only", "female_only"), builder);
                                        })
                                .requires(source -> source.hasPermission(2))
                                .executes(context -> {
                                            if (context.getSource().isPlayer()) {
                                                List<ResourceLocation> allDiscoveredSkins = new ArrayList<>();

                                                ServerLevel level = context.getSource().getLevel();
                                                Map<ResourceLocation, VillagerType> types = ModCultures.getAllVillagerTypes();
                                                BlockPos spawnLocation = context.getSource().getEntity().blockPosition();

                                                int count = 0, additionalCount = 0;
                                                for(ResourceLocation rl : types.keySet()){
                                                    //System.out.println("---" + rl.toString() + "---");
                                                    List<MillVillager> additionalSkins = new ArrayList<>();
                                                    VillagerType thisType = types.get(rl);
                                                    MillVillager baseMillager = new MillVillager(ModEntities.MILL_VILLAGER.get(), level);

                                                    String arg = StringArgumentType.getString(context, "filter");
                                                    if(arg.equals("male_only") && thisType.gender().equals(Gender.FEMALE)){
                                                        continue;
                                                    }
                                                    if(arg.equals("female_only") && thisType.gender().equals(Gender.MALE)){
                                                        continue;
                                                    }

                                                    baseMillager.setPos(spawnLocation.getCenter());
                                                    float scale = thisType.isChild() ? 0.5F : 1F;
                                                    String role = DisplayUtils.resolveRoleKey(thisType.id());
                                                    //System.out.println(thisType.textures().getFirst());
                                                    baseMillager.initAppearance(thisType.modelType(), thisType.textures().getFirst(), null, null, scale,"Testy", "Testerson", role);
                                                    baseMillager.setSpeechText(thisType.textures().getFirst().toString());
                                                    //baseMillager.setVillagerTypeId(rl);
                                                    baseMillager.setNoAi(true);
                                                    allDiscoveredSkins.add(thisType.textures().getFirst());

                                                    for(ResourceLocation textureRL : thisType.textures()){
                                                        if(!(textureRL == thisType.textures().get(0))){
                                                            MillVillager additonal = new MillVillager(ModEntities.MILL_VILLAGER.get(), level);
                                                            additonal.initAppearance(thisType.modelType(), textureRL, null, null, scale,"Testy", "Testerson", role);
                                                            additonal.setSpeechText(textureRL.toString());
                                                            //baseMillager.setVillagerTypeId(rl);
                                                            additonal.setNoAi(true);
                                                            additionalSkins.add(additonal);
                                                            //System.out.println(textureRL.toString());
                                                            allDiscoveredSkins.add(textureRL);
                                                        }
                                                    }

                                                    Display.TextDisplay textDisplay = new Display.TextDisplay(EntityType.TEXT_DISPLAY, level);
                                                    textDisplay.setText(Component.literal(rl.toString() + "\n (" + thisType.textures().size() + " skins)"));
                                                    textDisplay.setPos(spawnLocation.getCenter().add(0, 4, 0));

                                                    level.addFreshEntity(baseMillager);
                                                    level.addFreshEntity(textDisplay);

                                                    count++;

                                                    if(!additionalSkins.isEmpty()){
                                                        int offset = -2;
                                                        for(MillVillager additional : additionalSkins){
                                                            additional.setPos(spawnLocation.getCenter().add(0, 0, offset));
                                                            offset = offset - 2;
                                                            additionalCount++;
                                                            level.addFreshEntity(additional);
                                                        }
                                                    }

                                                    spawnLocation = new BlockPos(spawnLocation.getX() + 5, spawnLocation.getY(), spawnLocation.getZ());
                                                }

                                                int finalCount = count;
                                                int finalAdditionalCount = additionalCount;
                                                long unique = allDiscoveredSkins.stream().distinct().count();
                                                context.getSource().sendSuccess(() -> Component.literal("Spawned " + finalCount + " millager types with " + finalAdditionalCount + " total additional skins" ), false);
                                                context.getSource().sendSuccess(() -> Component.literal( unique + " skins were unique" ), false);
                                                context.getSource().sendSystemMessage(Component.literal("Final position: " + spawnLocation.getX() + "," + spawnLocation.getY() + "," + spawnLocation.getZ()));
                                                return 1;
                                            } else {
                                                context.getSource().sendFailure(Component.literal("Must be a player executed command"));
                                                return 0;
                                            }

                                        }
                                )))));


    event.getDispatcher().register(
                Commands.literal("cle")
                        .then(Commands.literal("spawn")
                        .then(Commands.literal("villager_clothes")
                                .then(Commands.argument("filter", StringArgumentType.word())
                                        .suggests((context, builder) -> {
                                            return SharedSuggestionProvider.suggest(List.of("all", "male_only", "female_only"), builder);
                                        })
                                .requires(source -> source.hasPermission(2))
                                .executes(context -> {
                                            if (context.getSource().isPlayer()) {
                                                List<ClothesCombination> allDiscoveredClothesCombinations = new ArrayList<>();
                                                int skipped = 0;

                                                ServerLevel level = context.getSource().getLevel();
                                                Map<ResourceLocation, VillagerType> types = ModCultures.getAllVillagerTypes();
                                                BlockPos spawnLocation = context.getSource().getEntity().blockPosition();

                                                int count = 0, additionalCount = 0;
                                                for(ResourceLocation rl : types.keySet()){
                                                    //System.out.println("---" + rl.toString() + "---");
                                                    List<MillVillager> additionalClothes = new ArrayList<>();
                                                    VillagerType thisType = types.get(rl);
                                                    MillVillager baseMillager = new MillVillager(ModEntities.MILL_VILLAGER.get(), level);

                                                    String arg = StringArgumentType.getString(context, "filter");
                                                    if(arg.equals("male_only") && thisType.gender().equals(Gender.FEMALE)){
                                                        continue;
                                                    }
                                                    if(arg.equals("female_only") && thisType.gender().equals(Gender.MALE)){
                                                        continue;
                                                    }

                                                    if(thisType.clothes().isEmpty()){
                                                        skipped++;
                                                        continue; //do not spawn if villager does not have dynamic clothes
                                                    }

                                                    baseMillager.setPos(spawnLocation.getCenter());
                                                    float scale = thisType.isChild() ? 0.5F : 1F;
                                                    String role = DisplayUtils.resolveRoleKey(thisType.id());
                                                    //System.out.println(thisType.textures().getFirst());
                                                    String setName = thisType.clothes().entrySet().stream().findFirst().get().getKey(); //wtf is this line lol
                                                    ResourceLocation firstCloth0 = thisType.clothes().get(setName).layer0().getFirst();
                                                    //find first set with layer 1. not all villager types even use layer 1, it is mostly used for women's hair.
                                                    String firstSetWithAnyLayer1 = "";
                                                    for(Map.Entry<String, VillagerType.ClothSet> x : thisType.clothes().entrySet()){
                                                        if(!x.getValue().layer1().isEmpty()){
                                                            firstSetWithAnyLayer1 = x.getKey();
                                                        }
                                                    }

                                                    ResourceLocation firstCloth1 = firstSetWithAnyLayer1.isEmpty() ? null : thisType.clothes().get(firstSetWithAnyLayer1).layer1().getFirst();
                                                    List<ClothesCombination> clothesCombinations = new ArrayList<>();
                                                    final ClothesCombination DEFAULT = new ClothesCombination(new ClothFromSet(setName, firstCloth0), firstSetWithAnyLayer1.isEmpty() ? null : new ClothFromSet(firstSetWithAnyLayer1, firstCloth1));

                                                    baseMillager.initAppearance(thisType.modelType(), thisType.textures().getFirst(), DEFAULT.layer0.setItem(), DEFAULT.layer1 == null ? null : DEFAULT.layer1.setItem(), scale,"Testy", "Testerson", role);
                                                    baseMillager.setSpeechText(DEFAULT.toString());
                                                    //baseMillager.setVillagerTypeId(rl);
                                                    baseMillager.setNoAi(true);

                                                    //now we set up clothes combos
                                                    List<ClothFromSet> cloths0 = new ArrayList<>();
                                                    List<ClothFromSet> cloths1 = new ArrayList<>();
                                                    for(Map.Entry<String, VillagerType.ClothSet> entry : thisType.clothes().entrySet()){
                                                        for(var x : entry.getValue().layer0()){
                                                            ClothFromSet clothX = new ClothFromSet(entry.getKey() ,x);
                                                            cloths0.add(clothX);
                                                        }
                                                        for(var x : entry.getValue().layer1()){
                                                            ClothFromSet clothY = new ClothFromSet(entry.getKey() ,x);
                                                            cloths1.add(clothY);
                                                        }

                                                    }
                                                    for(var x : cloths0){
                                                        if(cloths1.isEmpty()){
                                                            ClothesCombination cc = new ClothesCombination(x, null);
                                                            clothesCombinations.add(cc);
                                                            allDiscoveredClothesCombinations.add(cc);
                                                        }
                                                        else{
                                                            for(var y : cloths1){
                                                                ClothesCombination cc = new ClothesCombination(x, y);
                                                                clothesCombinations.add(cc);
                                                                allDiscoveredClothesCombinations.add(cc);
                                                            }
                                                        }

                                                    }

                                                    for(ClothesCombination combo : clothesCombinations){
                                                            if (!(DEFAULT.equals(combo))) { //we already spawned default set of clothes
                                                                MillVillager additonal = new MillVillager(ModEntities.MILL_VILLAGER.get(), level);
                                                                additonal.initAppearance(thisType.modelType(), thisType.textures().getFirst(), combo.layer0.setItem(), combo.layer1 == null ? null : combo.layer1.setItem(), scale, "Testy", "Testerson", role);
                                                                additonal.setSpeechText(combo.toString());
                                                                //baseMillager.setVillagerTypeId(rl);
                                                                additonal.setNoAi(true);
                                                                additionalClothes.add(additonal);
                                                            }
                                                        }

                                                    Display.TextDisplay textDisplay = new Display.TextDisplay(EntityType.TEXT_DISPLAY, level);
                                                    textDisplay.setText(Component.literal(rl.toString() + "\n(base texture: " + thisType.textures().getFirst().toString() + ")" + "\n (" + clothesCombinations.stream().count() + " clothes combinations)"));
                                                    textDisplay.setPos(spawnLocation.getCenter().add(0, 4, 0));

                                                    level.addFreshEntity(baseMillager);
                                                    level.addFreshEntity(textDisplay);

                                                    count++;

                                                    if(!additionalClothes.isEmpty()){
                                                        int offset = -4;
                                                        for(MillVillager additional : additionalClothes){
                                                            additional.setPos(spawnLocation.getCenter().add(0, 0, offset));
                                                            offset = offset - 4;
                                                            additionalCount++;
                                                            level.addFreshEntity(additional);
                                                        }
                                                    }

                                                    spawnLocation = new BlockPos(spawnLocation.getX() + 8, spawnLocation.getY(), spawnLocation.getZ());
                                                }

                                                int finalCount = count;
                                                int finalAdditionalCount = additionalCount;
                                                long unique = allDiscoveredClothesCombinations.stream().distinct().count();
                                                int finalSkipped = skipped;
                                                context.getSource().sendSuccess(() -> Component.literal("Spawned " + finalCount + " clothed millager types with " + finalAdditionalCount + " total clothes combinations" ), false);
                                                context.getSource().sendSuccess(() -> Component.literal( unique + " clothes combinations were unique" ), false);
                                                context.getSource().sendSuccess(() -> Component.literal( finalSkipped + " millager types were skipped as they did not use the clothes system" ), false);
                                                context.getSource().sendSystemMessage(Component.literal("Final position: " + spawnLocation.getX() + "," + spawnLocation.getY() + "," + spawnLocation.getZ()));
                                                return 1;
                                            } else {
                                                context.getSource().sendFailure(Component.literal("Must be a player executed command"));
                                                return 0;
                                            }

                                        }
                                )))));
    }


    public record ClothFromSet(String setName, ResourceLocation setItem){
        @Override
        public @NotNull String toString(){
            return setName + ": " + setItem.toString();
        }
    }

    public record ClothesCombination (ClothFromSet layer0, ClothFromSet layer1) {
        @Override
        public @NotNull String toString(){
            return layer0 + " ... " + layer1;
        }
    }

}
