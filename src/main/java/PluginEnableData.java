import com.fasterxml.jackson.annotation.JsonProperty;

public class PluginEnableData extends PluginData {
    @JsonProperty("enabled")
    public boolean Enabled;
}
