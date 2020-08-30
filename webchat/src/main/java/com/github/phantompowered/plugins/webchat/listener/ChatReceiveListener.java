package com.github.phantompowered.plugins.webchat.listener;

import com.github.phantompowered.plugins.webchat.HtmlComponentSerializer;
import com.github.phantompowered.plugins.webchat.WebChat;
import com.github.phantompowered.proxy.api.chat.ChatMessageType;
import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.event.annotation.Listener;
import com.github.phantompowered.proxy.api.events.connection.ChatEvent;
import io.javalin.websocket.WsContext;

import java.util.Map;

public class ChatReceiveListener {

    @Listener
    public void handleChat(ChatEvent event) {
        if (event.getDirection() != ProtocolDirection.TO_CLIENT || (event.getType() != ChatMessageType.CHAT && event.getType() != ChatMessageType.SYSTEM)) {
            return;
        }

        Map<String, WsContext> sessions = event.getConnection().getProperty(WebChat.CONNECTION_SESSIONS_PROPERTY);
        if (sessions == null) {
            return;
        }

        String message = HtmlComponentSerializer.html().serialize(event.getMessage());
        // TODO doesn't work with translation/keybind components

        for (WsContext context : sessions.values()) {
            context.send(message);
        }
    }

}
