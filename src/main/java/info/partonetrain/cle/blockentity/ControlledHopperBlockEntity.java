package info.partonetrain.cle.blockentity;

import info.partonetrain.cle.Cle;
import info.partonetrain.cle.CleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.millenaire.item.ModItems;
import org.millenaire.item.VillageBookItem;
import org.millenaire.village.Village;
import org.millenaire.village.VillageId;
import org.millenaire.village.VillageManager;
import org.millenaire.village.VillageSavedData;

public class ControlledHopperBlockEntity extends HopperBlockEntity {
    public VillageId villageId;
    public String villageName = "NONE";

    public ControlledHopperBlockEntity(BlockPos pos, BlockState blockState) {
        super(pos, blockState);
    }

    @Override
    public BlockEntityType<?> getType(){
        return Cle.CONTROLLED_HOPPER_BLOCK_ENTITY_TYPE.get();
    }

    @Override
    protected Component getDefaultName() {
        if(villageName.length() >= 10){
            return Component.translatable("cle.gui.controlled_hopper.short", villageName);
        }
        return Component.translatable("cle.gui.controlled_hopper", villageName);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.villageId = new VillageId(tag.getUUID("VillageId"));
        this.villageName = tag.getString("name");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if(villageId != null){
            tag.putUUID("VillageId", villageId.uuid());
        }
        else{
            Cle.LOGGER.error("Failed to save ControlledHopperBlockEntity properly at " + this.getBlockPos() + ", villageId is null");
        }
        tag.putString("name", villageName);
    }


    public void showColoredParticle(){
        if(this.level instanceof ServerLevel sl) {
            if(this.villageId == null){
                CleUtils.addParticlesToBlock(ParticleTypes.SONIC_BOOM, this.getBlockPos(), sl); //no village id
                return;
            }
            VillageManager vm = VillageSavedData.get(sl).getVillageManager();
            Village village = vm.getVillage(this.villageId);
            if(village == null){
                CleUtils.addParticlesToBlock(ParticleTypes.ANGRY_VILLAGER, this.getBlockPos(), sl); //invalid village id
                return;
            }
            String cultureName = village.getCultureId().getPath();
            DustParticleOptions dustParticle = new DustParticleOptions(Vec3.fromRGB24(CleUtils.getARGBfromCultureName(cultureName)).toVector3f(), 1.0f);
            CleUtils.addParticlesToBlock(dustParticle, this.getBlockPos(), sl);
        }
    }

    public static String createNameFromStack(ItemStack stack){
        String villageName = "";
        if(stack.is(ModItems.VILLAGE_SCROLL)){
            villageName = VillageBookItem.getVillageName(stack);
        }
        else{
            //warning: this is very stupid, all because I didn't want to register a new data component type for the name
            //"extract" name from lore
            //"cle.tooltip.controlled_hopper.lore": "Controlled Hopper of %1$s", -> the placeholder
            ItemLore lore = stack.get(DataComponents.LORE);
            if (lore != null && !lore.lines().isEmpty()) {
                Component component = lore.lines().get(0);
                if (component.getContents() instanceof TranslatableContents translated && translated.getKey().equals("cle.tooltip.controlled_hopper.lore")) {
                    Object[] args = translated.getArgs();
                    if (args.length > 0) { //not all TranslatableContents have args, but this one should
                        Object placeholder = args[0];
                        if(placeholder instanceof String name){
                            villageName = name;
                        }
                        else{
                            villageName = "UNEXPECTED ARG";
                        }
                    }
                    else{
                        villageName = "NO ARGS";
                    }
                }
                else{
                    villageName = "UNEXPECTED LORE";
                }
            }
        }
        return villageName.isEmpty() ? "???" : villageName;
    }
}
