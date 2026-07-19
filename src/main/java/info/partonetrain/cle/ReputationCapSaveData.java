package info.partonetrain.cle;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ReputationCapSaveData extends SavedData {

    /*
    * ok so basically there is some NBT nesting here
    * and I am not super familiar with that so here is some JSON
    * that models out how the NBT is supposed to be laid out

{
  "players": [
    {
      "player": "ed4172b1-ad3b-4139-b2f2-bde371b909c3",
      "caps": [
        {
          "village": "ac462cc9-5787-42ce-8ba1-1b7a8cde0d90",
          "reputation": 192,
          "day": 5
        },
        {
          "village": "13dcaeda-120f-46d0-b146-b0613674ffc3",
          "reputation": 8192,
          "day": 2
        }
      ]
    },
    {
      "player": "3fb16546-4bf0-4900-9d98-2280fdd0d5d6",
      "caps": [
        {
          "village": "0e488684-d871-4a8c-bb4e-944ad7b13e67",
          "reputation": 0,
          "day": 0
        }
      ]
    }
  ]
}
     */

    public static ReputationCapSaveData create(){
        return new ReputationCapSaveData();
    }

    public record ReputationCap(UUID villageUUID, int rep, int day){}

    public Map<UUID, List<ReputationCap>> players = new HashMap<>(); //playerUUID <-> ReputationCap

    @Override
    public @NotNull CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag playersList = new ListTag();
        for (Map.Entry<UUID, List<ReputationCap>> entry : players.entrySet()) {
            CompoundTag playerTag = new CompoundTag();
            playerTag.putUUID("player", entry.getKey());

            ListTag capsList = new ListTag();
            for (ReputationCap cap : entry.getValue()) {
                CompoundTag capTag = new CompoundTag();
                capTag.putUUID("village", cap.villageUUID());
                capTag.putInt("reputation", cap.rep());
                capTag.putInt("day", cap.day());
                capsList.add(capTag);
            }
            playerTag.put("caps", capsList);
            playersList.add(playerTag);
        }
        tag.put("players", playersList);
        return tag;
    }

    public static ReputationCapSaveData load(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        ReputationCapSaveData data = ReputationCapSaveData.create();
        ListTag playersList = tag.getList("players", Tag.TAG_COMPOUND);
        for (int i = 0; i < playersList.size(); i++) {
            CompoundTag playerTag = playersList.getCompound(i);
            UUID playerUUID = playerTag.getUUID("player");

            List<ReputationCap> caps = new ArrayList<>();
            ListTag capsList = playerTag.getList("caps", Tag.TAG_COMPOUND);
            for (int tagIndex = 0; tagIndex < capsList.size(); tagIndex++) {
                CompoundTag capTag = capsList.getCompound(tagIndex);
                UUID villageUUID = capTag.getUUID("village");
                int rep = capTag.getInt("reputation");
                int day = capTag.getInt("day");
                caps.add(new ReputationCap(villageUUID, rep, day));
            }
            data.players.put(playerUUID, caps);
        }
        return data;
    }

    public int add(UUID playerUUID, UUID villageUUID, int currentAmount, int levelDayNumber){
        List<ReputationCap> repCaps = players.get(playerUUID);
        int repToSet = Math.min(CleConfig.REPUTATIION_CAP_PER_DAY.getAsInt(), currentAmount); //the lower of the two values

        if(repCaps == null){ //if the player has no reputation caps set up yet
            repCaps = new ArrayList<>();
            repCaps.add(new ReputationCap(villageUUID, repToSet, levelDayNumber));
        }
        else {
            Optional<ReputationCap> repCapForThisVillage = findRepCap(playerUUID, villageUUID);
            if (repCapForThisVillage.isPresent()) { //if this player already has a reputation cap for this village
                if(levelDayNumber > repCapForThisVillage.get().day()){ //if the repCapForThisVillage is from an earlier day than now
                    boolean removed = repCaps.remove(repCapForThisVillage.get()); //remove the rep cap for only this village
                    repCaps.add(new ReputationCap(villageUUID, repToSet, levelDayNumber)); //add it back with updated info
                }
                else{ //if the repCapForThisVillage is from today
                    int previouslyAccumlatedRepToday = repCapForThisVillage.get().rep(); //
                    final int MAX_ALLOWED_GAIN_RN = CleConfig.REPUTATIION_CAP_PER_DAY.getAsInt() - previouslyAccumlatedRepToday;
                    //we are only allowed to gain up to the cap right now, so find the difference between the cap and how much we already have today.
                    repToSet = Math.min(MAX_ALLOWED_GAIN_RN, repToSet);

                    boolean removed = repCaps.remove(repCapForThisVillage.get()); //remove the rep cap for only this village
                    int repToRecord = Math.min(CleConfig.REPUTATIION_CAP_PER_DAY.getAsInt(), previouslyAccumlatedRepToday + repToSet);
                    //record the newly accumulated rep
                    repCaps.add(new ReputationCap(villageUUID, repToRecord, levelDayNumber)); //add it back with updated info
                }
            }
            else { //if the player doesn't yet have a reputation cap for this village
                repCaps.add(new ReputationCap(villageUUID, repToSet, levelDayNumber));
            }
        }
        players.remove(playerUUID); //the save data is now outdated
        players.put(playerUUID, repCaps);
        this.setDirty();
        if(repToSet < 0){
            Cle.LOGGER.info("Returned rep was negative, this should not happen!!!");
        }
        return repToSet; //this is what is actually used, how much is actually gained
    }

    public boolean reset(UUID playerUUID, UUID villageUUID){
        List<ReputationCap> caps = players.get(playerUUID);
        ReputationCap toReset = null;

        if (caps == null) {
            return false;
        }

        for(ReputationCap cap : caps){
            if(cap.villageUUID.equals(villageUUID)){
                toReset = cap;
                break;
            }
        }
        if(toReset != null){
            caps.remove(toReset);
            this.setDirty();
            return true;
        }

        return false;
    }

    public int getAccumulatedRep(UUID playerUUID, UUID villageUUID) {
        List<ReputationCap> repCaps = players.get(playerUUID);

        if (repCaps == null) { //if the player has no reputation caps set up yet
            return 0;
        } else {
            Optional<ReputationCap> repCapForThisVillage = findRepCap(playerUUID, villageUUID);
            if (repCapForThisVillage.isPresent()) { //if this player already has a reputation cap for this village
                return repCapForThisVillage.get().rep();
            }
        }

        return 0;
    }

    public boolean canPlayerGainMoreRepToday(UUID playerUUID, UUID villageUUID, int levelDayNumber){
        List<ReputationCap> repCaps = players.get(playerUUID);
        if(repCaps == null) { //if the player has no reputation caps set up yet
            return true;
        }
        Optional<ReputationCap> repCapForThisVillage = findRepCap(playerUUID, villageUUID);
        if (repCapForThisVillage.isPresent()) { //if this player already has a reputation cap for this village
            if(levelDayNumber == repCapForThisVillage.get().day()){ //if the repCapForThisVillage is from the same day
                return repCapForThisVillage.get().rep() < CleConfig.REPUTATIION_CAP_PER_DAY.getAsInt();
            }
        }
        else { //if the player doesn't yet have a reputation cap for this village
            return true; //rep cap will be added next
        }

        return true; //shouldn't get here right..?
    }

    public Optional<ReputationCap> findRepCap(UUID playerUUID, UUID villageUUID){
        List<ReputationCap> repCapsForThisPlayer = players.get(playerUUID);
        List<ReputationCap> repCapsForThisVillage = new ArrayList<>();
        for(ReputationCap repCap : repCapsForThisPlayer){
            if(repCap.villageUUID.equals(villageUUID)){
                repCapsForThisVillage.add(repCap);
            }
        }
        if(repCapsForThisVillage.isEmpty()){
            Cle.LOGGER.debug("NO repCap for player " + playerUUID + " for village " + villageUUID);
            return Optional.empty();
        }
        else{
            Cle.LOGGER.debug(repCapsForThisVillage.size() + " repCap(s) for player " + playerUUID + " for village " + villageUUID);
        }

        //find the most recent one
        //ideally there is only ever 1 rep cap per village
        //but i was having issues in an earlier version of this where there were more than 1
        int latestDay = -1;
        Optional<ReputationCap> latestReputationCap = Optional.empty();
        for(ReputationCap repCap : repCapsForThisVillage){
            if(repCap.day() > latestDay){
                latestDay = repCap.day();
                latestReputationCap = Optional.of(repCap);
            }
        }

        return latestReputationCap;
    }

    public static ReputationCapSaveData getInstance(ServerLevel sl) {
        if (sl != null) {
            return sl.getDataStorage().computeIfAbsent(new Factory<>(ReputationCapSaveData::new, ReputationCapSaveData::load, null), "cle_reputation_cap");
        }
        return null;
    }
}
