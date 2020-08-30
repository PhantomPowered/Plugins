package com.github.phantompowered.plugins.webchat.listener;

import com.github.phantompowered.plugins.webchat.WebChat;
import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.event.annotation.Listener;
import com.github.phantompowered.proxy.api.events.connection.ChatEvent;
import io.javalin.websocket.WsContext;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;

import java.util.Map;

public class ChatReceiveListener {

    @Listener
    public void handleChat(ChatEvent event) {
        if (event.getDirection() != ProtocolDirection.TO_CLIENT) {
            return;
        }

        Map<String, WsContext> sessions = event.getConnection().getProperty(WebChat.CONNECTION_SESSIONS_PROPERTY);
        if (sessions == null) {
            return;
        }

        String message = PlainComponentSerializer.plain().serialize(event.getMessage()); // TODO serialize as html to support colors/hover events
        // TODO doesn't work with translation components

        for (WsContext context : sessions.values()) {
            context.send(message);
        }
    }

}
