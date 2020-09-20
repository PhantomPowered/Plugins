package com.github.phantompowered.plugins.webchat.listener;

import com.github.phantompowered.plugins.webchat.ChatMode;
import com.github.phantompowered.plugins.webchat.HistoricalMessage;
import com.github.phantompowered.plugins.webchat.WebChat;
import com.github.phantompowered.proxy.api.chat.ChatMessageType;
import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.connection.ServiceConnection;
import com.github.phantompowered.proxy.api.event.annotation.Listener;
import com.github.phantompowered.proxy.api.events.connection.ChatEvent;
import com.github.phantompowered.proxy.api.events.connection.player.PlayerSendProxyMessageEvent;
import io.javalin.websocket.WsContext;

import java.util.List;
import java.util.Map;

public class ChatReceiveListener {

    private final WebChat webChat;

    public ChatReceiveListener(WebChat webChat) {
        this.webChat = webChat;
    }

    @Listener
    public void handleChat(ChatEvent event) {
        if (event.getDirection() != ProtocolDirection.TO_CLIENT || (event.getType() != ChatMessageType.CHAT && event.getType() != ChatMessageType.SYSTEM)) {
            return;
        }

        HistoricalMessage message = HistoricalMessage.now(ChatMode.SERVER, event.getMessage());

        this.handleMessage((ServiceConnection) event.getConnection(), message);
    }

    @Listener
    public void handleProxyMessage(PlayerSendProxyMessageEvent event) {
        if (event.getPlayer().getConnectedClient() == null || (event.getType() != ChatMessageType.CHAT && event.getType() != ChatMessageType.SYSTEM)) {
            return;
        }

        HistoricalMessage message = HistoricalMessage.now(ChatMode.PROXY, event.getMessage());

        this.handleMessage(event.getPlayer().getConnectedClient(), message);
    }

    private void handleMessage(ServiceConnection connection, HistoricalMessage message) {
        List<HistoricalMessage> messages = this.webChat.getHistory(connection);

        messages.add(message);

        if (messages.size() > this.webChat.getMaxHistorySize()) {
            messages.remove(0);
        }

        Map<String, WsContext> sessions = connection.getProperty(WebChat.CONNECTION_SESSIONS_PROPERTY);
        if (sessions == null) {
            return;
        }

        this.webChat.sendMessage(sessions.values(), message);
    }

}
