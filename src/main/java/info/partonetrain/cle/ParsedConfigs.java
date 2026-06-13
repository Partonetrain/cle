package info.partonetrain.cle;

public class ParsedConfigs {
    public static DamageMod modifiedDamageDealt;
    public static DamageMod modifiedDamageReceived;

    public static void parseDamageMods(){
        modifiedDamageDealt = DamageMod.parse(CleConfig.MODIFIED_DAMAGE_DEALT.get());
        modifiedDamageReceived = DamageMod.parse(CleConfig.MODIFIED_DAMAGE_RECEIVED.get());
    }

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

}
