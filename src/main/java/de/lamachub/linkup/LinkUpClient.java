package de.lamachub.linkup;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public final class LinkUpClient implements ClientModInitializer {
    public static final String MOD_ID = "linkup";
    private static boolean rightShiftWasDown;

    @Override
    public void onInitializeClient() {
        LinkUpData.load();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            boolean rightShiftDown = GLFW.glfwGetKey(client.getWindow().getWindow(), GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
            if (rightShiftDown && !rightShiftWasDown && client.gui.screen() == null) {
                client.gui.setScreen(new LinkUpScreen());
            }
            rightShiftWasDown = rightShiftDown;
        });
    }

    public static void message(String message) {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null) {
            client.player.sendSystemMessage(Component.literal("[LinkUp] " + message));
        }
    }
}
