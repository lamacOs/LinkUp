package de.lamachub.linkup;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public final class LinkUpScreen extends Screen {
    private int page = 0;
    private int scroll = 0;
    private int left, top, panelWidth, panelHeight, contentLeft, contentWidth, bodyTop, bodyBottom;
    private static final String[] PAGES = {"Übersicht", "Profile", "Wegpunkte", "Einstellungen"};

    public LinkUpScreen() { super(Component.literal("LinkUp")); }

    @Override
    protected void init() {
        clearWidgets();
        panelWidth = Math.max(200, Math.min(660, width - 20));
        panelHeight = Math.max(180, Math.min(420, height - 20));
        left = (width - panelWidth) / 2;
        top = (height - panelHeight) / 2;
        int sidebar = Math.min(142, Math.max(88, panelWidth / 4));
        contentLeft = left + sidebar + 18;
        contentWidth = Math.max(90, left + panelWidth - 16 - contentLeft);
        bodyTop = top + 96;
        bodyBottom = top + panelHeight - 48;

        int navY = top + 62;
        int navW = sidebar - 20;
        for (int i = 0; i < PAGES.length; i++) {
            final int target = i;
            addRenderableWidget(Button.builder(Component.literal((page == i ? "◆ " : "  ") + PAGES[i]), b -> {
                page = target; scroll = 0; rebuildWidgets();
            }).bounds(left + 10, navY + i * 32, navW, 27).build());
        }

        if (page == 0) {
            addFooterButton("Hotbar sichern", contentLeft, contentWidth / 2 - 4, () -> LinkUpData.saveHotbarProfile());
            addFooterButton("Wegpunkt setzen", contentLeft + contentWidth / 2 + 4, contentWidth / 2 - 4, () -> LinkUpData.saveWaypoint());
        } else if (page == 1) {
            addFooterButton("Hotbar-Profil speichern", contentLeft, contentWidth, () -> LinkUpData.saveHotbarProfile());
        } else if (page == 2) {
            addFooterButton("Aktuelle Position speichern", contentLeft, contentWidth, () -> LinkUpData.saveWaypoint());
        } else {
            addScrollButton("Kompaktes HUD: " + (LinkUpData.compactHud ? "AN" : "AUS"), 0, () -> {
                LinkUpData.compactHud = !LinkUpData.compactHud; LinkUpData.saveSettings(); rebuildWidgets();
            });
            addScrollButton("Chat-Zeitstempel: " + (LinkUpData.chatTimestamps ? "AN" : "AUS"), 42, () -> {
                LinkUpData.chatTimestamps = !LinkUpData.chatTimestamps; LinkUpData.saveSettings(); rebuildWidgets();
            });
            addScrollButton("Wegpunkt-Hinweise: " + (LinkUpData.waypointNotifications ? "AN" : "AUS"), 84, () -> {
                LinkUpData.waypointNotifications = !LinkUpData.waypointNotifications; LinkUpData.saveSettings(); rebuildWidgets();
            });
            addScrollButton("HUD mit J verschieben", 126, () -> LinkUpClient.openHudEditor());
        }
    }

    private void addFooterButton(String label, int x, int w, Runnable action) {
        int y = top + panelHeight - 38;
        if (w < 70 || y < top || y + 24 > top + panelHeight) return;
        addRenderableWidget(Button.builder(Component.literal(label), b -> action.run())
                .bounds(x, y, Math.min(w, left + panelWidth - 12 - x), 24).build());
    }

    private void addScrollButton(String label, int offset, Runnable action) {
        int y = bodyTop + offset - scroll;
        if (y < bodyTop || y + 26 > bodyBottom) return;
        addRenderableWidget(Button.builder(Component.literal(label), b -> action.run())
                .bounds(contentLeft, y, contentWidth, 24).build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        int right = left + panelWidth, bottom = top + panelHeight;
        g.fill(0, 0, width, height, 0x99050A12);
        g.fill(left, top, right, bottom, 0xF0121824);
        g.fill(left, top, right, top + 46, 0xFF1B2636);
        g.fill(left, top, left + 3, bottom, 0xFF01C7FC);
        g.fill(left + 3, top + 45, right, top + 47, 0xFF01C7FC);
        text(g, "LinkUp", left + 16, top + 12, 0xFF01C7FC);
        text(g, "CLIENT  /  MINECRAFT 26.3", left + 96, top + 14, 0xFFB7C6D9);
        text(g, "RSHIFT  •  ESC", right - 100, top + 14, 0xFF7F91A8);
        int sidebar = Math.min(142, Math.max(88, panelWidth / 4));
        g.fill(left + 8, top + 54, left + sidebar, bottom - 10, 0xFF151E2B);
        g.fill(left + sidebar + 7, top + 57, left + sidebar + 8, bottom - 12, 0xFF303D50);
        text(g, "WORKSPACE", left + 16, top + 54, 0xFF6E839B);
        text(g, PAGES[page].toUpperCase(), contentLeft, top + 57, 0xFFFFFFFF);
        g.fill(contentLeft, top + 75, right - 16, top + 76, 0xFF303D50);

        if (page == 0) {
            drawWrapped(g, "Dein Client. Dein Setup.", contentLeft, bodyTop - scroll, contentWidth, 0xFF01C7FC, 14);
            drawWrapped(g, "Ein aufgeräumtes Kontrollzentrum für deine lokalen Client-Werkzeuge. Deine Daten bleiben auf diesem Gerät.", contentLeft, bodyTop + 24 - scroll, contentWidth, 0xFFE0E8F2, 14);
            drawCard(g, contentLeft, bodyTop + 82 - scroll, contentWidth, 42);
            drawWrapped(g, "HOTBAR-PROFILE", contentLeft + 10, bodyTop + 91 - scroll, contentWidth - 20, 0xFF01C7FC, 14);
            drawWrapped(g, "Speichert die neun aktuellen Slots als Snapshot.", contentLeft + 10, bodyTop + 106 - scroll, contentWidth - 20, 0xFFB7C6D9, 13);
            drawCard(g, contentLeft, bodyTop + 132 - scroll, contentWidth, 42);
            drawWrapped(g, "WEGPUNKTE", contentLeft + 10, bodyTop + 141 - scroll, contentWidth - 20, 0xFFFFC46B, 14);
            drawWrapped(g, "Positionen sichern und Entfernung im HUD sehen.", contentLeft + 10, bodyTop + 156 - scroll, contentWidth - 20, 0xFFB7C6D9, 13);
            drawWrapped(g, "Tipp: Mit J kannst du das HUD verschieben. Mausrad scrollt durch Inhalte.", contentLeft, bodyTop + 190 - scroll, contentWidth, 0xFF9AA9BC, 13);
        } else if (page == 1 || page == 2) {
            List<String> entries = page == 1 ? LinkUpData.profiles() : LinkUpData.waypoints();
            if (entries.isEmpty()) {
                drawWrapped(g, page == 1 ? "Noch keine Profile gespeichert." : "Noch keine Wegpunkte gespeichert.", contentLeft, bodyTop - scroll, contentWidth, 0xFFB7C6D9, 14);
                drawWrapped(g, page == 1 ? "Unten „Hotbar-Profil speichern“ wählen." : "Unten „Aktuelle Position speichern“ wählen.", contentLeft, bodyTop + 24 - scroll, contentWidth, 0xFF8FA1B7, 13);
            } else {
                int y = bodyTop - scroll;
                for (String entry : entries) {
                    int lines = Math.max(1, font.split(Component.literal(entry), Math.max(30, contentWidth - 16)).size());
                    int h = Math.max(34, lines * 13 + 12);
                    if (y + h >= bodyTop && y <= bodyBottom) {
                        drawCard(g, contentLeft, y, contentWidth, h);
                        drawWrapped(g, entry, contentLeft + 8, y + 8, contentWidth - 16, 0xFFE0E8F2, 13);
                    }
                    y += h + 5;
                }
            }
        } else {
            drawWrapped(g, "Scrolle mit dem Mausrad durch die Einstellungen. Änderungen werden lokal gespeichert.", contentLeft, bodyTop - scroll, contentWidth, 0xFF9AA9BC, 13);
        }
        text(g, "LAMACHUB / LINKUP", left + 14, bottom - 18, 0xFF53677F);
        text(g, "MAUSRAD = SCROLLEN", right - 150, bottom - 18, 0xFF53677F);
        super.extractRenderState(g, mouseX, mouseY, delta);
    }

    private void drawCard(GuiGraphicsExtractor g, int x, int y, int w, int h) {
        if (y + h < bodyTop || y > bodyBottom) return;
        g.fill(x, Math.max(bodyTop, y), x + w, Math.min(bodyBottom, y + h), 0xFF1B2635);
        g.fill(x, Math.max(bodyTop, y), x + 2, Math.min(bodyBottom, y + h), 0xFF30475F);
    }

    private void text(GuiGraphicsExtractor g, String s, int x, int y, int color) {
        if (y >= bodyTop - 15 && y <= bodyBottom) g.text(font, s, x, y, color, true);
        else if (y < top + 48 || y > top + panelHeight - 24) g.text(font, s, x, y, color, true);
    }

    private void drawWrapped(GuiGraphicsExtractor g, String value, int x, int y, int maxWidth, int color, int lineHeight) {
        for (var line : font.split(Component.literal(value), Math.max(24, maxWidth))) {
            if (y >= bodyTop && y + lineHeight <= bodyBottom) g.text(font, line, x, y, color, true);
            y += lineHeight;
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int maxScroll = page == 3 ? 126 : page == 0 ? 190 : 5000;
        scroll = Math.max(0, Math.min(maxScroll, scroll - (int)(verticalAmount * 24)));
        rebuildWidgets();
        return true;
    }

    @Override public boolean isPauseScreen() { return false; }
}
