package charting.gui.superchart;

import charting.gui.util.NodeLoader;
import charting.util.Preconditions;
import charting.util.Range;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.time.temporal.ChronoUnit.*;

public class TimeAxis extends SuperChartAxis {
    private final static List<ChronoUnit> UNITS = List.of(YEARS, MONTHS, DAYS, HOURS, MINUTES, SECONDS);

    private final ObjectProperty<TimeAxisPosConverter> timeAxisPosConverter =
            new SimpleObjectProperty<>(TimeAxisPosConverter.of(Instant::getEpochSecond, Instant::ofEpochSecond));
    private final ObjectProperty<DateTimeFormatter> markFormatter =
            new SimpleObjectProperty<>(DateTimeFormatter.ofPattern("EE MMM dd ''yy"));

    private final TreeMap<Double, Mark<ZonedDateTime>> marks = new TreeMap<>();

    private final ZoneId zoneId = ZoneId.of("UTC");

    private final static double GAP = 125;
    private double gap;

    private double anchor;

    private double mouseDownX = Double.NaN;
    private Range mouseDownAxis;

    private final Font font = new Font(15);

    public TimeAxis() {
        NodeLoader.loadExisting(this);

        ChangeListener<Object> calculateListener = (obs, oldVal, newVal) -> calculateMarks();
        widthProperty().addListener(calculateListener);
        heightProperty().addListener(calculateListener);
        axisProperty().addListener(calculateListener);
        cursorMarkPositionProperty().addListener(calculateListener);
        timeAxisPosConverter.addListener(calculateListener);
        markFormatter.addListener(calculateListener);

        addEventHandler(MouseEvent.MOUSE_PRESSED, this::onMousePressed);
        addEventHandler(MouseEvent.MOUSE_DRAGGED, this::onMouseDragged);
        addEventHandler(ScrollEvent.SCROLL, this::onScroll);
    }

    public TimeAxisPosConverter getTimeAxisPosConverter() {
        return timeAxisPosConverter.get();
    }

    public ObjectProperty<TimeAxisPosConverter> timeAxisPosConverterProperty() {
        return timeAxisPosConverter;
    }

    public void setTimeAxisPosConverter(TimeAxisPosConverter timeAxisPosConverter) {
        this.timeAxisPosConverter.set(timeAxisPosConverter);
    }

    public DateTimeFormatter getMarkFormatter() {
        return markFormatter.get();
    }

    public ObjectProperty<DateTimeFormatter> markFormatterProperty() {
        return markFormatter;
    }

    public void setMarkFormatter(DateTimeFormatter markFormatter) {
        this.markFormatter.set(markFormatter);
    }

    private void onMousePressed(MouseEvent e) {
        e.consume();

        mouseDownX = e.getX();
        mouseDownAxis = getAxis();
    }

    private void onMouseDragged(MouseEvent e) {
        e.consume();

        if (mouseDownAxis == null) {
            return;
        }

        double f = Math.max(1, getCanvas().getWidth() - mouseDownX) / Math.max(1, getCanvas().getWidth() - e.getX());
        double start = mouseDownAxis.end() - mouseDownAxis.getLength() * f;
        setAxis(new Range(start, mouseDownAxis.end()));
    }

    private void onScroll(ScrollEvent e) {
        e.consume();

        if (getAxis() == null) {
            return;
        }

        setAxis(getAxis().scaled(e.getDeltaY() > 0 ? 1 / 1.05 : e.getDeltaY() < 0 ? 1.05 : 1, getAxis().end()));
    }

    private double getScale() {
        return getAxis() == null ? Double.NaN : (getCanvas().getWidth() / getAxis().getLength());
    }

    private double getTranslate() {
        return getAxis() == null ? Double.NaN : -getAxis().start();
    }

    public void setAnchorYear(Instant instant) {
        anchor = getTimeAxisPosConverter().toIndex(instant);
    }

