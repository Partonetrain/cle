package info.partonetrain.cle;

import info.partonetrain.cle.entity.ThrownInuitTridentEntity;
import info.partonetrain.cle.item.AlternativeInuitTrident;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforgespi.locating.IModFile;
import org.millenaire.entity.MillVillager;
import org.millenaire.entity.ModEntities;
import org.millenaire.item.ModCreativeTabs;
import org.millenaire.item.ModItems;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Supplier;

@Mod(Cle.MODID)
public class Cle {
    public static final String MODID = "cle";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredItem<Item> ALTERNATIVE_INUIT_TRIDENT = ITEMS.register("inuit_trident", () ->
            new AlternativeInuitTrident((new Item.Properties()).rarity(Rarity.COMMON).durability(250).attributes(AlternativeInuitTrident.createAttributes()).component(DataComponents.TOOL, TridentItem.createToolProperties())));

    public static final DeferredItem<Item> ALTERNATIVE_MAYAN_MACE = ITEMS.register("mayan_mace", () ->
            new MaceItem((new Item.Properties()).rarity(Rarity.COMMON).durability(500).component(DataComponents.TOOL, MaceItem.createToolProperties()).attributes(MaceItem.createAttributes())));

    public static final DeferredItem<Item> ALTERNATIVE_BYZANTINE_MACE = ITEMS.register("byzantine_mace", () ->
            new MaceItem((new Item.Properties()).rarity(Rarity.COMMON).durability(500).component(DataComponents.TOOL, MaceItem.createToolProperties()).attributes(MaceItem.createAttributes())));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("cle_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.cle"))
            .withTabsBefore(ModCreativeTabs.MILLENAIRE_TAB.getId())
            .icon(() -> ALTERNATIVE_INUIT_TRIDENT.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ALTERNATIVE_INUIT_TRIDENT.get());
                output.accept(ALTERNATIVE_MAYAN_MACE.get());
                output.accept(ALTERNATIVE_BYZANTINE_MACE.get());
            }).build());

    public static final Supplier<EntityType<ThrownInuitTridentEntity>> THROWN_INUIT_TRIDENT_ENTITY_TYPE = ENTITY_TYPES.register("thrown_inuit_trident",
            () -> EntityType.Builder.<ThrownInuitTridentEntity>of(ThrownInuitTridentEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .build("thrown_inuit_trident"));

    public static final TagKey<EntityType<?>> MILLAGERS_AVOID_HUNTING = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(MODID, "millagers_avoid_hunting"));
    public static final TagKey<EntityType<?>> MILLAGERS_TRY_HUNTING = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(MODID, "millagers_try_hunting"));

    public static final TagKey<EntityType<?>> MILLAGERS_DEAL_MODIFIED_DAMAGE_TO = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(MODID, "millagers_deal_modified_damage_to"));
    public static final TagKey<EntityType<?>> MILLAGERS_TAKE_MODIFIED_DAMAGE_FROM = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(MODID, "millagers_take_modified_damage_from"));

    public static final TagKey<EntityType<?>> HUNTS_MILLAGERS = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(MODID, "hunts_millagers"));
    public static final TagKey<EntityType<?>> DESPAWNS_IN_MILLAGE = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(MODID, "despawns_in_millage"));

    public Cle(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        ENTITY_TYPES.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::addPackFinders);
        modContainer.registerConfig(ModConfig.Type.STARTUP, CleConfig.SPEC);
    }

    private void addPackFinders(AddPackFindersEvent event){
        //these datapacks add enchantment tags to cle's alternative items
        //and add #c:hidden_from_recipe_viewers to millenaire's original items
        if (event.getPackType() == PackType.SERVER_DATA) {
            if(CleConfig.ALTERNATIVE_INUIT_TRIDENT.getAsBoolean()){
                addSubDataPack("cle_alternative_inuit_trident", event);
            }
            if(CleConfig.ALTERNATIVE_MACES.getAsBoolean()){
                addSubDataPack("cle_alternative_maces", event);
            }
            if(CleConfig.COMPOST_DATAPACK.getAsBoolean()){
                addSubDataPack("cle_compost", event);
            }
        }
    }

    private static void addSubDataPack(String subPackName, AddPackFindersEvent event){
        IModFile thisJar = ModList.get().getModFileById(MODID).getFile();
        Path path = thisJar.findResource("datapacks", subPackName);

        PackSelectionConfig packSelectionConfig = new PackSelectionConfig(false, Pack.Position.TOP, false);
        PackLocationInfo packLocationInfo = new PackLocationInfo(subPackName, Component.literal(subPackName), PackSource.BUILT_IN,
                Optional.empty()
        );

        PathPackResources.PathResourcesSupplier pathResourcesSupplier = new PathPackResources.PathResourcesSupplier(path);
        Pack pack = Pack.readMetaAndCreate(packLocationInfo, pathResourcesSupplier, PackType.SERVER_DATA, packSelectionConfig);
        event.addRepositorySource(packConsumer -> {
            packConsumer.accept(pack);
        });
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        //LOGGER.info("HELLO FROM COMMON SETUP");
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(ALTERNATIVE_INUIT_TRIDENT);
        }
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        ParsedConfigs.parseDamageMods();
    }

    @SubscribeEvent
    public void onPlayerTick(EntityTickEvent.Post event) {
        if(!CleConfig.ALTERNATIVE_INUIT_TRIDENT.getAsBoolean() || !CleConfig.ALTERNATIVE_MACES.getAsBoolean()){
            return;
        }

        if (event.getEntity().level().isClientSide()) {
            return;
        }

        if(event.getEntity() instanceof Player player && !player.getAbilities().instabuild){
            for (ItemStack stack : player.getInventory().items) {
                if (!stack.isEmpty()) {
                    if(CleConfig.ALTERNATIVE_INUIT_TRIDENT.getAsBoolean() && stack.is(ModItems.INUIT_TRIDENT)){
                        stack = stack.transmuteCopy(ALTERNATIVE_INUIT_TRIDENT);
                    }

                    if(CleConfig.ALTERNATIVE_MACES.getAsBoolean()){
                        if(stack.is(ModItems.MAYAN_MACE)){
                            stack = stack.transmuteCopy(ALTERNATIVE_MAYAN_MACE);
                        }
                        else if(stack.is(ModItems.BYZANTINE_MACE)){
                            stack = stack.transmuteCopy(ALTERNATIVE_BYZANTINE_MACE);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent.Pre event){
        LivingEntity damaged = event.getEntity();
        Entity cause = event.getContainer().getSource().getEntity();
        Cle.LOGGER.info(event.getContainer().getSource().toString());

        if(damaged != null){
            if(damaged instanceof MillVillager){
                if(cause != null && cause.getType().is(MILLAGERS_TAKE_MODIFIED_DAMAGE_FROM)){
                    event.setNewDamage(ParsedConfigs.modifiedDamageReceived.apply(event.getOriginalDamage()));
                }
            }
            else if(damaged.getType().is(MILLAGERS_DEAL_MODIFIED_DAMAGE_TO)){
                if(cause instanceof MillVillager){
                    event.setNewDamage(ParsedConfigs.modifiedDamageDealt.apply(event.getOriginalDamage()));
                }
            }
        }

    }
}
