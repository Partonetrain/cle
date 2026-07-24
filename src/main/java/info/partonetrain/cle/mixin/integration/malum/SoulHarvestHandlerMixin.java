package info.partonetrain.cle.mixin.integration.malum;

import com.sammy.malum.core.handlers.SoulHarvestHandler;
import info.partonetrain.cle.CleConfig;
import info.partonetrain.cle.CleUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedHashMap;

@Mixin(SoulHarvestHandler.class)
public class SoulHarvestHandlerMixin {
    @Inject(method = "dropSpirits", at= @At(value = "INVOKE", target = "Lcom/sammy/malum/core/handlers/SoulHarvestHandler$SpiritSpawner;spawnSpirits(Lnet/minecraft/world/level/Level;)V"))
    private static void cle$dropSpirits(LivingEntity target, LivingEntity attacker, CallbackInfo ci){
        if(attacker instanceof ServerPlayer sp && CleConfig.SPIRIT_REAP_REPUTATION_LOSS.getAsInt() > -1){
            LinkedHashMap<String, String> langKeysWithPlaceholders = new LinkedHashMap<>();
            langKeysWithPlaceholders.put("cle.malum.millager_afraid_spirit_harvest.1", null);
            if(target.hasCustomName()){
                langKeysWithPlaceholders.put("cle.malum.millager_afraid_spirit_harvest.2", target.getName().getString());
            }
            else{
                langKeysWithPlaceholders.put("cle.malum.millager_afraid_spirit_harvest.2.proper", target.getName().getString());
            }

            CleUtils.changeNearbyPlayerReputationFromPlayer((ServerLevel) sp.level(), sp, CleConfig.SPIRIT_REAP_REPUTATION_LOSS.getAsInt() * -1, langKeysWithPlaceholders);
        }

    }
}
