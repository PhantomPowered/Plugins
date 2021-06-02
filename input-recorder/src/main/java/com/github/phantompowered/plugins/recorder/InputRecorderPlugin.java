package com.github.phantompowered.plugins.recorder;

import com.github.phantompowered.proxy.api.command.CommandMap;
import com.github.phantompowered.proxy.api.plugin.PluginContainer;
import com.github.phantompowered.proxy.api.plugin.PluginState;
import com.github.phantompowered.proxy.api.plugin.annotation.Inject;
import com.github.phantompowered.proxy.api.plugin.annotation.Plugin;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;

@Plugin(
        id = "com.github.phantompowered.plugins.recorder",
        displayName = "InputRecorder",
        version = 1,
        authors = "derrop"
)
public class InputRecorderPlugin {

    @Inject(state = PluginState.LOADED)
    public void loadServices(ServiceRegistry registry, PluginContainer container) {
        registry.setProvider(container, PathPlayer.class, new PathPlayer(registry));
        registry.setProvider(container, PathRecorder.class, new PathRecorder(registry, container));
    }

    @Inject(state = PluginState.ENABLED)
    public void loadCommand(ServiceRegistry registry, PluginContainer container) {
        registry.getProviderUnchecked(CommandMap.class).registerCommand(container, new CommandRecordPath(
                registry.getProviderUnchecked(PathRecorder.class),
                registry.getProviderUnchecked(PathPlayer.class)
        ), "recordpath");
    }

}
