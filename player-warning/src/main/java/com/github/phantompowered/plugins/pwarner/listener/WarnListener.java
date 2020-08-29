package com.github.phantompowered.plugins.pwarner.listener;

import com.github.phantompowered.plugins.pwarner.storage.PlayerWarningData;
import com.github.phantompowered.plugins.pwarner.storage.PlayerWarningDatabase;
import com.github.phantompowered.plugins.pwarner.storage.WarnedEquipmentSlot;
import com.github.phantompowered.proxy.api.block.material.Material;
import com.github.phantompowered.proxy.api.chat.ChatColor;
import com.github.phantompowered.proxy.api.entity.PlayerInfo;
import com.github.phantompowered.proxy.api.entity.types.living.human.EntityPlayer;
import com.github.phantompowered.proxy.api.event.annotation.Listener;
import com.github.phantompowered.proxy.api.events.connection.service.EquipmentSlotChangeEvent;
import com.github.phantompowered.proxy.api.item.ItemMeta;
import com.github.phantompowered.proxy.api.player.Player;
import com.github.phantompowered.proxy.api.scoreboard.Team;

public class WarnListener {

    private final PlayerWarningDatabase database;

    public WarnListener(PlayerWarningDatabase database) {
        this.database = database;
    }

    @Listener
    public void handleEquipmentSlotChange(EquipmentSlotChangeEvent event) {
        if (!(event.getEntity() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer entity = (EntityPlayer) event.getEntity();
        Player player = event.getConnection().getPlayer();
        if (player == null) {
            return;
        }

        PlayerWarningData data = this.database.getData(player.getUniqueId());
        if (data == null) {
            return;
        }

        PlayerInfo playerInfo = entity.getPlayerInfo();
        if (playerInfo == null) {
            return;
        }

        String name = playerInfo.getDisplayName() != null ? playerInfo.getDisplayName() : playerInfo.getUsername();
        Material material = event.getItem().getMaterial();
        int amount = event.getItem().getAmount();
        ItemMeta itemMeta = event.getItem().getItemMeta();
        String itemName = material + (itemMeta != null && itemMeta.getDisplayName() != null ? " " + itemMeta.getDisplayName().key() : "");

        WarnedEquipmentSlot slot = data.getEquipmentSlotData(event.getSlot().getSlotId(), material);
        if (slot != null) {
            Team team = event.getConnection().getScoreboard().getTeamByEntry(name);

            String playerColor = team == null ? "" : ChatColor.getLastColors(team.getPrefix());
            ChatColor itemColor = slot.getColor() == null ? ChatColor.YELLOW : slot.getColor();
            String fullName = playerColor + name;

            int maxDurability = material.getMaxDurability();
            int durability = maxDurability <= 0 ? -1 : maxDurability - event.getItem().getMeta();

            String out = String.format("§7%s §7has %dx %s%s", fullName, amount, itemColor, itemName);
            if (durability >= 0) {
                out += String.format(" §7(§a%d§7/§a%d§7)", durability, maxDurability);
            }
            player.sendMessage(out);
        }
    }

}
