import com.fasterxml.jackson.annotation.JsonProperty;

public class PluginActivateData extends PluginData {
    @JsonProperty("session_id")
    public String SessionId;
}
