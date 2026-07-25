package info.partonetrain.cle.mixin.integration.wayward_attributes;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.item.Item;
import org.millenaire.entity.VillagerCombat;
import org.millenaire.item.MillenaireBow;
import org.millenaire.item.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.lodestar.wayward_attributes.tweaks.RangedAttributeTweaks;

import static team.lodestar.wayward_attributes.tweaks.RangedAttributeTweaks.addRangedItemProperties;

@Mixin(RangedAttributeTweaks.class)
public class RangedAttributeTweaksMixin {
    @Inject(method = "addBowProperties", at=@At("HEAD"), cancellable = true)
    private static void cle$addBowProperties(Item item, DataComponentPatch.Builder builder, CallbackInfo ci){
        if(item instanceof MillenaireBow millenaireBow){
            /*
            yumibow: 2 speedFactor, 0.5 damageBonus, 1 enchantability (same enchantability as vanilla bow)
            inuitbow: 1 speedFactor, 0 damageBonus, 20 enchantability
            seljuk_bow: identical to vanilla, actually literally, it uses vanilla Bow class instead of MillenaireBow and has same durability
             */

            addRangedItemProperties(item, builder, 2f + millenaireBow.getDamageBonus(), 3f * millenaireBow.getSpeedFactor(), 1.0f / millenaireBow.getSpeedFactor());
            ci.cancel();
        }
    }
}
