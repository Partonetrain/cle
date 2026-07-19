package info.partonetrain.cle.mixin;

import org.millenaire.village.VillageReputation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.UUID;

@Mixin(VillageReputation.class)
public interface VillageReputationAccessor {
    @Accessor("reputations")
    Map<UUID, Integer> cle$getReputations();
}
