package info.partonetrain.cle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import org.millenaire.culture.ModCultures;
import org.millenaire.culture.VillagerType;
import org.millenaire.entity.MillVillager;
import org.millenaire.village.Village;

public class MillagerTagHandler {

    //doesnt work

    /*
    public static final String CLE_CHECKED = "cle.checked";

    public static boolean hasMillagerBeenChecked(MillVillager millager){
        return millager.getTags().contains(CLE_CHECKED);
    }

    public static void giveMillagerTags(MillVillager millager){
        try {
            if (!CleConfig.MILLAGER_ENTITY_TAGGING.getAsBoolean()) {
                return;
            }
            if (millager.level() instanceof ClientLevel) {
                return;
            }
            if (hasMillagerBeenChecked(millager)) {
                return;
            }

            VillagerType vType = ModCultures.getVillagerType(millager.getVillagerTypeId());
            boolean bandit = (vType != null && vType.isHostile());
            if (bandit) {
                millager.getTags().add(getTagName(MillagerTagType.BANDIT));
            }

            Village village = Village.resolve((ServerLevel) millager.level(), millager.getVillageId());
            if (village != null) {
                if(village.isLoneBuilding()){
                    millager.getTags().add(getTagName(MillagerTagType.BELONGS_TO_LONE_BUILDING));
                }
                if (village.isPlayerControlled()) {
                    millager.getTags().add(getTagName(MillagerTagType.BELONGS_TO_PLAYER_CONTROLLED_MILLAGE));
                }
                else {
                    millager.getTags().add(getTagName(MillagerTagType.BELONGS_TO_NATURAL_MILLAGE));
                }
            }

            millager.getTags().add(CLE_CHECKED);
        }
        catch (Exception exception){
            Cle.LOGGER.error("Caught exception while trying to tag millager: " + exception.getMessage());
        }
    }

    public static String getTagName(MillagerTagType millagerTagType){
        return "cle." + millagerTagType.name().toLowerCase();
    }

    public enum MillagerTagType{
        BELONGS_TO_NATURAL_MILLAGE,
        BELONGS_TO_PLAYER_CONTROLLED_MILLAGE,
        BELONGS_TO_LONE_BUILDING,
        BANDIT
    }

     */
}
