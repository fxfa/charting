package charting.util;

public record Range(double start, double end) {
    public static Range NAN = new Range(Double.NaN, Double.NaN);

    public double getLength() {
        return end - start;
    }

    public double getCenter() {
        return start + getLength() / 2;
    }

    public Range scaled(double factor) {
        return scaled(factor, getCenter());
    }

    public Range scaled(double factor, double pivot) {
        return new Range((start - pivot) * factor + pivot, (end - pivot) * factor + pivot);
    }

    public Range translated(double dist) {
        return new Range(start + dist, end + dist);
    }

    /**
     * Converts a position from this source to a target.
     */
    public double toTargetPosition(double position, Range target) {
        if (target == null) {
            return Double.NaN;
        }

        return target.getLength() / getLength() * (position - start()) + target.start();
    }

    /**
     * Converts a length from this source to a target.
     */
    public double toTargetLength(double sourceLength, Range target) {
        if (target == null) {
            return Double.NaN;
        }

        return target.getLength() / getLength() * sourceLength;
    }

    public boolean isPositive() {
        return start < end;
    }
}
