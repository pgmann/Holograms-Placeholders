package com.sainttx.holograms.placeholders;

import com.comphenix.protocol.ProtocolLibrary;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.plugin.java.JavaPlugin;

public class PlaceholderPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new HologramPacketAdapter(this));
    }
}
