package info.partonetrain.cle.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import info.partonetrain.cle.Cle;
import info.partonetrain.cle.config.CleConfig;
import info.partonetrain.cle.config.ParsedConfigs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.millenaire.building.BuildingId;
import org.millenaire.building.BuildingInstance;
import org.millenaire.building.BuildingInventory;
import org.millenaire.goal.GoalContext;
import org.millenaire.village.NightActionHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NightActionHelper.class)
public class NightActionHelperMixin {

    @WrapOperation(method = "handleChildGrowth", at= @At(value = "INVOKE", target = "Lorg/millenaire/building/BuildingInventory;getCount(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/Item;)I", ordinal = 0))
    private static int cle$handleChildGrowth1(BuildingInventory instance, Level level, Item item, Operation<Integer> original){
        if(CleConfig.DISABLE_HARDCODED_EGG_CHECK.getAsBoolean()) {
            return 0;
        }
        return original.call(instance, level, item);
    }

    @Inject(method = "perform", at= @At(value = "INVOKE_ASSIGN", target = "Lorg/millenaire/culture/ModCultures;getVillagerType(Lnet/minecraft/resources/ResourceLocation;)Lorg/millenaire/culture/VillagerType;"))
    private static void cle$perform(GoalContext ctx, CallbackInfoReturnable<Boolean> cir){
        if(!ParsedConfigs.itemsToRandomlyConsume.isEmpty()) {
            BuildingId homeId = ctx.villager().getHomeBuilding();
            BuildingInstance home = homeId == null ? null : ctx.village().getBuilding(homeId);

            if (home != null && home.getInventory() != null) {
                if (ctx.level().getRandom().nextFloat() < 0.25F) {
                    BuildingInventory inv = home.getInventory();

                    for (ResourceLocation rl : ParsedConfigs.itemsToRandomlyConsume) {
                        Item item = BuiltInRegistries.ITEM.get(rl);

                        if (inv.getCount(ctx.level(), item) > 0) {
                            inv.remove(ctx.level(), item, 1);
                            Cle.LOGGER.info(ctx.villager().getFirstName() + " " + ctx.villager().getFamilyName() + " removed 1 " + rl.toString());
                            break;
                        }
                    }
                }
            }
        }

    }

}
