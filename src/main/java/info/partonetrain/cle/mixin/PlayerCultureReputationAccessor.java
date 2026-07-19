package info.partonetrain.cle.mixin;

import net.minecraft.resources.ResourceLocation;
import org.millenaire.village.PlayerCultureReputation;
import org.millenaire.village.VillageReputation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.UUID;

@Mixin(PlayerCultureReputation.class)
public interface PlayerCultureReputationAccessor {
    @Accessor("data")
    Map<UUID, Map<ResourceLocation, Integer>> cle$getData();
}
