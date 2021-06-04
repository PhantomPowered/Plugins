package com.github.phantompowered.plugins.webchat;

import com.github.phantompowered.proxy.api.command.sender.CommandSender;
import com.github.phantompowered.proxy.api.connection.ServiceConnection;
import io.javalin.websocket.WsContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.UUID;

public class WebCommandSender implements CommandSender {
    private final WebChat webChat;
    private final WsContext context;

    public WebCommandSender(WebChat webChat, WsContext context) {
        this.webChat = webChat;
        this.context = context;
    }

    @Override
    public void sendMessage(@NotNull String s) {
        this.sendMessage(LegacyComponentSerializer.legacySection().deserialize(s));
    }

    @Override
    public void sendMessage(@NotNull Component component) {
        this.webChat.sendMessage(Collections.singleton(this.context), HistoricalMessage.now(ChatMode.PROXY, component));
    }

    @Override
    public void sendMessages(@NotNull String... strings) {
        for (String string : strings) {
            this.sendMessage(string);
        }
    }

    @Override
    public boolean checkPermission(@NotNull String s) {
        return true;
    }

    @Override
    public @NotNull String getName() {
        return ((ServiceConnection) this.context.attribute(WebChat.SESSION_ATTRIBUTE)).getName();
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return ((ServiceConnection) this.context.attribute(WebChat.SESSION_ATTRIBUTE)).getUniqueId();
    }
}
