package com.github.phantompowered.plugins.priorityjoin;

import com.github.phantompowered.proxy.api.command.basic.NonTabCompleteableCommandCallback;
import com.github.phantompowered.proxy.api.command.exception.CommandExecutionException;
import com.github.phantompowered.proxy.api.command.result.CommandResult;
import com.github.phantompowered.proxy.api.command.sender.CommandSender;
import com.github.phantompowered.proxy.api.player.id.PlayerId;
import com.github.phantompowered.proxy.api.player.id.PlayerIdStorage;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;
import org.jetbrains.annotations.NotNull;

public class CommandPriorityJoin extends NonTabCompleteableCommandCallback {
    private final ServiceRegistry registry;
    private final PriorityJoinDatabase database;

    public CommandPriorityJoin(ServiceRegistry registry, PriorityJoinDatabase database) {
        super("command.priorityjoin", null);
        this.registry = registry;
        this.database = database;
    }

    @Override
    public @NotNull CommandResult process(@NotNull CommandSender sender, @NotNull String[] args, @NotNull String s) throws CommandExecutionException {
        if (args.length != 2) {
            sender.sendMessage("Please use 'pj <clientName> <targetName>'");
            return CommandResult.BREAK;
        }

        PlayerIdStorage storage = this.registry.getProviderUnchecked(PlayerIdStorage.class);
        PlayerId client = storage.getPlayerId(args[0]);
        PlayerId target = storage.getPlayerId(args[1]);

        if (client == null || target == null) {
            sender.sendMessage("One of your arguments is no player name that exists");
            return CommandResult.NOT_FOUND;
        }

        this.database.setTarget(client.getUniqueId(), target.getUniqueId());
        sender.sendMessage("Successfully changed the target for " + client + " to " + target);

        return CommandResult.SUCCESS;
    }
}
