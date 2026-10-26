package de.lamachub.linkup;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

import java.util.Locale;

public final class LinkUpHud {
    private LinkUpHud() {}

    public static void register() {
        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath(LinkUpClient.MOD_ID, "hud"),
                LinkUpHud::render
        );
    }

    private static void render(GuiGraphicsExtractor graphics, net.minecraft.client.DeltaTracker tickCounter) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null) return;
        String coordinates = coordinates(client);
        String nearest = nearestWaypoint(client, client.player.getX(), client.player.getY(), client.player.getZ());
        int x = LinkUpData.hudX;
        int y = LinkUpData.hudY;
        int width = hudWidth(client, coordinates, nearest);
        int height = hudHeight(nearest);

        if (LinkUpData.compactHud) {
            graphics.fill(x - 3, y - 3, x + width, y + height, 0x990E141D);
            graphics.fill(x - 3, y - 3, x, y + height, 0xFF01C7FC);
        }
        graphics.text(client.font, "LinkUp", x + 3, y, 0xFF01C7FC, true);
        graphics.text(client.font, coordinates, x + 3, y + 12, 0xFFE3EAF2, true);
        if (!nearest.isEmpty()) graphics.text(client.font, nearest, x + 3, y + 24, 0xFFFFC46B, true);
    }

    public static String coordinates(Minecraft client) {
        var p = client.player.position();
        return String.format(Locale.ROOT, "XYZ %.1f / %.1f / %.1f", p.x, p.y, p.z);
    }

    public static String nearestWaypoint(Minecraft client, double x, double y, double z) {
        if (client.level == null) return "";
        String currentDimension = client.level.dimension().toString();
        String nearest = "";
        double nearestDistance = Double.MAX_VALUE;
        for (String waypoint : LinkUpData.waypoints()) {
            int split = waypoint.lastIndexOf(" @ ");
            if (split < 0 || !waypoint.substring(0, split).equals(currentDimension)) continue;
            String[] coordinates = waypoint.substring(split + 3).split(", ");
            if (coordinates.length != 3) continue;
            try {
                double wx = Double.parseDouble(coordinates[0]);
                double wy = Double.parseDouble(coordinates[1]);
                double wz = Double.parseDouble(coordinates[2]);
                double distance = Math.sqrt(Math.pow(wx - x, 2) + Math.pow(wy - y, 2) + Math.pow(wz - z, 2));
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearest = String.format(Locale.ROOT, "Wegpunkt: %.0f m", distance);
                }
                if (LinkUpData.waypointNotifications && distance <= 5.0) LinkUpData.notifyWaypoint(waypoint);
            } catch (NumberFormatException ignored) {
                // Ignore old or malformed saved waypoint entries.
            }
        }
        return nearest;
    }

    public static int hudWidth(Minecraft client, String coordinates, String nearest) {
        int width = Math.max(client.font.width(coordinates), Math.max(client.font.width(nearest), client.font.width("LinkUp"))) + 12;
        return width;
    }

    public static int hudHeight(String nearest) { return nearest.isEmpty() ? 30 : 42; }
}
