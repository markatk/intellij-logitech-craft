import com.fasterxml.jackson.annotation.JsonProperty;

public class CrownTurnData extends PluginData {
    @JsonProperty("device_id")
    public int DeviceId;

    @JsonProperty("unit_id")
    public int UnitId;

    @JsonProperty("feature_id")
    public int FeatureId;

    @JsonProperty("task_id")
    public String TaskId;

    @JsonProperty("task_options")
    public TaskOptionsData TaskOptions;

    @JsonProperty("delta")
    public int Delta;

    @JsonProperty("ratchet_delta")
    public int RatchetDelta;

    @JsonProperty("time_stamp")
    public int TimeStamp;

    public static class TaskOptionsData {
        @JsonProperty("current_tool")
        public String CurrentTool;

        @JsonProperty("current_tool_option")
        public String CurrentToolOption;
    }
}