    private void calculateMarks() {
        if (getCanvas().getWidth() == 0 || getAxis() == null) {
            return;
        }

        Preconditions.checkArgument(getAxis().isPositive());

        gap = GAP / getScale();

        marks.clear();
        Mark<ZonedDateTime> m = floorMark(anchor, YEARS);
        marks.put(m.pos(), m);

        double start = -getTranslate();
        double end = getCanvas().getWidth() / getScale() - getTranslate();
        loadRightMarks(start, end);
        loadLeftMarks(start, end);
        redraw();
    }

    private void loadRightMarks(double start, double end) {
        for (ChronoUnit u : UNITS) {
            if (u == YEARS) {
                Mark<ZonedDateTime> current = marks.lastEntry().getValue();
                while (current.pos() <= end) {
                    Mark<ZonedDateTime> next = ceilMark(current.pos() + gap, YEARS);
                    marks.put(next.pos(), next);
                    current = next;
                }
            } else {
                Iterator<Mark<ZonedDateTime>> it = new ArrayList<>(marks.values()).iterator();
                Mark<ZonedDateTime> c = it.next();
                while (it.hasNext()) {
                    if (c.pos() > end) {
                        break;
                    }

                    Mark<ZonedDateTime> n = it.next();
                    if (n.pos() >= start) {
                        loadMarks(c, n, u, start, end);
                    }
                    c = n;
                }
            }
        }
    }

    private void loadLeftMarks(double start, double end) {
        for (ChronoUnit u : UNITS) {
            if (u == YEARS) {
                Mark<ZonedDateTime> current = marks.firstEntry().getValue();
                while (current.pos() >= start) {
                    Mark<ZonedDateTime> previous = floorMark(current.pos() - gap, YEARS);
                    marks.put(previous.pos(), previous);
                    current = previous;
                }
            } else {
                Iterator<Mark<ZonedDateTime>> it = new ArrayList<>(marks.descendingMap().values()).iterator();
                Mark<ZonedDateTime> c = it.next();
                while (it.hasNext()) {
                    if (c.pos() < start) {
                        break;
                    }

                    Mark<ZonedDateTime> n = it.next();
                    if (n.pos() <= end) {
                        loadMarks(c, n, u, start, end);
                    }
                    c = n;
                }
            }
        }
    }

    private void loadMarks(Mark<ZonedDateTime> from, Mark<ZonedDateTime> to,
                           ChronoUnit unit, double start, double end) {
        Mark<ZonedDateTime> lastMark = from;

        if (from.pos() < to.pos()) {
            while (lastMark.pos() + 2 * gap <= to.pos() && lastMark.pos() <= end) {
                Mark<ZonedDateTime> mark = ceilMark(lastMark.pos() + gap, unit);
                if (mark.pos() + gap > to.pos()) {
                    break;
                }

                marks.put(mark.pos(), mark);
                lastMark = mark;
            }
        } else {
            while (lastMark.pos() - 2 * gap >= to.pos() && lastMark.pos() >= start) {
                Mark<ZonedDateTime> mark = floorMark(lastMark.pos() - gap, unit);
                if (mark.pos() - gap < to.pos()) {
                    break;
                }

                marks.put(mark.pos(), mark);
                lastMark = mark;
            }
        }
    }

    private Mark<ZonedDateTime> floorMark(double pos, ChronoUnit unit) {
        ZonedDateTime t = getTimeAxisPosConverter().toInstant((long) Math.floor(pos)).atZone(zoneId);
        long p = getTimeAxisPosConverter().toIndex(floorTo(t, unit).toInstant());
        t = getTimeAxisPosConverter().toInstant(p).atZone(zoneId);
        return new Mark<>(t, getMarkString(t, unit), p);
    }

