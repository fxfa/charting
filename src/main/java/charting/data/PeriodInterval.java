package charting.data;

import charting.util.Preconditions;

import java.time.Period;

public final class PeriodInterval implements Interval {
    private final Period period;

    public PeriodInterval(Period period) {
        Preconditions.checkArgument(!period.isZero());
        Preconditions.checkArgument(!period.isNegative());

        this.period = period;
    }

    public PeriodInterval(int days) {
        this(Period.ofDays(days));
    }

    public static PeriodInterval parse(String text) {
        return new PeriodInterval(Period.parse(text));
    }

    public Period getPeriod() {
        return period;
    }

    public String toString() {
        return period.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof PeriodInterval o)) {
            return false;
        }

        return period.equals(o.period);
    }

    @Override
    public int hashCode() {
        return period.hashCode();
    }
}