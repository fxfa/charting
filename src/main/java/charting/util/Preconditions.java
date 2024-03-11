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

    public static void checkIndex(int i, int length, String message) {
        if (i < 0 || i >= length) {
            throw new IndexOutOfBoundsException(message);
        }
    }

    public static void checkIndex(int i, int length) {
        if (i < 0 || i >= length) {
            throw new IndexOutOfBoundsException(i);
        }
    }

    private Preconditions() {
    }
}
