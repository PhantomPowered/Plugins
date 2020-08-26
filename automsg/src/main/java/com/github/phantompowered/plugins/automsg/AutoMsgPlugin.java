package com.github.phantompowered.plugins.automsg;

import com.github.phantompowered.proxy.api.command.CommandMap;
import com.github.phantompowered.proxy.api.event.EventManager;
import com.github.phantompowered.proxy.api.plugin.PluginContainer;
import com.github.phantompowered.proxy.api.plugin.PluginState;
import com.github.phantompowered.proxy.api.plugin.annotation.Inject;
import com.github.phantompowered.proxy.api.plugin.annotation.Plugin;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;

@Plugin(
        id = "com.github.phantompowered.plugins.automsg",
        displayName = "AutoMsg",
        version = 1,
        website = "https://github.com/PhantomPowered/Plugins",
        authors = "derrop"
)
public class AutoMsgPlugin {

    @Inject(state = PluginState.ENABLED)
    public void enable(PluginContainer container, ServiceRegistry registry) {
        AutoMsgDatabase database = new AutoMsgDatabase(registry);

        registry.getProviderUnchecked(EventManager.class).registerListener(container, new AutoMsgListener(database));
        registry.getProviderUnchecked(CommandMap.class).registerCommand(container, new AutoMsgCommand(database), "automsg");
    }

}
