package info.partonetrain.cle.recipe;

import info.partonetrain.cle.Cle;
import info.partonetrain.cle.CleConfig;
import info.partonetrain.cle.ControlledHopper;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.millenaire.item.ModItems;
import org.millenaire.item.VillageBookItem;
import org.millenaire.village.Village;
import org.millenaire.village.VillageId;
import org.millenaire.village.VillageSavedData;
import java.util.UUID;

public class ControlledHopperRecipe extends CustomRecipe {

    public ControlledHopperRecipe(CraftingBookCategory category) {
        super(category);
    }

    //village scroll (of controlled village) + hopper = controlled hopper
    @Override
    public boolean matches(CraftingInput craftingInput, Level level) {
        if(!CleConfig.CONTROLLED_HOPPERS.getAsBoolean()){
            return false;
        }

        for(ItemStack stack : craftingInput.items()){
            if(!stack.isEmpty() && !stack.is(Items.HOPPER) && !stack.is(ModItems.VILLAGE_SCROLL)) {
                Cle.LOGGER.info(stack.getDescriptionId());
                return false;
            }
        }

        //check if village is controlled
        ItemStack scroll = findScroll(craftingInput);
        if (scroll == ItemStack.EMPTY) {
            return false;
        }
        UUID villageUuid = new VillageId(VillageBookItem.getVillageId(scroll)).uuid();
        if (level instanceof ServerLevel serverLevel) {
            Village village = VillageSavedData.get(serverLevel).getVillageManager().getVillage(new VillageId(villageUuid));
            if (village == null) {
                return false;
            }
            return village.isPlayerControlled();
        }


        return false;
    }

    @Override
    public ItemStack assemble(CraftingInput craftingInput, HolderLookup.Provider registries) {
        ItemStack scroll = findScroll(craftingInput);
        UUID villageUuid = new VillageId(VillageBookItem.getVillageId(scroll)).uuid();

        return makeHopperWithComponent(villageUuid);
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return i*i1 >= 2;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return new ItemStack(Items.HOPPER);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Cle.CONTROLLED_HOPPER_RECIPE_SERIALIZER.get();
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> ret = NonNullList.withSize(input.size(), ItemStack.EMPTY);
        ItemStack scroll = findScroll(input);
        if(!scroll.isEmpty()){
            ret.add(scroll);
        }
        return ret;
    }

    public ItemStack findScroll(CraftingInput craftingInput){
        for(ItemStack stack : craftingInput.items()){
            if(stack.is(ModItems.VILLAGE_SCROLL)){
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    public ItemStack makeHopperWithComponent(UUID uuid){
        ItemStack result = new ItemStack(Items.HOPPER);
        result.set(Cle.CONTROLLED_HOPPER_DATA_COMPONENT_TYPE, new ControlledHopper(uuid)); //cle:controlled_hopper
        result.set(DataComponents.CUSTOM_NAME, Component.translatable("cle.tooltip.controlled_hopper.name"));
        return result;
    }

}
