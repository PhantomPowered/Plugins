package com.github.phantompowered.proxy.plugins.pwarner;

import com.github.phantompowered.proxy.api.command.CommandMap;
import com.github.phantompowered.proxy.api.event.EventManager;
import com.github.phantompowered.proxy.api.plugin.PluginContainer;
import com.github.phantompowered.proxy.api.plugin.PluginState;
import com.github.phantompowered.proxy.api.plugin.annotation.Inject;
import com.github.phantompowered.proxy.api.plugin.annotation.Plugin;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;
import com.github.phantompowered.proxy.plugins.pwarner.command.CommandPlayerWarns;
import com.github.phantompowered.proxy.plugins.pwarner.listener.WarnListener;
import com.github.phantompowered.proxy.plugins.pwarner.storage.PlayerWarningDatabase;

@Plugin(
        id = "com.github.phantompowered.proxy.plugins.playerwarner",
        displayName = "PlayerWarnings",
        version = 1,
        authors = "derrop"
)
public class PWarnerPlugin {

    // TODO disable the invisibility effect

    @Inject(state = PluginState.ENABLED)
    public void enable(PluginContainer container, ServiceRegistry registry) {
        PlayerWarningDatabase database = new PlayerWarningDatabase(registry);
        registry.getProviderUnchecked(EventManager.class).registerListener(container, new WarnListener(database));
        registry.getProviderUnchecked(CommandMap.class).registerCommand(container, new CommandPlayerWarns(database), "playerwarns", "pw");
    }

}
