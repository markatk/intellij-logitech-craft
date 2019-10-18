import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PluginRegistrationStatusData extends PluginData {
    @JsonProperty("sequence_id")
    public int SequenceId;

    @JsonProperty("session_id")
    public String SessionId;

    @JsonProperty("status")
    public int Status;

    @JsonProperty("enable")
    public boolean Enable;

    @JsonProperty("flags")
    public List<String> Flags;
}
