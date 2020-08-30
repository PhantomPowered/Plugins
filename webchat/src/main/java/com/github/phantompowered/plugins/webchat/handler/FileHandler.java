package com.github.phantompowered.plugins.webchat.handler;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class FileHandler implements Handler {

    private final byte[] data;

    public FileHandler(String path, Map<String, String> replacements) throws IOException {
        String value = new String(FileHandler.class.getClassLoader().getResourceAsStream(path).readAllBytes(), StandardCharsets.UTF_8);
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            value = value.replace(entry.getKey(), entry.getValue());
        }
        this.data = value.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void handle(@NotNull Context ctx) {
        ctx.status(200).result(this.data);
    }
}
