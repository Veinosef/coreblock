package net.veinosef.coreblock;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
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
    public static void register() {
        ServerWorldEvents.LOAD.register((server, world) -> {
            if (world.getRegistryKey() == ServerWorld.OVERWORLD) {
                BlockPos corePos = new BlockPos(0, 64, 0);

                if (world.isAir(corePos)) {
                    world.setBlockState(new BlockPos(0, 63, 0), Blocks.BEDROCK.getDefaultState());

                    for (int x = -1; x <= 1; x++) {
                        for (int z = -1; z <= 1; z++) {
                            BlockPos pos = new BlockPos(x, 64, z);
                            world.setBlockState(pos, Blocks.DIRT.getDefaultState());
                        }
                    }
                    world.setSpawnPos(new BlockPos(0, 65, 0), 0.0f);
                }

                generateWorldIcon(server);
            }
        });
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
