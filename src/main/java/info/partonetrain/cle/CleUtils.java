package info.partonetrain.cle;

import info.partonetrain.cle.mixin.PlayerCultureReputationAccessor;
import info.partonetrain.cle.mixin.VillageReputationAccessor;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import org.millenaire.entity.MillVillager;
import org.millenaire.village.PlayerCultureReputation;
import org.millenaire.village.Village;

import java.util.*;

public class CleUtils {

    public static Map<UUID, Integer> playerRepNotifCooldowns = new HashMap<>(); //todo this

    //returns false if no player was found or villager did not belong to a village.
    public static boolean changeNearbyPlayerReputation(ServerLevel serverLevel, MillVillager villager, int amount, LinkedHashMap<String, String> langKeysWithPlaceholders){
        List<ServerPlayer> nearbyPlayers = serverLevel.getEntitiesOfClass(ServerPlayer.class, villager.getHitbox().inflate(5));

        for(ServerPlayer player : nearbyPlayers){
            Village village = Village.resolve(serverLevel, villager.getVillageId());
            if(village != null){
                village.adjustReputation(serverLevel, player.getUUID(), amount);
                Cle.LOGGER.info("changeNearbyPlayerReputation " + player.getUUID() + ": " + amount);

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
            }
            else{
                return false;
            }
        }

        return !nearbyPlayers.isEmpty();
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
        ServerPlayer player = (ServerPlayer) serverLevel.getPlayerByUUID(playerUUID);
        MutableComponent actionBarMessage = Component.translatable("cle.reputation_cap_reached", villageName);
        player.displayClientMessage(actionBarMessage, true);
        player.playNotifySound(SoundEvents.VILLAGER_NO, player.getSoundSource(), 1.0F, 1.0F);
    }

}
