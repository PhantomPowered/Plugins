/*
 * MIT License
 *
 * Copyright (c) derrop and derklaro
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.phantompowered.plugins.pathfinding;

import com.github.phantompowered.plugins.pathfinding.command.CommandPath;
import com.github.phantompowered.plugins.pathfinding.provider.DefaultPathProvider;
import com.github.phantompowered.plugins.pathfinding.provider.PathProvider;
import com.github.phantompowered.plugins.pathfinding.walk.DefaultPathWalker;
import com.github.phantompowered.plugins.pathfinding.walk.PathWalker;
import com.github.phantompowered.proxy.api.command.CommandMap;
import com.github.phantompowered.proxy.api.plugin.PluginContainer;
import com.github.phantompowered.proxy.api.plugin.PluginState;
import com.github.phantompowered.proxy.api.plugin.annotation.Inject;
import com.github.phantompowered.proxy.api.plugin.annotation.Plugin;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;
import com.github.phantompowered.proxy.api.tick.TickHandlerProvider;

@Plugin(
        id = "com.github.phantompowered.plugins.pathfinding",
        displayName = "PathFinding",
        version = 1,
        website = "https://github.com/PhantomPowered/Plugins",
        authors = "derrop"
)
public class PathFindingPlugin {

    @Inject(state = PluginState.ENABLED)
    public void enable(PluginContainer plugin, ServiceRegistry registry) {
        registry.setProvider(plugin, PathProvider.class, new DefaultPathProvider());

        DefaultPathWalker pathWalker = new DefaultPathWalker();
        registry.setProvider(plugin, PathWalker.class, pathWalker);
        registry.getProviderUnchecked(TickHandlerProvider.class).registerHandler(pathWalker);

        registry.getProviderUnchecked(CommandMap.class).registerCommand(plugin, new CommandPath(registry), "path");
    }

}
