# LinkUp — Fabric client mod

LinkUp is a client-side Minecraft Java mod project targeting Fabric and Minecraft 26.3.

## Build

Pushes to `main` run the GitHub Actions build. When the build succeeds, open the **Actions** tab, choose the latest **Build LinkUp** run, and download the `LinkUp-Fabric-Minecraft-26.3` artifact.

## Current implemented features

- In-game client-style dashboard opened with **Right Shift**.
- Local settings stored in `.minecraft/config/linkup.properties`.
- Local hotbar snapshots (the mod does not move items or alter server inventory).
- Local waypoints storing dimension and coordinates.
- Settings toggles are persisted locally.

## Not implemented yet

Automatic inventory rearrangement, live player-to-player social features, waypoint HUD markers/alerts, AI chat, and NeoForge support are not implemented in this build. The interface explicitly labels the current limits; those features are not represented as working.

## Safety and multiplayer

LinkUp does not bypass server rules, fabricate server-side actions, or send inventory packets outside normal client interactions.

## License

MIT.
