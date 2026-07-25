package info.partonetrain.cle;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class CleConfig {
    private static final ModConfigSpec.Builder BUILDER;
    public static final ModConfigSpec SPEC;

    //general
    public static ModConfigSpec.BooleanValue PREVENT_HOPPER_INTERACTIONS;
    public static ModConfigSpec.BooleanValue USE_FOOD_COMPONENTS;
    public static ModConfigSpec.BooleanValue YOGURT_GIVES_BOWL;
    public static ModConfigSpec.BooleanValue DRINK_GIVES_BOTTLE;
    public static ModConfigSpec.BooleanValue ALTERNATIVE_INUIT_TRIDENT;
    public static ModConfigSpec.BooleanValue ALTERNATIVE_MACES;
    public static ModConfigSpec.BooleanValue CONVERT_ALTERNATIVE_MILLAGERS;
    public static ModConfigSpec.BooleanValue COMPOST_DATAPACK;
    public static ModConfigSpec.ConfigValue<String> MODIFIED_DAMAGE_DEALT;
    public static ModConfigSpec.ConfigValue<String> MODIFIED_DAMAGE_RECEIVED;
    public static ModConfigSpec.IntValue REPUTATIION_CAP_PER_DAY;
    public static ModConfigSpec.BooleanValue ATTACKS_MILLAGERS;
    public static ModConfigSpec.BooleanValue DESPAWNS_IN_MILLAGE;
    public static ModConfigSpec.IntValue MAX_DISTANCE;
    public static ModConfigSpec.BooleanValue TRAVEL_BOOK_INFO_PANEL;
    //client
    public static ModConfigSpec.ConfigValue<String> SORT_CONSTRUCTIONS_PANEL;
    public static ModConfigSpec.BooleanValue PREVENT_KEYBINDS;
    //malum
    public static ModConfigSpec.IntValue SPIRIT_REAP_REPUTATION_LOSS;
    //ars
    public static ModConfigSpec.ConfigValue<List<String>>  SPELL_EFFECT_REPUTATIONS;

    static {
        BUILDER = new ModConfigSpec.Builder();
        registerConfig(BUILDER);
        SPEC = BUILDER.build();
    }

    public static void registerConfig(ModConfigSpec.Builder builder) {

        BUILDER.push("General");

        PREVENT_HOPPER_INTERACTIONS = BUILDER
                .comment("If true, prevent all hopper interactions with Locked Chests")
                .comment("Locked Chests should not work with standard modded item transfers (i.e., Create chutes) regardless of this config, because they lack Capabilities")
                .comment("They only work with hoppers by default due to inheriting vanilla chest code")
                .comment("However, other modded hoppers may or may not be affected by this - it depends on how they were implemented")
                .define("Prevent Hopper Interactions", false);

        USE_FOOD_COMPONENTS = BUILDER
                .comment("If true, Millenaire foods will use vanilla food components")
                .comment("This can enhance compatibility with mods like AppleSkin")
                .comment("However, other mods that utilize this component probably will not correctly account for the food's durability")
                .define("Use Food Components", true);

        YOGURT_GIVES_BOWL = BUILDER
                .comment("If true, Yogurt will give the player a bowl after it is eaten (only in survival)")
                .define("Yogurt Gives Bowl", true);

        DRINK_GIVES_BOTTLE = BUILDER
                .comment("If true, all drinks will give the player a glass bottle after it is drunk (only in survival)")
                .define("Drink Gives Bottle", true);

        ALTERNATIVE_INUIT_TRIDENT = BUILDER
                .comment("If true, any Inuit Tridents obtained by survival mode players will behave like a Minecraft trident instead of a sword")
                .comment("WARNING: this auto-replaces millenaire:inuittrident in player inventories with cle:inuit_trident")
                .define("Alternative Inuit Trident", true);

        ALTERNATIVE_MACES = BUILDER
                .comment("If true, any Mayan or Byzantine Maces obtained by survival mode players will behave like a Minecraft mace instead of a sword")
                .comment("WARNING: this auto-replaces millenaire:mayan_mace and millenaire:byzantine_mace in player inventories with the Cle versions")
                .define("Alternative Maces", true);

        CONVERT_ALTERNATIVE_MILLAGERS = BUILDER
                .comment("If true, any alternative items that are enabled will also be converted in Millager inventories")
                .comment("This doesn't have much of an effect in practice, only increasing damage for relevant millagers")
                .define("Convert Alternative Items on Millagers", true);

        COMPOST_DATAPACK = BUILDER
                .comment("If true, enables a datapack containing composter values for Millenaire crops and related blocks will be enabled")
                .define("Compost Datapack", true);

        MODIFIED_DAMAGE_DEALT = BUILDER
                .comment("Entities in the entity type tag " + Cle.MILLAGERS_DEAL_MODIFIED_DAMAGE_TO.location() + " will take modified damage when attacked by millagers")
                .comment("The format of this is (operation)(value), so for example \"x2\" will make entities take twice as much damage and \"+2\" will make entities take 2 more damage")
                .define("Modified Damage Dealt", "x2");

        MODIFIED_DAMAGE_RECEIVED = BUILDER
                .comment("Entities in the entity type tag " + Cle.MILLAGERS_TAKE_MODIFIED_DAMAGE_FROM.location() + " will deal modified damage when attacking millagers")
                .comment("The format is the same as above")
                .define("Modified Damage Received", "/2");

        ATTACKS_MILLAGERS = BUILDER
                .comment("If true, any entity in the the entity type tag " + Cle.HUNTS_MILLAGERS.location() + " will hunt millagers, even if their original logic said they should not")
                .comment("(This is a config option unlike the other tags because it has the potential to introduce lag)")
                .define("Hunts Millagers", true);

        DESPAWNS_IN_MILLAGE = BUILDER
                .comment("If true, any entity in the the entity type tag " + Cle.HUNTS_MILLAGERS.location() + " will despawn if it enters a millage")
                .comment("(Similar to Hunts Millagers, this has the potential to introduce lag)")
                .define("Despawns In Millage", true);

        REPUTATIION_CAP_PER_DAY = BUILDER.comment("If greater than -1, the maximum amount of reputation a player can gain with per village per Minecraft day")
                .comment("This can be used to ensure players don't just dump stacks of donations into a village and instantly be considered friends")
                .comment("For reference, the reputation 'FRIEND OF THE VILLAGE' happens at 8192 points, and the reputation 'ONE OF US' happens at 32768 points")
                .comment("This also affects player reputation for entire culture; a tenth of all reputation gained at a village is gained as culture reputation")
                .defineInRange("Reputation Cap per Day", -1, -1, 32768);

        MAX_DISTANCE = BUILDER.comment("If not 0, the max distance from world spawn at which Millenaire villages and long buildings can generate")
                .comment("This can be used in combination with the spawnProtectionRadius option in Millenaire server config to define a square region around world spawn in which villages are allowed to generate")
                .defineInRange("Max Radius", 0, 0, 12_550_821);

        TRAVEL_BOOK_INFO_PANEL = BUILDER.comment("If true, Millenaire's Travel Book will open the Millenaire Info Panel instead of the Travel Book screen")
                .comment("(The Travel Book can still be opened from the Info Panel)")
                .define("Travel Book Info Panel", false);

        BUILDER.pop();

        BUILDER.push("Client");

        SORT_CONSTRUCTIONS_PANEL = BUILDER.comment("If this option is not empty, the Constructions panel in any town hall will attempt to sort its list by moving items that contain matching strings in its target to the bottom")
                .comment("This should make more important buildings appear first and less important buildings appear last")
                .comment("Separate entries by comma. Entries should not include spaces.")
                .define("Sort Constructions Panel", "wall,tower");

        PREVENT_KEYBINDS = BUILDER.comment("If true, Millenaire's keybindings will not be registered")
                .comment("There is currently no alternative to the Stance key, however")
                .define("Prevent Keybindings", false);

        BUILDER.pop();

        BUILDER.push("Malum");

        SPIRIT_REAP_REPUTATION_LOSS = BUILDER
                .comment("The amount of reputation lost for reaping Spirits nearby a millager")
                .comment("Set to -1 for no loss")
                .defineInRange("Spirit Reap Reputation Loss", 4, -1, 8192);

        BUILDER.pop();

        BUILDER.push("Ars Nouveau");

        SPELL_EFFECT_REPUTATIONS = BUILDER
                .comment("A map of spell effects to integers, that when resolved on a millager, will affect reputation with their village")
                .comment("The number of amplifies in the spell recipe will multiply the value provided")
                .define("Spell Effect Reputations", List.of(
                        "ars_nouveau:glyph_bubble,-4",
                        "ars_nouveau:glyph_cold_snap,-5",
                        "ars_nouveau:glyph_crush,-3",
                        "ars_nouveau:glyph_cut,-3",
                        "ars_nouveau:glyph_exchange,-2",
                        "ars_nouveau:glyph_explosion,-8",
                        "ars_nouveau:glyph_fangs,-9",
                        "ars_nouveau:glyph_flare,-3",
                        "ars_nouveau:glyph_freeze,-3",
                        "ars_nouveau:glyph_gravity,-2",
                        "ars_nouveau:glyph_harm,-3",
                        "ars_nouveau:glyph_heal,1",
                        "ars_nouveau:glyph_ignite,-6",
                        "ars_nouveau:glyph_gust,-4",
                        "ars_nouveau:glyph_launch,-4",
                        "ars_nouveau:glyph_leap,-4",
                        "ars_nouveau:glyph_lightning,-9",
                        "ars_nouveau:glyph_pull,-3",
                        "ars_nouveau:glyph_snare,-5",
                        "ars_nouveau:glyph_summon_undead,-9",
                        "ars_nouveau:glyph_summon_vex,-9",
                        "ars_nouveau:glyph_summon_wolves,-9",
                        "ars_nouveau:glyph_wind_burst,-4",
                        "ars_nouveau:glyph_wind_shear,-4",
                        "ars_nouveau:glyph_wither,-7",

                        "ars_elemental:glyph_cauterize,-4",
                        "ars_elemental:glyph_cavitate,-4",
                        "ars_elemental:glyph_charm,9",
                        "ars_elemental:glyph_conflagrate,-8",
                        "ars_elemental:glyph_geyser,-6",
                        "ars_elemental:glyph_discharge,-5",
                        "ars_elemental:glyph_mist,-2",
                        "ars_elemental:glyph_nullify_defense,-4",
                        "ars_elemental:glyph_oxidize,-5",
                        "ars_elemental:glyph_phantom_grasp,-3",
                        "ars_elemental:glyph_phantom_grasp,-3",
                        "ars_elemental:glyph_poison_spores,-4",
                        "ars_elemental:glyph_rage,-9",
                        "ars_elemental:glyph_slip_feet,-3",
                        "ars_elemental:glyph_spark,-4",
                        "ars_elemental:glyph_spike,-6",
                        "ars_elemental:glyph_summon_bee,-9",
                        "ars_elemental:glyph_summon_slime,-9",
                        "ars_elemental:glyph_water_jet,-5",
                        "ars_elemental:glyph_watery_grave,-7"
                ));

        BUILDER.pop();

    }


}
