package info.partonetrain.cle.mixin;

import org.millenaire.config.VillagerConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(VillagerConfig.class)
public interface VillagerConfigAccessor {

    //why is this private?
    @Accessor("NAMED_CONFIGS")
    static Map<String, VillagerConfig> cle$getNamedConfigs() {
        throw new AssertionError();
    }

}