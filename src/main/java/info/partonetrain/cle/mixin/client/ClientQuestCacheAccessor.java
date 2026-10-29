package info.partonetrain.cle.mixin.client;

import org.millenaire.client.ClientQuestCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ClientQuestCache.class)
public interface ClientQuestCacheAccessor {

    @Accessor("quests")
    static Map<Long, ClientQuestCache.CachedQuest> cle$getQuests() {
        throw new AssertionError();
    }
}
