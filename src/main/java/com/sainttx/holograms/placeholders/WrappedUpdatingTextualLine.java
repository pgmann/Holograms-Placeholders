package com.sainttx.holograms.placeholders;

import com.sainttx.holograms.api.Hologram;
import com.sainttx.holograms.api.HologramManager;
import com.sainttx.holograms.api.line.TextualHologramLine;
import com.sainttx.holograms.api.line.UpdatingHologramLine;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public class WrappedUpdatingTextualLine implements UpdatingHologramLine,TextualHologramLine {

    private HologramManager manager;
    private TextualHologramLine line;
    private long delay;
    private long lastUpdate;

    public WrappedUpdatingTextualLine(HologramManager manager, TextualHologramLine line) {
        this(manager, line, 1000L);
    }

    public WrappedUpdatingTextualLine(HologramManager manager, TextualHologramLine line, long updateDelayMillis) {
        Validate.notNull(line, "Line cannot be null");
        this.manager = manager;
        this.line = line;
        this.delay = updateDelayMillis;
    }

    @Override
    public String getText() {
        return line.getText();
    }

    @Override
    public void setText(String text) {
        line.setText(text);
    }

    @Override
    public void update() {
        this.lastUpdate = System.currentTimeMillis();
        line.setText(line.getText());
    }

    @Override
    public long getDelay() {
        return delay;
    }

    @Override
    public long getLastUpdateTime() {
        return lastUpdate;
    }

    @Override
    public void setLocation(Location location) {
        line.setLocation(location);
    }

    @Override
    public Location getLocation() {
        return line.getLocation();
    }

    @Override
    public void hide() {
        line.hide();
        manager.untrackLine(this);
    }

    @Override
    public boolean show() {
        if (line.show()) {
            manager.trackLine(this);
            return true;
        }
        return false;
    }

    @Override
    public boolean isHidden() {
        return line.isHidden();
    }

    @Override
    public double getHeight() {
        return line.getHeight();
    }

    @Override
    public Hologram getHologram() {
        return line.getHologram();
    }

    @Override
    public String getRaw() {
        return line.getRaw();
    }
}
