package charting.data;

public record AssetId(String namespace, String value) {
    @Override
    public String toString() {
        return namespace + ": " + value;
    }
}
