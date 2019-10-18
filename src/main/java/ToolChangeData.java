import com.fasterxml.jackson.annotation.JsonProperty;

public class ToolChangeData extends PluginData {
    @JsonProperty("reset_options")
    public boolean ResetOptions;

    @JsonProperty("session_id")
    public String SessionId;

    @JsonProperty("tool_id")
    public String ToolId;
}
