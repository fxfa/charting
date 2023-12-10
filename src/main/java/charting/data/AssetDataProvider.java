package charting.data;

import java.util.Optional;
import java.util.Set;

public interface AssetDataProvider {
    Set<AssetId> getAssetIds();

    Optional<String> getAssetName(AssetId assetId);
}
