package com.sainttx.holograms.placeholders;

import net.minecraft.server.v1_14_R1.Entity;
import net.minecraft.server.v1_14_R1.WorldServer;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;

public class NMSManager {
    /**
     * This is a workaround for a bug with ProtocolLib:
     * https://github.com/dmulloy2/ProtocolLib/issues/663
     *
     * Implemented based on HolographicDisplays' workaround:
     * https://github.com/filoghost/HolographicDisplays/commit/ffb868093067a7f049ccbf6f075cad5ab0bb98a8
     *
     * @param bukkitWorld the world the entity is in
     * @param entityID the ID of the entity to retrieve
     * @return
     */
    public static org.bukkit.entity.Entity getEntityFromID(org.bukkit.World bukkitWorld, int entityID) {
        WorldServer nmsWorld = ((CraftWorld) bukkitWorld).getHandle();
        Entity nmsEntity = nmsWorld.getEntity(entityID);

        if (nmsEntity == null) {
            return null;
        }

        return nmsEntity.getBukkitEntity();
    }
}
