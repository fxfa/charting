package charting.gui.superchart.indicatorspane;

public record Indicator(String name) {
    @Override
    public String toString() {
        return name;
    }
}
