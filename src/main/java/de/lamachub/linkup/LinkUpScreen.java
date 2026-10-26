package de.lamachub.linkup;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public final class LinkUpScreen extends Screen {
    private int page = 0;
    private int scroll = 0;

    public LinkUpScreen() {
        super(Component.literal("LinkUp"));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        int left = Math.max(12, (width - 440) / 2);
        int top = Math.max(12, (height - 300) / 2);
        int right = Math.min(width - 12, left + 440);
        int bottom = Math.min(height - 12, top + 300);

        graphics.fill(left, top, right, bottom, 0xF0181C25);
        graphics.fill(left, top, right, top + 36, 0xFF222B39);
        graphics.fill(left, top + 35, right, top + 37, 0xFF01C7FC);
        text(graphics, "LinkUp", left + 14, top + 12, 0xFF01C7FC);
        text(graphics, "CLIENT DASHBOARD  •  Minecraft 26.3", left + 92, top + 12, 0xFFE3EAF2);

        int navY = top + 52;
        String[] nav = {"Übersicht", "Profile", "Wegpunkte", "Einstellungen"};
        for (int i = 0; i < nav.length; i++) {
            int y = navY + i * 30;
            graphics.fill(left + 10, y - 4, left + 112, y + 20, page == i ? 0xFF17445A : 0xFF252D38);
            text(graphics, nav[i], left + 18, y + 3, page == i ? 0xFF01C7FC : 0xFFE0E6ED);
        }

        int contentX = left + 130;
        int contentY = top + 52;
        text(graphics, nav[page], contentX, contentY, 0xFFFFFFFF);
        graphics.fill(contentX, contentY + 15, right - 14, contentY + 16, 0xFF3C4654);

        if (page == 0) {
            line(graphics, contentX, contentY + 30, "Lokale Client-Funktionen", 0xFF01C7FC);
            line(graphics, contentX, contentY + 52, "• Einstellungen und Profile werden offline gespeichert.", 0xFFE3EAF2);
            line(graphics, contentX, contentY + 72, "• Hotbar-Profil speichern: Schaltfläche unten.", 0xFFE3EAF2);
            line(graphics, contentX, contentY + 92, "• Wegpunkt: speichert deine aktuelle Position lokal.", 0xFFE3EAF2);
            line(graphics, contentX, contentY + 112, "• Öffnen/Schließen: rechte Umschalttaste (Right Shift).", 0xFFE3EAF2);
            line(graphics, contentX, contentY + 150, "Noch nicht enthalten: automatische Inventar-Umsortierung,", 0xFFFFC46B);
            line(graphics, contentX, contentY + 168, "Online-Social und KI-Chat. Kein Server-Bypass.", 0xFFFFC46B);
            drawAction(graphics, contentX, bottom - 42, "Hotbar-Profil speichern");
            drawAction(graphics, contentX + 176, bottom - 42, "Wegpunkt speichern");
        } else if (page == 1) {
            List<String> entries = LinkUpData.profiles();
            if (entries.isEmpty()) line(graphics, contentX, contentY + 34, "Noch keine Profile. Speichere eins in der Übersicht.", 0xFFB9C4D2);
            for (int i = scroll; i < Math.min(entries.size(), scroll + 7); i++) {
                line(graphics, contentX, contentY + 30 + (i - scroll) * 22, entries.get(i), 0xFFE3EAF2);
            }
            line(graphics, contentX, bottom - 27, "Lokale Snapshots; sie verändern dein Inventar nicht automatisch.", 0xFFFFC46B);
        } else if (page == 2) {
            List<String> entries = LinkUpData.waypoints();
            if (entries.isEmpty()) line(graphics, contentX, contentY + 34, "Noch keine Wegpunkte. Speichere einen in der Übersicht.", 0xFFB9C4D2);
            for (int i = scroll; i < Math.min(entries.size(), scroll + 7); i++) {
                line(graphics, contentX, contentY + 30 + (i - scroll) * 22, entries.get(i), 0xFFE3EAF2);
            }
            drawAction(graphics, contentX, bottom - 42, "Aktuelle Position speichern");
        } else {
            drawToggle(graphics, contentX, contentY + 34, "Kompakte HUD-Option", LinkUpData.compactHud);
            drawToggle(graphics, contentX, contentY + 64, "Chat-Zeitstempel-Option", LinkUpData.chatTimestamps);
            drawToggle(graphics, contentX, contentY + 94, "Wegpunkt-Benachrichtigungen", LinkUpData.waypointNotifications);
            line(graphics, contentX, contentY + 140, "Optionen werden lokal gespeichert.", 0xFFB9C4D2);
            line(graphics, contentX, contentY + 160, "Noch nicht aktivierte Effekte bleiben inaktiv.", 0xFFFFC46B);
        }
        text(graphics, "ESC  •  Schließen", left + 14, bottom - 17, 0xFF9BA8B8);
    }

    private void text(GuiGraphicsExtractor graphics, String value, int x, int y, int color) {
        graphics.text(font, value, x, y, color, true);
    }

    private void line(GuiGraphicsExtractor graphics, int x, int y, String value, int color) {
        text(graphics, value, x, y, color);
    }

    private void drawAction(GuiGraphicsExtractor graphics, int x, int y, String label) {
        graphics.fill(x, y, x + 164, y + 25, 0xFF17445A);
        text(graphics, label, x + 7, y + 8, 0xFFFFFFFF);
    }

    private void drawToggle(GuiGraphicsExtractor graphics, int x, int y, String label, boolean enabled) {
        graphics.fill(x, y, x + 290, y + 24, 0xFF252D38);
        text(graphics, label, x + 8, y + 8, 0xFFE3EAF2);
        text(graphics, enabled ? "AN" : "AUS", x + 250, y + 8, enabled ? 0xFF01C7FC : 0xFFFFC46B);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);
        int left = Math.max(12, (width - 440) / 2);
        int top = Math.max(12, (height - 300) / 2);
        int bottom = Math.min(height - 12, top + 300);
        int navY = top + 52;
        for (int i = 0; i < 4; i++) {
            int y = navY + i * 30;
            if (mouseX >= left + 10 && mouseX <= left + 112 && mouseY >= y - 4 && mouseY <= y + 20) {
                page = i;
                scroll = 0;
                return true;
            }
        }
        int contentX = left + 130;
        int contentY = top + 52;
        if (page == 0 && mouseY >= bottom - 42 && mouseY <= bottom - 17) {
            if (mouseX >= contentX && mouseX <= contentX + 164) LinkUpData.saveHotbarProfile();
            else if (mouseX >= contentX + 176 && mouseX <= contentX + 340) LinkUpData.saveWaypoint();
            return true;
        }
        if (page == 2 && mouseY >= bottom - 42 && mouseY <= bottom - 17 && mouseX >= contentX && mouseX <= contentX + 164) {
            LinkUpData.saveWaypoint();
            return true;
        }
        if (page == 3) {
            if (mouseY >= contentY + 34 && mouseY <= contentY + 58) LinkUpData.compactHud = !LinkUpData.compactHud;
            else if (mouseY >= contentY + 64 && mouseY <= contentY + 88) LinkUpData.chatTimestamps = !LinkUpData.chatTimestamps;
            else if (mouseY >= contentY + 94 && mouseY <= contentY + 118) LinkUpData.waypointNotifications = !LinkUpData.waypointNotifications;
            else return super.mouseClicked(mouseX, mouseY, button);
            LinkUpData.saveSettings();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int count = page == 1 ? LinkUpData.profiles().size() : page == 2 ? LinkUpData.waypoints().size() : 0;
        scroll = Math.max(0, Math.min(Math.max(0, count - 7), scroll + (verticalAmount < 0 ? 1 : -1)));
        return true;
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
