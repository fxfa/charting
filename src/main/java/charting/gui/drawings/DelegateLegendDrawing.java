package charting.gui.drawings;

import charting.gui.chart.ChartLegendString;
import charting.gui.chart.Drawable;
import charting.gui.chart.DrawingContext;
import charting.gui.chart.LegendDrawing;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.*;

public abstract class DelegateLegendDrawing implements LegendDrawing {
    private final ObjectProperty<Collection<LegendDrawing>> delegates =
            new SimpleObjectProperty<>(Collections.emptyList());

    protected DelegateLegendDrawing() {
    }

    @SafeVarargs
    protected <L extends LegendDrawing> DelegateLegendDrawing(L... delegates) {
        setDelegates(delegates);
    }

    protected DelegateLegendDrawing(Collection<? extends LegendDrawing> delegates) {
        setDelegates(delegates);
    }

    protected final Iterable<LegendDrawing> getDelegates() {
        return delegates.get();
    }

    protected final ObjectProperty<Collection<LegendDrawing>> delegatesProperty() {
        return delegates;
    }

    protected final void setDelegates(Collection<? extends LegendDrawing> delegates) {
        this.delegates.set(List.copyOf(delegates));
    }

    @SafeVarargs
    protected final <L extends LegendDrawing> void setDelegates(L... drawings) {
        this.delegates.set(Arrays.asList(drawings));
    }

    @Override
    public List<Drawable> getDrawables(DrawingContext drawingContext) {
        if (getDelegates() == null) {
            return Collections.emptyList();
        }

        List<Drawable> drawables = new LinkedList<>();

        for (LegendDrawing d : getDelegates()) {
            drawables.addAll(d.getDrawables(drawingContext));
        }

        return drawables;
    }

    @Override
    public List<ChartLegendString> getLegend(double x) {
        if (getDelegates() == null) {
            return Collections.emptyList();
        }

        List<ChartLegendString> strings = new LinkedList<>();

        for (LegendDrawing d : getDelegates()) {
            strings.addAll(d.getLegend(x));
        }

        return strings;
    }
}
