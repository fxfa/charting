package charting.data;

public record Ticker(AssetId sourceAssetId, AssetId targetAssetId, TickerSourceId tickerSourceId) {
    @Override
    public String toString() {
        return String.format("(%s) -> (%s)/(%s)", tickerSourceId, sourceAssetId, targetAssetId);
    }
}
