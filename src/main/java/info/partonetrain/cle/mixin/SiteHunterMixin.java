package info.partonetrain.cle.mixin;

import info.partonetrain.cle.config.SlightlyLessJankEarlyConfigReader;
import org.millenaire.world.SiteHunter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(SiteHunter.class)
public class SiteHunterMixin {

    //MIN_BAND
    @ModifyArg(method = "<clinit>", at= @At(value = "INVOKE", target = "Lorg/millenaire/world/SiteHunter;floatProp(Ljava/lang/String;F)F", ordinal = 0), index = 1)
    private static float cle$clinit1(float fallback){
        SlightlyLessJankEarlyConfigReader.readConfigsEarly();
        if(SlightlyLessJankEarlyConfigReader.minBand != -1){
            return (float) SlightlyLessJankEarlyConfigReader.minBand;
        }
        return fallback;
    }

    //MAX_CORE_SPREAD
    @ModifyArg(method = "<clinit>", at= @At(value = "INVOKE", target = "Ljava/lang/Integer;getInteger(Ljava/lang/String;I)Ljava/lang/Integer;", ordinal = 1), index = 1)
    private static int cle$clinit2(int fallback){
        SlightlyLessJankEarlyConfigReader.readConfigsEarly();
        if(SlightlyLessJankEarlyConfigReader.maxCoreSpread != -1){
            return SlightlyLessJankEarlyConfigReader.maxCoreSpread;
        }
        return fallback;
    }
}
