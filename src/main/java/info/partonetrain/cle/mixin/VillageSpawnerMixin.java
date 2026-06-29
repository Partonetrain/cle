package info.partonetrain.cle.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import info.partonetrain.cle.CleConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import org.millenaire.village.NightActionHelper;
import org.millenaire.world.VillageSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(VillageSpawner.class)
public class VillageSpawnerMixin {

    /*
    @ModifyReturnValue(method = "dryRunPlacement", at=@At("RETURN"))
    private static VillageSpawner.ValidationResult cle$dryRunPlacement(VillageSpawner.ValidationResult original, @Local(argsOnly = true) ServerLevel level, @Local(argsOnly = true) BlockPos center){
        if(CleConfig.MAX_MIN_COORDINATE.get() != 0) {
            int max = CleConfig.MAX_MIN_COORDINATE.get();
            int min = -1 * CleConfig.MAX_MIN_COORDINATE.get();

            int centerX = center.getX();
            int centerZ = center.getZ();

            if(centerX > max || centerX < min || centerZ > max || centerZ < min) {
                return VillageSpawner.ValidationResult.fail(Component.translatable("cle.outside_of_config_range"));
            }
        }
        return original;
    }
    */
}
