package com.github.phantompowered.plugins.betterlogin;

import com.github.phantompowered.plugins.betterlogin.connection.LoginServiceConnection;
import com.github.phantompowered.proxy.api.block.material.Material;
import com.github.phantompowered.proxy.api.event.annotation.Listener;
import com.github.phantompowered.proxy.api.events.connection.player.*;
import com.github.phantompowered.proxy.api.item.ItemStack;
import com.github.phantompowered.proxy.api.location.Location;
import com.github.phantompowered.proxy.api.nbt.NBTTagCompound;
import com.github.phantompowered.proxy.api.player.Player;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;
import com.github.phantompowered.proxy.item.ProxyItemStack;

public class LoginPrepareListener {

    public static final Location SPAWN = new Location(0, 100, 0, 0, 0);
    public static final ItemStack[] PARENT_INVENTORY = new ItemStack[63];

    static {
        PARENT_INVENTORY[1] = new ProxyItemStack(Material.BARRIER, 1, 0, new NBTTagCompound());
        //PARENT_INVENTORY[3] = new ItemStack(Material.COMPASS.getId(), 1, 0, new NBTTagCompound().setTag("display", new NBTTagCompound().setString("Name", "§cWatch replay")));
        // PARENT_INVENTORY[5] = new ItemStack(Material.SKULL_ITEM.getId(), 1, 0, new NBTTagCompound().setTag("display", new NBTTagCompound().setString("Name", "§cConnect with client")));
        //   PARENT_INVENTORY[7] = new ItemStack(Material.SKULL_ITEM.getId(), 1, 0, new NBTTagCompound().setTag("display", new NBTTagCompound().setString("Name", "§7Spectate player")));
        //   PARENT_INVENTORY[8] = new ItemStack(Material.BARRIER.getId(), 1, 0, new NBTTagCompound().setTag("display", new NBTTagCompound().setString("Name", "§cExit")));
        // TODO items are not added
    }

    private final ServiceRegistry serviceRegistry;

    public LoginPrepareListener(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Listener
    public void handleLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        //event.setTargetConnection(new LoginServiceConnection(this.proxy, player));
    }

    @Listener
    public void handleInventoryClose(PlayerInventoryCloseEvent event) {
        if (event.getPlayer().getConnectedClient() instanceof LoginServiceConnection) {
            event.cancel(true);
        }
    }

    @Listener
    public void handleInventoryClick(PlayerInventoryClickEvent event) {
        if (event.getPlayer().getConnectedClient() instanceof LoginServiceConnection) {
            event.cancel(true);
        }
    }

    @Listener
    public void handlePlayerMove(PlayerMoveEvent event) {
        if (event.getPlayer().getConnectedClient() instanceof LoginServiceConnection
                && ((LoginServiceConnection) event.getPlayer().getConnectedClient()).getConnectionTimestamp() + 1000 < System.currentTimeMillis()) {
            event.cancel(true);
        }
    }

    @Listener
    public void handleBlockBreak(PlayerBlockBreakEvent event) {
        if (event.getPlayer().getConnectedClient() instanceof LoginServiceConnection) {
            event.cancel(true);
        }
    }

    @Listener
    public void handleBlockPlace(PlayerBlockPlaceEvent event) {
        if (event.getPlayer().getConnectedClient() instanceof LoginServiceConnection) {
            event.cancel(true);
        }
    }

}
