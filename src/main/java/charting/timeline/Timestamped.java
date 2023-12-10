package charting.timeline;

import java.time.Instant;

public record Timestamped<T>(Instant timestamp, T value) {
}
