package info.partonetrain.cle.mixin;

import info.partonetrain.cle.Cle;
import info.partonetrain.cle.CleConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import org.millenaire.village.Village;
import org.millenaire.village.VillageEnvironmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(VillageEnvironmentHelper.class)
public class VillageEnvironmentHelperMixin {

    @Inject(method = "despawnDangerousMobs", at=@At("HEAD"))
    private static void cle$despawnDangerousMobs(ServerLevel level, AABB dangerousMobsArea, Village village, CallbackInfo ci){
        if(CleConfig.DESPAWNS_IN_MILLAGE.getAsBoolean()) {
            List<Entity> nearbyEntities = level.getEntitiesOfClass(Entity.class, dangerousMobsArea);

            for (Entity e : nearbyEntities) {
                if (e.getType().is(Cle.DESPAWNS_IN_MILLAGE)) {
                    e.discard();
                }
            }
        }
    }

}
