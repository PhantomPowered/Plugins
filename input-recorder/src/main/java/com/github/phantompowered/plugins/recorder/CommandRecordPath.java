package com.github.phantompowered.plugins.recorder;

import com.github.phantompowered.proxy.api.command.basic.NonTabCompleteableCommandCallback;
import com.github.phantompowered.proxy.api.command.exception.CommandExecutionException;
import com.github.phantompowered.proxy.api.command.result.CommandResult;
import com.github.phantompowered.proxy.api.command.sender.CommandSender;
import com.github.phantompowered.proxy.api.player.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class CommandRecordPath extends NonTabCompleteableCommandCallback {

    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    private final PathRecorder recorder;
    private final PathPlayer player;

    public CommandRecordPath(PathRecorder recorder, PathPlayer player) {
        super("command.recorder.record", null);
        this.recorder = recorder;
        this.player = player;
    }

    @Override
    public @NotNull CommandResult process(@NotNull CommandSender sender, @NotNull String[] args, @NotNull String s) throws CommandExecutionException {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command is only available for players");
            return CommandResult.BREAK;
        }

        Player player = (Player) sender;

        if (args.length == 2 && args[0].equalsIgnoreCase("record")) {
            PacketRecorder runningRecorder = this.recorder.getRecorder(player);
            if (runningRecorder != null) {
                try {
                    runningRecorder.close();
                    sender.sendMessage("Finished recording!");
                } catch (IOException exception) {
                    exception.printStackTrace();
                    sender.sendMessage("Failed to stop recording, see the console for more information");
                }
                return CommandResult.SUCCESS;
            }

            Path path = Paths.get(args[1]);
            if (Files.exists(path)) {
                sender.sendMessage("This file already exists");
                return CommandResult.BREAK;
            }

            try {
                sender.sendMessage("Recording started! Use this command again to stop recording");
                this.recorder.recordPath(player, Files.newOutputStream(path), () -> sender.sendMessage("Recording finished"));
            } catch (IOException exception) {
                exception.printStackTrace();
                sender.sendMessage("An error occurred while starting the record, see the console for more information");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("play")) {
            Path path = Paths.get(args[1]);
            if (Files.notExists(path)) {
                sender.sendMessage("This file doesn't exist");
                return CommandResult.BREAK;
            }

            sender.sendMessage("Searching for a free worker to start the playback");
            this.executorService.execute(() -> {
                try {
                    sender.sendMessage("Playing the record...");
                    this.player.playPath(player.getConnectedClient(), Files.newInputStream(path));
                    sender.sendMessage("Playback finished!");
                } catch (IOException | InterruptedException exception) {
                    exception.printStackTrace();
                    sender.sendMessage("An error occurred while playing the record, see the console for more information");
                }
            });
        } else {
            sender.sendMessage("recordpath record <filename>");
            sender.sendMessage("recordpath play <filename> | Make sure to use this where the record started");
        }

        return CommandResult.SUCCESS;
    }
}
