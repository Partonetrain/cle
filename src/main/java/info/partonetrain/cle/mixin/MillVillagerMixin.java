package info.partonetrain.cle.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import info.partonetrain.cle.Cle;
import info.partonetrain.cle.CleConfig;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.millenaire.entity.MillVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(MillVillager.class)
public class MillVillagerMixin extends PathfinderMob {

    protected MillVillagerMixin(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "triggerMobAttacks", at=@At("RETURN"))
    public void cle$triggerMobAttacks(CallbackInfo ci, @Local AABB box){
        if(CleConfig.ATTACKS_MILLAGERS.getAsBoolean()) {
            List<Mob> nearbyEntities = this.level().getEntitiesOfClass(Mob.class, box);

            for (Mob mob : nearbyEntities) {
                if (mob.getType().is(Cle.HUNTS_MILLAGERS) && mob.getTarget() == null && mob.hasLineOfSight(this)) {
                    mob.setTarget(this);
                }
            }
        }
    }

}
