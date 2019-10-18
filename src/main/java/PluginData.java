import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class PluginData {
    @JsonProperty("message_type")
    public String MessageType;
}
