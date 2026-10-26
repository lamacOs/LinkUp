package de.lamachub.linkup;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public final class LinkUpHudEditorScreen extends Screen {
    private boolean dragging;
    private int dragOffsetX;
    private int dragOffsetY;

    public LinkUpHudEditorScreen() { super(Component.literal("LinkUp HUD verschieben")); }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        Minecraft client = Minecraft.getInstance();
        g.fill(0, 0, width, height, 0x99050A12);
        g.fill(18, 18, Math.min(width - 18, 420), 64, 0xF0121824);
        g.fill(18, 18, Math.min(width - 18, 420), 20, 0xFF01C7FC);
        g.text(font, "LINKUP HUD-EDITOR", 30, 29, 0xFF01C7FC, true);
        g.text(font, "Ziehe das HUD mit gedrückter linker Maustaste. J oder ESC beendet.", 30, 44, 0xFFE0E8F2, true);

        String coords = client.player == null ? "XYZ 0.0 / 0.0 / 0.0" : LinkUpHud.coordinates(client);
        String nearest = client.player == null ? "" : LinkUpHud.nearestWaypoint(client,
                client.player.getX(), client.player.getY(), client.player.getZ());
        int x = LinkUpData.hudX;
        int y = LinkUpData.hudY;
        int w = LinkUpHud.hudWidth(client, coords, nearest);
        int h = LinkUpHud.hudHeight(nearest);
        g.fill(x - 5, y - 5, x + w + 2, y + h + 2, 0xAA01C7FC);
        g.fill(x - 3, y - 3, x + w, y + h, 0xEE0E141D);
        g.fill(x - 3, y - 3, x, y + h, 0xFF01C7FC);
        g.text(font, "LinkUp", x + 3, y, 0xFF01C7FC, true);
        g.text(font, coords, x + 3, y + 12, 0xFFE3EAF2, true);
        if (!nearest.isEmpty()) g.text(font, nearest, x + 3, y + 24, 0xFFFFC46B, true);
        super.extractRenderState(g, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0) {
            Minecraft client = Minecraft.getInstance();
            String coords = client.player == null ? "XYZ 0.0 / 0.0 / 0.0" : LinkUpHud.coordinates(client);
            String nearest = client.player == null ? "" : LinkUpHud.nearestWaypoint(client,
                    client.player.getX(), client.player.getY(), client.player.getZ());
            int x = LinkUpData.hudX, y = LinkUpData.hudY;
            int w = LinkUpHud.hudWidth(client, coords, nearest), h = LinkUpHud.hudHeight(nearest);
            if (event.x() >= x - 5 && event.x() <= x + w + 2
                    && event.y() >= y - 5 && event.y() <= y + h + 2) {
                dragging = true;
                dragOffsetX = (int) event.x() - x;
                dragOffsetY = (int) event.y() - y;
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double mouseX, double mouseY) {
        if (dragging && event.button() == 0) {
            Minecraft client = Minecraft.getInstance();
            String coords = client.player == null ? "XYZ 0.0 / 0.0 / 0.0" : LinkUpHud.coordinates(client);
            String nearest = client.player == null ? "" : LinkUpHud.nearestWaypoint(client,
                    client.player.getX(), client.player.getY(), client.player.getZ());
            int maxX = Math.max(0, width - LinkUpHud.hudWidth(client, coords, nearest));
            int maxY = Math.max(0, height - LinkUpHud.hudHeight(nearest));
            LinkUpData.setHudPosition(Math.min(maxX, Math.max(0, (int) mouseX - dragOffsetX)),
                    Math.min(maxY, Math.max(0, (int) mouseY - dragOffsetY)));
            return true;
        }
        return super.mouseDragged(event, mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (event.button() == 0 && dragging) {
            dragging = false;
            LinkUpData.saveSettings();
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == InputConstants.KEY_J || event.key() == InputConstants.KEY_ESCAPE) {
            onClose();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override public void onClose() { Minecraft.getInstance().gui.setScreen(null); }
    @Override public boolean isPauseScreen() { return false; }
}
