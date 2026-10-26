package de.lamachub.linkup;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

public final class LinkUpData {
    private static final Properties DATA = new Properties();
    private static Path file;
    private static final List<String> profiles = new ArrayList<>();
    private static final List<String> waypoints = new ArrayList<>();
    public static boolean compactHud = true;
    public static boolean chatTimestamps = true;
    public static boolean waypointNotifications = true;

    private LinkUpData() {}

    public static void load() {
        file = Minecraft.getInstance().gameDirectory.toPath().resolve("config").resolve("linkup.properties");
        try {
            Files.createDirectories(file.getParent());
            if (Files.exists(file)) {
                try (var in = Files.newInputStream(file)) { DATA.load(in); }
            }
            compactHud = Boolean.parseBoolean(DATA.getProperty("compactHud", "true"));
            chatTimestamps = Boolean.parseBoolean(DATA.getProperty("chatTimestamps", "true"));
            waypointNotifications = Boolean.parseBoolean(DATA.getProperty("waypointNotifications", "true"));
            for (int i = 0; DATA.containsKey("profile." + i); i++) profiles.add(DATA.getProperty("profile." + i));
            for (int i = 0; DATA.containsKey("waypoint." + i); i++) waypoints.add(DATA.getProperty("waypoint." + i));
        } catch (IOException e) {
            System.err.println("[LinkUp] Could not load local settings: " + e.getMessage());
        }
    }

    public static List<String> profiles() { return List.copyOf(profiles); }
    public static List<String> waypoints() { return List.copyOf(waypoints); }

    public static void saveSettings() {
        DATA.setProperty("compactHud", Boolean.toString(compactHud));
        DATA.setProperty("chatTimestamps", Boolean.toString(chatTimestamps));
        DATA.setProperty("waypointNotifications", Boolean.toString(waypointNotifications));
        persist();
    }

    public static void saveHotbarProfile() {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;
        StringBuilder profile = new StringBuilder("Hotbar ");
        for (int slot = 0; slot < 9; slot++) {
            if (slot > 0) profile.append(" | ");
            ItemStack stack = client.player.getInventory().getItem(slot);
            profile.append(slot + 1).append(":");
            profile.append(stack.isEmpty() ? "leer" : stack.getHoverName().getString() + " x" + stack.getCount());
        }
        profiles.add(profile.toString());
        rebuildList("profile.", profiles);
        persist();
        LinkUpClient.message("Hotbar-Profil lokal gespeichert.");
    }

    public static void saveWaypoint() {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null) return;
        var p = client.player.position();
        String dimension = client.level.dimension().toString();
        String waypoint = String.format(Locale.ROOT, "%s @ %.1f, %.1f, %.1f", dimension, p.x, p.y, p.z);
        waypoints.add(waypoint);
        rebuildList("waypoint.", waypoints);
        persist();
        LinkUpClient.message("Wegpunkt gespeichert: " + waypoint);
    }

    private static void rebuildList(String prefix, List<String> values) {
        DATA.keySet().removeIf(key -> key.toString().startsWith(prefix));
        for (int i = 0; i < values.size(); i++) DATA.setProperty(prefix + i, values.get(i));
    }

    private static void persist() {
        if (file == null) return;
        try (var out = Files.newOutputStream(file)) {
            DATA.store(out, "LinkUp local client settings");
        } catch (IOException e) {
            LinkUpClient.message("Speichern fehlgeschlagen: " + e.getMessage());
        }
    }
}
