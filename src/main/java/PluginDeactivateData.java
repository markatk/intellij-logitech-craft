import com.fasterxml.jackson.annotation.JsonProperty;

public class PluginDeactivateData extends PluginData {
    @JsonProperty("session_id")
    public String SessionId;
}
