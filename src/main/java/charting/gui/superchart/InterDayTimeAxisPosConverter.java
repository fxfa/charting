package charting.gui.superchart;

import charting.data.Candle;
import charting.timeline.Timeline;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 *  Uses a timeline to do a day based conversion:
 *  If the index/instant is inside the timelines range it will use selecting/ranking operations to do the conversion.
 *  If the index/instant is outside the timelines range it will calculate the result based on the day difference
 *  between the start/end of the timeline.
 */
public class InterDayTimeAxisPosConverter implements TimeAxisPosConverter {
    private final Timeline<? extends Candle> timeline;

    public InterDayTimeAxisPosConverter(Timeline<? extends Candle> timeline) {
        this.timeline = timeline;
    }

    @Override
    public long toIndex(Instant i) {
        if (timeline.isEmpty()) {
            return ChronoUnit.DAYS.between(Instant.EPOCH, i);
        }

        if (i.isBefore(timeline.first().timestamp())) {
            return ChronoUnit.DAYS.between(timeline.first().timestamp(), i);
        }
        if (i.isAfter(timeline.last().timestamp())) {
            return timeline.size() - 1 + ChronoUnit.DAYS.between(timeline.last().timestamp(), i);
        }

        return timeline.floorIndexOf(i);
    }

    @Override
    public Instant toInstant(long i) {
        if (timeline.isEmpty()) {
            return Instant.EPOCH.plus(i, ChronoUnit.DAYS);
        }

        if (i < 0) {
            return timeline.first().timestamp().minus(-i, ChronoUnit.DAYS);
        }
        if (i >= timeline.size()) {
            return timeline.last().timestamp().plus(i + 1 - timeline.size(), ChronoUnit.DAYS);
        }

        return timeline.get((int) i).timestamp();
    }
}
