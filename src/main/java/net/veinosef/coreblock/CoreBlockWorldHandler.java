package net.veinosef.coreblock;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;

public class CoreBlockWorldHandler {

    public static void register() {
        // Dünyadaki tüm arazi üretimini anında silip %100 saf boşluk yapar
        ServerChunkEvents.CHUNK_LOAD.register((world, chunk) -> {
            if (world.getRegistryKey() == ServerWorld.OVERWORLD && chunk instanceof WorldChunk worldChunk) {
                int cx = chunk.getPos().x;
                int cz = chunk.getPos().z;

                // Sadece merkez platformun olduğu yer haricindeki tüm blokları havaya çevir
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        int wx = (cx << 4) + x;
                        int wz = (cz << 4) + z;

                        for (int y = world.getBottomY(); y < 120; y++) {
                            if (wx >= -1 && wx <= 1 && wz >= -1 && wz <= 1) {
                                if (y == 63 || y == 64) continue; // 3x3 ada ve alt bedrock kalır
                            }
                            BlockPos p = new BlockPos(wx, y, wz);
                            if (!chunk.getBlockState(p).isAir()) {
                                chunk.setBlockState(p, Blocks.AIR.getDefaultState(), false);
                            }
                        }
                    }
                }
            }
        });

        // Dünya açıldığında platformu hazırla
        ServerWorldEvents.LOAD.register((server, world) -> {
            if (world.getRegistryKey() == ServerWorld.OVERWORLD) {
                setupCoreIsland(world);
                generateWorldIcon(server);
            }
        });

        // Oyuncu girdiğinde platforma ışınla
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerWorld world = server.getOverworld();
            setupCoreIsland(world);
            handler.getPlayer().teleport(world, 0.5, 65.0, 0.5, 0.0f, 0.0f);
        });
    }

    public static void setupCoreIsland(ServerWorld world) {
        // Çimento ve kumun düşmesini engelleyen alt Bedrock
        world.setBlockState(new BlockPos(0, 63, 0), Blocks.BEDROCK.getDefaultState());

        // 3x3 Başlangıç Platformu
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                world.setBlockState(new BlockPos(x, 64, z), Blocks.DIRT.getDefaultState());
            }
        }

        world.setSpawnPos(new BlockPos(0, 65, 0), 0.0f);
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
