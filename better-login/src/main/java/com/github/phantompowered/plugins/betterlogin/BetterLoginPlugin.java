package com.github.phantompowered.plugins.betterlogin;

import com.github.phantompowered.proxy.api.event.EventManager;
import com.github.phantompowered.proxy.api.plugin.PluginContainer;
import com.github.phantompowered.proxy.api.plugin.PluginState;
import com.github.phantompowered.proxy.api.plugin.annotation.Dependency;
import com.github.phantompowered.proxy.api.plugin.annotation.Inject;
import com.github.phantompowered.proxy.api.plugin.annotation.Plugin;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;

@Plugin(
        id = "com.github.phantompowered.proxy.plugins.betterlogin",
        displayName = "BetterLogin",
        version = 1,
        authors = "derrop",
        dependencies = @Dependency(id = "com.github.phantompowered.plugins.replay", minimumVersion = 1, optional = true)
)
public class BetterLoginPlugin {

    // TODO add the possibility that users have to type into the chat when logging in

    @Inject(state = PluginState.ENABLED)
    public void enable(PluginContainer container, ServiceRegistry registry) {
        registry.getProviderUnchecked(EventManager.class).registerListener(container, new LoginPrepareListener(registry));
    }

}
