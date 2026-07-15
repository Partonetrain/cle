package info.partonetrain.cle;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.millenaire.entity.MillVillager;
import org.millenaire.village.Village;

import java.util.LinkedHashMap;
import java.util.List;

public class CleUtils {

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

}