    private Mark<ZonedDateTime> roundMark(double pos, ChronoUnit unit) {
        ZonedDateTime t = getTimeAxisPosConverter().toInstant(Math.round(pos)).atZone(zoneId);
        long p = getTimeAxisPosConverter().toIndex(floorTo(t, unit).toInstant());
        t = getTimeAxisPosConverter().toInstant(p).atZone(zoneId);
        return new Mark<>(t, getMarkString(t, unit), p);
    }

    private Mark<ZonedDateTime> ceilMark(double pos, ChronoUnit unit) {
        ZonedDateTime t = getTimeAxisPosConverter().toInstant((long) Math.ceil(pos)).atZone(zoneId);
        long p = getTimeAxisPosConverter().toIndex(ceilTo(t, unit).toInstant());
        t = getTimeAxisPosConverter().toInstant(p).atZone(zoneId);
        return new Mark<>(t, getMarkString(t, unit), p);
    }

    private String getMarkString(ZonedDateTime t, ChronoUnit unit) {
        if (unit == YEARS) {
            return String.valueOf(t.getYear());
        }
        if (unit == MONTHS) {
            return t.getMonth().getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault());
        }
        if (unit == DAYS) {
            return String.valueOf(t.getDayOfMonth());
        }
        if (unit == HOURS || unit == MINUTES) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            return t.format(formatter);
        }
        if (unit == SECONDS) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            return t.format(formatter);
        }
        throw new IllegalStateException();
    }

    private void redraw() {
        redrawMarks();
        redrawCursorMark();
    }

    private void redrawMarks() {
        GraphicsContext gc = getCanvas().getGraphicsContext2D();
        gc.clearRect(0, 0, getCanvas().getWidth(), getCanvas().getHeight());

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(font);
        gc.setFill(getMarkColor());
        for (Mark<ZonedDateTime> m : marks.values()) {
            if (m.pos() > getCanvas().getWidth() / getScale() - getTranslate() + gap) {
                break;
            }

            if (m.pos() >= -getTranslate() - gap) {
                gc.fillText(m.string(), (m.pos() + getTranslate()) * getScale(), getCanvas().getHeight() / 2);
            }
        }
    }

    private void redrawCursorMark() {
        if (Double.isNaN(getCursorMarkPosition())) {
            return;
        }

        GraphicsContext gc = getCanvas().getGraphicsContext2D();

        Mark<ZonedDateTime> mark = roundMark(-getTranslate() + getCursorMarkPosition() / getScale(), SECONDS);
        String text = mark.item().format(getMarkFormatter());

        Text t = new Text(text);
        t.setFont(font);
        t.setTextAlignment(TextAlignment.CENTER);
        t.setTextOrigin(VPos.CENTER);
        double w = t.getLayoutBounds().getWidth();
        double h = t.getLayoutBounds().getHeight();
        double padding = (getHeight() - h) / 2;

        gc.setFill(getCursorMarkBackgroundColor());
        gc.fillRoundRect((mark.pos() + getTranslate()) * getScale() - w / 2 - padding, 1,
                w + padding * 2, getCanvas().getHeight() - 1, 12, 12);
        gc.setFill(getMarkColor());
        gc.fillText(text, (mark.pos() + getTranslate()) * getScale(), getCanvas().getHeight() / 2);
    }

    private ZonedDateTime ceilTo(ZonedDateTime dateTime, ChronoUnit unit) {
        ZonedDateTime t = floorTo(dateTime, unit);
        return t.equals(dateTime) ? t : t.plus(1, unit);
    }

    private ZonedDateTime floorTo(ZonedDateTime dateTime, ChronoUnit unit) {
        if (unit == YEARS) {
            return Year.of(dateTime.getYear()).atDay(1).atStartOfDay(dateTime.getZone());
        }

        if (unit == MONTHS) {
            return YearMonth.of(dateTime.getYear(), dateTime.getMonth())
                    .atDay(1).atStartOfDay(dateTime.getZone());
        }

        return dateTime.truncatedTo(unit);
    }
}
