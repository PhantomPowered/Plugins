package com.github.phantompowered.plugins.webchat;

import net.kyori.adventure.text.*;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class HtmlComponentSerializer implements ComponentSerializer<Component, TextComponent, String> {

    private static final HtmlComponentSerializer INSTANCE = new HtmlComponentSerializer();

    public static HtmlComponentSerializer html() {
        return INSTANCE;
    }

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
    // not implemented
    public TextComponent deserialize(@NonNull String input) {
        return TextComponent.of(input);
    }

    @NotNull
    @Override
    public String serialize(@NonNull Component component) {
        StringBuilder builder = new StringBuilder();
        this.serialize(component, builder);
        return builder.toString().replace("\n", "<br>");
    }

    private void serialize(Component component, StringBuilder builder) {
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

        if (component instanceof KeybindComponent) {
            if (this.keybind != null) {
                builder.append(this.keybind.apply((KeybindComponent) component));
            }
        } else if (component instanceof ScoreComponent) {
            builder.append(((ScoreComponent) component).value());
        } else if (component instanceof SelectorComponent) {
            builder.append(((SelectorComponent) component).pattern());
        } else if (component instanceof TextComponent) {
            builder.append(((TextComponent) component).content());
        } else if (component instanceof TranslatableComponent) {
            if (this.translatable != null) {
                builder.append(this.translatable.apply((TranslatableComponent) component));
            }
        } else {
            throw new UnsupportedOperationException("Don't know how to turn " + component.getClass().getSimpleName() + " into a string");
        }

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

        for (Component child : component.children()) {
            this.serialize(child, builder);
        }
    }

}
