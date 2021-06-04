package com.github.phantompowered.plugins.webchat.handler;

import com.github.derrop.documents.Documents;
import com.github.phantompowered.plugins.webchat.ChatMode;
import com.github.phantompowered.plugins.webchat.WebChat;
import com.github.phantompowered.plugins.webchat.WebCommandSender;
import com.github.phantompowered.proxy.api.command.CommandMap;
import com.github.phantompowered.proxy.api.command.exception.CommandExecutionException;
import com.github.phantompowered.proxy.api.command.exception.PermissionDeniedException;
import com.github.phantompowered.proxy.api.command.result.CommandResult;
import com.github.phantompowered.proxy.api.command.sender.CommandSender;
import com.github.phantompowered.proxy.api.connection.ServiceConnection;
import com.github.phantompowered.proxy.api.connection.ServiceConnector;
import com.github.phantompowered.proxy.api.network.exception.CancelProceedException;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class WebChatWsHandler implements WsMessageHandler {

    private final WebChat webChat;
    private final ServiceRegistry registry;
    private final boolean allowSending;

    public WebChatWsHandler(WebChat webChat, ServiceRegistry registry, boolean allowSending) {
        this.webChat = webChat;
        this.registry = registry;
        this.allowSending = allowSending;
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) {
        ServiceConnection connection = ctx.attribute(WebChat.SESSION_ATTRIBUTE);
        if (connection == null) {
            String name = ctx.message();
            if (name.isEmpty()) {
                ctx.send("{\"success\":false,\"message\":\"No name provided\"}");
                ctx.session.close();
                return;
            }

            connection = this.registry.getProviderUnchecked(ServiceConnector.class).getOnlineClients().stream()
                    .filter(filter -> filter.getName() != null && filter.getName().equalsIgnoreCase(name))
                    .findFirst()
                    .orElse(null);

            if (connection == null) {
                ctx.send(Documents.newDocument().append("success", false).append("message", "Connection with the name '" + name + "' not found").toString());
                ctx.session.close();
                return;
            }

            ctx.attribute(WebChat.SESSION_ATTRIBUTE, connection);

            Map<String, WsContext> sessions = connection.getProperty(WebChat.CONNECTION_SESSIONS_PROPERTY);
            if (sessions == null) {
                sessions = new HashMap<>();
                connection.setProperty(WebChat.CONNECTION_SESSIONS_PROPERTY, sessions);
            }

            sessions.put(ctx.getSessionId(), ctx);
            ctx.attribute("mode", ChatMode.ALL);

            ctx.send(Documents.newDocument()
                    .append("success", true)
                    .append("message", "Successfully logged in as '" + name + "'")
                    .append("selectedMode", ChatMode.ALL)
                    .append("modes", ChatMode.values())
                    .toString());

            this.webChat.sendHistory(Collections.singleton(ctx), connection);

            return;
        }


        JsonObject input = JsonParser.parseString(ctx.message()).getAsJsonObject();
        if (!input.has("action")) {
            return;
        }
        String action = input.get("action").getAsString();
        String value = input.get("value").getAsString();

        switch (action) {
            case "chat":
                this.chat(ctx, connection, value);
                break;

            case "changeMode":
                ChatMode mode = ChatMode.valueOf(value);
                ctx.attribute("mode", mode);
                ctx.send(Documents.newDocument().append("selectedMode", mode).toString());
                break;

            default:
                ctx.send(Documents.newDocument().append("success", false).append("message", "Unknown action: " + action));
                break;
        }
    }

    private void chat(WsContext ctx, ServiceConnection connection, String message) {
        if (!this.allowSending) {
            ctx.send("{\"success\":false,\"message\":\"Sending messages through the WebChat has been disabled in the config\"}");
            return;
        }

        if (message.length() == 0) {
            ctx.send("{\"success\":false,\"message\":\"No message has been provided\"}");
            return;
        }

        if (message.contains(" && ")) {
            for (String line : message.split(" && ")) {
                this.chat(ctx, connection, line);
            }
            return;
        }

        if (message.toLowerCase().startsWith(CommandMap.INGAME_PREFIX)) {
            CommandSender sender = new WebCommandSender(this.webChat, ctx);

            try {
                CommandMap commandMap = this.registry.getProviderUnchecked(CommandMap.class);
                if (commandMap.process(sender, message.substring(CommandMap.INGAME_PREFIX.length())) != CommandResult.NOT_FOUND) {
                    return;
                }

                sender.sendMessage("Command not found");
            } catch (final CommandExecutionException | PermissionDeniedException ex) {
                sender.sendMessage("Unable to process command: " + ex.getMessage());
            }

            return;
        }

        if (message.length() > 100) {
            ctx.send("{\"success\":false,\"message\":\"Message too long (max 100, was " + message.length() + ")\"}");
            return;
        }

        connection.chat(message);
        ctx.send("{\"success\":true,\"message\":\"Message successfully sent to the server\"}");
    }

}
