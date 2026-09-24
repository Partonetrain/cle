package info.partonetrain.cle.mixin;

import org.millenaire.quest.Quest;
import org.millenaire.quest.QuestRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(QuestRegistry.class)
public interface QuestRegistryAccessor {

    @Accessor("QUESTS")
    static Map<String, Quest> cle$getQuests() {
        throw new AssertionError();
    }

}