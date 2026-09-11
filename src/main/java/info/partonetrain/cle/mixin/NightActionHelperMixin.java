package info.partonetrain.cle.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import info.partonetrain.cle.CleConfig;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.millenaire.building.BuildingInventory;
import org.millenaire.village.NightActionHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NightActionHelper.class)
public class NightActionHelperMixin {

    @WrapOperation(method = "handleChildGrowth", at= @At(value = "INVOKE", target = "Lorg/millenaire/building/BuildingInventory;getCount(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/Item;)I", ordinal = 0))
    private static int cle$handleChildGrowth1(BuildingInventory instance, Level level, Item item, Operation<Integer> original){
        if(CleConfig.DISABLE_HARDCODED_EGG_CHECK.getAsBoolean()) {
            return 0;
        }
        return original.call(instance, level, item);
    }

}
