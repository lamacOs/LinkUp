package de.lamachub.linkup;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public final class LinkUpScreen extends Screen {
    private int page = 0;
    private int scroll = 0;
    private int panelLeft;
    private int panelTop;
    private int panelWidth;
    private int panelHeight;
    private int contentLeft;
    private int contentWidth;

    private static final String[] PAGES = {"Übersicht", "Profile", "Wegpunkte", "Einstellungen"};

    public LinkUpScreen() {
        super(Component.literal("LinkUp"));
    }

    @Override
    protected void init() {
        clearWidgets();
        panelWidth = Math.min(620, width - 24);
        panelHeight = Math.min(390, height - 24);
        panelLeft = (width - panelWidth) / 2;
        panelTop = (height - panelHeight) / 2;
        int sidebarWidth = Math.min(142, Math.max(104, panelWidth / 4));
        contentLeft = panelLeft + sidebarWidth + 18;
        contentWidth = Math.max(90, panelWidth - sidebarWidth - 34);

        int navY = panelTop + 65;
        for (int i = 0; i < PAGES.length; i++) {
            final int target = i;
            addRenderableWidget(Button.builder(Component.literal((page == i ? "◆  " : "   ") + PAGES[i]), b -> {
                page = target;
                scroll = 0;
                rebuildWidgets();
            }).bounds(panelLeft + 10, navY + i * 34, sidebarWidth - 20, 28).build());
        }

        int bodyTop = panelTop + 82;
        int buttonY = panelTop + panelHeight - 48;
        if (page == 0) {
            int half = Math.max(80, (contentWidth - 8) / 2);
            addButton("Hotbar sichern", contentLeft, buttonY, half, () -> LinkUpData.saveHotbarProfile());
            addButton("Wegpunkt setzen", contentLeft + half + 8, buttonY, half, () -> LinkUpData.saveWaypoint());
        } else if (page == 1) {
            addButton("Hotbar-Profil speichern", contentLeft, buttonY, Math.min(contentWidth, 220), () -> LinkUpData.saveHotbarProfile());
            addButton("Zurück nach oben", contentLeft + Math.min(contentWidth, 228), buttonY, Math.max(80, contentWidth - 228), () -> { scroll = 0; rebuildWidgets(); });
        } else if (page == 2) {
            addButton("Aktuelle Position speichern", contentLeft, buttonY, Math.min(contentWidth, 240), () -> LinkUpData.saveWaypoint());
            addButton("Liste nach oben", contentLeft + Math.min(contentWidth, 248), buttonY, Math.max(80, contentWidth - 248), () -> { scroll = 0; rebuildWidgets(); });
        } else {
            addButton("Kompaktes HUD: " + (LinkUpData.compactHud ? "AN" : "AUS"), contentLeft, bodyTop, contentWidth, () -> {
                LinkUpData.compactHud = !LinkUpData.compactHud; LinkUpData.saveSettings(); rebuildWidgets();
            });
            addButton("Chat-Zeitstempel: " + (LinkUpData.chatTimestamps ? "AN" : "AUS"), contentLeft, bodyTop + 42, contentWidth, () -> {
                LinkUpData.chatTimestamps = !LinkUpData.chatTimestamps; LinkUpData.saveSettings(); rebuildWidgets();
            });
            addButton("Wegpunkt-Hinweise: " + (LinkUpData.waypointNotifications ? "AN" : "AUS"), contentLeft, bodyTop + 84, contentWidth, () -> {
                LinkUpData.waypointNotifications = !LinkUpData.waypointNotifications; LinkUpData.saveSettings(); rebuildWidgets();
            });
        }
    }

    private void addButton(String label, int x, int y, int buttonWidth, Runnable action) {
        int safeWidth = Math.max(70, Math.min(buttonWidth, panelLeft + panelWidth - 12 - x));
        addRenderableWidget(Button.builder(Component.literal(label), b -> action.run())
                .bounds(x, y, safeWidth, 28).build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        int right = panelLeft + panelWidth;
        int bottom = panelTop + panelHeight;

        // Premium dark panel with cyan accent and layered sections.
        graphics.fill(0, 0, width, height, 0x88050A12);
        graphics.fill(panelLeft, panelTop, right, bottom, 0xF0121824);
        graphics.fill(panelLeft, panelTop, right, panelTop + 48, 0xFF1B2636);
        graphics.fill(panelLeft, panelTop, panelLeft + 3, bottom, 0xFF01C7FC);
        graphics.fill(panelLeft + 3, panelTop + 47, right, panelTop + 49, 0xFF01C7FC);
        text(graphics, "LinkUp", panelLeft + 18, panelTop + 13, 0xFF01C7FC);
        text(graphics, "CLIENT  /  MINECRAFT 26.3", panelLeft + 100, panelTop + 15, 0xFFB7C6D9);
        text(graphics, "RIGHT SHIFT  •  ESC ZUM SCHLIESSEN", right - 220, panelTop + 16, 0xFF7F91A8);

        int sidebarWidth = Math.min(142, Math.max(104, panelWidth / 4));
        graphics.fill(panelLeft + 8, panelTop + 55, panelLeft + sidebarWidth, bottom - 10, 0xFF151E2B);
        int dividerX = panelLeft + sidebarWidth + 7;
        graphics.fill(dividerX, panelTop + 58, dividerX + 1, bottom - 12, 0xFF303D50);

        text(graphics, "WORKSPACE", panelLeft + 18, panelTop + 55, 0xFF6E839B);
        text(graphics, PAGES[page].toUpperCase(), contentLeft, panelTop + 58, 0xFFFFFFFF);
        graphics.fill(contentLeft, panelTop + 76, right - 16, panelTop + 77, 0xFF303D50);

        int bodyTop = panelTop + 92;
        int maxTextWidth = Math.max(70, contentWidth);
        if (page == 0) {
            text(graphics, "Dein Client. Dein Setup.", contentLeft, bodyTop, 0xFF01C7FC);
            wrapped(graphics, "Ein aufgeräumtes Kontrollzentrum für deine lokalen Client-Werkzeuge. Deine gespeicherten Daten bleiben auf diesem Gerät.", contentLeft, bodyTop + 22, maxTextWidth, 0xFFE0E8F2, 14);
            card(graphics, contentLeft, bodyTop + 84, maxTextWidth, 46);
            text(graphics, "HOTBAR-PROFILE", contentLeft + 12, bodyTop + 94, 0xFF01C7FC);
            text(graphics, "Speichert die neun aktuellen Slots als Snapshot.", contentLeft + 12, bodyTop + 111, 0xFFB7C6D9);
            card(graphics, contentLeft, bodyTop + 138, maxTextWidth, 46);
            text(graphics, "WEGPUNKTE", contentLeft + 12, bodyTop + 148, 0xFFFFC46B);
            text(graphics, "Positionen sichern und Entfernung im HUD sehen.", contentLeft + 12, bodyTop + 165, 0xFFB7C6D9);
            wrapped(graphics, "Hinweis: Ein Snapshot sortiert dein Inventar noch nicht automatisch. Online-Social und KI-Chat sind noch nicht enthalten.", contentLeft, bodyTop + 198, maxTextWidth, 0xFF9AA9BC, 13);
        } else if (page == 1) {
            List<String> entries = LinkUpData.profiles();
            if (entries.isEmpty()) {
                text(graphics, "Noch keine Profile gespeichert.", contentLeft, bodyTop, 0xFFB7C6D9);
                wrapped(graphics, "Wechsle zur Übersicht und wähle „Hotbar sichern“, um den aktuellen Inhalt deiner Hotbar lokal zu speichern.", contentLeft, bodyTop + 22, maxTextWidth, 0xFF8FA1B7, 14);
            } else {
                int y = bodyTop;
                for (int i = scroll; i < Math.min(entries.size(), scroll + 5); i++) {
                    card(graphics, contentLeft, y, maxTextWidth, 34);
                    wrapped(graphics, entries.get(i), contentLeft + 8, y + 10, maxTextWidth - 16, 0xFFE0E8F2, 13);
                    y += 39;
                }
            }
        } else if (page == 2) {
            List<String> entries = LinkUpData.waypoints();
            if (entries.isEmpty()) {
                text(graphics, "Noch keine Wegpunkte.", contentLeft, bodyTop, 0xFFB7C6D9);
                wrapped(graphics, "Speichere deine aktuelle Position über „Wegpunkt setzen“. Die Position wird lokal abgelegt.", contentLeft, bodyTop + 22, maxTextWidth, 0xFF8FA1B7, 14);
            } else {
                int y = bodyTop;
                for (int i = scroll; i < Math.min(entries.size(), scroll + 5); i++) {
                    card(graphics, contentLeft, y, maxTextWidth, 34);
                    wrapped(graphics, entries.get(i), contentLeft + 8, y + 10, maxTextWidth - 16, 0xFFE0E8F2, 13);
                    y += 39;
                }
            }
        } else {
            wrapped(graphics, "Schalte die Optionen über die Buttons um. Einstellungen werden gespeichert; Funktionen, die noch nicht implementiert sind, bleiben ohne Effekt.", contentLeft, bodyTop + 130, maxTextWidth, 0xFF8FA1B7, 14);
        }
        text(graphics, "LAMACHUB  /  LINKUP", panelLeft + 16, bottom - 19, 0xFF53677F);
    }

    private void card(GuiGraphicsExtractor graphics, int x, int y, int w, int h) {
        graphics.fill(x, y, x + w, y + h, 0xFF1B2635);
        graphics.fill(x, y, x + 2, y + h, 0xFF30475F);
    }

    private void text(GuiGraphicsExtractor graphics, String value, int x, int y, int color) {
        graphics.text(font, value, x, y, color, true);
    }

    private void wrapped(GuiGraphicsExtractor graphics, String value, int x, int y, int maxWidth, int color, int lineHeight) {
        int safeWidth = Math.max(20, maxWidth);
        for (var line : font.split(Component.literal(value), safeWidth)) {
            graphics.text(font, line, x, y, color, true);
            y += lineHeight;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
