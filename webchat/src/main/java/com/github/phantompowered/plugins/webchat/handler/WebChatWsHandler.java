package com.github.phantompowered.plugins.webchat.handler;

import com.github.phantompowered.plugins.webchat.WebChat;
import com.github.phantompowered.proxy.api.connection.ServiceConnection;
import com.github.phantompowered.proxy.api.connection.ServiceConnector;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class WebChatWsHandler implements WsMessageHandler {

    private final ServiceRegistry registry;

    public WebChatWsHandler(ServiceRegistry registry) {
        this.registry = registry;
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
                ctx.send("{\"success\":false,\"message\":\"Connection with the name '" + name + "' not found\"}");
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

            ctx.send("{\"success\":true,\"message\":\"Successfully logged in\"}");

            return;
        }

        String message = ctx.message();

        if (message.length() == 0) {
            ctx.send("{\"success\":false,\"message\":\"No message has been provided\"}");
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
