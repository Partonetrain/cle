package info.partonetrain.cle.mixin;

import org.millenaire.quest.QuestManager;
import org.millenaire.village.VillageSavedData;
import org.millenaire.village.VillagerRecord;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.UUID;

@Mixin(QuestManager.class)
public interface QuestManagerAccessor {

    @Invoker("findVillagerRecord")
    static VillagerRecord cle$findVillagerRecord(VillageSavedData savedData, UUID villagerId) {
        throw new AssertionError();
    }

}