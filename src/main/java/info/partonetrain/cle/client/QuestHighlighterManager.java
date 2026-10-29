package info.partonetrain.cle.client;

import info.partonetrain.cle.CleConfig;
import info.partonetrain.cle.mixin.client.ClientQuestCacheAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import org.millenaire.client.ClientQuestCache;
import org.millenaire.entity.MillVillager;
import org.millenaire.network.QuestNetworkHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class QuestHighlighterManager {

    private static final int MAX_COOLDOWN = 100;
    private static int cooldown = MAX_COOLDOWN;
    private static List<UUID> questObjectives = new ArrayList<>();

    //reevaluate only every (config) ticks in the overworld
    public static boolean tickCooldown(){
        if(!CleConfig.QUEST_HIGHLIGHTING.get()){
            return false;
        }
        else if(Minecraft.getInstance().level == null){
            return false;
        }
        else if(Minecraft.getInstance().level.dimension() != Level.OVERWORLD){
            return false;
        }

        cooldown--;
        if(cooldown <= 0){
            cooldown = MAX_COOLDOWN;
            reevaluateQuestObjectices();
            return true;
        }
        return false;
    }

    public static void reevaluateQuestObjectices(){
        questObjectives.clear();
        Map<Long, ClientQuestCache.CachedQuest> quests = ClientQuestCacheAccessor.cle$getQuests();
        for(Map.Entry<Long, ClientQuestCache.CachedQuest> entry : quests.entrySet()){
            ClientQuestCache.CachedQuest cached = entry.getValue();
            if(cached.currentStep() > 0){ //only "accepted" quests
                questObjectives.add(cached.currentStepVillagerId());
            }
        }
    }

    //called in MinecraftMixin shouldEntityAppearGlowing
    public static boolean isMillagerQuestObjective(MillVillager millager){
        return questObjectives.contains(millager.getUUID());
    }
}
