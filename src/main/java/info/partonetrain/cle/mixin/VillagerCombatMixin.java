package info.partonetrain.cle.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import info.partonetrain.cle.Cle;
import info.partonetrain.cle.CleConfig;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import org.millenaire.entity.MillVillager;
import org.millenaire.entity.VillagerCombat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(VillagerCombat.class)
public class VillagerCombatMixin {

    @Mutable
    @Final
    @Shadow
    private final MillVillager villager;

    public VillagerCombatMixin(MillVillager villager) {
        this.villager = villager;
    }

    @Inject(method = "triggerMobAttacks", at=@At("RETURN"))
    public void cle$triggerMobAttacks(CallbackInfo ci, @Local AABB box){
        if(CleConfig.ATTACKS_MILLAGERS.getAsBoolean()) {
            List<Mob> nearbyEntities = villager.level().getEntitiesOfClass(Mob.class, box);

            for (Mob mob : nearbyEntities) {
                if (mob.getType().is(Cle.HUNTS_MILLAGERS) && mob.getTarget() == null && mob.hasLineOfSight(villager)) {
                    mob.setTarget(villager);
                }
            }
        }
    }
}
