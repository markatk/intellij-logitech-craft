import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.components.BaseComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.sun.jna.platform.win32.Kernel32;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.util.concurrent.CompletionStage;

public class LogitechCraftService implements BaseComponent {
    private static final String GUID = "702656e3-d177-4475-b137-0757051d5c49";
    private static final URI WEB_SOCKET_URI = URI.create("ws://localhost:10134");

    private Logger logger;
    private WebSocket webSocket;
    private ObjectMapper objectMapper;

    private String sessionId;

    public LogitechCraftService() {
        logger = Logger.getInstance("LogitechCraft");
        objectMapper = new ObjectMapper();
    }

    @Override
    public void initComponent() {
        LogitechCraftService self = this;

        logger.info("Starting logitech craft plugin");

        HttpClient httpClient = HttpClient.newHttpClient();
        webSocket = httpClient.newWebSocketBuilder().buildAsync(WEB_SOCKET_URI, new WebSocket.Listener() {
            private String textBuffer;

            @Override
            public void onOpen(WebSocket webSocket) {
                WebSocket.Listener.super.onOpen(webSocket);

                logger.info("Connected to plugin manager");

                PluginRegistrationData data = new PluginRegistrationData();
                data.MessageType = "register";
                data.PluginGUID = GUID;
                data.ExecutableName = "idea64.exe";
                data.PID = getPID();

                // serialize and send data
                String text;

                try {
                    text = objectMapper.writeValueAsString(data);
                } catch (JsonProcessingException e) {
                    logger.error("Unable to serialize register data", e);

                    return;
                }

                webSocket.sendText(text, true);
            }

            @Override
            public CompletionStage<?> onText(WebSocket webSocket, CharSequence charData, boolean last) {
                if (last == false) {
                    if (textBuffer == null) {
                        textBuffer = charData.toString();
                    } else {
                        textBuffer += charData.toString();
                    }

                    return WebSocket.Listener.super.onText(webSocket, charData, false);
                }

                String text = "";

                if (textBuffer != null) {
                    text = textBuffer;
                    textBuffer = null;
                }

                text += charData.toString();

                logger.trace("Text: " + text);

                // get message type
                triggerEvent(text, "register_ack", PluginRegistrationStatusData.class, self::onRegistrationStatusEvent);
                triggerEvent(text, "enable_plugin", PluginEnableData.class, self::onEnableEvent);
                triggerEvent(text, "activate_plugin", PluginActivateData.class, self::onActivateEvent);
                triggerEvent(text, "deactivate_plugin", PluginDeactivateData.class, self::onDeactivateEvent);
                triggerEvent(text, "crown_touch_event", CrownTouchData.class, self::onCrownTouchEvent);
                triggerEvent(text, "crown_turn_event", CrownTurnData.class, self::onCrownTurnEvent);

                return WebSocket.Listener.super.onText(webSocket, charData, last);
            }

            @Override
            public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
                logger.info("Closed connection with plugin manager");

                return null;
            }

            @Override
            public void onError(WebSocket webSocket, Throwable error) {
                logger.error("Error: ", error);
            }
        }).join();
    }

    @Override
    public void disposeComponent() {
        logger.info("Closing logitech craft plugin");

        webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "").thenRun(() -> logger.info("Closed logitech craft plugin"));
    }

    protected void onCrownTouchEvent(CrownTouchData data) {
        logger.debug("Crown touch event");

        logger.info("Crown touched");
    }

    protected void onCrownTurnEvent(CrownTurnData data) {
        logger.debug("Crown turn event");

        logger.info("Crown turned: " + data.Delta);
    }

    protected void onEnableEvent(PluginEnableData data) {
        logger.info("Enable plugin");
    }

    protected void onActivateEvent(PluginActivateData data) {
        logger.info("Activate plugin");
    }

    protected void onDeactivateEvent(PluginDeactivateData data) {
        logger.info("Deactivate plugin");
    }

    protected void onRegistrationStatusEvent(PluginRegistrationStatusData data) {
        logger.info("Plugin registration status: " + data.Status);

        if (data.Status != 200) {
            logger.warn("Plugin registration failed: " + data.Status);

            return;
        }

        if (data.Enable == false) {
            logger.warn("Plugin is disabled");

            return;
        }

        sessionId = data.SessionId;

        // send tool change event
        ToolChangeData toolData = new ToolChangeData();
        toolData.MessageType = "tool_change";
        toolData.ResetOptions = true;
        toolData.SessionId = sessionId;
        toolData.ToolId = "Editor";

        // serialize and send data
        String text;

        try {
            text = objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            logger.error("Unable to serialize tool change data", e);

            return;
        }

        webSocket.sendText(text, true);
    }

    private <T> void triggerEvent(String text, String eventName, Class<T> cls, EventFunction<T> function) {
        if (text.contains("\"" + eventName + "\"") == false) {
            return;
        }

        T data;

        try {
            data = objectMapper.readValue(text, cls);
        } catch (IOException e) {
            logger.error("Unable to deserialize websocket data", e);

            return;
        }

        function.trigger(data);
    }

    private long getPID() {
        return Kernel32.INSTANCE.GetCurrentProcessId();
    }

    @FunctionalInterface
    private interface EventFunction<T> {
        void trigger(T data);
    }
}
