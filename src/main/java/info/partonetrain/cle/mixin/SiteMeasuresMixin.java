package info.partonetrain.cle.mixin;

import info.partonetrain.cle.config.SlightlyLessJankEarlyConfigReader;
import org.millenaire.world.sitescore.SiteMeasures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(SiteMeasures.class)
public class SiteMeasuresMixin {

    //MIN_GENTLE2
    @ModifyArg(method = "<clinit>", at= @At(value = "INVOKE", target = "Lorg/millenaire/world/sitescore/SiteMeasures;prop(Ljava/lang/String;F)F", ordinal = 0), index = 1)
    private static float cle$clinit1(float fallback){
        SlightlyLessJankEarlyConfigReader.readConfigsEarly();
        if(SlightlyLessJankEarlyConfigReader.minGentle2 != -1){
            return SlightlyLessJankEarlyConfigReader.minGentle2;
        }
        return fallback;
    }

    //MIN_CENTRE_WEIGHTED
    @ModifyArg(method = "<clinit>", at= @At(value = "INVOKE", target = "Lorg/millenaire/world/sitescore/SiteMeasures;prop(Ljava/lang/String;F)F", ordinal = 1), index = 1)
    private static float cle$clinit2(float fallback){
        SlightlyLessJankEarlyConfigReader.readConfigsEarly();
        if(SlightlyLessJankEarlyConfigReader.minCentreWeighted != -1){
            return SlightlyLessJankEarlyConfigReader.minCentreWeighted;
        }
        return fallback;
    }

    //MIN_CONNECTED
    @ModifyArg(method = "<clinit>", at= @At(value = "INVOKE", target = "Lorg/millenaire/world/sitescore/SiteMeasures;prop(Ljava/lang/String;F)F", ordinal = 2), index = 1)
    private static float cle$clinit3(float fallback){
        SlightlyLessJankEarlyConfigReader.readConfigsEarly();
        if(SlightlyLessJankEarlyConfigReader.minConnected != -1){
            return SlightlyLessJankEarlyConfigReader.minConnected;
        }
        return fallback;
    }
}
