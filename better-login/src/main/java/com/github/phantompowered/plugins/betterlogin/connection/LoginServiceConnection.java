package com.github.phantompowered.plugins.betterlogin.connection;

import com.github.phantompowered.plugins.betterlogin.LoginPrepareListener;
import com.github.phantompowered.proxy.api.block.BlockAccess;
import com.github.phantompowered.proxy.api.block.Facing;
import com.github.phantompowered.proxy.api.block.material.Material;
import com.github.phantompowered.proxy.api.chat.ChatMessageType;
import com.github.phantompowered.proxy.api.chat.HistoricalMessage;
import com.github.phantompowered.proxy.api.connection.ProtocolState;
import com.github.phantompowered.proxy.api.connection.ServiceConnectResult;
import com.github.phantompowered.proxy.api.connection.ServiceConnection;
import com.github.phantompowered.proxy.api.connection.ServiceInventory;
import com.github.phantompowered.proxy.api.connection.ServiceWorldDataProvider;
import com.github.phantompowered.proxy.api.entity.types.Entity;
import com.github.phantompowered.proxy.api.location.BlockingObject;
import com.github.phantompowered.proxy.api.location.Location;
import com.github.phantompowered.proxy.api.location.Vector;
import com.github.phantompowered.proxy.api.network.NetworkAddress;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.api.player.Player;
import com.github.phantompowered.proxy.api.player.PlayerAbilities;
import com.github.phantompowered.proxy.api.player.id.PlayerId;
import com.github.phantompowered.proxy.api.player.inventory.InventoryType;
import com.github.phantompowered.proxy.api.scoreboard.Scoreboard;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;
import com.github.phantompowered.proxy.api.session.MCServiceCredentials;
import com.github.phantompowered.proxy.api.task.Task;
import com.github.phantompowered.proxy.api.task.util.TaskUtil;
import com.github.phantompowered.proxy.connection.player.DefaultPlayer;
import com.github.phantompowered.proxy.protocol.play.server.PacketPlayServerLogin;
import com.github.phantompowered.proxy.protocol.play.server.PacketPlayServerPlayerInfo;
import com.github.phantompowered.proxy.protocol.play.server.player.PacketPlayServerPlayerAbilities;
import com.github.phantompowered.proxy.protocol.play.server.player.spawn.PacketPlayServerPosition;
import com.github.phantompowered.proxy.protocol.play.server.world.PacketPlayServerTimeUpdate;
import com.github.phantompowered.proxy.protocol.play.server.world.material.PacketPlayServerEmptyMapChunk;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.UserAuthentication;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class LoginServiceConnection implements ServiceConnection, Entity.Callable {

    private static final ServiceWorldDataProvider WORLD_DATA_PROVIDER = new LoginServiceWorldDataProvider();

    private final Player player;

    private boolean connected = false;
    private LoginBlockAccess blockAccess;

    private final PlayerAbilities abilities = new LoginPlayerAbilities();
    private final long connectionTimestamp = System.currentTimeMillis();

    public LoginServiceConnection(Player player) {
        this.player = player;
    }

    public long getConnectionTimestamp() {
        return this.connectionTimestamp;
    }

    @Override
    public @NotNull ServiceRegistry getServiceRegistry() {
        return this.player.getServiceRegistry();
    }

    @Override
    public @Nullable Player getPlayer() {
        return this.connected ? this.player : null;
    }

    @Override
    public long getLastDisconnectionTimestamp() {
        return -1;
    }

    @Override
    public long getLastConnectionTryTimestamp() {
        return 0;
    }

    @Override
    public PlayerId getLastConnectedPlayer() {
        return null;
    }

    @Override
    public PlayerAbilities getAbilities() {
        return this.abilities;
    }

    @Override
    public ServiceInventory getInventory() {
        return null;
    }

    @Override
    public @NotNull MCServiceCredentials getCredentials() {
        return MCServiceCredentials.offline(this.player.getName(), null, false);
    }

    @Override
    public @Nullable UserAuthentication getAuthentication() {
        return null;
    }

    @Override
    public @Nullable UUID getUniqueId() {
        return this.player.getUniqueId();
    }

    @Override
    public @Nullable String getName() {
        return this.player.getName();
    }

    @Override
    public int getEntityId() {
        return -1;
    }

    @Override
    public int getDimension() {
        return this.player.getDimension();
    }

    @Override
    public @NotNull Location getLocation() {
        return LoginPrepareListener.SPAWN.clone();
    }

    @Override
    public @NotNull Location getHeadLocation() {
        return this.getLocation();
    }

    @Override
    public boolean isOnGround() {
        return true;
    }

    @Override
    public ServiceWorldDataProvider getWorldDataProvider() {
        return WORLD_DATA_PROVIDER;
    }

    @Override
    public @NotNull NetworkAddress getServerAddress() {
        return Objects.requireNonNull(NetworkAddress.parse("127.0.0.1"));
    }

    @Override
    public void chat(@NotNull String message) {
    }

    @Override
    public void displayMessage(@NotNull ChatMessageType type, @NotNull String message) {
        this.player.sendMessage(type, LegacyComponentSerializer.legacySection().deserialize(message));
    }

    @Override
    public void chat(@NotNull Component component) {
    }

    @Override
    public void displayMessage(@NotNull ChatMessageType type, @NotNull Component component) {
        this.player.sendMessage(type, component);
    }

    @Override
    public void chat(@NotNull Component... components) {
    }

    @Override
    public void displayMessage(@NotNull ChatMessageType type, @NotNull Component... components) {
        this.player.sendMessage(type, components);
    }

    @Override
    public boolean setTabHeaderAndFooter(Component header, Component footer) {
        return true;
    }

    @Override
    public void resetTabHeaderAndFooter() {

    }

    @Override
    public Component getTabHeader() {
        return null;
    }

    @Override
    public Component getTabFooter() {
        return null;
    }

    @Override
    public @NotNull Task<ServiceConnectResult> connect() throws IllegalStateException {
        return null;
    }

    @Override
    public @NotNull InetSocketAddress getSocketAddress() {
        return this.player.getSocketAddress();
    }

    @Override
    public void disconnect(@NotNull Component reason) {
        this.player.disconnect(reason);
    }

    @Override
    public void sendCustomPayload(@NotNull String s, @NotNull byte[] bytes) {
    }

    @Override
    public void sendCustomPayload(@NotNull String s, @NotNull ProtoBuf protoBuf) {
    }

    @Override
    public int getPing() {
        return 0;
    }

    @Override
    public List<HistoricalMessage> getReceivedMessages() {
        return Collections.emptyList();
    }

    @Override
    public void write(@NotNull Object packet) {
    }

    @Override
    public @NotNull Task<Boolean> writeWithResult(@NotNull Object packet) {
        return TaskUtil.completedTask(false);
    }

    @Override
    public void setProtocolState(@NotNull ProtocolState state) {
    }

    @Override
    public @NotNull ProtocolState getProtocolState() {
        return ProtocolState.PLAY;
    }

    @Override
    public @NotNull InetSocketAddress getAddress() {
        return this.player.getAddress();
    }

    @Override
    public void close(@Nullable Object goodbyeMessage) {
    }

    @Override
    public void delayedClose(@Nullable Packet goodbyeMessage) {
    }

    @Override
    public void setCompression(int compression) {
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public boolean isClosing() {
        return false;
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public Channel getWrappedChannel() {
        return null;
    }

    @Override
    public <T> @Nullable T getProperty(String key) {
        return null;
    }

    @Override
    public <T> void setProperty(String key, T value) {
    }

    @Override
    public void removeProperty(String key) {
    }

    @Override
    public void addOutgoingPacketListener(UUID key, Consumer<Packet> consumer) {
    }

    @Override
    public void removeOutgoingPacketListener(UUID key) {
    }

    @Override
    public void unregister() {
    }

    @Override
    public Scoreboard getScoreboard() {
        return null;
    }

    @Override
    public BlockAccess getBlockAccess() {
        return this.blockAccess;
    }

    @Override
    public void syncPackets(Player player, boolean switched) {
        if (!this.player.equals(player)) {
            throw new IllegalArgumentException("Cannot set LoginServiceConnection to other player than on initialization");
        }

        if (switched) {
            this.connected = true;
            return;
        }

        player.sendPacket(new PacketPlayServerLogin(1265, (short) 1, 0, (short) 0, (short) 255, "default", false));
        player.sendPacket(new PacketPlayServerPlayerAbilities(true, true, true, true, 0.05F, 0.1F));
        player.sendPacket(new PacketPlayServerTimeUpdate(1, 23700));
        player.sendPacket(new PacketPlayServerEmptyMapChunk(0, 0));
        player.sendPacket(new PacketPlayServerPlayerInfo(PacketPlayServerPlayerInfo.Action.ADD_PLAYER, new PacketPlayServerPlayerInfo.Item[]{
                new PacketPlayServerPlayerInfo.Item(new GameProfile(player.getUniqueId(), player.getName()), 1, 0, player.getName())
        }));

        this.blockAccess = new LoginBlockAccess((DefaultPlayer) player);

        player.sendPacket(new PacketPlayServerPosition(LoginPrepareListener.SPAWN.clone().add(new Location(0, 3, 0, 0, 0))));

        player.getInventory().setWindowId((byte) 1);
        player.getInventory().setContent(LoginPrepareListener.PARENT_INVENTORY);
        player.getInventory().setType(InventoryType.CHEST);
        player.getInventory().open();

        // TODO send keep alive packets

        this.connected = true;
    }

    @Override
    public void updateLocation(@NotNull Location location) {
    }

    @Override
    public Collection<Player> getViewers() {
        return Collections.emptyList();
    }

    @Override
    public void startViewing(Player player) {
    }

    @Override
    public void stopViewing(Player player) {
    }

    @Override
    public boolean isSneaking() {
        return false;
    }

    @Override
    public boolean isSprinting() {
        return false;
    }

    @Override
    public Location getTargetBlock(Set<Material> transparent, int range) {
        return null;
    }

    @Override
    public Location getTargetBlock(int range) {
        return null;
    }

    @Override
    public @NotNull BlockingObject getTargetObject(int range) {
        return BlockingObject.miss();
    }

    @Override
    public void sendPacket(@NotNull Packet packet) {
    }

    @Override
    public void sendPacket(@NotNull ByteBuf byteBuf) {
    }

    @Override
    public @NotNull NetworkUnsafe networkUnsafe() {
        return packet -> {
        };
    }

    @Override
    public void handleEntityPacket(@NotNull Packet packet) {
    }

    @Override
    public void teleport(@NotNull Location location) {

    }

    @Override
    public void lookAt(@NotNull Location location) {

    }

    @Override
    public void breakBlock(Location location, Facing facing) {

    }

    @Override
    public void performAirLeftClick() {

    }

    @Override
    public void performEntityLeftClick(@NotNull Entity entity) {

    }

    @Override
    public void performBlockLeftClick(@NotNull Location location, @NotNull Facing facing) {

    }

    @Override
    public void performAirRightClick() {

    }

    @Override
    public void performEntityRightClick(@NotNull Entity entity, @NotNull Vector vector) {

    }

    @Override
    public void performBlockRightClick(@NotNull Location location, @NotNull Facing facing, @NotNull Vector vector) {

    }

    @Override
    public void toggleSneaking(boolean b) {

    }

    @Override
    public void toggleSprinting(boolean b) {

    }

    @Override
    public void openInventory() {

    }

    @Override
    public void selectHeldItemSlot(int i) {

    }
}
