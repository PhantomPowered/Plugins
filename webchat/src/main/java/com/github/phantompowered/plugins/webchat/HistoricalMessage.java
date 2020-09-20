package com.github.phantompowered.plugins.webchat;

import net.kyori.adventure.text.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HistoricalMessage {

    private static final DateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");

    private final long time;
    private final ChatMode mode;
    private final Component message;

    private HistoricalMessage(long time, ChatMode mode, Component message) {
        this.time = time;
        this.mode = mode;
        this.message = message;
    }

    public static HistoricalMessage now(ChatMode mode, Component message) {
        return of(System.currentTimeMillis(), mode, message);
    }

    public static HistoricalMessage of(long time, ChatMode mode, Component message) {
        return new HistoricalMessage(time, mode, message);
    }

    public long getTime() {
        return this.time;
    }

    public ChatMode getMode() {
        return mode;
    }

    public Component getMessage() {
        return this.message;
    }

    public String asHtml() {
        return FORMAT.format(new Date(this.time)) + " " + HtmlComponentSerializer.html().serialize(this.message);
    }

}
