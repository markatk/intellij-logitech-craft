import com.fasterxml.jackson.annotation.JsonProperty;

public class CrownTouchData extends PluginData {
    @JsonProperty("device_id")
    public int DeviceId;

    @JsonProperty("unit_id")
    public int UnitId;

    @JsonProperty("feature_id")
    public int FeatureId;

    @JsonProperty("touch_state")
    public int TouchState;
}
