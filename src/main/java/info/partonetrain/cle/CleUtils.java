package info.partonetrain.cle;

import info.partonetrain.cle.mixin.PlayerCultureReputationAccessor;
import info.partonetrain.cle.mixin.VillageReputationAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.loading.FMLEnvironment;
import org.millenaire.entity.MillVillager;
import org.millenaire.village.PlayerCultureReputation;
import org.millenaire.village.Village;

import java.util.*;

public class CleUtils {

    public static Map<UUID, Integer> playerRepNotifCooldowns = new HashMap<>();
    public static final int REP_COOLDOWN = 500;

    public static void tickRepNotifCooldowns(){
        if(playerRepNotifCooldowns.isEmpty()){
            return;
        }
        List<UUID> removes = new ArrayList<>();
        for(UUID uuid : playerRepNotifCooldowns.keySet()){
            int left = playerRepNotifCooldowns.get(uuid) - 1;
            if(left < 0){
                removes.add(uuid);
            } else {
                playerRepNotifCooldowns.put(uuid, left);
            }
        }
        removes.forEach((u) -> playerRepNotifCooldowns.remove(u));
    }

    public static boolean changeNearbyPlayerReputationFromPlayer(ServerLevel serverLevel, ServerPlayer player, int amount, LinkedHashMap<String, String> langKeysWithPlaceholders) {
        List<MillVillager> nearbyMillagers = serverLevel.getEntitiesOfClass(MillVillager.class, player.getHitbox().inflate(7));
        if(nearbyMillagers.isEmpty()){
            return false;
        }

        for(MillVillager millager : nearbyMillagers){
            Village village = Village.resolve(serverLevel, millager.getVillageId());
            if(village != null){
                int oldRep = village.getReputation().get(player.getUUID());
                int newRep = village.adjustReputation(serverLevel, player.getUUID(), amount);
                printAdjustedReputation(oldRep, amount, newRep, "changeNearbyPlayerReputationFromPlayer");

                if(langKeysWithPlaceholders.get("cle.malum.millager_afraid_spirit_harvest.2") != null || langKeysWithPlaceholders.get("cle.malum.millager_afraid_spirit_harvest.2.proper") != null){
                    langKeysWithPlaceholders.put("cle.malum.millager_afraid_spirit_harvest.3", millager.getFirstName() + " " + millager.getFamilyName());
                }

                MutableComponent actionBarMessage = Component.empty();
                for(String key : langKeysWithPlaceholders.keySet()){
                    String placeholder = langKeysWithPlaceholders.get(key);
                    if(placeholder != null){
                        actionBarMessage.append(Component.translatable(key, placeholder));
                    }
                    else{
                        actionBarMessage.append(Component.translatable(key));
                    }
                }

                player.displayClientMessage(actionBarMessage, true);
                addParticlesAroundEntity(ParticleTypes.ANGRY_VILLAGER, millager, (ServerLevel) millager.level());
                return true;
            }
        }

        return false;
    }


    //returns false if no player was found or villager did not belong to a village.
    public static boolean changeNearbyPlayerReputationFromMillager(MillVillager villager, ServerLevel serverLevel, int amount, LinkedHashMap<String, String> langKeysWithPlaceholders){
        List<ServerPlayer> nearbyPlayers = serverLevel.getEntitiesOfClass(ServerPlayer.class, villager.getHitbox().inflate(5));

        if(nearbyPlayers.isEmpty()){
            return false;
        }

        for(ServerPlayer player : nearbyPlayers){
            Village village = Village.resolve(serverLevel, villager.getVillageId());
            if(village != null){
                int oldRep = village.getReputation().get(player.getUUID());
                int newRep = village.adjustReputation(serverLevel, player.getUUID(), amount);
                printAdjustedReputation(oldRep, amount, newRep, "changeNearbyPlayerReputationFromMillager");

                MutableComponent actionBarMessage = constructActionBarMessage(langKeysWithPlaceholders);
                player.displayClientMessage(actionBarMessage, true);
                return true;
                //for FleeBlockGoal the particles come from there
            }
        }
        return false;
    }

    public static MutableComponent constructActionBarMessage(LinkedHashMap<String, String> langKeysWithPlaceholders){
        MutableComponent actionBarMessage = Component.empty();
        for(String key : langKeysWithPlaceholders.keySet()){
            String placeholder = langKeysWithPlaceholders.get(key);
            if(placeholder != null){
                actionBarMessage.append(Component.translatable(key, placeholder));
            }
            else{
                actionBarMessage.append(Component.translatable(key));
            }
        }
        return actionBarMessage;
    }

    public static void addParticlesAroundEntity(ParticleOptions particleOption, LivingEntity livingEntity, ServerLevel serverLevel) {
        for(int i = 0; i < 3; ++i) {
            serverLevel.sendParticles(particleOption,
                    livingEntity.getRandomX(1.0), livingEntity.getRandomY() + 1.0, livingEntity.getRandomZ(1.0),
                    1,
                    livingEntity.getRandom().nextGaussian() * 0.02, livingEntity.getRandom().nextGaussian() * 0.02, livingEntity.getRandom().nextGaussian() * 0.02,
                    0);
        }
    }

    public static void resetReputation(Village village, ServerPlayer player){
        VillageReputationAccessor vra = (VillageReputationAccessor)village.getReputation();
        vra.cle$getReputations().put(player.getUUID(), 0);
        player.sendSystemMessage(Component.literal("Reputation with " + village.getVillageName() + " reset"));
        PlayerCultureReputationAccessor pcra = (PlayerCultureReputationAccessor) PlayerCultureReputation.get((ServerLevel) player.level());
        Map<UUID, Map<ResourceLocation, Integer>> data = pcra.cle$getData();
        ResourceLocation cultureID = village.getCultureId();
        Map<ResourceLocation, Integer> culturesToRep = data.get(player.getUUID());
        culturesToRep.put(cultureID, 0);
        data.put(player.getUUID(), culturesToRep);
        player.sendSystemMessage(Component.literal("Reputation with " + cultureID.toString() + " reset"));
    }

    public static void informPlayerRepCapReached(ServerLevel serverLevel, UUID playerUUID, String villageName){
        if(playerRepNotifCooldowns.containsKey(playerUUID)){
            return;
        }
        else{
            ServerPlayer player = (ServerPlayer) serverLevel.getPlayerByUUID(playerUUID);
            MutableComponent actionBarMessage = Component.translatable("cle.reputation_cap_reached", villageName);
            player.displayClientMessage(actionBarMessage, true);
            player.playNotifySound(SoundEvents.VILLAGER_NO, player.getSoundSource(), 0.25F, 1.0F);
            playerRepNotifCooldowns.put(playerUUID, REP_COOLDOWN);
        }
    }

    public static void printAdjustedReputation(int before, int amount, int after, String cause){
        String print = "Reputation adjusted by " + cause + " : " + before + "->" + amount + "=" + after;
        if(!FMLEnvironment.production){
            Minecraft.getInstance().player.displayClientMessage(Component.literal(print), false);
        }
        Cle.LOGGER.info(print);
    }

}
