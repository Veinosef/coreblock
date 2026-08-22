package net.veinosef.coreblock;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;

public class CoreBlockWorldHandler {
    private static boolean islandCreated = false;

    public static void register() {
        // Dünya yüklendiğinde adayı hazırla
        ServerWorldEvents.LOAD.register((server, world) -> {
            if (world.getRegistryKey() == ServerWorld.OVERWORLD) {
                setupCoreIsland(world);
                generateWorldIcon(server);
            }
        });

        // Oyuncu oyuna girdiğinde adaya ışınla
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerWorld world = server.getOverworld();
            setupCoreIsland(world);
            handler.getPlayer().teleport(world, 0.5, 65.0, 0.5, 0.0f, 0.0f);
        });
    }

    private static void setupCoreIsland(ServerWorld world) {
        if (islandCreated && !world.isAir(new BlockPos(0, 64, 0))) {
            return;
        }

        // Çevredeki 32x32 alanı havaya çevirip temizle
        for (int x = -16; x <= 16; x++) {
            for (int z = -16; z <= 16; z++) {
                for (int y = 50; y <= 90; y++) {
                    BlockPos p = new BlockPos(x, y, z);
                    if (!world.isAir(p)) {
                        world.setBlockState(p, Blocks.AIR.getDefaultState());
                    }
                }
            }
        }

        // Düşmeyi engelleyen taban Bedrock
        world.setBlockState(new BlockPos(0, 63, 0), Blocks.BEDROCK.getDefaultState());

        // 3x3 Başlangıç Toprak Platformu
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                world.setBlockState(new BlockPos(x, 64, z), Blocks.DIRT.getDefaultState());
            }
        }

        // Doğuş noktasını ayarla
        world.setSpawnPos(new BlockPos(0, 65, 0), 0.0f);
        islandCreated = true;
    }

    private static void generateWorldIcon(MinecraftServer server) {
        try {
            Path savePath = server.getSavePath(WorldSavePath.ROOT);
            File iconFile = new File(savePath.toFile(), "icon.png");

            if (!iconFile.exists()) {
                BufferedImage image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = image.createGraphics();

                g.setColor(new Color(15, 10, 25));
                g.fillRect(0, 0, 64, 64);

                g.setColor(new Color(40, 20, 60));
                g.fillRect(12, 12, 40, 40);

                g.setColor(new Color(110, 30, 180));
                g.fillRect(16, 16, 32, 32);

                g.setColor(new Color(0, 240, 255));
                g.fillRect(24, 24, 16, 16);

                g.setColor(Color.WHITE);
                g.fillRect(28, 28, 8, 8);

                g.dispose();
                ImageIO.write(image, "PNG", iconFile);
            }
        } catch (Exception ignored) {
        }
    }
}
