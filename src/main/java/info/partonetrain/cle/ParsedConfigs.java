package info.partonetrain.cle;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.*;

public class ParsedConfigs {
    //are these overengineered? probably.
    public static DamageMod modifiedDamageDealt;
    public static DamageMod modifiedDamageReceived;

    public static List<String> lowPriorityBuildings = new ArrayList<>(); //client only

    public static Map<ResourceLocation, Integer> spellEffectReputations = new HashMap<>();

    //public static List<MinDistanceConfig> minDistanceConfigs = new ArrayList<>();
    public static List<ResourceKey<Structure>> structuresToAvoid = new ArrayList<>();

    public static void parseDamageMods(){
        modifiedDamageDealt = DamageMod.parse(CleConfig.MODIFIED_DAMAGE_DEALT.get());
        modifiedDamageReceived = DamageMod.parse(CleConfig.MODIFIED_DAMAGE_RECEIVED.get());
    }

    public static void parseStructuresToAvoid(){
        for(String s : CleConfig.STRUCTURES_TO_AVOID.get()){
            ResourceKey<Structure> structureResourceKey = ResourceKey.create(Registries.STRUCTURE, ResourceLocation.parse(s));
            structuresToAvoid.add(structureResourceKey);
        }
    }

    /* MILLENAIRE ALREADY HAS CODE FOR THIS
    public static void parseMinDistances(){
        for(String s : CleConfig.MIN_DISTANCE_FROM_STRUCTURES.get()){
            minDistanceConfigs.add(MinDistanceConfig.parse(s));
        }
    }

     */

    public record DamageMod(Operation op, float value){

        public static DamageMod parse(String s){
            char firstChar = s.charAt(0);
            Operation op = Operation.parse(firstChar);
            int val = Integer.parseInt(s.replace(String.valueOf(firstChar), ""));
            return new DamageMod(op, val);
        }
        
        public float apply(float originalDamage){
            return op.apply(originalDamage, value);
        }
    }

    public enum Operation {
        ADDITION('+') {
            @Override
            public float apply(float x, float y) {
                return x + y;
            }
        },
        SUBTRACTION('-') {
            @Override
            public float apply(float x, float y) {
                return x - y;
            }
        },
        MULTIPLICATION('*') {
            @Override
            public float apply(float x, float y) {
                return x * y;
            }
        },
        DIVISION('/') {
            @Override
            public float apply(float x, float y) {
                if (y == 0) {
                    throw new ArithmeticException("You can't divide by zero bruh.");
                }
                return x / y;
            }
        };

        private final char symbol;

        Operation(char symbol) {
            this.symbol = symbol;
        }

        public abstract float apply(float x, float y);

        @Override
        public String toString() {
            return String.valueOf(this.symbol);
        }

        public static Operation parse(char c){
            return switch(c){
                case '+' -> ADDITION;
                case '-' -> SUBTRACTION;
                case '*', 'x' -> MULTIPLICATION;
                case '/' -> DIVISION;
                default -> throw new IllegalStateException("Unexpected value: " + c);
            };
        }
    }

    public static void parseLowPriorityBuildings(){
        String cfg = CleConfig.SORT_CONSTRUCTIONS_PANEL.get();
        String[] split = cfg.split(",");
        lowPriorityBuildings.addAll(Arrays.asList(split));
    }

    public static int parseSpells(){
        int parsed = 0;
        List<String> cfg = CleConfig.SPELL_EFFECT_REPUTATIONS.get();
        for(String s : cfg){
            String[] split = s.split(",");
            spellEffectReputations.put(ResourceLocation.parse(split[0]), Integer.parseInt(split[1]));
            parsed++;
        }

        return parsed;
    }

    /* MILLENAIRE ALREADY HAS CODE FOR THIS
    //I tried using Mojang's datafixer Either<R, L> here but couldn't figure it out
    public record MinDistanceConfig(TagKey<Structure> structureTag, ResourceKey<Structure> structure, int minDistance){

        public boolean isTag(){
            return structureTag != null;
        }

        //this assumes everything is in the right format...
        public static MinDistanceConfig parse(String s){
            String[] splits = s.split(",");
            String id = splits[0];
            String dist = splits[1];
            int parsedDist = Integer.parseInt(dist);

            if(id.startsWith("#")){
                TagKey<Structure> structureTag = TagKey.create(Registries.STRUCTURE, ResourceLocation.parse(id.replace("#","")));
                return new MinDistanceConfig(structureTag, null, parsedDist);
            }
            else{
                ResourceKey<Structure> structureKey = ResourceKey.create(Registries.STRUCTURE, ResourceLocation.parse(id));
                return new MinDistanceConfig(null, structureKey, parsedDist);
            }
        }
    }
     */

}
