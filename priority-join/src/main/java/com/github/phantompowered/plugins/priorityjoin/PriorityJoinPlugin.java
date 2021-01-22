package com.github.phantompowered.plugins.priorityjoin;

import com.github.phantompowered.proxy.api.command.CommandMap;
import com.github.phantompowered.proxy.api.event.EventManager;
import com.github.phantompowered.proxy.api.plugin.PluginContainer;
import com.github.phantompowered.proxy.api.plugin.PluginState;
import com.github.phantompowered.proxy.api.plugin.annotation.Inject;
import com.github.phantompowered.proxy.api.plugin.annotation.Plugin;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;

@Plugin(
        id = "com.github.phantompowered.proxy.plugins.priorityjoin",
        displayName = "PriorityJoin",
        version = 1,
        authors = "derrop"
)
public class PriorityJoinPlugin {

    @Inject(state = PluginState.ENABLED)
    public void enable(PluginContainer container, ServiceRegistry registry) {
        PriorityJoinDatabase database = new PriorityJoinDatabase(registry);

        registry.getProviderUnchecked(CommandMap.class).registerCommand(container, new CommandPriorityJoin(registry, database), "priorityjoin", "pj");
        registry.getProviderUnchecked(EventManager.class).registerListener(container, new PriorityJoinHandler(registry, database));
    }

}
