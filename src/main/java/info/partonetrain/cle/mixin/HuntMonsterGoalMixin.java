package info.partonetrain.cle.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import info.partonetrain.cle.Cle;
import net.minecraft.world.entity.LivingEntity;
import org.millenaire.combat.CombatHelper;
import org.millenaire.goal.impl.HuntMonsterGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HuntMonsterGoal.class)
public class HuntMonsterGoalMixin {
    @ModifyReturnValue(method = "isHuntableEntity", at=@At("RETURN"))
    private static boolean cle$isHuntableEntity(boolean original, @Local(argsOnly = true) LivingEntity entity){
        if(entity != null && entity.isAlive()){
            if(entity.getType().is(Cle.MILLAGERS_AVOID_HUNTING)){
                return false;
            }
            else if(entity.getType().is(Cle.MILLAGERS_TRY_HUNTING)){
                return true;
            }
        }
        return original;
    }
}
