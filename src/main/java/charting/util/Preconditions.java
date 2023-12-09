package charting.util;

public final class Preconditions {
    public static void checkArgument(boolean argCondition, String message) {
        if (!argCondition) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void checkArgument(boolean argCondition) {
        checkArgument(argCondition, null);
    }

    private Preconditions() {
    }
}
