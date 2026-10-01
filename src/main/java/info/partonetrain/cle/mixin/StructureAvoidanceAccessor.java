package info.partonetrain.cle.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.millenaire.quest.Quest;
import org.millenaire.quest.QuestRegistry;
import org.millenaire.world.StructureAvoidance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(StructureAvoidance.class)
public interface StructureAvoidanceAccessor {

    @Accessor("AVOIDED_STRUCTURE_KEYS")
    static List<ResourceKey<Structure>> cle$getAvoidedStructureKeys() {
        throw new AssertionError();
    }

}