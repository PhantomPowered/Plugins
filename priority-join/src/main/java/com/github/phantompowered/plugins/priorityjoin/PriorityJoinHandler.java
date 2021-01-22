package com.github.phantompowered.plugins.priorityjoin;

import com.github.phantompowered.proxy.api.connection.ServiceConnector;
import com.github.phantompowered.proxy.api.event.annotation.Listener;
import com.github.phantompowered.proxy.api.events.connection.ServiceConnectorChooseClientEvent;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;

import java.util.UUID;

public class PriorityJoinHandler {

    private final ServiceRegistry registry;
    private final PriorityJoinDatabase database;

    public PriorityJoinHandler(ServiceRegistry registry, PriorityJoinDatabase database) {
        this.registry = registry;
        this.database = database;
    }

    @Listener
    public void handleClientChoose(ServiceConnectorChooseClientEvent event) {
        UUID preferredId = this.database.getTarget(event.getUniqueId());
        if (preferredId == null) {
            return;
        }

        this.registry.getProviderUnchecked(ServiceConnector.class).getOnlineClients().stream()
                .filter(connection -> preferredId.equals(connection.getUniqueId()))
                .findFirst()
                .ifPresent(event::setConnection);
    }

}
