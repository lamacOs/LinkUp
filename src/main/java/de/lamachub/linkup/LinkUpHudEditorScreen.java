package de.lamachub.linkup;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            Minecraft client = Minecraft.getInstance();
            String coords = client.player == null ? "XYZ 0.0 / 0.0 / 0.0" : LinkUpHud.coordinates(client);
            String nearest = client.player == null ? "" : LinkUpHud.nearestWaypoint(client,
                    client.player.getX(), client.player.getY(), client.player.getZ());
            int x = LinkUpData.hudX, y = LinkUpData.hudY;
            int w = LinkUpHud.hudWidth(client, coords, nearest), h = LinkUpHud.hudHeight(nearest);
            if (mouseX >= x - 5 && mouseX <= x + w + 2 && mouseY >= y - 5 && mouseY <= y + h + 2) {
                dragging = true;
                dragOffsetX = (int) mouseX - x;
                dragOffsetY = (int) mouseY - y;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (dragging && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
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
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) dragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_J || keyCode == GLFW.GLFW_KEY_ESCAPE) {
            onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override public void onClose() { Minecraft.getInstance().gui.setScreen(null); }
    @Override public boolean isPauseScreen() { return false; }
}
