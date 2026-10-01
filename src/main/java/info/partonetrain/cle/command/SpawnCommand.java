package info.partonetrain.cle.command;

import info.partonetrain.cle.CleUtils;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.millenaire.DisplayUtils;
import org.millenaire.culture.Gender;
import org.millenaire.culture.ModCultures;
import org.millenaire.culture.VillagerType;
import org.millenaire.entity.MillVillager;
import org.millenaire.entity.ModEntities;
import org.millenaire.village.VillageId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SpawnCommand {
    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("cle")
                        .then(Commands.literal("spawn")
                        .then(Commands.literal("all_villager_types")
                                .requires(source -> source.hasPermission(2))
                                .executes(context -> {
                                            if (context.getSource().isPlayer()) {
                                                ServerLevel level = context.getSource().getLevel();
                                                Map<ResourceLocation, VillagerType> types = ModCultures.getAllVillagerTypes();
                                                BlockPos spawnLocation = context.getSource().getEntity().blockPosition();

                                                int count = 0, additionalCount = 0;
                                                for(ResourceLocation rl : types.keySet()){
                                                    //System.out.println("---" + rl.toString() + "---");
                                                    List<MillVillager> additionalSkins = new ArrayList<>();
                                                    VillagerType thisType = types.get(rl);
                                                    MillVillager baseMillager = new MillVillager(ModEntities.MILL_VILLAGER.get(), level);

                                                    /*
                                                    if(thisType.gender().equals(Gender.MALE)){
                                                        continue;
                                                    }
                                                     */

                                                    baseMillager.setPos(spawnLocation.getCenter());
                                                    float scale = thisType.isChild() ? 0.5F : 1F;
                                                    String role = DisplayUtils.resolveRoleKey(thisType.id());
                                                    //System.out.println(thisType.textures().getFirst());
                                                    baseMillager.initAppearance(thisType.modelType(), thisType.textures().getFirst(), null, null, scale,"Testy", "Testerson", role);
                                                    baseMillager.setSpeechText(thisType.textures().getFirst().toString());
                                                    //baseMillager.setVillagerTypeId(rl);
                                                    baseMillager.setNoAi(true);

                                                    for(ResourceLocation textureRL : thisType.textures()){
                                                        if(!(textureRL == thisType.textures().get(0))){
                                                            MillVillager additonal = new MillVillager(ModEntities.MILL_VILLAGER.get(), level);
                                                            additonal.initAppearance(thisType.modelType(), textureRL, null, null, scale,"Testy", "Testerson", role);
                                                            additonal.setSpeechText(textureRL.toString());
                                                            //baseMillager.setVillagerTypeId(rl);
                                                            additonal.setNoAi(true);
                                                            additionalSkins.add(additonal);
                                                            //System.out.println(textureRL.toString());
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
                                                context.getSource().sendSuccess(() -> Component.literal("Spawned " + finalCount + " millager types with " + finalAdditionalCount + " total additional skins" ), false);
                                                context.getSource().sendSystemMessage(Component.literal("Final position: " + spawnLocation.getX() + "," + spawnLocation.getY() + "," + spawnLocation.getZ()));
                                                return 1;
                                            } else {
                                                context.getSource().sendFailure(Component.literal("Must be a player executed command"));
                                                return 0;
                                            }

                                        }
                                ))));

    }

}
