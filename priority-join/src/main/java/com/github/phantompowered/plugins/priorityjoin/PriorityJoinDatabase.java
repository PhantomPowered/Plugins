package com.github.phantompowered.plugins.priorityjoin;

import com.github.phantompowered.proxy.api.database.DatabaseProvidedStorage;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;

import java.util.UUID;

public class PriorityJoinDatabase extends DatabaseProvidedStorage<UUID> {

    public PriorityJoinDatabase(ServiceRegistry registry) {
        super(registry, "priority_join_priorities", UUID.class);
    }

    public UUID getTarget(UUID clientUniqueId) {
        return super.get(clientUniqueId.toString());
    }

    public void setTarget(UUID clientUniqueId, UUID targetUniqueId) {
        super.insertOrUpdate(clientUniqueId.toString(), targetUniqueId);
    }

}
