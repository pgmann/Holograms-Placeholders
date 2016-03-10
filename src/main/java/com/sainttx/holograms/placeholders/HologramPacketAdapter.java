package com.sainttx.holograms.placeholders;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.sainttx.holograms.api.HologramPlugin;
import com.sainttx.holograms.api.entity.HologramEntity;
import com.sainttx.holograms.api.line.HologramLine;
import com.sainttx.holograms.api.line.TextualHologramLine;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

public class HologramPacketAdapter extends PacketAdapter {

    private HologramPlugin plugin;
    private Set<UUID> sendingMetadataPacket = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private Set<HologramLine> trackedPlaceholderLines = Collections.newSetFromMap(new WeakHashMap<>());

    public HologramPacketAdapter(Plugin plugin) {
        super(plugin, PacketType.Play.Server.SPAWN_ENTITY,
                PacketType.Play.Server.NAMED_ENTITY_SPAWN,
                PacketType.Play.Server.ENTITY_METADATA);
        this.plugin = JavaPlugin.getPlugin(HologramPlugin.class);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        Player player = event.getPlayer();
        Entity entity = packet.getEntityModifier(event).read(0);

        if (entity == null) {
            return;
        }

        HologramEntity hologram = plugin.getEntityController().getHologramEntity(entity);
        if (hologram == null || !(hologram.getHologramLine() instanceof TextualHologramLine)) {
            return;
        }

        TextualHologramLine line = ((TextualHologramLine) hologram.getHologramLine());
        boolean tracked = trackedPlaceholderLines.contains(line);
        if (!tracked && event.getPacketType() == PacketType.Play.Server.SPAWN_ENTITY
                && PlaceholderAPI.containsPlaceholders(line.getText())) {
            trackedPlaceholderLines.add(line);
            plugin.getHologramManager().trackLine(new WrappedUpdatingTextualLine(plugin.getHologramManager(), line));
        } else if (tracked && event.getPacketType() == PacketType.Play.Server.ENTITY_METADATA
                && !sendingMetadataPacket.contains(player.getUniqueId())) {
            List<WrappedWatchableObject> metadata = packet.getWatchableCollectionModifier().read(0);
            for (WrappedWatchableObject watchable : metadata) {
                Object value = watchable.getValue();
                if (watchable.getIndex() == 2 && value instanceof String) {
                    String parsed = PlaceholderAPI.setPlaceholders(player, line.getText());
                    watchable.setValue(parsed);
                    event.setCancelled(true);
                    sendMetadataPacket(player, packet);
                }
            }
        }
    }

    // Avoid
    private void sendMetadataPacket(Player player, PacketContainer packet) {
        sendingMetadataPacket.add(player.getUniqueId());
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet.deepClone());
        } catch (Exception ignored) {
        }
        sendingMetadataPacket.remove(player.getUniqueId());
    }
}
