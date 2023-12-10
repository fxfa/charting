package charting.data;

public record TickerSourceId(String namespace, String value) {
    @Override
    public String toString() {
        return namespace + ": " + value;
    }
}
