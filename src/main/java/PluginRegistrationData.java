import com.fasterxml.jackson.annotation.JsonProperty;

public class PluginRegistrationData extends PluginData {
    public long PID;

    @JsonProperty("application_version")
    public String ApplicationVersion;

    @JsonProperty("execName")
    public String ExecutableName;

    @JsonProperty("plugin_guid")
    public String PluginGUID;
}
