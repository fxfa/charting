package charting.data;

import org.junit.jupiter.api.Test;

import java.time.Period;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PeriodIntervalTest {
    @Test
    void throwsIfPeriodIsNonPositive() {
        assertThrows(IllegalArgumentException.class, () -> new PeriodInterval(Period.ofDays(-1)));
        assertThrows(IllegalArgumentException.class, () -> new PeriodInterval(Period.ZERO));

        assertDoesNotThrow(() -> new PeriodInterval(Period.ofDays(1)));
    }
}