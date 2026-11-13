package info.partonetrain.cle.compat;

import com.mojang.authlib.GameProfile;
import dev.ftb.mods.ftblibrary.icon.FaceIcon;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class ClientFTBLibraryCompat {


    /*
    public static class MillenaireFaceIcon extends FaceIcon {
        public MillenaireFaceIcon(GameProfile gameProfile) {
            super(gameProfile);
        }

        public static FaceIcon getFace(UUID villagerUUID) {
            FaceIcon icon = (FaceIcon)CACHE.get(profile.getId());
            if (icon == null) {
                icon = new FaceIcon(profile);
                CACHE.put(profile.getId(), icon);
            }

            return icon;
        }

    }
     */
}
