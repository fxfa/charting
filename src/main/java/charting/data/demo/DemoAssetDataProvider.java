package charting.data.demo;

import charting.data.AssetDataProvider;
import charting.data.AssetId;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class DemoAssetDataProvider implements AssetDataProvider {
    public static final AssetId USD = new AssetId("ISO_CURRENCY_CODE", "USD");
    public static final AssetId AAPL = new AssetId("US_TICKER_SYMBOL", "AAPL");
    public static final AssetId MSFT = new AssetId("US_TICKER_SYMBOL", "MSFT");
    public static final AssetId TSLA = new AssetId("US_TICKER_SYMBOL", "TSLA");

    private static final Set<AssetId> ASSET_IDS = Set.of(USD, AAPL, TSLA);

    private static final Map<AssetId, String> ASSET_NAMES = Map.of(USD, "US-Dollar",
            AAPL, "Apple Inc.", MSFT, "Microsoft Corp.", TSLA, "Tesla Inc.");

    @Override
    public Set<AssetId> getAssetIds() {
        return ASSET_IDS;
    }

    @Override
    public Optional<String> getAssetName(AssetId assetId) {
        return Optional.ofNullable(ASSET_NAMES.get(assetId));
    }
}
