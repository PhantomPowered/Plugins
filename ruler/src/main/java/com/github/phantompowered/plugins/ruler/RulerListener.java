package com.github.phantompowered.plugins.ruler;

import com.github.phantompowered.proxy.api.block.material.Material;
import com.github.phantompowered.proxy.api.connection.ServiceConnection;
import com.github.phantompowered.proxy.api.entity.PlayerInfo;
import com.github.phantompowered.proxy.api.entity.types.Entity;
import com.github.phantompowered.proxy.api.entity.types.living.human.EntityPlayer;
import com.github.phantompowered.proxy.api.event.annotation.Listener;
import com.github.phantompowered.proxy.api.events.connection.player.PlayerInteractEvent;
import com.github.phantompowered.proxy.api.location.Location;
import com.github.phantompowered.proxy.api.raytrace.BlockingObject;
import net.kyori.adventure.text.TextComponent;

public class RulerListener {

    @Listener
    public void handleInteract(PlayerInteractEvent event) {
        if (!event.getAction().isLeftClick()) {
            return;
        }

        ServiceConnection connection = event.getPlayer().getConnectedClient();
        if (connection == null) {
            return;
        }

        if (!connection.isSneaking()) {
            return;
        }

        BlockingObject object = connection.getTargetObject(100);
        Location location = object.getLocation();
        if (object.getType() == BlockingObject.Type.MISS || location == null) {
            return;
        }

        String text;

        switch (object.getType()) {
            case BLOCK:
                Material material = connection.getBlockAccess().getMaterial(location);
                text = "§7Range to block §e" + material + " " + location.toShortString() + "§7: §e" + String.format("%.3f", connection.getLocation().distance(location)) + " blocks";
                break;

            case ENTITY:
                Entity entity = object.getEntity();
                if (entity == null) {
                    return;
                }
                String name = String.valueOf(entity.getType() != null ? entity.getType() : entity.getLivingType());
                if (entity instanceof EntityPlayer) {
                    PlayerInfo playerInfo = ((EntityPlayer) entity).getPlayerInfo();
                    if (playerInfo != null) {
                        name = playerInfo.getUsername();
                    }
                }
                text = "§7Range to entity §e" + name + "§7: §e" + String.format("%.3f", connection.getLocation().distance(entity.getLocation())) + " blocks";
                break;

            default:
                return;
        }

        event.getPlayer().sendActionBar(1, TextComponent.of(text));
    }
}
