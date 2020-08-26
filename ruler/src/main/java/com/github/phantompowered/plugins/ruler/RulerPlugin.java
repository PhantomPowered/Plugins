package com.github.phantompowered.plugins.ruler;

import com.github.phantompowered.proxy.api.event.EventManager;
import com.github.phantompowered.proxy.api.plugin.PluginContainer;
import com.github.phantompowered.proxy.api.plugin.PluginState;
import com.github.phantompowered.proxy.api.plugin.annotation.Inject;
import com.github.phantompowered.proxy.api.plugin.annotation.Plugin;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;

@Plugin(
        id = "com.github.phantompowered.plugins.ruler",
        displayName = "Ruler",
        version = 1,
        website = "https://github.com/PhantomPowered/Plugins",
        authors = "derrop"
)
public class RulerPlugin {

    @Inject(state = PluginState.ENABLED)
    public void enable(ServiceRegistry registry, PluginContainer container) {
        registry.getProviderUnchecked(EventManager.class).registerListener(container, new RulerListener());
    }

}
