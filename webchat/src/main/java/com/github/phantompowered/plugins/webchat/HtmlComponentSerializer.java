package com.github.phantompowered.plugins.webchat;

import net.kyori.adventure.text.*;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.apache.commons.text.StringEscapeUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class HtmlComponentSerializer implements ComponentSerializer<Component, TextComponent, String> {

    private static final HtmlComponentSerializer INSTANCE = new HtmlComponentSerializer();

    public static HtmlComponentSerializer html() {
        return INSTANCE;
    }

    // TODO implement
    private final @Nullable Function<KeybindComponent, String> keybind;
    private final @Nullable Function<TranslatableComponent, String> translatable;

    public HtmlComponentSerializer() {
        this(null, null);
    }

    public HtmlComponentSerializer(final @Nullable Function<KeybindComponent, String> keybind, final @Nullable Function<TranslatableComponent, String> translatable) {
        this.keybind = keybind;
        this.translatable = translatable;
    }

    @NotNull
    @Override
    public TextComponent deserialize(@NonNull String input) {
        // not implemented
        return TextComponent.of(input);
    }

    @NotNull
    @Override
    public String serialize(@NonNull Component component) {
        StringBuilder builder = new StringBuilder();
        this.serialize(component, builder);
        return builder.toString();
    }

    private void serialize(Component component, StringBuilder builder) {
        String rawText = this.getRawText(component);
        if (!rawText.isEmpty()) {
            this.serializeStyle(component, this.prepareText(rawText), builder);
        }

        for (Component child : component.children()) {
            this.serialize(child, builder);
        }
    }

    private void serializeStyle(Component component, String rawText, StringBuilder builder) {
        Style style = component.style();

        builder.append("<a");

        TextColor color = component.color();
        boolean underlined = style.hasDecoration(TextDecoration.UNDERLINED);
        if (color != null || underlined) {
            builder.append(" style=\"");
            if (color != null) {
                builder.append("color: ").append(color.asHexString()).append("; ");
            }
            if (underlined) {
                builder.append("text-decoration: underline");
            }
            builder.append('"');
        }

        builder.append('>');

        if (style.hasDecoration(TextDecoration.BOLD)) {
            builder.append("<b>");
        }
        if (style.hasDecoration(TextDecoration.ITALIC)) {
            builder.append("<i>");
        }
        if (style.hasDecoration(TextDecoration.STRIKETHROUGH)) {
            builder.append("<del>");
        }

        builder.append(rawText);

        if (style.hasDecoration(TextDecoration.BOLD)) {
            builder.append("</b>");
        }
        if (style.hasDecoration(TextDecoration.ITALIC)) {
            builder.append("</i>");
        }
        if (style.hasDecoration(TextDecoration.STRIKETHROUGH)) {
            builder.append("</del>");
        }

        builder.append("</a>");
    }

    private String prepareText(String rawText) {
        return StringEscapeUtils.escapeHtml4(rawText).replace("\n", "<br>");
    }

    private String getRawText(Component component) {
        if (component instanceof KeybindComponent) {
            if (this.keybind != null) {
                return this.keybind.apply((KeybindComponent) component);
            }
            return ((KeybindComponent) component).keybind();
        } else if (component instanceof ScoreComponent) {
            return ((ScoreComponent) component).value();
        } else if (component instanceof SelectorComponent) {
            return ((SelectorComponent) component).pattern();
        } else if (component instanceof TextComponent) {
            return ((TextComponent) component).content();
        } else if (component instanceof TranslatableComponent) {
            if (this.translatable != null) {
                return this.translatable.apply((TranslatableComponent) component);
            }
            return ((TranslatableComponent) component).key();
        } else {
            throw new UnsupportedOperationException("Don't know how to turn " + component.getClass().getSimpleName() + " into a string");
        }
    }

}
