package info.partonetrain.cle;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CleConfig {
    private static final ModConfigSpec.Builder BUILDER;
    public static final ModConfigSpec SPEC;

    public static ModConfigSpec.BooleanValue PREVENT_HOPPER_INTERACTIONS;
    public static ModConfigSpec.BooleanValue USE_FOOD_COMPONENTS;
    public static ModConfigSpec.BooleanValue YOGURT_GIVES_BOWL;
    public static ModConfigSpec.BooleanValue DRINK_GIVES_BOTTLE;
    public static ModConfigSpec.BooleanValue ALTERNATIVE_INUIT_TRIDENT;
    public static ModConfigSpec.BooleanValue ALTERNATIVE_MACES;
    public static ModConfigSpec.IntValue MAX_MIN_COORDINATE;
    public static ModConfigSpec.BooleanValue COMPOST_DATAPACK;
    public static ModConfigSpec.ConfigValue<String> MODIFIED_DAMAGE_DEALT;
    public static ModConfigSpec.ConfigValue<String> MODIFIED_DAMAGE_RECEIVED;

    public static ModConfigSpec.BooleanValue ATTACKS_MILLAGERS;
    public static ModConfigSpec.BooleanValue DESPAWNS_IN_MILLAGE;

    static {
        BUILDER = new ModConfigSpec.Builder();
        registerConfig(BUILDER);
        SPEC = BUILDER.build();
    }

    public static void registerConfig(ModConfigSpec.Builder builder) {

        PREVENT_HOPPER_INTERACTIONS = BUILDER
                .comment("If true, prevent all hopper interactions with Locked Chests")
                .comment("Locked Chests should not work with standard modded item transfers (i.e., Create chutes) regardless of this config, because they lack capabilities")
                .comment("They only work with hoppers by default due to inheriting vanilla chest code")
                .comment("However, other modded hoppers may or may not be affected by this - it depends on how they were implemented")
                .define("Prevent Hopper Interactions", false);

        USE_FOOD_COMPONENTS = BUILDER
                .comment("If true, Millenaire foods will use vanilla food components")
                .comment("This can enhance compatibility with mods like AppleSkin")
                .comment("However, other mods that utilize this component probably will not correctly account for the food's durability")
                .define("Use Food Components", false);

        YOGURT_GIVES_BOWL = BUILDER
                .comment("If true, Yogurt will give the player a bowl after it is eaten (only in survival)")
                .define("Yogurt Gives Bowl", false); //implemented, untested

        DRINK_GIVES_BOTTLE = BUILDER
                .comment("If true, all drinks will give the player a glass bottle after it is drunk (only in survival)")
                .define("Drink Gives Bottle", false);

        ALTERNATIVE_INUIT_TRIDENT = BUILDER
                .comment("If true, any Inuit Tridents obtained by survival mode players will behave like a Minecraft trident instead of a sword")
                .comment("WARNING: this auto-replaces millenaire:inuittrident in player inventories with cle:inuittrident")
                .define("Alternative Inuit Trident", false); //NYI

        ALTERNATIVE_MACES = BUILDER
                .comment("If true, any Mayan of Byzantine Maces obtained by survival mode players will behave like a Minecraft mace instead of a sword")
                .comment("WARNING: this auto-replaces millenaire:mayan_mace and millenaire:byzantine_mace in player inventories with the Cle versions")
                .define("Alternative Maces", false); //NYI

        MAX_MIN_COORDINATE = BUILDER
                .comment("The maximum and minimum x/z coordinates that Millenaire villages can spawn in.")
                .comment("Only use positive values. The minimum is calculated automatically by multiplying the max by -1.")
                .comment("Set to 0 for no max")
                .defineInRange("Max & Min Coordinates", 12_550_821, 0, Integer.MAX_VALUE); //implemented, untested

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
    }


}
